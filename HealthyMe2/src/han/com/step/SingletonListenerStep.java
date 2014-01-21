package han.com.step;

import android.util.Log;
import han.com.datapool.CurrentGoal;
import han.com.pedometer.PedometerSettings;
import han.com.pedometer.StepListener;
import han.com.tts.Speak;
import han.com.utils.Values;

/**
 *
 * @author han
 */
public class SingletonListenerStep implements StepListener {

    private static String className = SingletonListenerStep.class.getName();
    private static SingletonListenerStep instance;

    public static SingletonListenerStep getInstance() {
        if (instance == null) {
            instance = new SingletonListenerStep();
        }
        return instance;
    }
    private int totalSteps;//raw steps
    private float progressDistance;
    private PedometerSettings pedometerSettings;
    private InterfaceReportStep reportStep;
    private float lastCalories;
    private float totalCalories;
    private boolean speakStep;

    private SingletonListenerStep() {
    }

    public void clearRecord() {
        totalSteps = 0;
        progressDistance = 0;
        totalCalories = 0;
    }

    public synchronized int getTotalSteps() {
        return totalSteps;
    }

    private synchronized void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public float getProgressDistance() {
        return progressDistance;
    }

    public void setProgressDistance(float progressDistance) {
        this.progressDistance = progressDistance;
    }

    public PedometerSettings getPedometerSettings() {
        return pedometerSettings;
    }

    public void setPedometerSettings(PedometerSettings pedometerSettings) {
        this.pedometerSettings = pedometerSettings;
    }

    public void setReportStep(InterfaceReportStep reportStep) {
        this.reportStep = reportStep;
    }

    public synchronized void updateCalories(float newCalories) {

        float increment = newCalories - lastCalories;
        totalCalories += increment;
        lastCalories = newCalories;
    }

    public synchronized float getTotalCalories() {
        return totalCalories;
    }

    public synchronized void resetLastCalories() {
        lastCalories = 0;
    }

    public boolean isSpeakStep() {
        return speakStep;
    }

    public void switchSpeakStep() {
        speakStep = !speakStep;
    }

    public void onStep() {
        if (!CurrentGoal.isTrackIsStarted()) {
            return;
        }
        setTotalSteps(getTotalSteps() + 1);

        if (pedometerSettings != null) {
            float stepLength = pedometerSettings.getStepLength() / 100;
            Log.d(className, "step length (meters): " + stepLength);

            if (Values.MetricMode.equals("mile")) {
                stepLength *= Values.METER_FOR_FEET;
            }

            progressDistance += stepLength;
            Log.d(className, "step total distance: " + progressDistance + " " + Values.MetricMode);
        }

        if (reportStep != null) {
            reportStep.reportStep(getTotalSteps(), progressDistance);
        }

        if (speakStep) {
            Speak.getInstance(null).speak("step");
        }
    }

    public void passValue() {
    }
}
