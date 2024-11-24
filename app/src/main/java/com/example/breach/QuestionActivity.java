package com.example.breach;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    private ProgressBar progBarTime;
    private TextView txtQuestion, txtSeekStart, txtSeekEnd, txtPlayer, txtSeekProgress;
    private Button btnSubmit;
    private CheckBox cbAnswer0, cbAnswer1, cbAnswer2, cbAnswer3;;
    private RadioButton rbAnswer0, rbAnswer1, rbAnswer2, rbAnswer3;
    private SeekBar seekAnswer;

    private RadioGroup rgAnswer;
    private LinearLayout llInput, llCheckbox;
    private RelativeLayout llSeekbar;

    private int importedPlayerAmount = 4;
    // Represents the answers each player inputs. The length is the amount of players in the game.
    private String[] arrPlayerAnswers = new String[importedPlayerAmount];
    private String questionText;
    private String[] arrOptions = new String[4];
    private int playerIndex = 0;
    private String questionType;

    private DBHelper myDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        progBarTime = findViewById(R.id.progressbar_time_left);
        txtQuestion = findViewById(R.id.textview_question);
        btnSubmit = findViewById(R.id.button_submit);
        txtPlayer = findViewById(R.id.textview_player);

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
        txtSeekProgress = findViewById(R.id.textview_seek_progress);

        llInput = findViewById(R.id.linearlayout_input);
        llCheckbox = findViewById(R.id.linearlayout_answer_checkbox);
        llSeekbar = findViewById(R.id.linearlayout_answer_seekbar);
        rgAnswer = findViewById(R.id.radiogroup_answer);



        // main
        importedPlayerAmount = getIntent().getIntExtra(getString(R.string.number_of_players), 1);
//        Toast.makeText(this, "Number of players: " + importedPlayerAmount, Toast.LENGTH_SHORT).show();
        createDB();
        displayRandomQuestion();
        startTimer(10000);


        // region onClickListeners()
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerIndex >= arrPlayerAnswers.length) {
                    return;
                }

                switch (questionType) {
                    case "SB":
                        arrPlayerAnswers[playerIndex] = String.valueOf(seekAnswer.getProgress());
                        break;

                    case "RB":
                        RadioButton rbChecked;
                        if (rbAnswer0.isChecked()) {
                            rbChecked = rbAnswer0;
                        } else if (rbAnswer1.isChecked()) {
                            rbChecked = rbAnswer1;
                        } else if (rbAnswer2.isChecked()) {
                            rbChecked = rbAnswer2;
                        } else if (rbAnswer3.isChecked()) {
                            rbChecked = rbAnswer3;
                        } else {
                            Toast.makeText(QuestionActivity.this, "Please select an option!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        arrPlayerAnswers[playerIndex] = rbChecked.getText().toString();
                        break;

                    default:
                        String ret = "";
                        ret += cbAnswer0.isChecked() ? "T" : "F";
                        ret += cbAnswer1.isChecked() ? "T" : "F";
                        ret += cbAnswer2.isChecked() ? "T" : "F";
                        ret += cbAnswer3.isChecked() ? "T" : "F";
                        arrPlayerAnswers[playerIndex] = ret;
                        break;
                }
                setActivityInputType();
                playerIndex++;
                Log.i("Answers", "arrPlayerAnswers: " + Arrays.toString(arrPlayerAnswers));

                if (playerIndex >= arrPlayerAnswers.length) {
                    Intent resultScreenIntent = new Intent(QuestionActivity.this.getApplicationContext(), ResultActivity.class);
                    resultScreenIntent.putStringArrayListExtra(getString(R.string.question_array_options), new ArrayList<String>(Arrays.asList(arrOptions)));
                    resultScreenIntent.putStringArrayListExtra(getString(R.string.question_array_answers), new ArrayList<String>(Arrays.asList(arrPlayerAnswers)));
                    resultScreenIntent.putExtra(getString(R.string.question_type), questionType);
                    resultScreenIntent.putExtra(getString(R.string.question_text), questionText);
                    Log.i("beforeResult", questionType + "   " + resultScreenIntent.getStringExtra(getString(R.string.question_type)));
                    startActivity(resultScreenIntent);
                    return;
                } else {
                    txtPlayer.setText("Player " + (playerIndex + 1));
                }
            }
        });
        // endregion onClickListeners()

        seekAnswer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtSeekProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void startTimer(long ms) {
        progBarTime.setMax((int) ms);
        new CountDownTimer(ms, 400) {
            @Override
            public void onTick(long millisUntilFinished) {
                progBarTime.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                progBarTime.setProgress(0);
                // Maybe have a random input be submitted for the player?
//                switch (new Random().nextInt(4)) {
//                    case 0:
//
//                }
            }
        }.start();
    }

    // Depending on the argument, the LinearLayout with the user inputs will display one type of input.
    public void setActivityInputType() {
        clearInputLayout();

        if (questionType.isEmpty()) return;

        switch (questionType) {
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
//        Toast.makeText(this, "rand row: " + randInt, Toast.LENGTH_SHORT).show();

        result.moveToPosition(randInt);

//        Log.i("db", result.toString());

        questionText = result.getString(1);

        for (int i = 0; i < arrOptions.length; i++) {
            arrOptions[i] = result.getString(i + 2);
//            Log.i("db", arrOptions[i] != null ? arrOptions[i] : "null");
        }

        questionType = result.getString(0);
        setActivityInputType();
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