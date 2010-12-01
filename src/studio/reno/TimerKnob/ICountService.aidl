package studio.reno.TimerKnob;
import studio.reno.TimerKnob.ICountCallback;

interface ICountService {
	void startCountDown(in int sec);
	void stopCountDown();
	void setCallback(in ICountCallback callback);
	void enableTick(in boolean enable);
}