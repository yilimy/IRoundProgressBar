package com.orice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.orice.R;

/**
 *
 * 圆形进度条
 *
 * Created by Orice on 2017/2/28.
 */

public class RoundProgressBar extends View {

    private Paint paint;
    private int txtColor;
    private float txtSize;
    private boolean txtVisible = false;
    private int roundColor;
    private float strokeWidth;

    private int mProgress;
    private int max;
    private RectF oval;

    private boolean isRoate = false;
    //是否补角
    private boolean supplement = false;
    //是否逆时针
    private boolean antiClock = false;

    private int state = 0;
    private static final int STATE_PROGRESSING = 0;
    private static final int STATE_PAUSE = 1;
    private int emptyWidth;
    private int trianglePadding;

    public RoundProgressBar(Context context) {
        this(context,null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgress);
        txtColor = typedArray.getColor(R.styleable.RoundProgress_txtColor,Color.BLACK);
        txtSize = typedArray.getDimension(R.styleable.RoundProgress_txtSize,12);
        mProgress = typedArray.getInteger(R.styleable.RoundProgress_progress, 0);
        max = typedArray.getInteger(R.styleable.RoundProgress_max, 100);
        txtVisible = typedArray.getBoolean(R.styleable.RoundProgress_txtVisible, false);
        roundColor = typedArray.getColor(R.styleable.RoundProgress_roundColor,Color.GRAY);
        strokeWidth = typedArray.getDimension(R.styleable.RoundProgress_strokeWidth, 5);
        supplement = typedArray.getBoolean(R.styleable.RoundProgress_supplement, false);
        antiClock = typedArray.getBoolean(R.styleable.RoundProgress_AntiClock, false);
        state = typedArray.getInt(R.styleable.RoundProgress_state, 0);
        emptyWidth = typedArray.getInteger(R.styleable.RoundProgress_emptyWidth, 5);
        trianglePadding = typedArray.getInteger(R.styleable.RoundProgress_trianglePadding, 10);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.reset();
        paint.setAntiAlias(true);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        float radius = Math.min(cx,cy) - strokeWidth / 2;

        //外环
        drawOuterOval(canvas, cx, cy, radius);

        if(radius > strokeWidth + emptyWidth){
            radius = radius - strokeWidth - emptyWidth;//半径缩小5
        }

        switch (state){
            case STATE_PAUSE:
                drawTriangle(canvas, cx, cy, radius);
                break;
            case STATE_PROGRESSING:
                drawSector(canvas, cx, cy, radius);
                if(txtVisible && mProgress != 0 ){
                    drawText(canvas, cx, cy);
                }
                break;
        }

    }

    /**
     * 绘制外部环形
     * @param canvas
     * @param centerX 中心点x坐标
     * @param centerY 中心点y坐标
     * @param radius 半径
     */
    private void drawOuterOval(Canvas canvas, int centerX, int centerY, float radius){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(roundColor);
        canvas.drawCircle(centerX,centerY,radius,paint);
    }

    /**
     * 进度数字
     * @param canvas
     * @param pointX 文字中心x坐标
     * @param pointY 文字中心y坐标
     */
    private void drawText(Canvas canvas, int pointX, int pointY){
        paint.setStrokeWidth(0);
        paint.setColor(txtColor);
        paint.setTextSize(txtSize);
        int precent = mProgress * 100 / max;
        float textWidth = paint.measureText(precent + "%");
        canvas.drawText(precent + "%", pointX - textWidth / 2, pointY + txtSize / 2, paint);
    }

    /**
     * 绘制扇形进度条
     * @param canvas
     * @param centerX 中心点x坐标
     * @param centerY 中心点y坐标
     * @param radius 半径
     */
    private void drawSector(Canvas canvas, int centerX, int centerY, float radius){
        paint.setColor(roundColor);
        paint.setStrokeWidth(0);
        if(oval == null){
            oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        }
        int progress = mProgress;
        if(supplement){
            progress = max - mProgress;
        }
        float sweepAngle = 360 * progress / max;
        if(antiClock){
            sweepAngle = -1 * sweepAngle;
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawArc(oval, -90, sweepAngle, true, paint);
    }

    /**
     * 绘制等边三角形
     * @param canvas
     * @param centerX 中心点x坐标
     * @param centerY 中心点y坐标
     * @param radius 所属圆半径
     */
    private void drawTriangle(Canvas canvas, int centerX, int centerY, float radius){
        if(radius > trianglePadding){
            radius = radius - trianglePadding;//半径缩小10
        }
        Path mPath = new Path();
        double cos60 = Math.cos(Math.PI / 3);
        double sin60 = Math.sin(Math.PI / 3);
        mPath.moveTo(centerX + radius, centerY);
        mPath.lineTo((float) (centerX - radius * cos60), (float) (centerY + radius * sin60));
        mPath.lineTo((float) (centerX - radius * cos60), (float) (centerY - radius * sin60));
        mPath.close();
        paint.setColor(roundColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(mPath,paint);
    }

    public synchronized int getMax(){
        return max;
    }

    public synchronized void setMax(int max){
        if(max < 0){
            throw new IllegalArgumentException("max can not less than 0");
        }
        this.max = max;
    }

    public synchronized int getProgress(){
        return mProgress;
    }

    public synchronized void setProgress(int progress){
        state = STATE_PROGRESSING;
        if(progress < 0){
            this.mProgress = 0;
        }else if(progress > max){
            this.mProgress = max;
        }else{
            this.mProgress = progress;
        }
        isRoate = !isRoate;
        postInvalidate();
    }

    public synchronized void pause(){
        state = STATE_PAUSE;
        postInvalidate();
    }

    public int getTxtColor() {
        return txtColor;
    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
    }

    public float getTxtSize() {
        return txtSize;
    }

    public void setTxtSize(float txtSize) {
        this.txtSize = txtSize;
    }

    public boolean isTxtVisible() {
        return txtVisible;
    }

    public void setTxtVisible(boolean txtVisible) {
        this.txtVisible = txtVisible;
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
