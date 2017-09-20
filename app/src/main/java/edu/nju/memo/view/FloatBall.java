package edu.nju.memo.view;

import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

//import com.czy.floatball.R;

/**
 * Created by ZY on 2016/8/10.
 * 悬浮球
 */
public class FloatBall extends View {

    public int width = 150;

    public int height = 150;
    //默认显示的文本
//    private String text = "50%";

    // 悬浮按钮的状态
    private FloatState floatState;

    //是否在拖动
    private boolean isDrag;

    private Paint ballPaint;

//    private Paint textPaint;

//    private Bitmap bitmap;

    public FloatBall(Context context) {
        super(context);
        init();
    }

    public FloatBall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        floatState = FloatState.LEFT;

        ballPaint = new Paint();
        ballPaint.setColor(Color.GRAY);
        ballPaint.setAntiAlias(true);

//        textPaint = new Paint();
//        textPaint.setTextSize(25);
//        textPaint.setColor(Color.WHITE);
//        textPaint.setAntiAlias(true);
//        textPaint.setFakeBoldText(true);

//        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.ninja);
        //将图片裁剪到指定大小
//        bitmap = Bitmap.createScaledBitmap(src, width, height, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (floatState) {
            case LEFT:
                canvas.drawCircle(0, height / 2, width / 2, ballPaint);
                break;
            case RIGHT:
                canvas.drawCircle(width, height / 2, width / 2, ballPaint);
                break;
            case DRAGING:
                canvas.drawCircle(width / 2, height / 2, width / 2, ballPaint);
                break;
        }
//        if (!isDrag) {
//            canvas.drawCircle(width / 2, height / 2, width / 2, ballPaint);
//            float textWidth = textPaint.measureText(text);
//            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//            float dy = -(fontMetrics.descent + fontMetrics.ascent) / 2;
//            canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + dy, textPaint);
//        } else {
            //正在被拖动时则显示指定图片
//            canvas.drawCircle(width / 2, height / 2, width / 2, ballPaint);
//        }
    }

    public void setFloatState(FloatState floatState) {
        this.floatState = floatState;
        invalidate();
    }

    //设置当前移动状态
    public void setDragState(boolean isDrag) {
        this.isDrag = isDrag;
        invalidate();
    }
}
