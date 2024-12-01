package com.example.breach;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
    private ArrayList<String> arrLocation;
    // endregion


    // region Intent stuff
    private Intent mainScreenIntent;
    
    private long importedGameDuration;
    private int importedPlayerAmount;
    private int importedQuestionAmount;

    
    
    private Intent gotoEndIntent;
    
    private String exportedLocationName;
    private String exportedPlayerRole;
    private String exportedReason;
    private String exportedReasonPlayer;
    private String exportedPlayerAmount;
    // endregion

    private boolean timerRunning = false;
//    private long importedGameDuration = 6000;
    private CountDownTimer timer;

    // The timer checks pauseGameIndex / totalGameTime every tick. On trigger, decrements pauseGameIndex.
    // Ex: 4 questions for a 5 min game, timer is checking (4 / 5 min), then (3 / 5 min), ...
    private long totalGameTime;
    private int pauseGameIndex;

    private DBHelper myDbHelper;
    private SQLiteDatabase db;

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

        mainScreenIntent = getIntent();
        importedGameDuration = mainScreenIntent.getLongExtra(getString(R.string.main_game_duration), 6000);
        importedPlayerAmount = mainScreenIntent.getIntExtra(getString(R.string.main_number_of_players), 1);
        importedQuestionAmount = mainScreenIntent.getIntExtra(getString(R.string.main_number_of_questions), 1);

        totalGameTime = importedGameDuration;
        pauseGameIndex = importedQuestionAmount - 1;

        gotoEndIntent = new Intent(this, EndActivity.class);
        exportedLocationName = getString(R.string.location_name);
        exportedPlayerRole = getString(R.string.player_role);
        exportedReason = getString(R.string.reason);
        exportedReasonPlayer = getString(R.string.reason_player);
        exportedPlayerAmount = getString(R.string.main_number_of_players);


        // region main
        txtTimer.setText(formatMsToTime(importedGameDuration));
//        txtTimer.setText(formatMsToTime(importedGameDuration));

            // region location setting
        createDB();
        Cursor curLocations = db.rawQuery("SELECT * FROM Locations", null);
        curLocations.moveToFirst();

        ArrayList<String> arrLeft = new ArrayList<>(), arrRight = new ArrayList<>();

        do {
            if (curLocations.getPosition() % 2 == 0) {
                arrLeft.add(curLocations.getString(0));
            } else {
                arrRight.add(curLocations.getString(0));
            }
        } while (curLocations.moveToNext());

//        Toast.makeText(this, "Left:" + arrLeft.toString(), Toast.LENGTH_SHORT).show();

        listLocationsLeft.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_location, arrLeft));
        listLocationsRight.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_location, arrRight));
            // endregion location




            // region roles
        arrLocation = getRandomLocationAndRoles();
        String[] arrPlayers = new String[importedPlayerAmount];

        for (int i = 0; i < importedPlayerAmount; i++) {
            arrPlayers[i] = "Player " + (i + 1);
        }

        // Removes the location name from the list of roles. Saves this location in the intent to be passed to EndActivity.
        gotoEndIntent.putExtra(exportedLocationName, arrLocation.remove(0));
        listRoles.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_role, arrPlayers));

        gotoEndIntent.putExtra(getString(R.string.breacher_name), "steve");
            // endregion roles

        int temp = pauseGameIndex;
        Log.i("Time Intervals", String.format("pauseGameIndex: %d importedQuestionAmount: %d totalGameTime: %d", temp, importedQuestionAmount, totalGameTime));
        while (temp >= 0) {
            Log.i("Time Intervals", "Question at: " + formatMsToTime((long)(((double) temp-- / importedQuestionAmount) * totalGameTime) + 500));
        }
        // endregion main







        // region Button.onClickListeners()
        listRoles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // I'll just use the EndActivity since all we need the player to see is a location and role.
//                gotoEndIntent.putExtra(exportedPlayerRole, arrLocation.remove(new Random().nextInt(arrLocation.size())));
//                gotoEndIntent.putExtra(exportedReason, exportedReasonPlayer);

                // This toast shows that hitting the back button doesn't re-roll the randLocationIndex, so therefore the deletion of arrLocations is saved, therefore no two roles can be the same.
//                Toast.makeText(OngoingActivity.this, gotoEndIntent.getStringExtra(exportedPlayerRole), Toast.LENGTH_SHORT).show();
                Toast.makeText(OngoingActivity.this, arrLocation.get(new Random().nextInt(arrLocation.size())) + "", Toast.LENGTH_SHORT).show();
//                startActivity(gotoEndIntent);
            }
        });

        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    timerRunning = true;
                    btnTimer.setText("Pause Timer");

                    timer = new CountDownTimer(importedGameDuration, 500) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            txtTimer.setText(formatMsToTime(millisUntilFinished));
//                            txtTimer.setText(millisUntilFinished + "");

                            // idea from https://stackoverflow.com/a/6469166
                            importedGameDuration = millisUntilFinished;

                            if (millisUntilFinished <= (((double) pauseGameIndex / importedQuestionAmount) * totalGameTime) + 500) {
                                pauseGameIndex--;
                                Log.i("TimerPlease", String.format("index: %d question am: %d totalTime %.0f output: %.0f", pauseGameIndex, importedQuestionAmount, (float) totalGameTime, (float) (((double) pauseGameIndex / importedQuestionAmount) * totalGameTime)));
                                btnTimer.performClick();
                                Intent gotoQuestionIntent = new Intent(OngoingActivity.this.getApplicationContext(), QuestionActivity.class);
                                gotoQuestionIntent.putExtra(exportedPlayerAmount, importedPlayerAmount);
                                startActivity(gotoQuestionIntent);
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                } else {
                    try {
                        timer.cancel();
                        timerRunning = false;
                        btnTimer.setText("Start Timer");
                    } catch (Exception e) {

                    }
                }
            }
        });

        btnEndGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerRunning = true;
                btnTimer.performClick();
                startActivity(gotoEndIntent);
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

    // Database stuff
    public ArrayList<String> getRandomLocationAndRoles() {
        Cursor result = db.rawQuery("SELECT * FROM Locations", null);
        ArrayList<String> arrReturn = new ArrayList<>();
        int randInt = new Random().nextInt(result.getCount());
        Log.i("Ongoing db", "randInt: " + randInt);
//
//        result.moveToFirst();
//        do {
//            Log.i("Ongoing db", String.format("Index: %d   Name: %s   RolesId: %d", result.getPosition(), result.getString(0), result.getInt(1)));
//        } while (result.moveToNext());

        result.moveToPosition(randInt);
        arrReturn.add(result.getString(0)); // Adds the location name to the return array

        Cursor result2 = db.rawQuery("SELECT * FROM Roles WHERE Id = " + result.getInt(1), null);
        result2.moveToFirst();

        for (int i = 1; i < result2.getColumnCount(); i++) { // i = 1 since result2[1] = Id
            arrReturn.add(result2.getString(i));
        }

        Log.i("Ongoing db", Arrays.toString(arrReturn.toArray()));
        return arrReturn;
    }

    public void createDB() {
        myDbHelper = new DBHelper(this);

        try {
            myDbHelper.createDatabase();
//            myDbHelper.copyDatabaseFromAssets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            myDbHelper.openDatabase();
        } catch (SQLiteException e) {
            throw new RuntimeException(e);
        }

        db = myDbHelper.getWritableDatabase();
    }

    public void getResult(String query) {

        Cursor result = db.rawQuery(query, null);
        result.moveToFirst();

        do {
//            Log.i("db", result.getString(1));
        } while (result.moveToNext());

    }
}