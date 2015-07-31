package com.kc.rc;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ChannelValueService extends Service {
	private ChannelValues channelValues = new ChannelValues();
	private BluetoothService mBluetooth;

	String tag = ChannelValueService.class.getSimpleName();
	Binder mBinder = new LocalBinder();
	BluetoothServiceConnection mBluetoothServiceConnection;

	public ChannelValueService() {

	}

	private void initBluetoothService() {
		mBluetoothServiceConnection = new BluetoothServiceConnection();
		bindService(
				new Intent(ChannelValueService.this, BluetoothService.class),
				mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);

	}

	private String mLastValues = "";

	public void setValue(int channelId, int value) {
		channelValues.setValue(channelId, value);
		Log.d(tag, channelValues.toString());

		if (mBluetooth == null) {
			initBluetoothService();
		} else {

		//	if (mBluetooth.getState() != BluetoothService.STATE_CONNECTED) {
				String values = channelValues.toString();
				if (!mLastValues.equals(values)) {
					Log.d(tag, "Send to bluetooth");
					mBluetooth.sendMessage(channelValues.toBytes());
					mLastValues = values;
				}
		//	}
		}
	}

	public int getValue(int channelId) {

		return channelValues.getValue(channelId);
	}

	class LocalBinder extends Binder {
		ChannelValueService getService() {
			return ChannelValueService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//unbindService(mBluetoothServiceConnection);
		mBluetoothServiceConnection=null;
		Log.d("ChannelValueService", "onDestroy()");
	}

	class BluetoothServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBluetooth = ((BluetoothService.LocalBinder) service)
					.getService(new IncomingHandler(ChannelValueService.this));
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetooth = null;
		}

	}

	static class IncomingHandler extends Handler {
		private final WeakReference<ChannelValueService> mTarget;

		public IncomingHandler(ChannelValueService context) {
			mTarget = new WeakReference<ChannelValueService>(
					(ChannelValueService) context);
		}

		@Override
		public void handleMessage(Message msg) {
			ChannelValueService target = mTarget.get();
			switch (msg.what) {
			case BluetoothService.MESSAGE_TOAST:

				Toast.makeText(target, msg.getData().getString("Toast"),
						Toast.LENGTH_LONG).show();
			default:

			}

			super.handleMessage(msg);
		}

	}

}