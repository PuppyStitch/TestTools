package com.sprd.validationtools.itemstest.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorUtils {

	private static final String TAG = "SensorUtils";
	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;
	private Context mContext = null;
	private int mSensorType = -1;
	private SensorEventListener mSensorEventListener = null;

	public SensorUtils(Context context, int sensorType) {
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mContext = context;
		mSensorType = sensorType;
		mSensor = mSensorManager.getDefaultSensor(mSensorType);
		mSensorEventListener = new SensorEventListener() {
			public void onAccuracyChanged(Sensor s, int accuracy) {
			}

			public void onSensorChanged(SensorEvent event) {
			}
		};
	}

	public boolean enableSensor() {
		if (mSensorManager == null) {
			mSensorManager = (SensorManager) mContext
					.getSystemService(Context.SENSOR_SERVICE);
		}
		Log.d(TAG, "enableSensor mSensorType=" + mSensorType);
		if (mSensorManager != null) {
			boolean ret = mSensorManager.registerListener(mSensorEventListener,
					mSensor, SensorManager.SENSOR_DELAY_UI);
			return ret;
		}
		return false;
	}

	public void disableSensor() {
		if (mSensorManager == null) {
			mSensorManager = (SensorManager) mContext
					.getSystemService(Context.SENSOR_SERVICE);
		}
		Log.d(TAG, "enableSensor mSensorType=" + mSensorType);
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(mSensorEventListener);
		}
	}
}
