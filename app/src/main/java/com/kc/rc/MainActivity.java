package com.kc.rc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	String tag = MainActivity.class.getSimpleName();
	ChannelValueService mChannelValueService;
	ChannelServiceConnection mChannlValueServiceConnection;

	class ChannelServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder boundService) {
			mChannelValueService = ((ChannelValueService.LocalBinder) boundService)
					.getService();

			Log.i(tag, "onServiceConnected(): Connected");

		}

		public void onServiceDisconnected(ComponentName name) {
			mChannelValueService = null;
			Log.i(tag, "onServiceDisconnected(): Disconnected");

		}
	}

	private void initService() {
		mChannlValueServiceConnection = new ChannelServiceConnection();

		boolean ret = bindService(new Intent(this, ChannelValueService.class),
				mChannlValueServiceConnection, Context.BIND_AUTO_CREATE);
		Log.i(tag, "initService() bound value: " + ret);

	}

	private void releaseService() {
		unbindService(mChannlValueServiceConnection);
		mChannlValueServiceConnection = null;
		Log.d(tag, "releaseService(): unbound.");
	}

	ChannelValueListener channelValueListener = new ChannelValueListener() {

		@Override
		public void onChannelValueUpdate(int channel, int value) {

			if (mChannelValueService != null) {
				mChannelValueService.setValue(channel, value);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initService();

		JoyStickView jsv1 = (JoyStickView) this
				.findViewById(R.id.joyStickView1);
		jsv1.setLockY(true);
		JoyStickView jsv2 = (JoyStickView) this
				.findViewById(R.id.joyStickView2);

		jsv1.setChannelValueListener(channelValueListener);
		jsv1.setVerticalChannelId(0); // Throttle
		jsv1.setHorizontalChannelId(1);// Yaw
		

		jsv2.setChannelValueListener(channelValueListener);
		jsv2.setVerticalChannelId(2); // Pitch
		jsv2.setHorizontalChannelId(3);// Roll

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.aux_setting) {
			Intent intent = new Intent(this, AuxSettingActivity.class);

			startActivityForResult(intent, 888);
			return true;
		}
		if (id == R.id.bt_connection) {
			Intent intent = new Intent(this, BluetoothSetupActivity.class);

			startActivityForResult(intent, 888);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseService();
	}

}
