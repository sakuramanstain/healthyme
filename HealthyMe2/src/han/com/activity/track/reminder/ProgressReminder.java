package han.com.activity.track.reminder;

import han.com.datapool.PreferenceItem;

/**
 *
 * @author hanaldo
 */
public class ProgressReminder implements PreferenceItem {

    private boolean[] pressedCheckpoints;
    private boolean turnedOn;

    public ProgressReminder() {
        pressedCheckpoints = new boolean[]{false, false, false, false, false};
        turnedOn = false;
    }

    public boolean[] getPressedCheckpoints() {
        return pressedCheckpoints;
    }

    public void setPressedCheckpoints(boolean[] pressedCheckpoints) {
        this.pressedCheckpoints = pressedCheckpoints;
    }

    public boolean isTurnedOn() {
        return turnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        this.turnedOn = turnedOn;
    }
}
