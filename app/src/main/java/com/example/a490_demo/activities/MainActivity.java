package com.example.a490_demo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.a490_demo.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = findViewById(R.id.button);
        buttonStart.setText("Begin");

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startVideoActivity();
            }
        });

    }


    public void startVideoActivity()
    {
        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
        startActivity(intent);
    }
}