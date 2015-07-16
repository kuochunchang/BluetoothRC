package com.kc.rc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AuxSettingActivity extends Activity {
	private SeekBar aux1SeekBar;
	private SeekBar aux2SeekBar;
	private SeekBar aux3SeekBar;
	private SeekBar aux4SeekBar;

	private static final int AUX1_CHNNEL_ID = 4;
	private static final int AUX2_CHNNEL_ID = 5;
	private static final int AUX3_CHNNEL_ID = 6;
	private static final int AUX4_CHNNEL_ID = 7;
	
	private String tag = AuxSettingActivity.class.getSimpleName();

	private ChannelValueService mChannelValueService;
	private ChannelServiceConnection mChannlValueServiceConnection;

	class ChannelServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder boundService) {
			mChannelValueService = ((ChannelValueService.LocalBinder) boundService)
					.getService();
			
			Log.i(tag, "onServiceConnected(): Connected");

			aux1SeekBar.setProgress(mChannelValueService.getValue(AUX1_CHNNEL_ID));
			aux2SeekBar.setProgress(mChannelValueService.getValue(AUX2_CHNNEL_ID));
			aux3SeekBar.setProgress(mChannelValueService.getValue(AUX3_CHNNEL_ID));
			aux4SeekBar.setProgress(mChannelValueService.getValue(AUX4_CHNNEL_ID));

		}

		public void onServiceDisconnected(ComponentName name) {
			mChannelValueService = null;
			Log.i(tag, "onServiceDisconnected(): Disconnected");

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_aux_setting);

		aux1SeekBar = (SeekBar) findViewById(R.id.SeekBar1);
		aux2SeekBar = (SeekBar) findViewById(R.id.SeekBar2);
		aux3SeekBar = (SeekBar) findViewById(R.id.SeekBar3);
		aux4SeekBar = (SeekBar) findViewById(R.id.SeekBar4);

		aux1SeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		aux2SeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		aux3SeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		aux4SeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

		initService();

	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	private void initService() {
		mChannlValueServiceConnection = new ChannelServiceConnection();

		Intent i = new Intent();
		i.setClassName("com.kc.rc",
				com.kc.rc.ChannelValueService.class.getName());
		boolean ret = bindService(i, mChannlValueServiceConnection,
				Context.BIND_AUTO_CREATE);
		Log.i(tag, "initService() bound value: " + ret);

	}

	class SeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			int value = toLimitedProgress(seekBar.getProgress());
			seekBar.setProgress(value);

			if (seekBar.getId() == R.id.SeekBar1) {
				mChannelValueService.setValue(AUX1_CHNNEL_ID, value);
			}
			if (seekBar.getId() == R.id.SeekBar2) {
				mChannelValueService.setValue(AUX2_CHNNEL_ID, value);
			}
			if (seekBar.getId() == R.id.SeekBar3) {
				mChannelValueService.setValue(AUX3_CHNNEL_ID, value);
			}
			if (seekBar.getId() == R.id.SeekBar4) {
				mChannelValueService.setValue(AUX4_CHNNEL_ID, value);
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		private int toLimitedProgress(int progress) {
			int lp;
			if (progress < 25) {
				lp = 0;
			} else if (progress > 75) {
				lp = 100;
			} else {
				lp = 50;
			}

			return lp;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.aux_setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		/*
		 * if (id == R.id.action_settings) { return true; }
		 */
		return super.onOptionsItemSelected(item);
	}

	private void releaseService() {
		unbindService(mChannlValueServiceConnection);
		mChannlValueServiceConnection = null;
		Log.d(tag, "releaseService(): unbound.");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseService();
	}
}
