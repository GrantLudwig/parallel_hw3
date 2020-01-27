/*
 * Grant Ludwig
 * CPSC 4600, Seattle University
 * BitonicPipline.java
 * 1/26/20
 */

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @class BitonicPipeline runner class for testing threaded bitonic sort
 */
public class BitonicPipeline {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    public static final int TIME_ALLOWED = 10;  // seconds
    public static final int NUM_RAND_THREADS = 4; // should be a power of two
    public static final int NUM_STAGE_THREADS = NUM_RAND_THREADS;
    public static final int NUM_BITONIC_THREADS = 3;

    /**
     * @param args not used
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        double[] array;
        int work = 0;

        Thread[] randThreads = new Thread[NUM_RAND_THREADS];
        Thread[] stageThreads = new Thread[NUM_STAGE_THREADS];
        Thread[] bitonicThreads = new Thread[NUM_BITONIC_THREADS];

        // build arrays of SynchronousQueues
        SynchronousQueue<double[]> randInputQueue[];
        randInputQueue = new SynchronousQueue[NUM_RAND_THREADS];

        SynchronousQueue<double[]> stageOutputQueue[];
        stageOutputQueue = new SynchronousQueue[NUM_STAGE_THREADS];

        SynchronousQueue<double[]> outputQueues[];
        outputQueues = new SynchronousQueue[NUM_BITONIC_THREADS];

        // Random Threads
        for (int i = 0; i < NUM_RAND_THREADS; i++)
            randInputQueue[i] = new SynchronousQueue<double[]>();
        for (int i = 0; i < NUM_RAND_THREADS; i++) {
            randThreads[i] = new Thread(new RandomArrayGenerator(N / NUM_RAND_THREADS, randInputQueue[i]));
            randThreads[i].start();
        }

        // Stage Threads
        for (int i = 0; i < NUM_STAGE_THREADS; i++)
            stageOutputQueue[i] = new SynchronousQueue<double[]>();
        for (int i = 0; i < NUM_STAGE_THREADS; i++) {
            stageThreads[i] = new Thread(new StageOne(randInputQueue[i], stageOutputQueue[i]));
            stageThreads[i].start();
        }

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

        // Test
        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            try {
                array = new double[1];
                array = outputQueues[2].poll(TIME_ALLOWED * 1000, TimeUnit.MILLISECONDS);
                if (!RandomArrayGenerator.isSorted(array) || N != array.length || array == null)
                    System.out.println("failed");
                work++;
            } catch (InterruptedException e) {
                return;
            }
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");

        // stop threads
        for (int i = 0; i < randThreads.length; i++)
            randThreads[i].interrupt();
        for (int i = 0; i < stageThreads.length; i++)
            stageThreads[i].interrupt();
        for (int i = 0; i < bitonicThreads.length; i++)
            bitonicThreads[i].interrupt();
    }
}