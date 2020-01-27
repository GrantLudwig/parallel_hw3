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
     * @param input1 SynchronousQueue with the up array of the sort, array is assumed to be sorted ascending
     * @param input2 SynchronousQueue with the "down" array of the sort, array is assumed to be sorted ascending
     * @param output SynchronousQueue to output the sorted array
     * @param name of the object, usefull for debugging
     */
    public BitonicStage(SynchronousQueue<double[]> input1, SynchronousQueue<double[]> input2, SynchronousQueue<double[]> output, String name) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.name = name;
    }

    /**
     * Constructor
     * @param input1 SynchronousQueue with the up array of the sort, array is assumed to be sorted ascending
     * @param input2 SynchronousQueue with the "down" array of the sort, array is assumed to be sorted ascending
     * @param output SynchronousQueue to output the sorted array
     */
    public BitonicStage(SynchronousQueue<double[]> input1, SynchronousQueue<double[]> input2, SynchronousQueue<double[]> output) {
        this(input1, input2, output, "");
    }

    /**
     * Performs the bitonic merge portion of the bitonic sort algorthim
     * @param indexStart The starting index of the section of the array to be merged
     * @param half Half point of the array to be merged
     */
    public void bitonic_merge(int indexStart, int half) {
        double temp;
        for (int i = 0; i < half; i++)
            if (array[indexStart + i] > array[indexStart + half + i]) {
                temp = array[indexStart + i];
                array[indexStart + i] = array[indexStart + half + i];
                array[indexStart + half + i] = temp;
            }
    }

    /**
     * Performs the bitonic sort portion of the bitonic sort algorithm
     * @param indexStart The starting index of the section of the array to be sorted
     * @param length of the array to be sorted
     */
    public void bitonic_sort(int indexStart, int length) {
        if (length > 1) {
            int half = length / 2;
            bitonic_merge(indexStart, half);
            bitonic_sort(indexStart, half);
            bitonic_sort(indexStart + half, half);
        }
    }

    /**
     * Processes two asecnding sorted arrays and returns a sorted array of the combined two arrays
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

    /***
     * Run when used in a thread
     */
    @Override
    public void run() {
        double[] upArray = new double[1];
        double[] downArray = new double[1];
        double[] returnArray = new double[1];
        while (true) {
            try {
                upArray = input1.poll(timeout * 1000, TimeUnit.MILLISECONDS);
                downArray = input2.poll(timeout * 1000, TimeUnit.MILLISECONDS);
                returnArray = process(upArray, downArray);
                output.offer(returnArray, timeout * 1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}