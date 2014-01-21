package han.com.datapool;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import han.com.R;
import han.com.db.DatabaseHandler;
import han.com.db.GoalRecord;
import han.com.db.UserGoal;
import han.com.utils.Values;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 *
 * @author han
 */
public class CurrentGoal {

    private static final String className = CurrentGoal.class.getName();
    public static final Object[][] WorkoutNames = new Object[][]{
        new Object[]{"Running", R.drawable.ic_sport_run, R.drawable.ic_sport_run_black},
        new Object[]{"Walking", R.drawable.ic_sport_walk, R.drawable.ic_sport_walk_black},
        new Object[]{"Cycling", R.drawable.ic_sport_cycling, R.drawable.ic_sport_cycling_black},
        new Object[]{"Hiking", R.drawable.ic_sport_walk, R.drawable.ic_sport_walk_black},
        new Object[]{"Yoga", R.drawable.ic_sport_yoga, R.drawable.ic_sport_yoga_black},
        new Object[]{"Tennis", R.drawable.ic_sport_tennis, R.drawable.ic_sport_tennis_black},
        new Object[]{"Volleyball", R.drawable.ic_sport_volleyball, R.drawable.ic_sport_volleyball_black}
    };
    public static final Object[][] IntenseNames = new Object[][]{
        new Object[]{"High", 5, R.drawable.ic_action_track},
        new Object[]{"Mid", 3, R.drawable.ic_action_track},
        new Object[]{"Low", 1, R.drawable.ic_action_track}
    };
    private static UserGoal GoalData;
    private static GoalRecord GoalRecord;
    private static boolean TrackIsStarted = false;
    private static float TargetDistanceMetersOrFeet;
    private static long TargetTimemillisecond;
    private static int TargetSteps;
    private static float TargetCalories;
    private static float ProgressOutOfOneHundred;
    private static float CurrentDistance;
    private static String CurrentDistanceString;
    public static boolean[] CheckPointsReported = new boolean[5];
    private static long CurrentTotalTime;
    private static String CurrentTotalTimeString;
    private static int CurrentTotalSteps;
    private static float CurrentTotalCalories;
    private static String CurrentTotalCaloriesString;

    public static void clearCurrentTrackingInfo() {
        GoalRecord = null;
        TargetDistanceMetersOrFeet = 0;
        TargetTimemillisecond = 0;
        ProgressOutOfOneHundred = 0;
        CurrentDistance = 0;
        CheckPointsReported = new boolean[5];
        CurrentTotalTime = 0;
        CurrentTotalSteps = 0;
        CurrentTotalCalories = 0;
        CurrentDistanceString = "0";
        CurrentTotalTimeString = "00:00:00";
        CurrentTotalCaloriesString = "0";
    }

    public static UserGoal getGoalData() {
        return GoalData;
    }

    public static void setGoalData(UserGoal GoalData) {
        CurrentGoal.GoalData = GoalData;
    }

    public static GoalRecord getGoalRecord() {
        return GoalRecord;
    }

    public static void setGoalRecord(GoalRecord GoalRecord) {
        CurrentGoal.GoalRecord = GoalRecord;
    }

    public static void setTargetDistanceMetersOrFeet(float TargetDistanceMetersOrFeet) {
        CurrentGoal.TargetDistanceMetersOrFeet = TargetDistanceMetersOrFeet;
    }

    public static void setTargetTimemillisecond(long TargetTimemillisecond) {
        CurrentGoal.TargetTimemillisecond = TargetTimemillisecond;
    }

    public static void setTargetSteps(int targetSteps) {
        CurrentGoal.TargetSteps = targetSteps;
    }

    public static void setTargetCalories(float TargetCalories) {
        CurrentGoal.TargetCalories = TargetCalories;
    }

    public static String getCurrentDistanceString() {
        return CurrentDistanceString;
    }

    public static String getCurrentTotalTimeString() {
        return CurrentTotalTimeString;
    }

    public static String getCurrentTotalCaloriesString() {
        return CurrentTotalCaloriesString;
    }

    public static void updateCurrentDistanceProgress(float currentDistance, Handler updateProgressDisplayHandler) throws Exception {
        if (GoalRecord == null) {
            return;
        }

        CurrentDistance = currentDistance;
        CurrentDistanceString = Values.formatDistance(CurrentDistance / 1000f);
        if (Values.MetricMode.equals("mile")) {
            CurrentDistanceString = Values.formatDistance(CurrentDistance / Values.MILE_FOR_FEET);
        }

        if (GoalData.getGoalType().equals(UserGoal.GOAL_TYPE_DISTANCE)) {
            if (TargetDistanceMetersOrFeet <= 0) {
                throw new Exception("Current TargetDistanceMeters is not valid!");
            }
            ProgressOutOfOneHundred = CurrentDistance / TargetDistanceMetersOrFeet * 100;

            Message msg = Message.obtain();
            msg.obj = new Object[]{ProgressOutOfOneHundred, CurrentDistanceString, GoalData.getGoalUnit()};
            updateProgressDisplayHandler.sendMessage(msg);
        }
    }

