package studio.reno.TimerKnob;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class Knob extends View {
	private static final long TIME_SLICE = 50;

	float 
	baseDegrees = 0, 
	lastHandDegrees = 0, 
	degrees = 0, 
	startDegrees = 0,
	angleRangeLow = 0,
	angleRangeHigh = 360,
	lowValue = 0,
	highValue = 360;
	
	long lastTime;
	int cx=0, cy=0;
	final float EPSILON = 0.0001f;
	boolean justStarted = false;
	boolean dragging = false;
	public static enum Action{START, DRAGGING, END, TURNING, STOP};
	protected Bitmap fixedPart = null;
	protected Bitmap rotatingPart = null;

	private float speedDown=0.5f;
	private float lastDelta;

	private float quantum = 1;
	private float quantumAngle = 0;

	OnRotateListener onRotateListener = null;
	public interface OnRotateListener{
		void onRotating(Action action, float degrees, float value);
	}
	
	void setOnRotateListener(OnRotateListener listener){
		onRotateListener = listener;
	}
	
	public Knob(Context context) {
		super(context);
		
        setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					startDrag(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_MOVE:
					dragging(event.getX(), event.getY());
					break;
				case MotionEvent.ACTION_UP:
					endDrag(event.getX(), event.getY());
					break;
				}
				return true;
			}        	
        });
	}
	
	protected boolean checkPosition(float x, float y) {
		return true;
	}

	protected void onDraw(Canvas canvas, float degrees, int cx, int cy){
		drawPart(canvas, fixedPart);
		canvas.save();
		canvas.rotate(degrees,cx,cy);
		drawPart(canvas, rotatingPart);
		canvas.restore();
	}
	
	private void drawPart(Canvas canvas, Bitmap part) {
		if (part==null) return;
		Paint p = new Paint();
		Matrix m = new Matrix();
		m.setTranslate(cx()-part.getWidth()/2, cy()-part.getHeight()/2);
		canvas.drawBitmap(part, m, p);
		p.setColor(Color.RED);
	}

	@Override
	protected void onDraw(Canvas canvas){
		float d = degrees;
		if (quantum>EPSILON) {
			if (quantumAngle<EPSILON) 
				quantumAngle = (angleRangeHigh-angleRangeLow)/(highValue-lowValue)*quantum;
			d = Math.round(degrees/quantumAngle)*quantumAngle;
		}

		onDraw(canvas, d+baseDegrees, cx(), cy());
	}
		
	public void setValue(float value){
		setAngle((value-lowValue)*(angleRangeHigh-angleRangeLow)/(highValue-lowValue)+angleRangeLow);
	}
	
	public float getValue(){
		float v = (degrees-angleRangeLow)*(highValue-lowValue)/(angleRangeHigh-angleRangeLow)+lowValue;
		if (quantum>EPSILON)v = Math.round(v/quantum)*quantum;
		return v;
	}
	
	public void setAngle(float degrees){
		this.degrees = degrees;
		invalidate();
	}
	
	public float getAngle(){
		return degrees;
	}
	
	public void setRange(float lowDegrees, float highDegrees, float lowValue, float highValue){
		angleRangeLow = lowDegrees;
		angleRangeHigh = highDegrees;
		this.lowValue = lowValue;
		this.highValue = highValue;
	}
		
	private void startDrag(float x, float y){
		if (!checkPosition(x,y)) return;
		startDegrees = calcDegrees(x, y);
		justStarted = true;
		dragging = true;
		lastDelta = 0;
		fireRotateEvent(Action.START);
		lastTime = System.currentTimeMillis();
	}
	
	private void dragging(float x, float y){
		if (!dragging || !checkPosition(x,y)) return;
		float curHandDegrees = calcDegrees(x, y);
		if (curHandDegrees<0) curHandDegrees +=360;
		
		if (!justStarted) {
			float delta = curHandDegrees - lastHandDegrees;
			if (delta < EPSILON && delta > -EPSILON){
				return;
			} else if (delta > 180) {
				delta -= 360;
			} else if (delta < -180) {
				delta += 360;
			}
			rotate(delta);
			fireRotateEvent(Action.DRAGGING);
			lastDelta = delta;
		} else {
			justStarted = false;
		}
		
		lastHandDegrees = curHandDegrees;
		lastTime = System.currentTimeMillis();
	}

	private void endDrag(float x, float y){
		if (!dragging) return;
		dragging = false;
		fireRotateEvent(Action.END);
		if (speedDown > EPSILON) slide(lastDelta);
	}

	private boolean rotate(float delta) {
		degrees += delta;
		
		boolean limited = false;
		if (degrees<angleRangeLow) {degrees = angleRangeLow; limited = true;}
		else if (degrees>angleRangeHigh) {degrees = angleRangeHigh; limited = true;}
		invalidate();
		return limited;
	}
	
	private void slide(final float delta) {
		if (dragging) return;
		if (delta < speedDown && delta > -speedDown) {
			fireRotateEvent(Action.STOP);
			return;
		}
		
		this.postDelayed(new Runnable(){
			@Override
			public void run() {
				float d = (delta > 0)? delta - speedDown:delta + speedDown;
				if (rotate(d)) d = 0;
				fireRotateEvent(Action.TURNING);
				slide(d);
			}
			}, TIME_SLICE);
	}

	private void fireRotateEvent(Action action) {
		onRotating(action, degrees, getValue());
	}
	
	protected void onRotating(Action action, float degrees, float value){
		if (onRotateListener!=null) onRotateListener.onRotating(action, degrees, value);		
	}
	
	private float calcDegrees(float x, float y){
		float dx = x-cx();
		float dy = y-cy();
		if (Math.abs(dx)<EPSILON) {
			return dy>0 ? 90 : -90;
		} else {
			return ((float) (Math.atan(dy/dx)*180/Math.PI))+(dx>0?0:180);
		}
	}
	
	private int cx(){
		if (cx==0) cx = getWidth()/2;
		return cx;
	}
	
	private int cy(){
		if (cy==0) cy = getHeight()/2;
		return cy;
	}

	public void setBitmaps(Bitmap rotating, Bitmap fixed) {
		fixedPart = fixed;
		rotatingPart = rotating;
	}
	
	public void setBitmaps(Bitmap rotating){
		setBitmaps(rotating, null);
	}
	
	public void setResistance(float value){
		speedDown = value;
	}

	public void setQuantum(float q) {
		quantum = q;
	}

	public void quantization(float q) {
		setValue(Math.round(getValue()/q)*q);
	}
}
