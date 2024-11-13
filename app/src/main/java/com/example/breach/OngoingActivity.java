package com.example.breach;

import android.content.Intent;
import android.os.Bundle;
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
    private String intentLocationName = "finalLocation";
    // endregion

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
        txtTimer.setText(formatMsToTime(1010101010));
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

//        for (int i = 0; i < 5; i++) {
//
//        }

        // Removes the location name from the list of roles. Saves this loctaion in the intent to be passed to EndActivity.
        ongoingScreenIntent.putExtra(intentLocationName, arrLocations[randLocationIndex].remove(0));
        listRoles.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_role, arrLocations[randLocationIndex]));

//        for (int i = 0; i < intentIntPlayers; i++) {
//
//        }
            // endregion roles
        // endregion
    }



    private String formatMsToTime(int intMs) {
        return String.format("%01d:%02d:%02d",
                (int) (intMs / 3600000),
                (int) (intMs % 3600000) / 60000,
                (int) (intMs % 60000) / 1000);
    }
}