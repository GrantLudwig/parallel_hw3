// Grant Ludwig
// BitonicPipline.java
// 1/26/20

import java.util.concurrent.SynchronousQueue;

public class BitonicPipeline {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    //public static final int N = 16;
    public static final int TIME_ALLOWED = 10;  // seconds
    //public static final int TIME_ALLOWED = 1;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Thread  rand1,
                rand2,
                rand3,
                rand4,
                t1,
                t2,
                t3,
                t4,
                t5,
                t6,
                t7;

        // Would like to create an array of SynchronousQueues but cannot find out how
        SynchronousQueue<double[]> inputQueue1 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> inputQueue2 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> inputQueue3 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> inputQueue4 = new SynchronousQueue<double[]>();

        SynchronousQueue<double[]> outputQueue1 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> outputQueue2 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> outputQueue3 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> outputQueue4 = new SynchronousQueue<double[]>();

        SynchronousQueue<double[]> tempOutputQueue1 = new SynchronousQueue<double[]>();
        SynchronousQueue<double[]> tempOutputQueue2 = new SynchronousQueue<double[]>();

        SynchronousQueue<double[]> finalOutputQueue = new SynchronousQueue<double[]>();

        // Setup all threads
        // get random numbers
        rand1 = new Thread(new RandomArrayGenerator(N / 4, inputQueue1));
        rand1.start();
        rand2 = new Thread(new RandomArrayGenerator(N / 4, inputQueue2));
        rand2.start();
        rand3 = new Thread(new RandomArrayGenerator(N / 4, inputQueue3));
        rand3.start();
        rand4 = new Thread(new RandomArrayGenerator(N / 4, inputQueue4));
        rand4.start();

        t1 = new Thread(new StageOne(inputQueue1, outputQueue1));
        t1.start();
        t2 = new Thread(new StageOne(inputQueue2, outputQueue2));
        t2.start();
        t3 = new Thread(new StageOne(inputQueue3, outputQueue3));
        t3.start();
        t4 = new Thread(new StageOne(inputQueue4, outputQueue4));
        t4.start();

        t5 = new Thread(new BitonicStage(outputQueue1, outputQueue2, tempOutputQueues1));
        t5.start();
        t6 = new Thread(new BitonicStage(outputQueue3, outputQueue4, tempOutputQueues2));
        t6.start();
        t7 = new Thread(new BitonicStage(tempOutputQueues1, tempOutputQueues2, finalOutputQueue));
        t7.start();

        double[] array = new double[N];
        int work = 0;
        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            finalOutputQueue.offer(array, timeout * 1000, TimeUnit.MILLISECONDS);

            if (!RandomArrayGenerator.isSorted(array) || N != array.length)
                System.out.println("failed");
            work++;
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}