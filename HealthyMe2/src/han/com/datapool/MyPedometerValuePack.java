package han.com.datapool;

/**
 *
 * @author hanaldo
 * @version 0.1
 */
public class MyPedometerValuePack {

    private String sensitivity;
    private String bodyWeight;
    private String stepLength;
    private String exerciseType;

    public MyPedometerValuePack() {
    }

    public MyPedometerValuePack(String sensitivity, String bodyWeight, String stepLength, String exerciseType) {
        this.sensitivity = sensitivity;
        this.bodyWeight = bodyWeight;
        this.stepLength = stepLength;
        this.exerciseType = exerciseType;
    }

    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(String bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public String getStepLength() {
        return stepLength;
    }

    public void setStepLength(String stepLength) {
        this.stepLength = stepLength;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
}
