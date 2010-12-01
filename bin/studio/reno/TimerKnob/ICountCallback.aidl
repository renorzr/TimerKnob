package studio.reno.TimerKnob;

interface ICountCallback {
	void updateTime(in int sec);
	void alert();
}