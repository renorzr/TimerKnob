package studio.reno.TimerKnob;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CountService extends Service {
	private ICountService.Stub binder = new ICountService.Stub(){
		@Override
		public void startCountDown(int sec) {
			CountService.this.startCountDown(sec);
		}

		@Override
		public void stopCountDown() {
			CountService.this.stopCountDown();
		}

		@Override
		public void setCallback(ICountCallback callback) throws RemoteException {
			CountService.this.callback = callback;
		}

		@Override
		public void enableTick(boolean enable) {
			CountService.this.enableTick(enable);
		}
	};
	private static final int NOTIFY_COUNTING_ID = 1;
	private static final int NOTIFY_ALERT_ID = 2;
	private static final long NOTIFY_INTERVAL = 2000;
	boolean counting = false;
	MediaPlayer soundRing, soundTick;
	int lastPosition = 0;
	
	private int countSeconds;
	private long startCountDownTime;
	public ICountCallback callback;
	
	NotificationManager notificationManager;
	Notification notification;

	protected PendingIntent contentIntent;
	private long lastNotifyTime = 0;

	@Override
	public void onCreate(){
		Log.d("Service", "onCreate");
		soundTick = MediaPlayer.create(this, R.raw.tick);
		soundRing = MediaPlayer.create(this, R.raw.ring);
		new Thread(){
			@Override
			public void run(){
				while (true){
					try {Thread.sleep(200);}
					catch (InterruptedException e) {e.printStackTrace();}
					if (!counting) {
						updateCountingNotification(0);
						continue;
					}
					
					int elapsed=(int)(System.currentTimeMillis()-startCountDownTime)/1000;
					int leftSeconds = countSeconds - elapsed;

					if (notificationManager!=null){
						updateCountingNotification(leftSeconds);
					}
		
					if (callback!=null)
						try {
							callback.updateTime(leftSeconds);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					
					if (leftSeconds<=0) {
						alert();
						stopCountDown();
					}					
				}
			}
		}.start();
	}
	
	protected void enableTick(boolean enable) {
		float vol = enable?1.0f:0.0f;
		soundTick.setVolume(vol, vol);
	}

	@Override
	public IBinder onBind(Intent i) {
		Log.d("Service", "onBind");
		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent i){
		Log.d("Service", "onUnbind");
		return true;
	}
	
	@Override
	public void onRebind(Intent i){
		Log.d("Service", "onRebind");
	}
	
	@Override
	public void onDestroy(){
		Log.d("Service","destroyed");
	}
	
	public void startCountDown(final int sec) {
		if (sec<=0) return;
		counting = true;
		soundTick.setLooping(true);
		try {
			soundTick.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		soundTick.start();
		startCountDownTime = System.currentTimeMillis();
		countSeconds = sec;

		// prepare notification
		if (notificationManager==null){
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Intent ni = new Intent(this, Main.class);
			contentIntent = PendingIntent.getActivity(this, 0, ni, 0);
			notification = new Notification(R.drawable.icon, null, System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.setLatestEventInfo(getApplicationContext(), "counting down", null, contentIntent);
		}
		if (lastNotifyTime+1000<System.currentTimeMillis()){
			notificationManager.cancelAll();
			notificationManager.notify(NOTIFY_COUNTING_ID, notification);
			lastNotifyTime = System.currentTimeMillis();
		}

		Log.d("CountService","start count");
		
	
	}

	private CharSequence toText(int sec) {
		int m = sec/60;
		
		if (m<1) {
			return "< 1 minute";
		} else if (m==1) {
			return "1 minute";
		} else if (m<60) {
			return m+" minutes";
		} else {
			return String.format("%02d:%02d", m/60,m%60);
		}
	}

	public void stopCountDown() {
		counting = false;
		soundTick.stop();
	}

	private void alert() {
		Log.d("CountService", "alert");
		Notification n = new Notification(R.drawable.icon, "time's up", System.currentTimeMillis());
		n.vibrate = new long[]{0,1000,500,1000,500,1000};
		n.setLatestEventInfo(getApplicationContext(), "time's up", null, contentIntent);
		n.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(NOTIFY_ALERT_ID, n);
		soundRing.start();
		try {callback.alert();}
		catch (RemoteException e) {e.printStackTrace();}
	}

	private void updateCountingNotification(int sec) {
		if (notificationManager!=null && notification!=null&& lastNotifyTime+NOTIFY_INTERVAL<System.currentTimeMillis()) {
			synchronized(notificationManager){
				if (sec>0){
					notification.setLatestEventInfo(getApplicationContext(), toText(sec), null, contentIntent);
					synchronized(notification){
						notificationManager.notify(NOTIFY_COUNTING_ID,notification);
					}
				} else {
					notificationManager.cancel(NOTIFY_COUNTING_ID);					
				}
			}
			lastNotifyTime = System.currentTimeMillis();
		}
	}

}
