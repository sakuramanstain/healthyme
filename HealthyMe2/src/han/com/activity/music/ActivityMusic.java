package han.com.activity.music;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;
import han.com.activity.main.fragment.FragmentTrack;
import han.com.datapool.MySharedPreferences;
import han.com.music.AllSongs;
import han.com.music.MusicDoneCallback;
import han.com.music.MusicPlayer;
import han.com.music.MusicStore;
import han.com.sdcard.ExternalStorageManager;
import han.com.tts.Speak;
import han.com.utils.AnimationManager;
import han.com.utils.MyWidgets;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hanaldo
 */
public class ActivityMusic extends Activity {

    private static final String className = ActivityMusic.class.getName();
    private ImageView buttonRefresh, songArt, buttonNext, buttonPlay, buttonLocalFile;
    private List<TextView> tagViews;
    private TextView songNameText;
    private ProgressDialog syncProgressDialog;
    private Handler handlerShowProgress, handlerShowTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        MyWidgets.makeSubActivityTitle(this, "Music", R.drawable.ic_title_music);

        buttonRefresh = (ImageView) findViewById(R.id.title_sub_act_setting1);
        buttonRefresh.setImageResource(R.drawable.ic_setting_refresh);
        buttonRefresh.setVisibility(View.VISIBLE);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new AlertDialog.Builder(ActivityMusic.this)
                        .setTitle("Synchronize New Music")
                        .setMessage("Are you sure you want to synchronize now? This will erase all previous songs, and this may take 15-30 minutes.")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        MusicPlayer.getInstance().stopMusic(buttonPlay);
                        ExternalStorageManager.makeMusicFolder();
                        ExternalStorageManager.cleanMusicFolder();
                        ActivityMusic.this.emptyTagViews();
                        MusicStore.getInstance().clearAll();
                        new AsyncLoadMusicStore(ActivityMusic.this).execute();
                    }
                }).create().show();
            }
        });

        songArt = (ImageView) findViewById(R.id.act_music_song_art);
        songNameText = (TextView) findViewById(R.id.act_music_song_name);

        buttonPlay = (ImageView) findViewById(R.id.act_music_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().stopMusic(buttonPlay);
                } else {
                    if (!checkMusicSystemIsReady()) {
                        return;
                    }

                    MusicPlayer.getInstance().setMusicDoneCallback(new MusicDoneCallback() {
                        public void done() {
                            MusicPlayer.getInstance().playMusic(MusicStore.getInstance().nextSong(), songArt, songNameText, buttonPlay, MySharedPreferences.getInstance(null).getPlayLocalMusic());
                            if (FragmentTrack.getUpdateMusicInfo() != null) {
                                FragmentTrack.getUpdateMusicInfo().sendEmptyMessage(0);
                            }
                        }
                    });
                    MusicPlayer.getInstance().playMusic(MusicStore.getInstance().nextSong(), songArt, songNameText, buttonPlay, MySharedPreferences.getInstance(null).getPlayLocalMusic());
                    buttonPlay.setImageResource(R.drawable.ic_music_stop);
                }
            }
        });

        buttonNext = (ImageView) findViewById(R.id.act_music_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!checkMusicSystemIsReady()) {
                    return;
                }

                MusicPlayer.getInstance().setMusicDoneCallback(new MusicDoneCallback() {
                    public void done() {
                        MusicPlayer.getInstance().playMusic(MusicStore.getInstance().nextSong(), songArt, songNameText, buttonPlay, MySharedPreferences.getInstance(null).getPlayLocalMusic());
                        if (FragmentTrack.getUpdateMusicInfo() != null) {
                            FragmentTrack.getUpdateMusicInfo().sendEmptyMessage(0);
                        }
                    }
                });
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().stopMusic(buttonPlay);
                }
                MusicPlayer.getInstance().playMusic(MusicStore.getInstance().nextSong(), songArt, songNameText, buttonPlay, MySharedPreferences.getInstance(null).getPlayLocalMusic());
            }
        });

        buttonLocalFile = (ImageView) findViewById(R.id.act_music_local_file);
        buttonLocalFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                MusicPlayer.getInstance().stopMusic(buttonPlay);

                if (MySharedPreferences.getInstance(null).getPlayLocalMusic()) {
                    buttonLocalFile.setImageResource(R.drawable.ic_remote);
                    MySharedPreferences.getInstance(null).setPlayLocalMusic(false);
                    Speak.getInstance(null).speak("play music remotely");
                } else {
                    if (!MySharedPreferences.getInstance(null).getMusicFilesAreLocal()) {
                        ExternalStorageManager.makeMusicFolder();
                        ExternalStorageManager.cleanMusicFolder();
                        ActivityMusic.this.emptyTagViews();
                        MusicStore.getInstance().clearAll();
                    }

                    buttonLocalFile.setImageResource(R.drawable.ic_local);
                    MySharedPreferences.getInstance(null).setPlayLocalMusic(true);
                    Speak.getInstance(null).speak("play and save music locally");
                }
            }
        });



        syncProgressDialog = new ProgressDialog(this);
        syncProgressDialog.setMessage("Sync Music");
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        syncProgressDialog.setProgress(0);
        syncProgressDialog.setProgressNumberFormat(null);
        syncProgressDialog.setProgressPercentFormat(null);

        handlerShowProgress = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    buttonRefresh.startAnimation(AnimationManager.makeRotateAnimation());
                    syncProgressDialog.show();
                } else if (msg.what == 1) {
                    syncProgressDialog.dismiss();
                    buttonRefresh.clearAnimation();
                }
            }
        };

        tagViews = new LinkedList<TextView>();
        tagViews.add((TextView) findViewById(R.id.act_music_radio_1));
        tagViews.add((TextView) findViewById(R.id.act_music_radio_2));
        tagViews.add((TextView) findViewById(R.id.act_music_radio_3));
        tagViews.add((TextView) findViewById(R.id.act_music_radio_4));
        tagViews.add((TextView) findViewById(R.id.act_music_radio_5));
        tagViews.add((TextView) findViewById(R.id.act_music_radio_6));


        handlerShowTags = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    @SuppressWarnings("unchecked")
                    List<ListInfo> tags = MusicStore.getInstance().getAllSongs().getAllTagsInfo();

                    int index = 0;
                    for (final TextView tv : tagViews) {
                        if (index >= tags.size()) {
                            tv.setOnClickListener(null);
                            tv.setVisibility(View.INVISIBLE);
                            continue;
                        }

                        final String tagName = tags.get(index).getListName();
                        final int listSize = tags.get(index).getListSize();

                        tv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View arg0) {
                                selectStationText(tv, tagName, listSize);
                            }
                        });
                        tv.setText(tagName + " (" + listSize + ")");
                        tv.setVisibility(View.VISIBLE);

                        if (MusicStore.getInstance().getCurrentTag() != null) {
                            if (tagName.equals(MusicStore.getInstance().getCurrentTag())) {
                                tv.setTextColor(Color.parseColor("#00ffff"));
                            }
                        }

                        index++;
                    }
                } else if (msg.what == 1) {
                    for (TextView tv : tagViews) {
                        tv.setText("");
                        tv.setVisibility(View.INVISIBLE);
                        tv.setOnClickListener(null);
                    }
                }
            }
        };

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(className, "onResume");
        if (MusicPlayer.getInstance().hasSongNow()) {
            if (MySharedPreferences.getInstance(null).getMusicFilesAreLocal()) {
                songNameText.setText(MusicPlayer.getInstance().getCurrentAuthor() + " - " + MusicPlayer.getInstance().getCurrentTitle());
            }
            Bitmap image = MusicPlayer.getInstance().getCurrentImage();
            if (image != null) {
                songArt.setImageBitmap(image);
            } else {
                songArt.setImageResource(R.drawable.music);
            }
        }

        if (MusicStore.getInstance().getAllSongs().getAllTagsInfo().isEmpty()) {
            AllSongs allSongs = (AllSongs) MySharedPreferences.getInstance(null).getPreferenceItem(
                    MySharedPreferences.MUSIC_SONGS,
                    AllSongs.class);
            if (allSongs != null) {
                MusicStore.getInstance().clearAll();
                MusicStore.getInstance().putAllSongs(allSongs);
                Log.d(className, "MusicStore reloaded");
                MusicStore.getInstance().overwritePreference();
            }
        }
        updateTagViews();
        Log.d(className, "getCurrentTag(): " + MusicStore.getInstance().getCurrentTag());

        if (MySharedPreferences.getInstance(null).getPlayLocalMusic()) {
            buttonLocalFile.setImageResource(R.drawable.ic_local);
        } else {
            buttonLocalFile.setImageResource(R.drawable.ic_remote);
        }

        if (MusicPlayer.getInstance().isPlaying()) {
            buttonPlay.setImageResource(R.drawable.ic_music_stop);
        } else {
            buttonPlay.setImageResource(R.drawable.ic_music_play);
        }
    }

    public void selectStationText(TextView text, String tagName, int stationSize) {
        MusicPlayer.getInstance().stopMusic(buttonPlay);
        songArt.setImageResource(R.drawable.music);
        songNameText.setText("");
        for (TextView t : tagViews) {
            if (t.getId() == text.getId()) {
                t.setTextColor(Color.parseColor("#80ff00"));
                MusicStore.getInstance().setCurrentTag(tagName);
                MusicStore.getInstance().shuffleCurrentList();
                Log.d(className, "select station: " + tagName);
                continue;
            }
            t.setTextColor(Color.parseColor("#c4c4c4"));
        }
    }

    public ImageView getSongArt() {
        return songArt;
    }

    public TextView getSongNameText() {
        return songNameText;
    }

    public void showSyncProgressDialog() {
        handlerShowProgress.sendEmptyMessage(0);
    }

    public void dismissSyncProgressDialog() {
        handlerShowProgress.sendEmptyMessage(1);
    }

    public ProgressDialog getSyncProgressDialog() {
        return syncProgressDialog;
    }

    public void updateTagViews() {
        handlerShowTags.sendEmptyMessage(0);
    }

    public void emptyTagViews() {
        handlerShowTags.sendEmptyMessage(1);
    }

    private boolean checkMusicSystemIsReady() {
        if (!MySharedPreferences.getInstance(null).getMusicFilesAreLocal()
                && MySharedPreferences.getInstance(null).getPlayLocalMusic()) {
            String s = "Music files are not made local yet, please re-synchronize music to local!";
            Toast.makeText(ActivityMusic.this, s, Toast.LENGTH_LONG).show();
            Speak.getInstance(null).speak(s);
            return false;
        }

        if (MusicStore.getInstance().getCurrentTag() == null) {
            String s = "Please choose your playlist first!";
            Toast.makeText(ActivityMusic.this, s, Toast.LENGTH_SHORT).show();
            Speak.getInstance(null).speak(s);
            return false;
        }
        return true;
    }
}
