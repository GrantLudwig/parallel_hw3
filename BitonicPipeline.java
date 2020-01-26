// Grant Ludwig
// BitonicPipline.java
// 1/26/20

import java.util.concurrent.SynchronousQueue;

public class BitonicPipeline {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    //public static final int N = 16;
    public static final int TIME_ALLOWED = 10;  // seconds
    //public static final int TIME_ALLOWED = 1;
    SynchronousQueue<double[]>[] inputQueue = new SynchronousQueue<double[]>[4];
    SynchronousQueue<double[]>[] outputQueue = new SynchronousQueue<double[]>[4];
    SynchronousQueue<double[]>[] tempOutputQueues = new SynchronousQueue<double[]>[2];
    SynchronousQueue<double[]> finalOutputQueue = new SynchronousQueue<double[]>();

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int work = 0;
        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            // get random numbers
            for (int i = 0; i < inputQueue.length; i++) {
                RandomArrayGenerator randNum = new RandomArrayGenerator(N / 4, inputQueue[i]);
                randNum.start();
            }
            for (int i = 0; i < inputQueue.length; i++) {
                StageOne stage = new StageOne(inputQueue[i], outputQueue[i]);
                stage.start();
            }

            BitonicStage bitonic1 = new BitonicStage(outputQueue[0], outputQueue[1], tempOutputQueues[0]);
            bitonic1.start();

            BitonicStage bitonic2 = new BitonicStage(outputQueue[2], outputQueue[3], tempOutputQueues[1]);
            bitonic2.start();

            BitonicStage bitonicFinal = new BitonicStage(tempOutputQueues[0], tempOutputQueues[1], finalOutputQueue);
            bitonicFinal.start();

            double[] array = new double[N];
            finalOutputQueue.offer(array, timeout * 1000, TimeUnit.MILLISECONDS);

            if (!RandomArrayGenerator.isSorted(array) || N != array.length)
                System.out.println("failed");
            work++;
        }
        System.out.println("sorted " + work + " arrays (each: " + N + " doubles) in "
                + TIME_ALLOWED + " seconds");
    }
}