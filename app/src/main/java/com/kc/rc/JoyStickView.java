package com.kc.rc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoyStickView extends View {

	private int currentX = 0;
	private int currentY = 0;

	private int centerX = 0;
	private int centerY = 0;

	int width;
	int hight;

	private int topPos;
	private int leftPos;
	private int bottomPos;
	private int rightPos;

	private int stickRadious = 80;

	boolean isInitialized = false;
	private static final String tag = "JoyStickView";
	private Paint stickPaint;
	private Paint defaultPaint;
	private Paint bluetoothDisconnecPaint;

	boolean isLockY = false;
	private int defaultColor = Color.rgb(120, 150, 90);
	private int stickTouchedColor = Color.rgb(60, 70, 30);

	private ChannelValueListener channelValueListener;
	private int verticalChannelId;
	private int horizontalChannelId;

	public JoyStickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		defaultPaint = new Paint();
		defaultPaint.setColor(defaultColor);
		defaultPaint.setTextSize(30);

		stickPaint = new Paint();
		stickPaint.setColor(defaultColor);

		bluetoothDisconnecPaint = new Paint();
		bluetoothDisconnecPaint.setColor(Color.GRAY);
	}

	private void initCoordinate() {

		width = this.getWidth();
		hight = this.getHeight();

		centerX = width / 2;
		centerY = hight / 2;

		currentX = centerX;

		if (this.isLockY) {
			currentY = hight - stickRadious;
		} else {
			currentY = centerY;
		}

		topPos = 0;
		bottomPos = this.getHeight();
		leftPos = 0;
		rightPos = this.getWidth();

		isInitialized = true;
	}

	private int shiftX;
	private int shiftY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int touchedX = (int) event.getX();
		int touchedY = (int) event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Calculate the difference from central point to the point of
			// screen user touched.
			shiftX = touchedX - centerX;

			if (isLockY) {
				shiftY = touchedY - currentY;
			} else {
				shiftY = touchedY - centerY;
			}

			stickPaint.setColor(stickTouchedColor);
			performClick();
		}

		if (!leftOrRughtTouched(touchedX - shiftX)) {
			currentX = touchedX - shiftX;
		}
		if (!topOrBottomtTouched(touchedY - shiftY)) {
			currentY = touchedY - shiftY;
		}

		// Log.i(tag, "onTouchEvent" + event);
		if (event.getAction() == MotionEvent.ACTION_UP) {

			currentX = centerX;

			if (!isLockY) {
				currentY = centerY;
			}

			stickPaint.setColor(defaultColor);
			performClick();

		}

		postInvalidate();
		return true;
	}

	@Override
	public boolean performClick() {
		super.performClick();

		// Handle the action for the custom click here

		return true;
	}

	private boolean leftOrRughtTouched(int x) {
		if (x - stickRadious <= leftPos || x + stickRadious >= rightPos) {
			return true;
		}
		return false;
	}

	private boolean topOrBottomtTouched(int y) {
		if (y - stickRadious <= topPos || y + stickRadious >= bottomPos) {
			return true;
		}
		return false;
	}

	private void drawGraduation(Canvas canvas) {
		canvas.drawLine(width / 2, 10, width / 2, hight - 10, defaultPaint);
		for (int i = 0; i < 10; i++) {
			canvas.drawLine(width / 2, i * hight / 10, width / 2 + 10, i
					* hight / 10, defaultPaint);
		}

		canvas.drawLine(10, hight / 2, width - 10, hight / 2, defaultPaint);
		for (int i = 0; i < 10; i++) {
			canvas.drawLine(i * width / 10, hight / 2, i * width / 10,
					hight / 2 - 10, defaultPaint);
		}

	}

	private void drawBluetoothStatus(Canvas canvas) {
		canvas.drawRect(leftPos + 10, topPos + 10, 100, 40,
				bluetoothDisconnecPaint);
	}

	private void drawValues(Canvas canvas) {
		int theNormalizedY = normalizedY(currentY);
		int theNormalizedX = normalizedX(currentX);
		canvas.drawText(theNormalizedY + "", width / 2 + 10, 40, defaultPaint);
		canvas.drawText(theNormalizedX + "", width - 60, hight / 2 - 40,
				defaultPaint);
		if (channelValueListener != null) {
			channelValueListener.onChannelValueUpdate(verticalChannelId,
					theNormalizedY);
			channelValueListener.onChannelValueUpdate(horizontalChannelId,
					theNormalizedX);
		}
	}

	private int normalizedY(int sourceY) {
		int totalYGrids = hight - stickRadious * 2;
		int y = -1 * (sourceY - hight / 2) / (totalYGrids / 100);
		y = y > 50 ? 50 : y;
		y = y < -50 ? -50 : y;

		return y + 50;
	}

	private int normalizedX(int sourceX) {
		int totalXGrids = width - stickRadious * 2;
		int x = (sourceX - width / 2) / (totalXGrids / 100);
		x = x > 50 ? 50 : x;
		x = x < -50 ? -50 : x;

		return x + 50;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (!isInitialized) {
			initCoordinate();
		}

		drawGraduation(canvas);

		canvas.drawCircle(currentX, currentY, stickRadious, stickPaint);

		drawValues(canvas);

		// drawBluetoothStatus(canvas);

	}

	public void setLockY(boolean isLockY) {
		this.isLockY = isLockY;
	}

	public ChannelValueListener getChannelValueListener() {
		return channelValueListener;
	}

	public void setChannelValueListener(
			ChannelValueListener channelValueListener) {
		this.channelValueListener = channelValueListener;
	}

	public int getVerticalChannelId() {
		return verticalChannelId;
	}

	public void setVerticalChannelId(int verticalChannelId) {
		this.verticalChannelId = verticalChannelId;
	}

	public int getHorizontalChannelId() {
		return horizontalChannelId;
	}

	public void setHorizontalChannelId(int horizontalChannelId) {
		this.horizontalChannelId = horizontalChannelId;
	}
}