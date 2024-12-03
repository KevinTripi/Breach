package com.example.breach;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout playersContainer;
    private Button addButton, removeButton, startButton;
    private EditText breacherCountInput, gameTimeInput;
    private SeekBar questionsSeekBar;
    private TextView questionsLabel;
    private MediaPlayer mediaPlayer;
    private int playerCount = 1;
    private static final int MAX_PLAYERS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playersContainer = findViewById(R.id.players_container);
        addButton = findViewById(R.id.button_add);
        removeButton = findViewById(R.id.button_remove);
        startButton = findViewById(R.id.start_button);
        breacherCountInput = findViewById(R.id.breacher_count_input);
        gameTimeInput = findViewById(R.id.game_time_input);
        questionsSeekBar = findViewById(R.id.questions_seekbar);
        questionsLabel = findViewById(R.id.questions_label);

        mediaPlayer = MediaPlayer.create(this, R.raw.breach_menu_theme);
        mediaPlayer.setLooping(true); // Optional: Loop the music
        mediaPlayer.start();



//        TextView locationValue = findViewById(R.id.location_value);
//        TextView breacherValue = findViewById(R.id.breacher_value);
//
//        String tempLocation = "Military base"; // Replace with whatever the location variable is called
//        String tempPlayer = "John Doe"; // Replace with player variable
//
//        locationValue.setText(tempLocation);
//        breacherValue.setText(tempPlayer);
      
      
        addButton.setOnClickListener(v -> addPlayer());
        removeButton.setOnClickListener(v -> removePlayer());
        questionsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                questionsLabel.setText("Questions: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        startButton.setOnClickListener(v -> {
            String breacherCount = breacherCountInput.getText().toString();
            String gameTime = gameTimeInput.getText().toString();
            if (breacherCount.isEmpty() || gameTime.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                int breachers = Integer.parseInt(breacherCount);
                int time = Integer.parseInt(gameTime);
//                Toast.makeText(this, "Game started with " + breachers + " breachers for " + time + " minutes.", Toast.LENGTH_LONG).show();

                Intent mainScreenIntent = new Intent(MainActivity.this.getApplicationContext(), OngoingActivity.class);
                mainScreenIntent.putExtra(getString(R.string.main_number_of_players), playerCount);
                mainScreenIntent.putExtra(getString(R.string.main_number_of_questions), questionsSeekBar.getProgress());
                mainScreenIntent.putExtra(getString(R.string.main_game_duration), (time * 60000L)); // Turns the input # of minutes into ms
                startActivity(mainScreenIntent);
            }
        });
    }

    private void addPlayer() {
        if (playerCount < MAX_PLAYERS) {
            playerCount++;
            TextView newPlayer = new TextView(this);
            newPlayer.setText("Player " + playerCount);
            newPlayer.setTextColor(getColor(R.color.primary_text_color));
            playersContainer.addView(newPlayer);
        } else {
            Toast.makeText(this, "Max players reached!", Toast.LENGTH_SHORT).show();
        }
    }

    private void removePlayer() {
        if (playerCount > 1) {
            playersContainer.removeViewAt(playersContainer.getChildCount() - 1);
            playerCount--;
        } else {
            Toast.makeText(this, "No players to remove!", Toast.LENGTH_SHORT).show();
        }
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