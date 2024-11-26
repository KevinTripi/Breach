package com.example.breach;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        TextView locationValue = findViewById(R.id.location_value);
        TextView breacherValue = findViewById(R.id.breacher_value);

        String tempLocation = "Military base"; // Replace with whatever the location variable is called
        String tempPlayer = "John Doe"; // Replace with player variable

        locationValue.setText(tempLocation);
        breacherValue.setText(tempPlayer);

    }
}