package com.example.projecthelloondo;

import static com.example.projecthelloondo.R.styleable.OutlineTextView_textStroke;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class OutlineTextView extends TextView {
    private boolean Stroke = false;
    private float StrokeWidth = 0.0f;
    private int StrokeColor;

    public OutlineTextView( Context context) {
        super(context);
    }

    public OutlineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public OutlineTextView( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs){
        TypedArray a= context.obtainStyledAttributes(attrs,R.styleable.OutlineTextView);
        Stroke= a.getBoolean(R.styleable.OutlineTextView_textStroke, false);
        StrokeWidth = a.getFloat(R.styleable.OutlineTextView_textStrokeWidth,0.0f);
        StrokeColor = a.getColor(R.styleable.OutlineTextView_textStrokeColor,0xffffffff);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Stroke) {
            ColorStateList states = getTextColors();
            getPaint().setStyle(Paint.Style.STROKE);
            getPaint().setStrokeWidth(StrokeWidth);
            setTextColor(StrokeColor);
            super.onDraw(canvas);

            getPaint().setStyle(Paint.Style.FILL);
            setTextColor(states);
        }
        super.onDraw(canvas);
    }

}
