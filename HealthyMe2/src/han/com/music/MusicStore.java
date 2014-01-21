package han.com.music;

import android.util.Log;
import han.com.datapool.MySharedPreferences;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hanaldo
 */
public class MusicStore {

    private static final String className = MusicStore.class.getName();
    private static MusicStore instance;

    public static MusicStore getInstance() {
        if (instance == null) {
            instance = new MusicStore();
        }
        return instance;
    }
    private List<Integer> musicList;
    private int currentMusic;
    private AllSongs allSongs;
    private String currentTag;

    private MusicStore() {
        musicList = new LinkedList<Integer>();
        currentMusic = -1;
        allSongs = new AllSongs();
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(String currentTag) {
        this.currentTag = currentTag;
        Log.d(className, "music current tag: " + currentTag);
    }

    public AllSongs getAllSongs() {
        return allSongs;
    }

    public void clearAll() {
        musicList.clear();
        allSongs.getAllSongsByTag().clear();
        allSongs.getAllTagsInfo().clear();
        currentMusic = -1;
        currentTag = null;
        MySharedPreferences.getInstance(null).removePreferenceItem(MySharedPreferences.MUSIC_SONGS);
    }

    public boolean hasMusic() {
        return !musicList.isEmpty();
    }

    public void shuffleCurrentList() {
        if (currentTag != null) {
            musicList.clear();
            Collections.addAll(musicList, allSongs.getAllSongsByTag().get(currentTag));
        }
        if (musicList != null) {
            Collections.shuffle(musicList);
        }
        currentMusic = 0;
        Log.d(className, "Current play list is ready. " + musicList.size() + " songs");
    }

    public Integer nextSong() {
        if (musicList.isEmpty() || currentMusic < 0) {
            return null;
        }
        currentMusic = (currentMusic + 1) % musicList.size();
        return musicList.get(currentMusic);
    }

    public Integer previousSong() {
        if (musicList.isEmpty() || currentMusic < 0) {
            return null;
        }
        if (currentMusic == 0) {
            currentMusic = musicList.size() - 1;
        } else {
            currentMusic--;
        }
        return musicList.get(currentMusic);
    }

    public void overwritePreference() {
        MySharedPreferences.getInstance(null).setPreferenceItem(
                MySharedPreferences.MUSIC_SONGS,
                allSongs);
    }

    public void putAllSongs(AllSongs all) {
        allSongs = all;
    }
}
