package han.com.step;

import han.com.pedometer.StepDetector;

/**
 *
 * @author hanaldo
 */
public class SingletonSetbackSensitivity {

    private static SingletonSetbackSensitivity instance;

    public static SingletonSetbackSensitivity getInstance() {
        if (instance == null) {
            instance = new SingletonSetbackSensitivity();
        }
        return instance;
    }
    private StepDetector detector;

    private SingletonSetbackSensitivity() {
    }

    public void setDetector(StepDetector detector) {
        this.detector = detector;
    }

    public void setSensitivity(float s) {
        detector.setSensitivity(s);
    }
}
