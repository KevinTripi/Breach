package com.example.breach;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuestionActivity extends AppCompatActivity {

    private ProgressBar progBarTime;
    private TextView txtQuestion, txtSeekStart, txtSeekEnd;
    private Button btnSubmit;
    private CheckBox cbAnswer1, cbAnswer2, cbAnswer3, cbAnswer4;
    private SeekBar seekAnswer;

    private RadioGroup rgAnswer;
    private LinearLayout llInput, llSeekbar, llCheckbox;

    private int importedPlayerAmount;
//    private int importedPlayerAmount = 4;
    // Represents the answers each player inputs. The length is the amount of players in the game.
    private String[] answers = new String[importedPlayerAmount];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progBarTime = findViewById(R.id.progressbar_time_left);
        txtQuestion = findViewById(R.id.textview_question);
        btnSubmit = findViewById(R.id.button_submit);

        cbAnswer1 = findViewById(R.id.checkbox_1);
        cbAnswer2 = findViewById(R.id.checkbox_2);
        cbAnswer3 = findViewById(R.id.checkbox_3);
        cbAnswer4 = findViewById(R.id.checkbox_4);

        seekAnswer = findViewById(R.id.seekbar_answer);
        txtSeekEnd = findViewById(R.id.textview_seek_end);
        txtSeekStart = findViewById(R.id.textview_seek_start);

        llInput = findViewById(R.id.linearlayout_input);
        llCheckbox = findViewById(R.id.linearlayout_answer_checkbox);
        llSeekbar = findViewById(R.id.linearlayout_answer_seekbar);
        rgAnswer = findViewById(R.id.radiogroup_answer);



        importedPlayerAmount = getIntent().getIntExtra(getString(R.string.number_of_players), 1);
        Toast.makeText(this, "Number of players: " + importedPlayerAmount, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Number of players: " + importedPlayerAmount, Toast.LENGTH_SHORT).show();

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
            case "SeekBar":
                llSeekbar.setVisibility(View.VISIBLE);
                break;
            case "RadioButton":
                rgAnswer.setVisibility(View.VISIBLE);
                break;
            default:
                llCheckbox.setVisibility(View.VISIBLE);
                break;
        }
    }

    // Turns all user input types' visibility to gone.
    private void clearInputLayout() {
        cbAnswer1.setChecked(false);
        cbAnswer2.setChecked(false);
        cbAnswer3.setChecked(false);
        cbAnswer4.setChecked(false);
        rgAnswer.clearCheck();
        seekAnswer.setProgress(0);

        // from https://stackoverflow.com/a/8395263
        for (int i = 0; i < llInput.getChildCount(); i++) {
            llInput.getChildAt(i).setVisibility(View.GONE);
        }
    }
}