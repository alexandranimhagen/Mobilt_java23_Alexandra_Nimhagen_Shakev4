package com.example.helloworld;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor lightSensor;
    private SensorEventListener sensorEventListener;
    private boolean gameStarted = false;
    private long lastShakeTime = 0;
    private int score = 0; // Poängräknare

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Define the sensor event listener
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    handleShakeEvent(event);
                } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    handleGyroscopeEvent(event);
                } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    handleLightSensorEvent(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Not used in this app
            }
        };

        // Register sensor listeners
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("SensorError", getString(R.string.sensor_error_accelerometer));
        }

        if (gyroscope != null) {
            sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("SensorError", getString(R.string.sensor_error_gyroscope));
        }

        if (lightSensor != null) {
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("SensorError", getString(R.string.sensor_error_light));
        }
    }

    private void handleShakeEvent(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Visa accelerometerdata
        TextView accelerometerData = findViewById(R.id.accelerometerData);
        accelerometerData.setText(String.format(Locale.getDefault(), getString(R.string.acceleration_magnitude), x, y, z));

        // Beräkna accelerationsmängden
        float accelerationMagnitude = (float) Math.sqrt(x * x + y * y + z * z);
        Log.d("SensorData", String.format(Locale.getDefault(), "Acceleration Magnitude: %.2f", accelerationMagnitude));

        Button myButton = findViewById(R.id.myButton);

        int shakeThreshold = getResources().getInteger(R.integer.shake_threshold);
        if (accelerationMagnitude > shakeThreshold) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime;
                if (!gameStarted) {
                    Log.d("Game", "Starting game!");
                    startGame();
                } else {
                    Log.d("Game", getString(R.string.shake_detected));
                    score++; // Öka poängen när en skakning upptäcks
                    updateScore(); // Uppdatera poängvisningen
                }
            }
        }

        // Ändra färg på knappen beroende på accelerometerdata
        if (x > 5) {
            myButton.setBackgroundColor(Color.RED);
        } else {
            myButton.setBackgroundColor(Color.GREEN);
        }
    }

    private void handleGyroscopeEvent(SensorEvent event) {
        float rotationX = event.values[0];
        float rotationY = event.values[1];
        float rotationZ = event.values[2];

        // Visa gyroskopdata
        TextView gyroscopeData = findViewById(R.id.gyroscopeData);
        gyroscopeData.setText(String.format(Locale.getDefault(), "Gyroscope: x=%.2f, y=%.2f, z=%.2f", rotationX, rotationY, rotationZ));

        // Hantera rotationen av en bild
        ImageView rotatingImage = findViewById(R.id.rotatingImage);
        float currentRotation = rotatingImage.getRotation();
        rotatingImage.setRotation(currentRotation + rotationZ * 10);
    }

    private void handleLightSensorEvent(SensorEvent event) {
        float lightLevel = event.values[0];
        // Ändra layoutens bakgrund baserat på ljusnivån
        View layout = findViewById(R.id.mainLayout);
        if (lightLevel < 10) {
            layout.setBackgroundColor(Color.BLACK);
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }
    }

    private void updateScore() {
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText(String.format(Locale.getDefault(), getString(R.string.score_text), score));
        Log.d("Game", "Current score: " + score);
    }

    private void startGame() {
        gameStarted = true;
        score = 0; // Nollställ poängen när spelet startar
        Toast.makeText(this, getString(R.string.game_started), Toast.LENGTH_SHORT).show();
        Log.d("Game", getString(R.string.game_started));
        updateScore(); // Visa poängen när spelet startar
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
