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
import android.widget.ProgressBar;
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
    private int score = 0; // Score counter

    // ProgressBars for visualizing sensor data
    private ProgressBar progressBarX, progressBarY, progressBarZ;
    private ProgressBar gyroProgressBarX, gyroProgressBarY, gyroProgressBarZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Initialize ProgressBars for accelerometer and gyroscope data
        progressBarX = findViewById(R.id.progressBarX);
        progressBarY = findViewById(R.id.progressBarY);
        progressBarZ = findViewById(R.id.progressBarZ);
        gyroProgressBarX = findViewById(R.id.gyroProgressBarX);
        gyroProgressBarY = findViewById(R.id.gyroProgressBarY);
        gyroProgressBarZ = findViewById(R.id.gyroProgressBarZ);

        // Define the sensor event listener to handle sensor data
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Check which sensor triggered the event and handle accordingly
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
                // Not used
            }
        };

        // Register listeners for the sensors if available, else log an error
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

        // Add Click Listener to Button to start or reset the game
        Button myButton = findViewById(R.id.myButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameStarted) {
                    gameStarted = false;
                    score = 0; // Reset score
                    updateScore();
                    Toast.makeText(MainActivity.this, "Game reset!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Start the game by shaking!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleShakeEvent(SensorEvent event) {
        // Get acceleration values from the accelerometer
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Update accelerometer ProgressBars based on acceleration values
        progressBarX.setProgress((int) Math.abs(x));
        progressBarY.setProgress((int) Math.abs(y));
        progressBarZ.setProgress((int) Math.abs(z));

        // Calculate acceleration magnitude and log it
        float accelerationMagnitude = (float) Math.sqrt(x * x + y * y + z * z);
        Log.d("SensorData", String.format(Locale.getDefault(), "Acceleration Magnitude: %.2f", accelerationMagnitude));

        // Check if the shake threshold is exceeded
        int shakeThreshold = getResources().getInteger(R.integer.shake_threshold);
        if (accelerationMagnitude > shakeThreshold) {
            long currentTime = System.currentTimeMillis();
            // Check if enough time has passed since the last shake
            if (currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime;
                if (!gameStarted) {
                    // Start the game if not started yet
                    Log.d("Game", "Starting game!");
                    startGame();
                } else {
                    // Increase score if game is already started and shake is detected
                    Log.d("Game", getString(R.string.shake_detected));
                    score++; // Increase score when a shake is detected
                    updateScore(); // Update score display
                }
            }
        }
    }

    private void handleGyroscopeEvent(SensorEvent event) {
        // Get rotation values from the gyroscope
        float rotationX = event.values[0];
        float rotationY = event.values[1];
        float rotationZ = event.values[2];

        // Update gyroscope ProgressBars based on rotation values
        gyroProgressBarX.setProgress((int) Math.abs(rotationX));
        gyroProgressBarY.setProgress((int) Math.abs(rotationY));
        gyroProgressBarZ.setProgress((int) Math.abs(rotationZ));

        // Rotate an image view based on gyroscope data
        ImageView rotatingImage = findViewById(R.id.rotatingImage);
        rotatingImage.setRotationX(rotatingImage.getRotationX() + rotationX * 5);
        rotatingImage.setRotationY(rotatingImage.getRotationY() + rotationY * 5);
        rotatingImage.setRotation(rotatingImage.getRotation() + rotationZ * 5);
    }

    private void handleLightSensorEvent(SensorEvent event) {
        // Get light level from the light sensor
        float lightLevel = event.values[0];
        // Change layout background color based on light level
        View layout = findViewById(R.id.mainLayout);
        if (lightLevel < 10) {
            layout.setBackgroundColor(Color.BLACK);
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }
    }

    private void updateScore() {
        // Update the score display
        TextView scoreText = findViewById(R.id.scoreText);
        scoreText.setText(String.format(Locale.getDefault(), getString(R.string.score_text), score));
        Log.d("Game", "Current score: " + score);
    }

    private void startGame() {
        // Start the game and reset the score
        gameStarted = true;
        score = 0; // Reset score when the game starts
        Toast.makeText(this, getString(R.string.game_started), Toast.LENGTH_SHORT).show();
        Log.d("Game", getString(R.string.game_started));
        updateScore(); // Display score when the game starts
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listeners again when the app is resumed
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listeners to save battery when the app is paused
        sensorManager.unregisterListener(sensorEventListener);
    }
}
