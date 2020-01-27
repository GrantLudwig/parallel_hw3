/*
 * Grant Ludwig
 * CPSC 4600, Seattle University
 * BitonicStage.java
 * 1/26/20
 */

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Implements Runnable
 * @class BitonicStage Class that implements bitonic sorting
 */
public class BitonicStage implements Runnable {
    private static final int timeout = 10;  // in seconds

    private double[] array; // used for merging the two arrays
    private SynchronousQueue<double[]> input1, input2, output;
    private String name;

    /**
     * Defualt Constructor
     * Used when there is no threading
     */
    public BitonicStage() {}

    /**
     * Constructor
     * @param input1
     * @param input2
     * @param output
     * @param name
     */
    public BitonicStage(SynchronousQueue<double[]> input1, SynchronousQueue<double[]> input2, SynchronousQueue<double[]> output, String name) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.name = name;
    }

    public BitonicStage(SynchronousQueue<double[]> input1, SynchronousQueue<double[]> input2, SynchronousQueue<double[]> output) {
        this(input1, input2, output, "");
    }

    public void bitonic_merge(int indexStart, int half) {
        double temp;
        for (int i = 0; i < half; i++)
            if (array[indexStart + i] > array[indexStart + half + i]) {
                temp = array[indexStart + i];
                array[indexStart + i] = array[indexStart + half + i];
                array[indexStart + half + i] = temp;
            }
    }

    public void bitonic_sort(int indexStart, int length) {
        if (length > 1) {
            int half = length / 2;
            bitonic_merge(indexStart, half, up);
            bitonic_sort(indexStart, half, up);
            bitonic_sort(indexStart + half, half);
        }
    }

    /**
     *
     * @param upArray Assumes is sorted ascending
     * @param downArray Assumes is sorted ascending
     * @return Ascending array of doubles
     */
    public double[] process(double[] upArray, double[] downArray) {
        // Setup array to return
        array = new double[upArray.length + downArray.length];

        int arrayIndex = 0;
        // Fill up half
        for (int i = 0; i < upArray.length; i++)
            array[arrayIndex++] = upArray[i];

        // Fill down half, need to invert the array
        for (int i = downArray.length - 1; i >= 0; i--)
            array[arrayIndex++] = downArray[i];

        bitonic_sort(0, array.length);

        return array;
    }

    @Override
    public void run() {
        double[] upArray = new double[1];
        double[] downArray = new double[1];
        double[] returnArray = new double[1];
        while (true) {
            try {
                upArray = input1.poll(timeout * 1000, TimeUnit.MILLISECONDS);
                //System.out.println(name + " pulled input1");
                downArray = input2.poll(timeout * 1000, TimeUnit.MILLISECONDS);
                //System.out.println(name + " pulled input2");
                returnArray = process(upArray, downArray);
                //System.out.println(name + " processed array");
//                System.out.println("");
//                System.out.println("Array " + name);
//                for (int i = 0; i < array.length; i++) {
//                    System.out.print(array[i]);
//                    System.out.print(" ");
//                }
//                System.out.println("");
                output.offer(returnArray, timeout * 1000, TimeUnit.MILLISECONDS);
                //System.out.println(name + " Complete *******************");
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}