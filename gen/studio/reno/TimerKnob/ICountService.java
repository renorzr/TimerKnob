/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workspace\\TimerKnob\\src\\studio\\reno\\TimerKnob\\ICountService.aidl
 */
package studio.reno.TimerKnob;
public interface ICountService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements studio.reno.TimerKnob.ICountService
{
private static final java.lang.String DESCRIPTOR = "studio.reno.TimerKnob.ICountService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an studio.reno.TimerKnob.ICountService interface,
 * generating a proxy if needed.
 */
public static studio.reno.TimerKnob.ICountService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof studio.reno.TimerKnob.ICountService))) {
return ((studio.reno.TimerKnob.ICountService)iin);
}
return new studio.reno.TimerKnob.ICountService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startCountDown:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.startCountDown(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopCountDown:
{
data.enforceInterface(DESCRIPTOR);
this.stopCountDown();
reply.writeNoException();
return true;
}
case TRANSACTION_setCallback:
{
data.enforceInterface(DESCRIPTOR);
studio.reno.TimerKnob.ICountCallback _arg0;
_arg0 = studio.reno.TimerKnob.ICountCallback.Stub.asInterface(data.readStrongBinder());
this.setCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_enableTick:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.enableTick(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements studio.reno.TimerKnob.ICountService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void startCountDown(int sec) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sec);
mRemote.transact(Stub.TRANSACTION_startCountDown, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopCountDown() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopCountDown, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setCallback(studio.reno.TimerKnob.ICountCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void enableTick(boolean enable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_enableTick, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startCountDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stopCountDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_enableTick = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void startCountDown(int sec) throws android.os.RemoteException;
public void stopCountDown() throws android.os.RemoteException;
public void setCallback(studio.reno.TimerKnob.ICountCallback callback) throws android.os.RemoteException;
public void enableTick(boolean enable) throws android.os.RemoteException;
}
