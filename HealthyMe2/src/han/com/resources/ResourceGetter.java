package han.com.resources;

import android.content.Context;
import han.com.R;

public class ResourceGetter {

    private static ResourceGetter instance;

    public static ResourceGetter getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceGetter(context);
        }
        return instance;
    }
    private final String serviceAddressAllTags;
    private final String serviceAddressTagsSong;
    private final String serviceAddressSongFile;
    private final String serviceAddressRegisterDevice;
    private final String serviceAddressSyncRecord;
    private final String serviceAddressAddPost;
    private final String serviceAddressPostImage;
    private final String serviceAddressGetPosts;
    private final String serviceAddressGetImages;
    private final String serviceAddressBase;
    private final String serviceAddressGetReply;
    private final String serviceAddressAddReply;

    private ResourceGetter(Context context) {
        String serverIP = context.getResources().getString(R.string.server_ip);
        serviceAddressAllTags = "http://" + serverIP + ":8080/HealthyMeGCM/getAllTags?mode=json";
        serviceAddressTagsSong = "http://" + serverIP + ":8080/HealthyMeGCM/getSongsByTag";
        serviceAddressSongFile = "http://" + serverIP + ":8080/HealthyMeGCM/getFile";
        serviceAddressRegisterDevice = "http://" + serverIP + ":8080/HealthyMeGCM/regDevice";
        serviceAddressSyncRecord = "http://" + serverIP + ":8080/HealthyMeGCM/syncUserRecord";
        serviceAddressAddPost = "http://" + serverIP + ":8080/HealthyMeGCM/addPost";
        serviceAddressPostImage = "http://" + serverIP + ":8080/HealthyMeGCM/uploadPostImage";
        serviceAddressGetPosts = "http://" + serverIP + ":8080/HealthyMeGCM/getPosts";
        serviceAddressGetImages = "http://" + serverIP + ":8080/HealthyMeGCM/getImages";
        serviceAddressBase = "http://" + serverIP + ":8080/HealthyMeGCM/";
        serviceAddressGetReply = "http://" + serverIP + ":8080/HealthyMeGCM/getReply";
        serviceAddressAddReply = "http://" + serverIP + ":8080/HealthyMeGCM/replyPost";
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

    public String getServiceAddressAddPost() {
        return serviceAddressAddPost;
    }

    public String getServiceAddressPostImage() {
        return serviceAddressPostImage;
    }

    public String getServiceAddressGetPosts() {
        return serviceAddressGetPosts;
    }

    public String getServiceAddressGetImages() {
        return serviceAddressGetImages;
    }

    public String getServiceAddressBase() {
        return serviceAddressBase;
    }

    public String getServiceAddressGetReply() {
        return serviceAddressGetReply;
    }

    public String getServiceAddressAddReply() {
        return serviceAddressAddReply;
    }
}
