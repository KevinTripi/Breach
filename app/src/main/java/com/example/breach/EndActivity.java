package com.example.breach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EndActivity extends AppCompatActivity {

    TextView txtLocation, txtBreachers;
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        txtBreachers = findViewById(R.id.textview_end_breachers);
        txtLocation = findViewById(R.id.textview_end_location);
        btnExit = findViewById(R.id.button_end_game);

        Intent fromOngoingIntent = getIntent();

        txtLocation.setText(fromOngoingIntent.getStringExtra(getString(R.string.location_name)));
        txtBreachers.setText("P" + (fromOngoingIntent.getIntExtra(getString(R.string.breacher_index), 0) + 1));

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Maybe have the previous game's data saved
                startActivity(new Intent(EndActivity.this.getApplicationContext(), MainActivity.class));
            }
        });
    }
}