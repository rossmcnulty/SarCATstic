package net.gnomeffinway.sarcatstic;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class OutlineTextView extends TextView {
    
    public OutlineTextView(Context context) {
        super(context);
    }
    
    public OutlineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public OutlineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < 15; i++) {
            super.draw(canvas);
        }
    }
}