package han.com.activity.track.goals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import han.com.R;

/**
 *
 * @author han
 */
public class ViewClockSetter extends View {

    private static final String className = ViewClockSetter.class.getName();
    private static Bitmap hand, clock;
    private static RectF shade;
    private float viewWidth, viewHeight, bearing, lastBearing, shadeOffset, centerX, centerY, scaleValue;
    private Paint shadePaint, centerPaint;
    private InterfaceClockSetterValue valueListener;
    private int loop;
    private boolean scale;

    public ViewClockSetter(Context context, int viewWidth, int viewHeight, float shadeOffset, boolean scale, float scaleValue) {
        super(context);

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.shadeOffset = shadeOffset;
        centerX = viewWidth / 2;
        centerY = viewHeight / 2;

        this.scale = scale;
        this.scaleValue = scaleValue;

        loadImages();
        makePaints();
    }

    private void loadImages() {
        Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;


        if (hand == null) {
            hand = BitmapFactory.decodeResource(getResources(), R.drawable.img_hand_min, options);
            hand = Bitmap.createScaledBitmap(hand, 190, 270, true);
        }

        if (clock == null) {
            clock = BitmapFactory.decodeResource(getResources(), R.drawable.img_clock, options);
            clock = Bitmap.createScaledBitmap(clock, (int) viewWidth, (int) viewHeight, true);
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

        if (shade == null) {
            shade = new RectF(shadeOffset, shadeOffset, viewWidth - shadeOffset, viewHeight - shadeOffset);
        }
    }

    public void setValueListener(InterfaceClockSetterValue valueListener) {
        this.valueListener = valueListener;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (scale) {
            canvas.scale(scaleValue, scaleValue);
        }

        canvas.drawBitmap(clock, 0, 0, null);

        for (int i = 0; i < loop; i++) {
            canvas.drawCircle(centerX, centerY, viewWidth / 2 - shadeOffset, shadePaint);
        }

        canvas.drawArc(shade, 270, bearing, true, shadePaint);

        canvas.save();

        //rotate hand
        canvas.rotate(bearing, centerX, centerY);

        canvas.drawBitmap(hand, 204, 113, null);
        //canvas.drawPoint(centerX, centerY, centerPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float fingerX = ev.getX();
        float fingerY = ev.getY();

        float dx = centerX - fingerX;
        float dy = centerY - fingerY;

        if (scale) {
            dx = centerX * scaleValue - fingerX;
            dy = centerY * scaleValue - fingerY;
        }

        float angle = (float) Math.toDegrees(Math.atan2(dx, dy));

        if (angle < 0) {
            angle += 360;
        }

        bearing = 360 - angle;
        invalidate();

        if (lastBearing > 270 && lastBearing < 360) {
            if (bearing > 0 && bearing < 90) {
                loop++;
            }
        } else if (lastBearing >= 0 && lastBearing < 90) {
            if (bearing > 270 && bearing < 360) {
                if (loop == 0) {
                    bearing = lastBearing;
                    return false;
                }
                loop--;
            }
        }

        lastBearing = bearing;

        if (valueListener != null) {
            valueListener.newValue(bearing + 360 * loop);
        }

        return true;
    }
}
