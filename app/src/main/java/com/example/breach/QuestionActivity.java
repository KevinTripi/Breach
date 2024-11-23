package com.example.breach;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

import javax.security.auth.login.LoginException;

public class QuestionActivity extends AppCompatActivity {

    private ProgressBar progBarTime;
    private TextView txtQuestion, txtSeekStart, txtSeekEnd;
    private Button btnSubmit;
    private CheckBox cbAnswer0, cbAnswer1, cbAnswer2, cbAnswer3;;
    private RadioButton rbAnswer0, rbAnswer1, rbAnswer2, rbAnswer3;
    private SeekBar seekAnswer;

    private RadioGroup rgAnswer;
    private LinearLayout llInput, llSeekbar, llCheckbox;

    private int importedPlayerAmount;
//    private int importedPlayerAmount = 4;
    // Represents the answers each player inputs. The length is the amount of players in the game.
    private String[] arrPlayerAnswers = new String[importedPlayerAmount];
    private String questionText;
    private String[] arrOptions = new String[4];

    private DBHelper myDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        progBarTime = findViewById(R.id.progressbar_time_left);
        txtQuestion = findViewById(R.id.textview_question);
        btnSubmit = findViewById(R.id.button_submit);

        cbAnswer0 = findViewById(R.id.checkbox_0);
        cbAnswer1 = findViewById(R.id.checkbox_1);
        cbAnswer2 = findViewById(R.id.checkbox_2);
        cbAnswer3 = findViewById(R.id.checkbox_3);

        rbAnswer0 = findViewById(R.id.radio_0);
        rbAnswer1 = findViewById(R.id.radio_1);
        rbAnswer2 = findViewById(R.id.radio_2);
        rbAnswer3 = findViewById(R.id.radio_3);

        seekAnswer = findViewById(R.id.seekbar_answer);
        txtSeekEnd = findViewById(R.id.textview_seek_end);
        txtSeekStart = findViewById(R.id.textview_seek_start);

        llInput = findViewById(R.id.linearlayout_input);
        llCheckbox = findViewById(R.id.linearlayout_answer_checkbox);
        llSeekbar = findViewById(R.id.linearlayout_answer_seekbar);
        rgAnswer = findViewById(R.id.radiogroup_answer);


        createDB();
//        getResult("SELECT * FROM Roles");

        importedPlayerAmount = getIntent().getIntExtra(getString(R.string.number_of_players), 1);
//        Toast.makeText(this, "Number of players: " + importedPlayerAmount, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Number of players: " + importedPlayerAmount, Toast.LENGTH_SHORT).show();

        displayRandomQuestion();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setActivityInputType("CheckBox");
            }
        });
    }

    // Depending on the argument, the LinearLayout with the user inputs will display one type of input.
    public void setActivityInputType(String inputType) {
        clearInputLayout();

        switch (inputType) {
            case "SB":
                llSeekbar.setVisibility(View.VISIBLE);
                txtSeekStart.setText(arrOptions[0]);
                txtSeekEnd.setText(arrOptions[1]);
                seekAnswer.setMax(Integer.parseInt(arrOptions[2]));
                break;
            case "RB":
                rgAnswer.setVisibility(View.VISIBLE);
                rbAnswer0.setText(arrOptions[0]);
                rbAnswer1.setText(arrOptions[1]);
                rbAnswer2.setText(arrOptions[2]);
                rbAnswer3.setText(arrOptions[3]);
                break;
            default:
                llCheckbox.setVisibility(View.VISIBLE);
                cbAnswer0.setText(arrOptions[0]);
                cbAnswer1.setText(arrOptions[1]);
                cbAnswer2.setText(arrOptions[2]);
                cbAnswer3.setText(arrOptions[3]);
                break;
        }

        txtQuestion.setText(questionText);
    }

    // Turns all user input types' visibility to gone.
    private void clearInputLayout() {
        cbAnswer1.setChecked(false);
        cbAnswer2.setChecked(false);
        cbAnswer3.setChecked(false);
        cbAnswer0.setChecked(false);
        rgAnswer.clearCheck();
        seekAnswer.setProgress(0);

        // from https://stackoverflow.com/a/8395263
        for (int i = 0; i < llInput.getChildCount(); i++) {
            llInput.getChildAt(i).setVisibility(View.GONE);
        }
    }

    public void displayRandomQuestion() {
        Cursor result = db.rawQuery("SELECT * FROM Questions", null);
        result.moveToFirst();

        int randInt = new Random().nextInt(result.getCount());
        Toast.makeText(this, "rand row: " + randInt, Toast.LENGTH_SHORT).show();

        result.moveToPosition(randInt);

//        Log.i("db", result.toString());

        questionText = result.getString(1);

        for (int i = 0; i < arrOptions.length; i++) {
            arrOptions[i] = result.getString(i + 2);
            Log.i("db", arrOptions[i] != null ? arrOptions[i] : "null");
        }

        setActivityInputType(result.getString(0));
    }


    // Database stuff
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