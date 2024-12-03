package com.example.breach;

import android.content.Intent;
import android.media.MediaPlayer;
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

    private MediaPlayer mediaPlayer;
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

        mediaPlayer = MediaPlayer.create(this, R.raw.breacher_end_screen);
        mediaPlayer.setLooping(true); // Optional: Loop the music
        mediaPlayer.start();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Maybe have the previous game's data saved
                startActivity(new Intent(EndActivity.this.getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}