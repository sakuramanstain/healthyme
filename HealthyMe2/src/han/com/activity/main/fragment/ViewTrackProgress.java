package han.com.activity.main.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.view.View;
import han.com.R;

public class ViewTrackProgress extends View {

    private static final String className = ViewTrackProgress.class.getName();
    private static Bitmap trackTop, trackBase;
    private static RectF maskTopLeft, maskTopRight, maskLeftArc, maskRightArc, maskBottom;
    private int viewWidth, viewHeight;
    private float progress, radius, side, radiusOffset, sideOffset, boxHeight, scaleValue;
    private final Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private Paint overlayPaint;
    private boolean scale;

    public ViewTrackProgress(Context context, int viewWidth, int viewHeight, boolean scale, float scaleValue) {
        super(context);

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        radius = 130;
        side = 280;
        radiusOffset = 38;
        sideOffset = 18;
        boxHeight = 55;
        this.scale = scale;
        this.scaleValue = scaleValue;

        loadImages();
        makePaints();
    }

    private void loadImages() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        if (trackTop == null) {
            trackTop = BitmapFactory.decodeResource(getResources(), R.drawable.img_track_top, options);
            trackTop = Bitmap.createScaledBitmap(trackTop, viewWidth, viewHeight, true);
        }

        if (trackBase == null) {
            trackBase = BitmapFactory.decodeResource(getResources(), R.drawable.img_track_bot, options);
            trackBase = Bitmap.createScaledBitmap(trackBase, viewWidth - 10, viewHeight - 10, true);
        }

        if (maskTopLeft == null) {
            maskTopLeft = new RectF(radius - radiusOffset + sideOffset, 0, side / 2 + radius - radiusOffset + sideOffset, boxHeight);
            maskLeftArc = new RectF(-radiusOffset, -45, radius * 2, radius * 2);
            maskBottom = new RectF(radius - radiusOffset + sideOffset, viewHeight - boxHeight, radius - radiusOffset + side + sideOffset, viewHeight);
            maskRightArc = new RectF(-radiusOffset + side + sideOffset, -45, -radiusOffset + side + radius * 2 + sideOffset, radius * 2);
            maskTopRight = new RectF(radius - radiusOffset + side / 2 + sideOffset, 0, side + radius - radiusOffset + sideOffset, boxHeight);
        }
    }

    private void makePaints() {
        overlayPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (scale) {
            canvas.scale(scaleValue, scaleValue);
        }

        canvas.save();
        canvas.translate(5, 5);
        canvas.drawBitmap(trackBase, 0, 0, null);
        canvas.restore();

        if (progress >= 100 || progress <= 0) {
            return;
        }

        int sc = canvas.saveLayer(0, 0, viewWidth, viewHeight, null,
                Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.drawBitmap(trackTop, 0, 0, overlayPaint);
        overlayPaint.setXfermode(mode);

        if (progress > 0 && progress <= 12.5) {
            float newSide = side / 2;
            newSide -= (newSide * progress / 12.5);

            maskTopLeft.set(maskTopLeft.left, maskTopLeft.top, newSide + radius - radiusOffset + sideOffset, maskTopLeft.bottom);
            canvas.drawRect(maskTopLeft, overlayPaint);

            canvas.drawArc(maskLeftArc, 270, -180, true, overlayPaint);

            maskBottom.set(radius - radiusOffset + sideOffset, maskBottom.top, maskBottom.right, maskBottom.bottom);
            canvas.drawRect(maskBottom, overlayPaint);

            canvas.drawArc(maskRightArc, 90, -180, true, overlayPaint);

            maskTopRight.set(maskTopRight.left, maskTopRight.top, side + radius - radiusOffset + sideOffset, maskTopRight.bottom);
            canvas.drawRect(maskTopRight, overlayPaint);

        } else if (progress >= 12.5 && progress <= 37.5) {
            float degree = 180;
            degree = degree * (progress - 12.5f) / 25;

            canvas.drawArc(maskLeftArc, 270 - degree, degree - 180, true, overlayPaint);
            canvas.drawRect(maskBottom, overlayPaint);
            canvas.drawArc(maskRightArc, 90, -180, true, overlayPaint);
            canvas.drawRect(maskTopRight, overlayPaint);

        } else if (progress >= 37.5 && progress <= 62.5) {
            float newSide = side;
            newSide = newSide * (progress - 37.5f) / 25;

            maskBottom.set(radius - radiusOffset + newSide + sideOffset, maskBottom.top, maskBottom.right, maskBottom.bottom);
            canvas.drawRect(maskBottom, overlayPaint);
            canvas.drawArc(maskRightArc, 90, -180, true, overlayPaint);
            canvas.drawRect(maskTopRight, overlayPaint);

        } else if (progress >= 62.5 && progress <= 87.5) {
            float degree = 180;
            degree = degree * (progress - 62.5f) / 25;

            canvas.drawArc(maskRightArc, 90 - degree, degree - 180, true, overlayPaint);
            canvas.drawRect(maskTopRight, overlayPaint);

        } else if (progress >= 87.5 && progress < 100) {
            float newSide = side / 2;
            newSide = newSide * (progress - 87.5f) / 12.5f;

            maskTopRight.set(maskTopRight.left, maskTopRight.top, side + radius - radiusOffset - newSide + sideOffset, maskTopRight.bottom);
            canvas.drawRect(maskTopRight, overlayPaint);
        }

        overlayPaint.setXfermode(null);
        canvas.restoreToCount(sc);

//        Paint p = new Paint();
//        p.setColor(Color.RED);
//        canvas.drawLine(110, 0, 110, 200, p);
//        canvas.drawLine(110 + side, 0, 110 + side, 200, p);

    }

    /**
     *
     * @param progress it is between 0 and 100
     */
    public void updateProgress(float progress) {
        this.progress = progress;
        invalidate();
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        progress += 0.2f;
//        if (progress > 100) {
//            progress = 0;
//        }
//        invalidate();
//        return true;
//    }
}
