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
    private Button btnSubmit, btnResultExit;
    private CheckBox cbAnswer0, cbAnswer1, cbAnswer2, cbAnswer3;;
    private RadioButton rbAnswer0, rbAnswer1, rbAnswer2, rbAnswer3;
    private SeekBar seekAnswer;

    private RadioGroup rgAnswer;
    private LinearLayout llInput, llCheckbox, llResults, llQuestions;
    private RelativeLayout llSeekbar;

    private Intent fromOngoingIntent;
    private int importedPlayerAmount;

    // Represents the answers each player inputs. The length is the amount of players in the game.
    private String[] arrPlayerAnswers;
    private String questionText;
    private String[] arrOptions = new String[4];
    private int playerIndex = 0;
    private String questionType;

    private DBHelper myDbHelper;
    private SQLiteDatabase db;

    // Represents how long the timer runs for each person.
    private long timerLength = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        progBarTime = findViewById(R.id.progressbar_time_left);
        txtQuestion = findViewById(R.id.textview_question);
        btnSubmit = findViewById(R.id.button_submit);
        btnResultExit = findViewById(R.id.button_results_submit);
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
        llResults = findViewById(R.id.linearlayout_results);
        llQuestions = findViewById(R.id.linearlayout_questions);
        rgAnswer = findViewById(R.id.radiogroup_answer);



        // main
        llQuestions.setVisibility(View.VISIBLE);
        llResults.setVisibility(View.GONE);
        importedPlayerAmount = getIntent().getIntExtra(getString(R.string.main_number_of_players), 1);
        arrPlayerAnswers = new String[importedPlayerAmount];
//        Toast.makeText(this, "Number of players: " + importedPlayerAmount, Toast.LENGTH_SHORT).show();
        createDB();
        displayRandomQuestion();

        progBarTime.setMax((int) timerLength);
        CountDownTimer timer = new CountDownTimer(timerLength, 400) {
            @Override
            public void onTick(long millisUntilFinished) {
                progBarTime.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                progBarTime.setProgress(0);
                // TODO: Maybe have a random input be submitted for the player?
//                switch (new Random().nextInt(4)) {
//                    case 0:
//
//                }
            }
        }.start();


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
                    timer.cancel();
                    inflateResultScreen();
//                    Intent gotoResultIntent = new Intent(QuestionActivity.this.getApplicationContext(), ResultActivity.class);
//                    gotoResultIntent.putStringArrayListExtra(getString(R.string.question_array_options), new ArrayList<String>(Arrays.asList(arrOptions)));
//                    gotoResultIntent.putStringArrayListExtra(getString(R.string.question_array_answers), new ArrayList<String>(Arrays.asList(arrPlayerAnswers)));
//                    gotoResultIntent.putExtra(getString(R.string.question_type), questionType);
//                    gotoResultIntent.putExtra(getString(R.string.question_text), questionText);
//
////                    Log.i("beforeResult", questionType + "   " + gotoResultIntent.getStringExtra(getString(R.string.question_type)));
//                    startActivity(gotoResultIntent);
                } else {
                    txtPlayer.setText("Player " + (playerIndex + 1));

                    try {
                        timer.cancel();
                    } catch (Exception e) {

                    }
                    timer.start();
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

        btnResultExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void inflateResultScreen() {
        TextView[] txtOptions = new TextView[4];
        TextView[] txtOptionPlayers = new TextView[4];
        LinearLayout llOptions = findViewById(R.id.linearlayout_results_options);

        llQuestions.setVisibility(View.GONE);
        llResults.setVisibility(View.VISIBLE);

        for (int i = 0; i < txtOptions.length; i++) {
            txtOptions[i] = llOptions.getChildAt(i).findViewById(R.id.textview_option);
            txtOptionPlayers[i] = llOptions.getChildAt(i).findViewById(R.id.textview_player);
        }

        if (arrOptions != null && arrPlayerAnswers != null) {
            switch (questionType) {
                case "SB":
                    int total = Integer.parseInt(arrOptions[2]);
                    int increment = total / txtOptions.length;

//                    for (int i = 0; i < txtOptionPlayers.length; i++) {
//                        txtOptionPlayers[i].setText("");
//                    }

                    for (int i = 0; i < txtOptions.length; i++) {
                        int floor = increment * i;
                        int ceiling = increment * (i + 1);

                        txtOptions[i].setText(String.format("%d - %d:", floor, ceiling));
                        txtOptionPlayers[i].setText("");

                        for (int j = 0; j < arrPlayerAnswers.length; j++) {
                            int result = Integer.parseInt(arrPlayerAnswers[j]);
                            if (result < ceiling && result >= floor) {
                                txtOptionPlayers[i].setText(String.format("%s%s: %s\n", txtOptionPlayers[i].getText(), formatPlayerText(j + 1), arrPlayerAnswers[j]));
                            } else if (result >= total) {
                                txtOptionPlayers[txtOptionPlayers.length - 1].setText(String.format("%s%s: %s\n", txtOptionPlayers[txtOptionPlayers.length - 1].getText(), formatPlayerText(j + 1), arrPlayerAnswers[j]));
                            }
                        }
                    }
                    break;

                case "RB":
                    for (int i = 0; i < txtOptions.length; i++) {
                        String option = arrOptions[i];
                        String optionPlayerText = "";

                        txtOptions[i].setText(option);

                        for (int j = 0; j < arrPlayerAnswers.length; j++) {
                            if (arrPlayerAnswers[j].equals(option)) {
                                optionPlayerText += formatPlayerText(j + 1) + "\n";
                            }
                        }

                        txtOptionPlayers[i].setText(optionPlayerText);
                    }
                    break;

                default:
                    for (int i = 0; i < arrOptions.length; i++) {
                        txtOptions[i].setText(arrOptions[i]);
                        txtOptionPlayers[i].setText("");
                    }

                    for (int i = 0; i < arrPlayerAnswers.length; i++) {
                        String answer = arrPlayerAnswers[i];

                        for (int j = 0; j < answer.length(); j++) {
                            if (answer.charAt(j) == 'T') {
                                txtOptionPlayers[j].setText(txtOptionPlayers[j].getText() + formatPlayerText(i + 1) + "\n");
                            }

                        }
                    }
                    break;
            }
        }

    }


    private String formatPlayerText(int index) {
        return "P" + index;
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