package han.com.activity.track.reminder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.view.MotionEvent;
import android.view.View;
import han.com.R;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author han
 */
public class ViewReminderSetter extends View {

    private static Bitmap bar, ball;
    private float scaleValue;
    private Paint shadePaint, centerPaint;
    private List<int[]> areas;//x,y,width,height
    private ProgressReminder stopReminder;
    private Activity activity;
    private boolean scale;
    private InterfaceReminderSwitchCallback reminderSwitchCallback;
    private int totalLights;

    public ViewReminderSetter(Activity activity, ProgressReminder stopReminder, boolean scale, float scaleValue) {
        super(activity);

        this.activity = activity;
        this.stopReminder = stopReminder;

        this.scale = scale;
        this.scaleValue = scaleValue;

        loadImages();
        makePaints();

        if (!scale) {
            this.scaleValue = 1;
        }

        areas = new LinkedList<int[]>();
        areas.add(new int[]{0, 0, 53, 55});
        areas.add(new int[]{(int) (160f * this.scaleValue), 0, 53, 55});
        areas.add(new int[]{(int) (317f * this.scaleValue), 0, 53, 55});
        areas.add(new int[]{(int) (473f * this.scaleValue), 0, 53, 55});
        areas.add(new int[]{(int) (624f * this.scaleValue), 0, 53, 55});

        checkLights();
    }

    private void checkLights() {
        totalLights = 0;

        boolean[] lights = stopReminder.getPressedCheckpoints();
        for (boolean light : lights) {
            if (light) {
                totalLights++;
            }
        }
    }

    private void loadImages() {
        Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        if (bar == null) {
            bar = BitmapFactory.decodeResource(getResources(), R.drawable.img_reminder_bar, options);
            bar = Bitmap.createScaledBitmap(bar, 685, 258, true);
        }

        if (ball == null) {
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.img_reminder_ball, options);
            ball = Bitmap.createScaledBitmap(ball, 52, 51, true);
        }
    }

    private void makePaints() {
        shadePaint = new Paint();
        shadePaint.setAntiAlias(true);
        shadePaint.setStyle(Paint.Style.FILL);
        shadePaint.setColor(Color.BLUE);
        shadePaint.setAlpha(40);

        centerPaint = new Paint();
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(Color.BLACK);
        centerPaint.setStrokeWidth(1);

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (scale) {
            canvas.scale(scaleValue, scaleValue);
        }
        //canvas.drawColor(Color.GRAY);
        canvas.drawBitmap(bar, 0, 0, null);

        if (true) {
            canvas.drawBitmap(ball, 1, 0, null);
        }

        if (stopReminder.getPressedCheckpoints()[1]) {
            canvas.drawBitmap(ball, 160, 0, null);
        }

        if (stopReminder.getPressedCheckpoints()[2]) {
            canvas.drawBitmap(ball, 317, 0, null);
        }

        if (stopReminder.getPressedCheckpoints()[3]) {
            canvas.drawBitmap(ball, 473, 0, null);
        }

        if (true) {
            canvas.drawBitmap(ball, 632, 0, null);//not use
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float fingerX = ev.getX();
        float fingerY = ev.getY();

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            for (int i = 0; i < areas.size(); i++) {
                if (i == 0 || i == 4) {
                    continue;//temp, change later
                }
                int[] area = areas.get(i);
                if (inArea(fingerX, fingerY, area)) {
                    stopReminder.getPressedCheckpoints()[i] = !stopReminder.getPressedCheckpoints()[i];
                    AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

                    if (stopReminder.getPressedCheckpoints()[i]) {
                        totalLights++;
                        if (totalLights == 1) {
                            if (reminderSwitchCallback != null) {
                                reminderSwitchCallback.switchOn();
                            }
                        }
                    } else {
                        totalLights--;
                    }

                    if (totalLights == 0) {
                        if (reminderSwitchCallback != null) {
                            reminderSwitchCallback.switchOff();
                        }
                    }
                }
            }

            invalidate();
        }
        return true;
    }

    private boolean inArea(float x, float y, int[] area) {
        if (x >= area[0] && x <= area[0] + area[2] && y >= area[1] && y <= area[1] + area[3] + 50) {
            return true;
        }
        return false;
    }

    public void setReminderSwitchCallback(InterfaceReminderSwitchCallback reminderSwitchCallback) {
        this.reminderSwitchCallback = reminderSwitchCallback;
    }
}
