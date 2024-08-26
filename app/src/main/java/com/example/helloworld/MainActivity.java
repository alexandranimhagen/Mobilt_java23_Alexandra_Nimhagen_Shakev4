package com.example.helloworld;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            ConstraintLayout cl = findViewById(R.id.main);
            TextView tv = findViewById(R.id.label);
            tv.setText("Jag heter Alex!");
            tv.setTextSize(26);

            Button b = findViewById(R.id.b);
            Button b2 = new Button (MainActivity.this);
            cl.addView(b2);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Alex", "Click");
                }
            });

            b.setBackgroundColor(Color.rgb(21,96, 83));
            String text = "Hello World?";
            Log.println(Log.DEBUG,"Alex'","Hello World!");
            Toast.makeText(MainActivity.this, "Hello World", Toast.LENGTH_SHORT).show();


            return insets;
        });
    }
}