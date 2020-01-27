// Grant Ludwig
// BitonicPipline.java
// 1/26/20

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class BitonicPipeline {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    public static final int TIME_ALLOWED = 10;  // seconds
    public static final int NUM_RAND_THREADS = 4; // should be a power of two
    public static final int NUM_STAGE_THREADS = NUM_RAND_THREADS;
    public static final int NUM_BITONIC_THREADS = 3;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        double[] array;
//        System.out.println("");
//        System.out.println("Array Before");
//        for (int i = 0; i < array.length; i++) {
//            System.out.print(array[i]);
//            System.out.print(" ");
//        }
//        System.out.println("");
        int work = 0;

        Thread[] randThreads = new Thread[NUM_RAND_THREADS];
        Thread[] stageThreads = new Thread[NUM_STAGE_THREADS];
        Thread[] bitonicThreads = new Thread[NUM_BITONIC_THREADS];

        // Would like to create an array of SynchronousQueues but cannot find out how
        SynchronousQueue<double[]> randInputQueue[];
        randInputQueue = new SynchronousQueue[NUM_RAND_THREADS];
//        SynchronousQueue<double[]> inputQueue2 = new SynchronousQueue<double[]>();
//        SynchronousQueue<double[]> inputQueue3 = new SynchronousQueue<double[]>();
//        SynchronousQueue<double[]> inputQueue4 = new SynchronousQueue<double[]>();

        SynchronousQueue<double[]> stageOutputQueue[];
        stageOutputQueue = new SynchronousQueue[NUM_STAGE_THREADS];
//        SynchronousQueue<double[]> outputQueue1 = new SynchronousQueue<double[]>();
//        SynchronousQueue<double[]> outputQueue2 = new SynchronousQueue<double[]>();
//        SynchronousQueue<double[]> outputQueue3 = new SynchronousQueue<double[]>();
//        SynchronousQueue<double[]> outputQueue4 = new SynchronousQueue<double[]>();

        SynchronousQueue<double[]> outputQueues[];
        outputQueues = new SynchronousQueue[NUM_BITONIC_THREADS];
//        SynchronousQueue<double[]> tempOutputQueue1 = new SynchronousQueue<double[]>();
//        SynchronousQueue<double[]> tempOutputQueue2 = new SynchronousQueue<double[]>();

//        SynchronousQueue<double[]> finalOutputQueue = new SynchronousQueue<double[]>();

        // Random Threads''
        for (int i = 0; i < NUM_RAND_THREADS; i++)
            randInputQueue[i] = new SynchronousQueue<double[]>();
        for (int i = 0; i < NUM_RAND_THREADS; i++) {
            randThreads[i] = new Thread(new RandomArrayGenerator(N / NUM_RAND_THREADS, randInputQueue[i]));
            randThreads[i].start();
        }
//        rand1 = new Thread(new RandomArrayGenerator(N / 4, inputQueue1));
//        rand1.start();
//        rand2 = new Thread(new RandomArrayGenerator(N / 4, inputQueue2));
//        rand2.start();
//        rand3 = new Thread(new RandomArrayGenerator(N / 4, inputQueue3));
//        rand3.start();
//        rand4 = new Thread(new RandomArrayGenerator(N / 4, inputQueue4));
//        rand4.start();

        // Stage Threads
        for (int i = 0; i < NUM_STAGE_THREADS; i++)
            stageOutputQueue[i] = new SynchronousQueue<double[]>();
        for (int i = 0; i < NUM_STAGE_THREADS; i++) {
            stageThreads[i] = new Thread(new StageOne(randInputQueue[i], stageOutputQueue[i]));
            stageThreads[i].start();
        }
//        t1 = new Thread(new StageOne(inputQueue1, outputQueue1));
//        t1.start();
//        t2 = new Thread(new StageOne(inputQueue2, outputQueue2));
//        t2.start();
//        t3 = new Thread(new StageOne(inputQueue3, outputQueue3));
//        t3.start();
//        t4 = new Thread(new StageOne(inputQueue4, outputQueue4));
//        t4.start();

        // Bitonic Threads
        for (int i = 0; i < NUM_BITONIC_THREADS; i++)
            outputQueues[i] = new SynchronousQueue<double[]>();
        for (int i = 0; i < NUM_BITONIC_THREADS - 1; i++) {
            bitonicThreads[i] = new Thread(new BitonicStage(stageOutputQueue[i * 2], stageOutputQueue[i * 2 + 1], outputQueues[i]));
            bitonicThreads[i].start();
        }
        // final thread
        bitonicThreads[2] = new Thread(new BitonicStage(outputQueues[0], outputQueues[1], outputQueues[2]));
        bitonicThreads[2].start();
//        t5 = new Thread(new BitonicStage(outputQueue1, outputQueue2, tempOutputQueue1, "First"));
//        t5.start();
//        t6 = new Thread(new BitonicStage(outputQueue3, outputQueue4, tempOutputQueue2, "Second"));
//        t6.start();
//        t7 = new Thread(new BitonicStage(tempOutputQueue1, tempOutputQueue2, finalOutputQueue, "Final"));
//        t7.start();

        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            try {
                array = new double[1];
                array = outputQueues[2].poll(TIME_ALLOWED * 1000, TimeUnit.MILLISECONDS);
                if (!RandomArrayGenerator.isSorted(array) || N != array.length || array == null)
                    System.out.println("failed");
//                System.out.println("");
//                System.out.println("Array");
//                for (int i = 0; i < array.length; i++) {
//                    System.out.print(array[i]);
//                    System.out.print(" ");
//                }
//                System.out.println("");
                work++;
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");

        // stop threads
        for (int i = 0; i < randThreads.length, i++)
            randThreads[i].interupt();
        for (int i = 0; i < stageThreads.length, i++)
            stageThreads[i].interupt();
        for (int i = 0; i < bitonicThreads.length, i++)
            bitonicThreads[i].interupt();
    }
}