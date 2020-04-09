package com.example.instantinvites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/*Program to run application that enables the user to use a compass,
uses the accelerometer and magnetic field sensor*/
public class Compass extends AppCompatActivity implements SensorEventListener {
    //Declare ImageView
    private ImageView imageView;

    //Declare lists for storing gravity and geomagnetic values
    private float[] grav_values = new float[3];
    private float[] geomag_values = new float[3];

    //Variables used for calculating rotation
    private float azimuth = 0f;
    private float curr_azimuth = 0f;

    //Declare sensor manager
    private SensorManager sensorManager;

    //Declare TextView
    private TextView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        //Store compass image into imageView and initialise sensorManager
        imageView = (ImageView) findViewById(R.id.compass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Store help view into TextView
        help = (TextView) findViewById(R.id.help);

        //Transfer the user to help screen after he/she taps help
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Compass.this, Help.class);
                startActivity(intent);
            }
        });
    }

    @Override
    //Register listeners for magnetic field and accelerometer when called
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    //Unregister listener for the sensor when called
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Constant alpha value
        final float alpha = 0.97f;
        synchronized (this){
            /*Utilise accelerometer sensor when necessary to calculate
            and obtain values needed for grav_values list*/
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                grav_values[0] = alpha*grav_values[0]+(1-alpha)*sensorEvent.values[0];
                grav_values[1] = alpha*grav_values[1]+(1-alpha)*sensorEvent.values[1];
                grav_values[2] = alpha*grav_values[2]+(1-alpha)*sensorEvent.values[2];
            }

            /*Utilise magnetic field sensor when necessary to calculate
            and obtain values needed for geomag_values list*/
            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                geomag_values[0] = alpha*geomag_values[0]+(1-alpha)*sensorEvent.values[0];
                geomag_values[1] = alpha*geomag_values[1]+(1-alpha)*sensorEvent.values[1];
                geomag_values[2] = alpha*geomag_values[2]+(1-alpha)*sensorEvent.values[2];
            }

            //Initialize lists R and I of type float
            float R[] = new float[9];
            float I[] = new float[9];

            /*Determine whether rotation matrix from SensorManager is
            successful or not*/
            boolean success = SensorManager.getRotationMatrix(R,I,grav_values, geomag_values);
            if (success){
                /*Declare orientation list and pass it to
                SensorManager to get orientation*/
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                //Calculate new value for azimuth
                azimuth = (float)Math.toDegrees(orientation[0]);
                azimuth = (azimuth+360)%360;

                //Set the rotate animation for the compass
                Animation animation = new RotateAnimation(-curr_azimuth,-azimuth,Animation.RELATIVE_TO_SELF,
                        0.5f,Animation.RELATIVE_TO_SELF,0.5f);

                //Set current azimuth to azimuth
                curr_azimuth = azimuth;

                //Set duration, repeat and after-fill for the movement of the compass
                animation.setDuration(500);
                animation.setRepeatCount(0);
                animation.setFillAfter(true);

                //Initialise rotation of the compass
                imageView.startAnimation(animation);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}

