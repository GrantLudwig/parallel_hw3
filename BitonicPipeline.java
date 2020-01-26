// Grant Ludwig
// BitonicPipline.java
// 1/26/20

import java.util.concurrent.SynchronousQueue;

public class BitonicPipeline {
    public static final int N = 1 << 22;  // size of the final sorted array (power of two)
    //public static final int N = 16;
    public static final int TIME_ALLOWED = 10;  // seconds
    //public static final int TIME_ALLOWED = 1;

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

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int work = 0;
        while (System.currentTimeMillis() < start + TIME_ALLOWED * 1000) {
            // get random numbers
            for (int i = 0; i < 4; i++) {
                RandomArrayGenerator randNum;
                if (i == 0)
                    randNum = new RandomArrayGenerator(N / 4, inputQueue1);
                else if (i == 1)
                    randNum = new RandomArrayGenerator(N / 4, inputQueue2);
                else if (i == 2)
                    randNum = new RandomArrayGenerator(N / 4, inputQueue3);
                else
                    randNum = new RandomArrayGenerator(N / 4, inputQueue4);
                randNum.start();
            }
            for (int i = 0; i < 4; i++) {
                StageOne stage;
                if (i == 0)
                    stage = new StageOne(inputQueue1, outputQueue1);
                else if (i == 1)
                    stage = new StageOne(inputQueue2, outputQueue2);
                else if (i == 2)
                    stage = new StageOne(inputQueue3, outputQueue3);
                else
                    stage = new StageOne(inputQueue4, outputQueue4);
                stage.start();
            }

            BitonicStage bitonic1 = new BitonicStage(outputQueue1, outputQueue2, tempOutputQueues1);
            bitonic1.start();

            BitonicStage bitonic2 = new BitonicStage(outputQueue3, outputQueue4, tempOutputQueues2);
            bitonic2.start();

            BitonicStage bitonicFinal = new BitonicStage(tempOutputQueues1, tempOutputQueues2, finalOutputQueue);
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