package han.com.resources;

import android.content.Context;
import han.com.R;

/**
 *
 * @author hanaldo
 */
public class ResourceGetter {

    private static ResourceGetter instance;

    public static ResourceGetter getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceGetter(context);
        }
        return instance;
    }
    private String serviceAddressAllTags;
    private String serviceAddressTagsSong;
    private String serviceAddressSongFile;
    private String serviceAddressRegisterDevice;
    private String serviceAddressSyncRecord;

    private ResourceGetter(Context context) {
        String serverIP = context.getResources().getString(R.string.server_ip);
        serviceAddressAllTags = "http://" + serverIP + ":8080/HealthyMeGCM/getAllTags?mode=json";
        serviceAddressTagsSong = "http://" + serverIP + ":8080/HealthyMeGCM/getSongsByTag";
        serviceAddressSongFile = "http://" + serverIP + ":8080/HealthyMeGCM/getFile";
        serviceAddressRegisterDevice = "http://" + serverIP + ":8080/HealthyMeGCM/regDevice";
        serviceAddressSyncRecord = "http://" + serverIP + ":8080/HealthyMeGCM/syncUserRecord";
    }

    public String getServiceAddressAllTags() {
        return serviceAddressAllTags;
    }

    public String getServiceAddressTagsSong() {
        return serviceAddressTagsSong;
    }

    public String getServiceAddressSongFile() {
        return serviceAddressSongFile;
    }

    public String getServiceAddressRegisterDevice() {
        return serviceAddressRegisterDevice;
    }

    public String getServiceAddressSyncRecord() {
        return serviceAddressSyncRecord;
    }
}
