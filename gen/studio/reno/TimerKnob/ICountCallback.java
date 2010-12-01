/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workspace\\TimerKnob\\src\\studio\\reno\\TimerKnob\\ICountCallback.aidl
 */
package studio.reno.TimerKnob;
public interface ICountCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements studio.reno.TimerKnob.ICountCallback
{
private static final java.lang.String DESCRIPTOR = "studio.reno.TimerKnob.ICountCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an studio.reno.TimerKnob.ICountCallback interface,
 * generating a proxy if needed.
 */
public static studio.reno.TimerKnob.ICountCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof studio.reno.TimerKnob.ICountCallback))) {
return ((studio.reno.TimerKnob.ICountCallback)iin);
}
return new studio.reno.TimerKnob.ICountCallback.Stub.Proxy(obj);
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
case TRANSACTION_updateTime:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.updateTime(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_alert:
{
data.enforceInterface(DESCRIPTOR);
this.alert();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements studio.reno.TimerKnob.ICountCallback
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
public void updateTime(int sec) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(sec);
mRemote.transact(Stub.TRANSACTION_updateTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void alert() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_alert, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_updateTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_alert = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void updateTime(int sec) throws android.os.RemoteException;
public void alert() throws android.os.RemoteException;
}
