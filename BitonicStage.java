// Grant Ludwig
// BitonicStage.java
// 1/26/20

public class BitonicStage implements Runnable {
    private double[] array;

    public void bitonic_merge(int indexStart, int half, boolean up) {
        double temp;
        if (up) {
            for (int i = 0; i < half; i++)
                if (array[indexStart + i] > array[indexStart + half + i]) {
                    temp = array[indexStart + i];
                    array[indexStart + i] = array[indexStart + half + i];
                    array[indexStart + half + i] = temp;
                }
        }
        else {
            for (int i = 0; i < half; i++)
                if (array[indexStart + i] < array[indexStart + half + i]) {
                    temp = array[indexStart + i];
                    array[indexStart + i] = array[indexStart + half + i];
                    array[indexStart + half + i] = temp;
                }
        }
    }

    public void bitonic_sort(int indexStart, int length, boolean up) {
        if (length > 1) {
            int half = length / 2;
            bitonic_merge(indexStart, half, up);
            bitonic_sort(indexStart, half, up);
            bitonic_sort(indexStart + half, half, up);
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

        bitonic_sort(0, 0, true);

        return array;
    }

    @Override
    public void run() {
        while (true) {
            // TODO
        }
    }
}