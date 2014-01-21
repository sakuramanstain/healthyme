package han.com.ui.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * @author hanaldo
 */
public class MyCalibriTextView extends TextView {
    
    private static Typeface typeFace;
    
    public MyCalibriTextView(Context context) {
        super(context);
        applyTypeFace(context);
    }
    
    public MyCalibriTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyTypeFace(context);
    }
    
    public MyCalibriTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyTypeFace(context);
    }
    
    private void applyTypeFace(Context context) {
        if (typeFace == null) {
            typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/Calibri.ttf");
        }
        setTypeface(typeFace, Typeface.BOLD);
    }
}
