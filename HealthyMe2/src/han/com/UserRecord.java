package han.com;

/**
 *
 * @author hanaldo
 */
public class UserRecord {

    private int recordId;
    private String name;
    private float distance;
    private long step;
    private long totalTime;
    private float calories;
    private long lastSync;

    public UserRecord(String name, float distance, long step, long totalTime, float calories) {
        this.name = name;
        this.distance = distance;
        this.step = step;
        this.totalTime = totalTime;
        this.calories = calories;
    }
}
