package com.example.drawcanvas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.EditText;

public class DrawingView extends View {

	
	private int STATE = DrawConstants.DRAW_NONE;

	private Paint mPaint;
	private Path mPath = null;
	private String TAG = "DrawingView";
	private Bitmap mBitmap = null;
	private Canvas mCanvas;
	private ToolBarLayoutListener mTBListerner;
	private Paint mBitmapPaint;

	private float mX, mY;

	private String textToPaint;

	private int previousColor = Color.BLUE;
	private static final float TOUCH_TOLERANCE = 4;

	/** Rectangle used (and re-used) for cropping source image. */
  private final Rect mRectSrc = new Rect();

  /** Rectangle used (and re-used) for specifying drawing area on canvas. */
  private final Rect mRectDst = new Rect();
  
	public DrawingView(Context context) {
		super(context);
		init();
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mTBListerner = (ToolBarLayoutListener)getContext();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);		
		mPaint.setStrokeWidth(4);

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		previousColor = mPaint.getColor();
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		DrawingView v = (DrawingView) findViewById(R.id.drawingView);		
		if(mCanvas == null){			
//			AssetManager mngr = getContext().getAssets();
//			
//			BitmapDrawable bDrawable = new BitmapDrawable(getResources(),"file:///android_asset/siteplan.png" );
//			Bitmap temp = bDrawable.getBitmap();
			Bitmap temp = BitmapFactory.decodeResource(getResources(),R.raw.siteplan);
			if(temp != null){
				mBitmap = Bitmap.createScaledBitmap(temp, v.getWidth(), v.getHeight(), false);				
			}
			else
			mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
			
			temp.recycle();
			
			mCanvas = new Canvas(mBitmap);
		}
		
		
	}

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {		
		Log.d(TAG,"touch_up");
		if (STATE == DrawConstants.DRAW_LINE){
			// commit the path to our offscreen
			mPath.lineTo(mX, mY);
			mCanvas.drawPath(mPath, mPaint);
	  }
		else if( STATE == DrawConstants.DRAW_ERASER){
			mPath.lineTo(mX, mY);			
			mCanvas.drawPath(mPath, mPaint);
		}
		else if (STATE == DrawConstants.DRAW_CIRCLE) {
			mPath.addCircle(mX, mY, 20, Direction.CW);
			mCanvas.drawPath(mPath, mPaint);
		}
		else if( (STATE == DrawConstants.DRAW_TEXT)){
			mPath.lineTo(mX, mY);			
			mCanvas.drawTextOnPath(textToPaint, mPath, 0,0, mPaint);
		}
		// kill this so we don't double draw		
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
		{
			Log.d(TAG,"Action_Down");
			if(mTBListerner.GetToolBarVisibility() == View.VISIBLE){
				mTBListerner.ChangeVisibilityToolsLayout();
				Log.d(TAG,"Change Layout Visibility");
			}
			
			touch_start(x, y);
			invalidate();
		}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG,"Action_move");
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG,"Action_up");
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	private void dumpFileToSdCard() {

		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/drawimage";
		File dir = new File(file_path);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(dir, "image_edited" + "_.png");
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);

		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bitmap.recycle();
		
	}
	

	@Override
	protected void onDraw(Canvas canvas) {

		if(mBitmap == null )
			return;
		
		Log.d(TAG,"onDraw");
		
		canvas.drawBitmap(mBitmap, 0,0, mBitmapPaint);
		
//		canvas.drawBitmap(mBitmap, 0,0, mBitmapPaint);
    // Wait for the touch to happen. Do not draw till then.
    if(mX == 0 && mY == 0)
    	return;
    
		if (STATE == DrawConstants.DRAW_LINE || STATE == DrawConstants.DRAW_ERASER) {
			Log.d(TAG,"drawPath in onDraw");
			Log.d(TAG,"Path " + mPath.toString());
			canvas.drawPath(mPath, mPaint);
		} else if (STATE == DrawConstants.DRAW_CIRCLE) {		
			Log.d(TAG,"DrawCircle in onDraw");
			mPath.addCircle(mX, mY, 20, Direction.CW);
			canvas.drawPath(mPath, mPaint);
		}
		
	}

	public void clear() {
		mPath.close();
		mPath.reset();
		mCanvas.drawColor(Color.WHITE);
		invalidate();
	}

	public void SetState(int state) {
			STATE = state;
			
		Log.d(TAG,"SetState: " + state);
		
		mX = mY = 0;
		
		if (STATE == DrawConstants.DRAW_ERASER) {
			mPaint.setAlpha(0xFF);
//			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			previousColor  = mPaint.getColor();
			mPaint.setColor(Color.WHITE);
			mPaint.setStrokeWidth(15);
			mPaint.setStyle(Paint.Style.STROKE);
		}
		else if(STATE == DrawConstants.DRAW_CLEAR){

		}		
		else if(STATE == DrawConstants.DRAW_TEXT){
			 mPaint.setStrokeWidth(1);
			 mPaint.setTextSize(30);			 
       mPaint.setTypeface(Typeface.SANS_SERIF);
       mPaint.setStyle(Paint.Style.FILL);
       mPaint.setColor(previousColor);
		}
		else {
			mPaint.setXfermode(null);
			mPaint.setAlpha(0xFF);
			mPaint.setStrokeWidth(4);			
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(previousColor);		
		}
	}
	
	public void colorChanged(int color) {
		mPaint.setColor(color);
		previousColor = color;
		
	}

	public int getColor() {
		return mPaint.getColor();
	}

	public void setText(String textToPaint) {
		this.textToPaint = textToPaint;		
	}

	public void save() {
		dumpFileToSdCard();		
	}
}
