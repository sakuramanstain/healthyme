package han.com.utils;

import java.text.DecimalFormat;

/**
 *
 * @author han
 */
public class Values {

    private static DecimalFormat CaloriesFormat = new DecimalFormat("####.#");
    private static DecimalFormat DistanceFormat = new DecimalFormat("####.##");
    public static float DisplayDensity;
    public static final int REQUEST_CODE = 20;
    public static final int RESULT_CODE_FINISH = 10;
    public static final float METER_FOR_FEET = 3.28084f;
    public static final float MILE_FOR_FEET = 5280f;
    public static final String MetricMode = "mile";

    public static String formatCalories(float value) {
        return CaloriesFormat.format(value);
    }

    public static String formatDistance(float value) {
        return DistanceFormat.format(value);
    }

    public static float getDistanceValue(int value1, int value2, int value3) {
        float dis = new Float(value1) * 10 + value2 + new Float(value3) / 10;
        return dis;
    }

    public static int getStepValue(int value1, int value2, int value3, int value4) {
        int steps = value1 * 1000 + value2 * 100 + value3 * 10 + value4;
        return steps;
    }

    public static int getCaloriesValue(int value1, int value2, int value3) {
        int steps = value1 * 100 + value2 * 10 + value3;
        return steps;
    }

    private Values() {
    }
}
