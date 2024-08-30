package com.example.uppg1shake;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private TextView accelData;
    private ImageView rotatingImage;
    private SeekBar speedControl;
    private Button resetButton;
    private float lastRotationZ = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI komponenter
        accelData = findViewById(R.id.accelerometer_data);
        rotatingImage = findViewById(R.id.rotating_image);
        speedControl = findViewById(R.id.speed_control);
        resetButton = findViewById(R.id.reset_button);

        // Sensorer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sätter skärmen till "stående" läge
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (gyroscope != null) {
            sensorManager.registerListener((SensorEventListener) this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelData.setText("X: " + x + " Y: " + y + " Z: " + z);
            // Threshold check för att visa Toast
            if (Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10) {
                Toast.makeText(this, "Shake detected!", Toast.LENGTH_SHORT).show();
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            rotatingImage.setRotation(z * 100);  // Rotera bild baserat på gyroskopets Z-värde
        }

        // Justera UI baserat på sensordata
        speedControl.setProgress((int) (Math.abs(y) * 10));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}