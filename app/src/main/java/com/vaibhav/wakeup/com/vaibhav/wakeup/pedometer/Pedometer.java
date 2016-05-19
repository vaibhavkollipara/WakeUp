package com.vaibhav.wakeup.com.vaibhav.wakeup.pedometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.vaibhav.wakeup.MainActivity;
import com.vaibhav.wakeup.R;

public class Pedometer extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private TextView tvSteps = null;
    private int initialValue=0;
    private int stepsRequired=0;
    private SharedPreferences sharedPref;
    private int currentStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        tvSteps = (TextView) findViewById(R.id.tvSteps);
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
   //     mStepDetectorSensor = mSensorManager
          //      .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sharedPref = getApplicationContext().getSharedPreferences("stepsRequired",Context.MODE_PRIVATE);
        stepsRequired = sharedPref.getInt("stepsRequired",0);
        sharedPref.edit().putInt("stepsRequired",0);
        sharedPref.edit().apply();
        currentStep=0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
     //   mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();

        mSensorManager.registerListener(this, mStepCounterSensor,SensorManager.SENSOR_DELAY_FASTEST);
      //  mSensorManager.registerListener(this, mStepDetectorSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("PedoMeter", "Event Detected");
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;
        if (values.length > 0) {
            if(initialValue==0){
                initialValue= (int) values[0];
            }
            value = (int) values[0];
        }
        currentStep = value - initialValue;
        if(currentStep>=stepsRequired){
            mSensorManager.unregisterListener(this, mStepCounterSensor);
          //  mSensorManager.unregisterListener(this, mStepDetectorSensor);
            Intent alarmOffIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(alarmOffIntent);
        }else {
                Log.i("Pedometer", "value : " + (currentStep));
                if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    tvSteps.setText(String.valueOf(stepsRequired-currentStep));
                } /*else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                    // For test only. Only allowed value is 1.0 i.e. for step taken
                    tvSteps.setText("Step_Detector :"+(stepsRequired-currentStep));
                }*/
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
