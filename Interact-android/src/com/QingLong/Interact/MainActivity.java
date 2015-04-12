package com.QingLong.Interact;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.shapes.ArcShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.view.MotionEvent;

public class MainActivity extends ActionBarActivity implements SensorEventListener,OnClickListener {
	////VARIABLES
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized,Touched = false,voice = true; private SensorManager mSensorManager; private Sensor mAccelerometer; private final float NOISE = (float) 1.5;
	boolean isFirstRead = true,GestureStarted = false;
	Networker NetworkTask; 
	protected static final int REQUEST_OK = 1;
	//////Check if screen is touched
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!voice)
		{
			int eventaction = event.getAction();
			if(eventaction != MotionEvent.ACTION_UP)
			{
				Touched = true;
				((ImageButton)findViewById(R.id.button1)).setImageDrawable(getResources().getDrawable(R.drawable.pause));
			}
			else
			{
				Touched=false;
				((ImageButton)findViewById(R.id.button1)).setImageDrawable(getResources().getDrawable(R.drawable.play));
			}
		}
		return true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		////switch button
		findViewById(R.id.btn_gesture).setOnClickListener(this);
		////Sensor initializations 
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		///Voice button 
		findViewById(R.id.button1).setOnClickListener(this);

	}
	/////Sensor stuff
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}
	public void onSensorChanged(SensorEvent event) {
		if(Touched)
		{
			Sensor mySensor = event.sensor;
			if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
			{
				float x = event.values[0];
				//float y = event.values[1];
				//float z = event.values[2];
				float z = event.values[1];
				float y = event.values[2];

				if (!mInitialized) {
					mLastX = x;
					mLastY = y;
					mLastZ = z;
					mInitialized = true;
				} else {
					boolean XisNoise,YisNoise,ZisNoise;
					XisNoise = Math.abs(x) < NOISE;
					YisNoise = Math.abs(y) < NOISE;
					ZisNoise = Math.abs(z) < NOISE;
					if(XisNoise && YisNoise && ZisNoise && GestureStarted)
					{
						//Gesture ended
						GestureStarted = false;
					}
					else if ((!XisNoise || !YisNoise || !ZisNoise) && !GestureStarted )
					{
						//Start Gesture
						GestureStarted = true;
						isFirstRead = true;
					}
					if (XisNoise) x = (float)0.0;
					if (YisNoise) y = (float)0.0;
					if (ZisNoise) z = (float)0.0;

					if(GestureStarted )
					{
						if(isFirstRead)
						{
							Log.d("accValues"," "+Float.toString(x)+"\t"+Float.toString(y)+"\t"+ Float.toString(z));
							float angle = (float) Math.toDegrees( Math.atan(Math.abs(y/x)));
							if(x > 0 && y < 0)
								angle = 360-angle;
							else if (x < 0 && y > 0 )
								angle = 180-angle;
							else if (x < 0&& y <= 0 || x<0 && y == 0 || x==0 && y<0)
								angle += 180;
							Log.d("Direction",angle + " "+mapDirection(angle,y));
							double mag = Math.sqrt(y*y + x*x);
							//Log.d("testing", NetworkTask.getStatus().toString());
							sendToNetwork(mapDirection(angle,y), Double.toString(Math.round(mag)));							
							isFirstRead = false;
						}
					}
					mLastX = x;
					mLastY = y;
					mLastZ = z;
				}

			}
		}
	}
	//////Voice Recognition stuff
	@Override
	public void onClick(View v) {
		ImageButton pressed = (ImageButton)(v);
		if(pressed.getId() == R.id.button1 && voice)
		{
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			try {
				startActivityForResult(i, REQUEST_OK);
			} catch (Exception e) {
				Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
			}
		}
		else if (pressed.getId() == R.id.btn_gesture)
		{
			RelativeLayout submitScoreLayout = (RelativeLayout)findViewById(R.id.activity_main);
			submitScoreLayout.removeAllViews();

			LayoutInflater inflater = getLayoutInflater();
			submitScoreLayout.addView(inflater.inflate(R.layout.gesture_main, null));

			((ImageButton)findViewById(R.id.btn_voice)).setOnClickListener(this);
			((ImageButton)findViewById(R.id.button1)).setOnClickListener(this);
			voice = false;
		}
		else if (pressed.getId() == R.id.btn_voice)
		{
			RelativeLayout submitScoreLayout = (RelativeLayout)findViewById(R.id.gesture_main);
			submitScoreLayout.removeAllViews();

			LayoutInflater inflater = getLayoutInflater();
			submitScoreLayout.addView(inflater.inflate(R.layout.activity_main, null));

			((ImageButton)findViewById(R.id.btn_gesture)).setOnClickListener(this);
			((ImageButton)findViewById(R.id.button1)).setOnClickListener(this);
			voice = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100)
		{
			String ip = data.getExtras().get("ip").toString();
			String port = data.getExtras().get("port").toString();
			NetworkTask = new Networker(ip, Integer.parseInt(port));
		}
		else if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
			ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
			if(thingsYouSaid.get(0).equals("map"))
			{
				sendToNetwork("map", " ");
			}
			else if(thingsYouSaid.get(0).length() > 7 && thingsYouSaid.get(0).substring(0, 6).equals("fly to") )
			{		
				sendToNetwork("ft", thingsYouSaid.get(0).substring(7));
			}
			else
			{
				sendToNetwork("se", thingsYouSaid.get(0));				
			}
		}
	}
	//////Helper functions
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.network_settings) {
			Intent i = new Intent(getApplicationContext(), NetworkingActivity.class);
			startActivityForResult(i, 100);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private String mapDirection (float theta,float y)
	{
		if(Float.isNaN(theta))
			return "motion in Z";

		String[] Directions = {"right","u-r","u-r","up","up","u-l","u-l","left","left","d-l","d-l","down","down","d-r","d-r","right"};
		return Directions[(int) Math.round((theta%360)/22.5)];
	}
	private void sendToNetwork(String s1,String s2)
	{
		if(NetworkTask == null)
		{
			Toast.makeText(getApplicationContext(), "Please Specify IP and Port for Connection\nMenu - Network settings", Toast.LENGTH_SHORT).show();
		}
		else 
		{
			if( NetworkTask.getStatus() == AsyncTask.Status.PENDING)
				NetworkTask.execute(s1,s2);
			else
				NetworkTask.doInBackground(s1,s2);
		}

	}
}
