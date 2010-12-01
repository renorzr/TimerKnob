package studio.reno.TimerKnob;

import studio.reno.TimerKnob.Knob.Action;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

public class TimerKnob {

	protected static final int DONE_MSG = 1;
	final int PADDING = 30;
	final int SPACE = 3;
	final int SCALE_LENGTH = 5;
	final int LONG_SCALE_LENGTH = 15;
	final int POINTER_LENGTH = 50;
	final int MAX_HOURS = 24;
	final int TEXT_SIZE = 30;
	private CharSequence text = "--:--";
	Knob knob;
	int soundDrag;
	int lastPosition = 0;
	
	SoundPool soundPool = new SoundPool(5, android.media.AudioManager.STREAM_MUSIC, 0);
	private Context context;
	protected ICountService service;
	Handler handler = new Handler();
	int width, height, radius, cx, cy;
	Bitmap background=null;
	
	OnRotateListener onRotateListener = null;
	protected long startCountDownTime = 0;
	public interface OnRotateListener{
		void onRotating(Action action, float degrees, float value);
	}
	
	void setOnRotateListener(OnRotateListener listener){
		onRotateListener = listener;
	}
	
	public TimerKnob(Context context) {
		this.context = context;
		final Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);

		soundDrag = soundPool.load(context, R.raw.drag, 1);
		
		knob = new Knob(context){

			@Override
			protected void onDraw(Canvas c){
				Paint p = new Paint();
				p.setAntiAlias(true);
				if (background==null){
					calcSize(getWidth(),getHeight());
					int r = radius;
					cx = TimerKnob.this.cx;
					cy = TimerKnob.this.cy;
					background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(background);
					p.setColor(0xFFFFFFE0);
					p.setStyle(Style.FILL);
					int ir = r - SPACE;
//					canvas.drawCircle(cx, cy, ir, p);
					canvas.drawBitmap(bmp, null, new Rect(cx-ir, cy-ir, cx+ir, cy+ir), p);
					p.setColor(Color.GRAY);
					p.setStyle(Style.STROKE);
//					canvas.drawCircle(cx, cy, r - SPACE, p);
					for (int i = 0; i<60; ++i){
						int scale_length = (i%5==0) ? LONG_SCALE_LENGTH : SCALE_LENGTH;
						double rad = i*6*Math.PI/180;
						int[] coords = calcRay(rad, cx, cy, r, r+scale_length);
						drawLine(canvas,coords,p);
					}					
				}
				c.drawBitmap(background, 0, 0, p);
				p.setColor(Color.RED);
				int [] coords = calcRay((degrees-90)*Math.PI/180, cx, cy, radius-SPACE, radius-SPACE-POINTER_LENGTH);
				drawLine(c,coords,p);
				drawText(c);
			}
			
			@Override
			protected boolean checkPosition(float x, float y){
				if (background==null)return false;
				return (x-cx)*(x-cx)+(y-cy)*(y-cy)<radius*radius;
			}
		};
		
		knob.setRange(0, MAX_HOURS*360, 0, MAX_HOURS*60);
		knob.setResistance(0);
		knob.setQuantum(0);
    			
        knob.setOnRotateListener(new Knob.OnRotateListener(){
    		@Override
    		public void onRotating(Action action, float degrees, float value) {
    			if (onRotateListener!=null) onRotateListener.onRotating(action, degrees, value);
    			
    			setText(Main.toText((int) (value*60)));
    			switch (action){
    			case START:
    				stopCountDown();
    				break;
    			case DRAGGING:
    				int v = (int) (6*knob.getValue());
    				if (v!=lastPosition) 
    					soundPool.play(soundDrag, 1, 1, 1, 0, 1);
    				lastPosition = v;
    				break;
    			case END:
    				startCountDownTime = System.currentTimeMillis();
    				startCountDown((int) (60*knob.getValue()));
    				Log.d("TimerKnob","done");
    				break;
    			case STOP:
    				break;
    			}
    		}

        });
        
        startCountService();
        Toast.makeText(context, "Drag to set time", Toast.LENGTH_SHORT).show();
	}

	private int[] calcRay(double rad, int x, int y, int r0, int r1){
		int x0 = (int) (x + Math.cos(rad) * r0);
		int x1 = (int) (x + Math.cos(rad) * r1);
		int y0 = (int) (y + Math.sin(rad) * r0);
		int y1 = (int) (y + Math.sin(rad) * r1);
		return new int[] {x0,y0,x1,y1};
	}
	
	private void drawLine(Canvas c, int[] coords, Paint p){
		c.drawLine(coords[0], coords[1], coords[2], coords[3], p);
	}
	
	protected void drawText(Canvas c) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setTextSize(TEXT_SIZE);
		p.setColor(0xFFFFFFCC);
		p.setShadowLayer(3, 0, 0, Color.BLACK);
		c.drawText((String) text, cx-p.measureText((String) text)/2, cy+TEXT_SIZE/2, p);
	}

	protected void calcSize(int width, int height) {
		this.width = width;
		this.height = height;
		radius = Math.min(width, height)/2-PADDING;
		cx = width/2;
		cy = height/2;
	}

	ServiceConnection conn = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName c, IBinder b) {
			Log.d("TimerKnob","Connected");
			service = ICountService.Stub.asInterface(b);
			try {
				service.setCallback(new ICountCallback.Stub(){

					@Override
					public void alert() {
						Log.d("TimerKnob","alert!");
				        Toast.makeText(context, "Time's up", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void updateTime(final int sec) {
						handler.post(new Runnable(){
							@Override
							public void run() {
								TimerKnob.this.updateTime(sec);
							}							
						});
					}				
				});
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName c) {
			Log.d("TimerKnob","Disconnected");
		}			
	};
	
	public void close(){
		context.unbindService(conn);
	}

	private void startCountService(){
		final Intent i = new Intent(ICountService.class.getName());
		context.startService(i);
		context.bindService(i, conn, 0);
	}

	private void startCountDown(int sec) {
		setText(Main.toText(sec));
		try {
			service.startCountDown(sec);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void stopCountDown(){
		try {
			service.stopCountDown();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void addTo(ViewGroup vg) {
		vg.addView(knob);
	}
	
	private void setText(CharSequence text){
		this.text = text;		
	}

	public void setSeconds(final int sec) {
		stopCountDown();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				if (sec>0) startCountDown(sec);
				updateTime(sec);
			}			
		}, 200);
	}

	private void updateTime(int sec) {
		//if (System.currentTimeMillis()-startCountDownTime<UPDATE_INTERVAL) return;
		knob.setValue((float)sec/60);
		setText(Main.toText(sec));
	}

	public void enableTick(boolean enable){
		try {
			service.enableTick(enable);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
