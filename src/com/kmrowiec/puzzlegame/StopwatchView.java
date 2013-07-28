package com.kmrowiec.puzzlegame;

import java.util.Timer;
import java.util.TimerTask;

import com.kmrowiec.puzzlegame.R;
import com.kmrowiec.puzzlegame.R.styleable;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class StopwatchView extends View  {

	private String TAG = StopwatchView.class.getSimpleName();

	private String mFormat = "%1$d:%2$02d:%3$02d.%4$d";
	private Paint mPaint;
	private Paint mBlurTextPaint;
	private String mText;
	private int mAscent;
	private int mHours = 0;
	private int mMinutes = 0;
	private int mSeconds = 0;
	private int mTenths = 0;
	
	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();

	public StopwatchView(Context context) {
		this(context, null);
	}

	public StopwatchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StopwatchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StopwatchView);
		try{
			setColor(a.getColor(R.styleable.StopwatchView_paintColor, 0xFF000000));
			setTextSize(a.getDimension(R.styleable.StopwatchView_textSize, 16.0f));
		} finally {
			a.recycle();
		}
	}

	private void init() {
		mText = "0:00:00.0";
		mPaint = new Paint();
		mPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
		mPaint.setColor(0xFF000000);
		mPaint.setAntiAlias(true);
		mPaint.setMaskFilter(new BlurMaskFilter(4.0f, Blur.SOLID));
		
		mBlurTextPaint = new Paint();
		mBlurTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
		mBlurTextPaint.setColor(0xFF000000);
		mBlurTextPaint.setAntiAlias(true);
		
		setTime(0, 0, 0, 0);
	}

	public void setColor(int color) {
		mPaint.setColor(color);
		invalidate();
	}
	
	public void setTextSize(float size) {
		mPaint.setTextSize(size);
		invalidate();
		requestLayout();
	}

	public void setTime(int hours, int minutes, int seconds, int tenths) {
		mHours = hours;
		mMinutes = minutes;
		mSeconds = seconds;
		mTenths = tenths;
		printTime();
	}
	
	public void printTime() {
		mText = String.format(mFormat, mHours, mMinutes, mSeconds, mTenths);
		requestLayout();
		invalidate();
	}
	
	public String getTimeFormat() {
		return mFormat;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {	
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if ( specMode == MeasureSpec.EXACTLY ) {
			//We have been told how big we are
			result = specSize;
		} else {

			result = (int)mPaint.measureText(mText) + getPaddingLeft() + getPaddingRight();

			if ( specMode == MeasureSpec.AT_MOST ) {
				// Respect AT_MOST value if that was what is called for by measureSpec
				result = Math.min(result, specSize);
			}
		}		
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if ( specMode == MeasureSpec.EXACTLY ) {
			//We have been told how big we are
			result = specSize;
		} else {
			mAscent = (int)mPaint.ascent();
			result = (int)(-mAscent + mPaint.descent() + getPaddingBottom() + getPaddingTop());

			if ( specMode == MeasureSpec.AT_MOST ) {
				// Respect AT_MOST value if that was what is called for by measureSpec
				result = Math.min(result, specSize);
			}
		}	
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent, mPaint);
	}
	
	public void step()
	{
		mTenths++;
    	if(mTenths>=100)
    	{
    		mTenths=0;
    		mSeconds++;
    	}
    	
    	if(mSeconds>=60)
    	{
    		mSeconds=0;
    		mMinutes++;
    	}
    	
    	if(mMinutes>=60)
    	{
    		mMinutes=0;
    		mHours++;
    	}
	}
	
	public int getTenths()
	{
		return mTenths;
	}
	
	public int getSeconds()
	{
		return mSeconds;
	}
	
	public int getMinutes()
	{
		return mMinutes;
	}
	
	public int getHours()
	{
		return mHours;
	}
	
	public void play()
	{
		mTimerTask = new TimerTask() 
		{
			public void run()
			{
				handler.post(new Runnable()
				{
					public void run()
					{
						step();
						printTime();
						Log.d("TIMER", "TimerTask run");
					}
				});
			}
		};
		// public void schedule (TimerTask task, long delay, long period) 
		t.schedule(mTimerTask, 500, 10);  // 
    }

    public void stop()
    {
    	if(mTimerTask!=null)
    	{
    		//hTextView.setText("Timer canceled: " + nCounter);
    		printTime();
    		Log.d("TIMER", "timer canceled");
    		mTimerTask.cancel();
    	}
    }
    public float getScore()
    {
    	return mTenths/100.0f+
    			mSeconds+
    			mMinutes*60.0f+
    			mHours*3600.0f;
    }
}