    public static void updateCurrentTimeProgress(long currentTime, Handler updateProgressDisplayHandler) throws Exception {
        if (GoalRecord == null) {
            return;
        }

        CurrentTotalTime = currentTime;
        CurrentTotalTimeString = DurationFormatUtils.formatDuration(CurrentTotalTime, "HH:mm:ss");

        if (GoalData.getGoalType().equals(UserGoal.GOAL_TYPE_TIME)) {
            if (TargetTimemillisecond <= 0) {
                throw new Exception("Current TargetTimeSeconds is not valid!");
            }
            ProgressOutOfOneHundred = (float) CurrentTotalTime / (float) TargetTimemillisecond * 100;

            Message msg = Message.obtain();
            msg.obj = new Object[]{ProgressOutOfOneHundred, CurrentTotalTimeString, ""};
            updateProgressDisplayHandler.sendMessage(msg);
        }
    }

    public static void updateCurrentStepProgress(int currentStep, float currentTotalCalories, Handler updateProgressDisplayHandler) throws Exception {
        if (GoalRecord == null) {
            return;
        }
        CurrentTotalSteps = currentStep;
        CurrentTotalCalories = currentTotalCalories;
        CurrentTotalCaloriesString = Values.formatCalories(CurrentTotalCalories);

        if (GoalData.getGoalType().equals(UserGoal.GOAL_TYPE_STEP)) {
            if (TargetSteps <= 0) {
                throw new Exception("Current TargetSteps is not valid!");
            }
            ProgressOutOfOneHundred = (float) CurrentTotalSteps / (float) TargetSteps * 100;

            Message msg = Message.obtain();
            msg.obj = new Object[]{ProgressOutOfOneHundred, String.valueOf(CurrentTotalSteps), "steps"};
            updateProgressDisplayHandler.sendMessage(msg);

        } else if (GoalData.getGoalType().equals(UserGoal.GOAL_TYPE_CALORIES)) {
            if (TargetCalories <= 0) {
                throw new Exception("Current TargetCalories is not valid!");
            }
            ProgressOutOfOneHundred = CurrentTotalCalories / TargetCalories * 100;

            Message msg = Message.obtain();
            msg.obj = new Object[]{ProgressOutOfOneHundred, CurrentTotalCaloriesString, "cal"};
            updateProgressDisplayHandler.sendMessage(msg);
        }
    }

    public static float getProgressOutOfOneHundred() {
        return ProgressOutOfOneHundred;
    }

    public static float getCurrentDistance() {
        return CurrentDistance;
    }

    public static int getCurrentTotalSteps() {
        return CurrentTotalSteps;
    }

    public static float getCurrentTotalCalories() {
        return CurrentTotalCalories;
    }

    public static long getCurrentTotalTime() {
        return CurrentTotalTime;
    }

    public static void log() {
        Log.d(className, "GoalData: " + GoalData);
        Log.d(className, "GoalRecord: " + GoalRecord);
        Log.d(className, "TargetDistanceMetersOrFeet: " + TargetDistanceMetersOrFeet);
        Log.d(className, "TargetTimeSeconds: " + TargetTimemillisecond);
        Log.d(className, "ProgressOutOfOneHundred: " + ProgressOutOfOneHundred);
        Log.d(className, "CurrentDistance: " + CurrentDistance);
        Log.d(className, "CurrentTotalTime: " + CurrentTotalTime);
        Log.d(className, "CurrentTotalCalories: " + CurrentTotalCalories);
    }

    public static synchronized boolean isTrackIsStarted() {
        return TrackIsStarted;
    }

    public static synchronized void setTrackIsStarted(boolean trackIsStarted) {
        TrackIsStarted = trackIsStarted;
    }

    public static UserGoal makeGoal(float disValue, int valid, String goalName, int goalOrder, String goalUnit, int intenseValue, String goalType) {
        UserGoal goal = new UserGoal();

        goal.setGoalName(goalName);
        if (valid == UserGoal.GOAL_IS_VALID) {
            goal.setGoalOrder(goalOrder);
        } else {
            goal.setGoalOrder(-1);
        }
        goal.setGoalUnit(goalUnit);

        goal.setGoalIntense(intenseValue);

        goal.setGoalType(goalType);
        goal.setGoalValue(disValue);
        goal.setValid(valid);
        long goalId = UserGoal.addNewUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), goal);
        goal.setGoalId(goalId);

        return goal;
    }

    private CurrentGoal() {
    }
}
