package com.example.breach;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class OngoingActivity extends AppCompatActivity {
    private Button btnTimer, btnEndGame;
    private TextView txtTimer;
    private ListView listRoles, listLocationsLeft, listLocationsRight;

    // region String arrays
    private ArrayList<String>[] arrLocations = new ArrayList[] {
        new ArrayList<>(Arrays.asList("loc1", "role1", "role2", "role3", "role4")),
        new ArrayList<>(Arrays.asList("loc2", "role5", "role6", "role7", "role8")),
        new ArrayList<>(Arrays.asList("loc3", "role34", "role2", "role3", "role4")),
        new ArrayList<>(Arrays.asList("loc4", "role74", "role2", "role3", "role4")),
        new ArrayList<>(Arrays.asList("loc5", "role23", "role2", "role3", "role4")),
        new ArrayList<>(Arrays.asList("loc6", "role89", "role2", "role3", "role4")),
        new ArrayList<>(Arrays.asList("loc7", "role4", "role2", "role3", "role4"))
    };
    // endregion


    // region Intent stuff
    private Intent startScreenIntent = getIntent();

    // Ex: (in mainActivity) myIntent.putExtra("replace me!", 80000);
//    private int intentIntDuration = startScreenIntent.getIntExtra("replace me!", 0);

//    private int intentIntPlayers = startScreenIntent.getIntExtra("replace me!", 2);


    private Intent ongoingScreenIntent;
    private String exportedLocationName = "finalLocation";
    private String exportedPlayerRole = "playerRole";
    private String exportedReason = "intentReason";
    private String exportedReasonPlayer = "player";
    // endregion

    private boolean timerRunning = false;
    private long intentIntDuration = 1010101022;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);

        btnTimer = findViewById(R.id.button_timer);
        btnEndGame = findViewById(R.id.button_end_game);

        txtTimer = findViewById(R.id.textview_timer);

        listRoles = findViewById(R.id.listview_role);
        listLocationsLeft = findViewById(R.id.listview_location_left);
        listLocationsRight = findViewById(R.id.listview_location_right);

        ongoingScreenIntent = new Intent(this, EndActivity.class);


        // region main
        txtTimer.setText(formatMsToTime(intentIntDuration));
//        txtTimer.setText(formatMsToTime(intentIntDuration));

            // region location setting
        ArrayList<String> arrLeft = new ArrayList<>(), arrRight = new ArrayList<>();

        for (int i = 0; i < arrLocations.length; i++) {
            if (i % 2 == 0) {
                arrLeft.add(arrLocations[i].get(0));
            } else {
                arrRight.add(arrLocations[i].get(0));
            }
        }

//        Toast.makeText(this, "Left:" + arrLeft.toString(), Toast.LENGTH_SHORT).show();

        listLocationsLeft.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_location, arrLeft));
        listLocationsRight.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_location, arrRight));
            // endregion location




            // region roles
        int randLocationIndex = new Random().nextInt(arrLocations.length);

        int intentIntPlayers = 5;

        String[] arrPlayers = new String[intentIntPlayers];

        for (int i = 0; i < intentIntPlayers; i++) {
            arrPlayers[i] = "Player " + (i + 1);
        }

        // Removes the location name from the list of roles. Saves this location in the intent to be passed to EndActivity.
        ongoingScreenIntent.putExtra(exportedLocationName, arrLocations[randLocationIndex].remove(0));
        listRoles.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_role, arrPlayers));
            // endregion roles
        // endregion







        // region Button.onClickListeners()
        listRoles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // I'll just use the EndActivity since all we need the player to see is a location and role.
                ongoingScreenIntent.putExtra(exportedPlayerRole, arrLocations[randLocationIndex].remove(new Random().nextInt(arrLocations[randLocationIndex].size())));
                ongoingScreenIntent.putExtra(exportedReason, exportedReasonPlayer);

                // This toast shows that hitting the back button doesn't re-roll the randLocationIndex, so therefore the deletion of arrLocations is saved, therefore no two roles can be the same.
                Toast.makeText(OngoingActivity.this, ongoingScreenIntent.getStringExtra(exportedPlayerRole) + "    " + randLocationIndex, Toast.LENGTH_SHORT).show();
//                startActivity(ongoingScreenIntent);
            }
        });

        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    timer = new CountDownTimer(intentIntDuration, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            txtTimer.setText(formatMsToTime(millisUntilFinished));
                            // idea from https://stackoverflow.com/a/6469166
                            intentIntDuration = millisUntilFinished;
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                    timerRunning = true;
                    btnTimer.setText("Start Timer");
                } else {
                    try {
                        timer.cancel();
                        timerRunning = false;
                        btnTimer.setText("Pause Timer");
                    } catch (Exception e) {

                    }
                }
            }
        });
        // endregion Button
    }



    private String formatMsToTime(long intMs) {
        return String.format("%01d:%02d:%02d",
                (int) (intMs / 3600000),
                (int) (intMs % 3600000) / 60000,
                (int) (intMs % 60000) / 1000);
    }
}