package han.com.music;

import han.com.activity.music.ListInfo;
import han.com.datapool.PreferenceItem;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hanaldo
 */
public class AllSongs implements PreferenceItem {

    private Map<String, Integer[]> allSongsByTag;
    private List<ListInfo> allTagsInfo;

    public AllSongs() {
        allSongsByTag = new HashMap<String, Integer[]>(5);
        allTagsInfo = new LinkedList<ListInfo>();
    }

    public AllSongs(Map<String, Integer[]> allSongsByTag, LinkedList<ListInfo> allTagsInfo) {
        this.allSongsByTag = allSongsByTag;
        this.allTagsInfo = allTagsInfo;
    }

    public Map<String, Integer[]> getAllSongsByTag() {
        return allSongsByTag;
    }

    public void setAllSongsByTag(Map<String, Integer[]> allSongsByTag) {
        this.allSongsByTag = allSongsByTag;
    }

    public List<ListInfo> getAllTagsInfo() {
        return allTagsInfo;
    }

    public void setAllTagsInfo(List<ListInfo> allTagsInfo) {
        this.allTagsInfo = allTagsInfo;
    }
}
