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

        System.out.println("Array Values:");
        System.out.println("Up Array");
        for (int i = 0; i < upArray.length; i++) {
            System.out.print(upArray[i]);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println("Down Array");
        for (int i = 0; i < downArray.length; i++) {
            System.out.print(downArray[i]);
            System.out.print(" ");
        }
        System.out.println();
        System.out.println("Full Array");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            System.out.print(" ");
        }

        return array;
    }

    @Override
    public void run() {
        while (true) {
            // TODO
        }
    }
}