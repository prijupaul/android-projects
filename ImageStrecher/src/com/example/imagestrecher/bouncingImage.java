package com.example.imagestrecher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class bouncingImage extends ImageView implements OnTouchListener {

	private static final int MAX_Y_OVERSCROLL_DISTANCE = 500;
	private static final int IMAGE_HEIGHT = 100;

	private Context mContext;
	private int mCurrentHeight;
	private int mLastMotionY;
	private int mStrechedHeight;

	public bouncingImage(Context context) {
		super(context);
		mContext = context;
		initBounceListView();
	}

	public bouncingImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initBounceListView();
	}

	public bouncingImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initBounceListView();
	}

	private void initBounceListView() {
		
		measureView(this);
		mCurrentHeight = getMeasuredHeight();

		setOnTouchListener(this);
	}

	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, IMAGE_HEIGHT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
	}

	private void applyHeaderHeightChanges(MotionEvent ev) {
		// getHistorySize has been available since API 1
		int pointerCount = ev.getHistorySize();
		for (int p = 0; p < pointerCount; p++) {

			int historicalY = (int) ev.getHistoricalY(p);
			
			// calculate the distance to grow
			int topHeight = (int) (((historicalY - mLastMotionY) + mCurrentHeight));
		
			if (topHeight > 0) {
				ViewGroup.LayoutParams params = this.getLayoutParams();
				params.height = topHeight;
				mStrechedHeight = topHeight;
				setLayoutParams(params);
			}
		}
	}

	private void resetHeaderHeight() {
		ViewGroup.LayoutParams params = this.getLayoutParams();
		params.height = mCurrentHeight;
		setLayoutParams(params);

		clearAnimation();

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		final int y = (int) event.getY();
		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:{
			resetHeaderHeight();
			break;
		}

		case MotionEvent.ACTION_DOWN:{
			mLastMotionY = y;
			mStrechedHeight = 0;
			return true;
		}
		case MotionEvent.ACTION_MOVE:{

			if (mStrechedHeight < MAX_Y_OVERSCROLL_DISTANCE) {
				applyHeaderHeightChanges(event);
			} else {
				resetHeaderHeight();
			}		
			}
		 break;
		}

		return false;
	}

}
