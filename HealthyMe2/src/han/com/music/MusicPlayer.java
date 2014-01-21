package han.com.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import han.com.R;
import han.com.datapool.MySharedPreferences;
import han.com.resources.ResourceGetter;
import han.com.sdcard.ExternalStorageManager;
import java.io.IOException;

/**
 *
 * @author han
 */
public class MusicPlayer {
    
    private static final String className = MusicPlayer.class.getName();
    private static MusicPlayer instance;
    
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }
    private MediaPlayer mediaPlayer;
    private MusicDoneCallback musicDoneCallback;
    private boolean hasSongNow;
    private Bitmap currentImage;
    private String currentAuthor;
    private String currentTitle;
    
    private MusicPlayer() {
        hasSongNow = false;
    }
    
    public void setMusicDoneCallback(MusicDoneCallback musicDoneCallback) {
        this.musicDoneCallback = musicDoneCallback;
    }
    
    public boolean hasSongNow() {
        return hasSongNow;
    }
    
    public boolean isPlaying() {
        if (mediaPlayer == null) {
            return false;
        }
        return mediaPlayer.isPlaying();
    }
    
    public void playMusic(int songId, ImageView songArt, TextView musicInfoView, View playButton, boolean local) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                if (local || MySharedPreferences.getInstance(null).getMusicFilesAreLocal()) {
                    String songPath = ExternalStorageManager.musicFolder + songId + ".mp3";
                    mediaPlayer.setDataSource(songPath);
                    Log.d(className, "prepare song at: " + songPath);
                    
                    try {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(songPath);
                        currentAuthor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        currentTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        
                        byte[] imageBytes = mmr.getEmbeddedPicture();
                        Log.d(className, "song image: " + imageBytes.length);
                        currentImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        if (songArt != null) {
                            if (currentImage != null) {
                                songArt.setImageBitmap(currentImage);
                            } else {
                                songArt.setImageResource(R.drawable.music);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(className, "no meta data: " + e.toString());
                    }
                } else {
                    String songPath = ResourceGetter.getInstance(null).getServiceAddressSongFile() + "/" + songId + ".mp3";
                    
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(songPath);
                    Log.d(className, "prepare song at: " + songPath);
                }
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer arg0) {
                        stopMusic(null);
                        if (musicDoneCallback != null) {
                            Log.d(className, "execute musicDoneCallback");
                            musicDoneCallback.done();
                        }
                    }
                });
                Log.d(className, "we have musicDoneCallback: " + musicDoneCallback);
                
                if (musicInfoView != null) {
                    if (local || MySharedPreferences.getInstance(null).getMusicFilesAreLocal()) {
                        musicInfoView.setText(currentAuthor + " - " + currentTitle);
                    }
                }
                
                Log.d(className, "play " + songId);
                mediaPlayer.start();
                if (playButton != null) {
                    ImageView iv = (ImageView) playButton;
                    iv.setImageResource(R.drawable.ic_music_stop);
                }
            } else {
                mediaPlayer.start();
            }
            hasSongNow = true;
        } catch (IOException e) {
            Log.wtf(className, e);
        }
    }
    
    public void stopMusic(View v) {
        clean();
        hasSongNow = false;
        currentImage = null;
        if (v != null) {
            ImageView iv = (ImageView) v;
            iv.setImageResource(R.drawable.ic_music_play);
        }
    }
    
    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }
    
    private void clean() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(className, "clean MusicPlayer");
        }
    }
    
    public Bitmap getCurrentImage() {
        return currentImage;
    }
    
    public String getCurrentAuthor() {
        return currentAuthor;
    }
    
    public String getCurrentTitle() {
        return currentTitle;
    }
}
