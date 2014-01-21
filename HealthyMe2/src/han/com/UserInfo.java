package han.com;

/**
 *
 * @author hanaldo
 * @version 0.1
 */
public class UserInfo {

    private String userName;
    private String deviceToken;
    private String pedometerSetting;

    public UserInfo(String userName, String deviceToken, String pedometerSetting) {
        this.userName = userName;
        this.deviceToken = deviceToken;
        this.pedometerSetting = pedometerSetting;
    }
}
