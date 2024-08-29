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

        // Register sensor listeners only if sensors are available
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("SensorError", "Accelerometer not available.");
        }

        if (gyroscope != null) {
            sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("SensorError", "Gyroscope not available.");
        }

        if (lightSensor != null) {
            sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("SensorError", "Light sensor not available.");
        }
    }

    private void handleShakeEvent(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        Log.d("SensorData", "Accelerometer: x=" + x + ", y=" + y + ", z=" + z);

        // Beräkna accelerationsmängden
        float accelerationMagnitude = (float) Math.sqrt(x * x + y * y + z * z);
        Log.d("SensorData", "Acceleration Magnitude: " + accelerationMagnitude);

        Button myButton = findViewById(R.id.myButton);

        float shakeThreshold = 12.0f; // Sätt ett rimligt tröskelvärde
        if (accelerationMagnitude > shakeThreshold) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime;
                if (!gameStarted) {
                    Log.d("Game", "Starting game!");
                    startGame();
                } else {
                    Log.d("Game", "Shake detected, increasing score");
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

    private void updateScore() {
        TextView scoreText = findViewById(R.id.scoreText);
        String scoreString = getString(R.string.score_text, score);
        scoreText.setText(scoreString);
        Log.d("Game", "Current score: " + score);
    }


    private void handleGyroscopeEvent(SensorEvent event) {
        float rotationRate = event.values[2]; // Använd rotation runt z-axeln
        ImageView rotatingImage = findViewById(R.id.rotatingImage);
        float currentRotation = rotatingImage.getRotation();
        rotatingImage.setRotation(currentRotation + rotationRate * 10);
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

    // Start the game when a shake is detected
    private void startGame() {
        gameStarted = true;
        score = 0; // Nollställ poängen när spelet startar
        Toast.makeText(this, "Game started!", Toast.LENGTH_SHORT).show();
        Log.d("Game", "Game started!");
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
