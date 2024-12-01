package com.example.breach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private TextView[] txtOptions = new TextView[4];
    private TextView[] txtOptionPlayers = new TextView[4];
    private Button btnExit;
    private Intent fromQuestionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnExit = findViewById(R.id.button_submit);
        LinearLayout llOptions = findViewById(R.id.linearlayout_options);

        for (int i = 0; i < txtOptions.length; i++) {
            txtOptions[i] = llOptions.getChildAt(i).findViewById(R.id.textview_option);
            txtOptionPlayers[i] = llOptions.getChildAt(i).findViewById(R.id.textview_player);
        }

        fromQuestionIntent = getIntent();
        ArrayList<String> arrOptions = fromQuestionIntent.getStringArrayListExtra(getString(R.string.question_array_options));
        ArrayList<String> arrAnswers = fromQuestionIntent.getStringArrayListExtra(getString(R.string.question_array_answers));

        if (arrOptions != null && arrAnswers != null) {
            switch (fromQuestionIntent.getStringExtra(getString(R.string.question_type))) {
                case "SB":
                    int total = Integer.parseInt(arrOptions.get(2));
                    int increment = total / txtOptions.length;

//                    for (int i = 0; i < txtOptionPlayers.length; i++) {
//                        txtOptionPlayers[i].setText("");
//                    }

                    for (int i = 0; i < txtOptions.length; i++) {
                        int floor = increment * i;
                        int ceiling = increment * (i + 1);

                        txtOptions[i].setText(String.format("%d - %d:", floor, ceiling));
                        txtOptionPlayers[i].setText("");

                        for (int j = 0; j < arrAnswers.size(); j++) {
                            int result = Integer.parseInt(arrAnswers.get(j));
                            if (result < ceiling && result >= floor) {
                                txtOptionPlayers[i].setText(String.format("%s%s: %s\n", txtOptionPlayers[i].getText(), formatPlayerText(j), arrAnswers.get(j)));
                            } else if (result >= total) {
                                txtOptionPlayers[txtOptionPlayers.length - 1].setText(String.format("%s%s: %s\n", txtOptionPlayers[txtOptionPlayers.length - 1].getText(), formatPlayerText(j), arrAnswers.get(j)));
                            }
                        }
                    }
                    break;

                case "RB":
                    for (int i = 0; i < txtOptions.length; i++) {
                        String option = arrOptions.get(i);
                        String optionPlayerText = "";

                        txtOptions[i].setText(option);

                        for (int j = 0; j < arrAnswers.size(); j++) {
                            if (arrAnswers.get(j).equals(option)) {
                                optionPlayerText += formatPlayerText(j + 1) + "\n";
                            }
                        }

                        txtOptionPlayers[i].setText(optionPlayerText);
                    }
                    break;

                default:
                    for (int i = 0; i < arrOptions.size(); i++) {
                        txtOptions[i].setText(arrOptions.get(i));
                        txtOptionPlayers[i].setText("");
                    }

                    for (int i = 0; i < arrAnswers.size(); i++) {
                        String answer = arrAnswers.get(i);

                        for (int j = 0; j < answer.length(); j++) {
                            if (answer.charAt(j) == 'T') {
                                txtOptionPlayers[j].setText(txtOptionPlayers[j].getText() + formatPlayerText(i + 1) + "\n");
                            }

                        }
                    }
                    break;
            }
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                startActivity(new Intent(ResultActivity.this.getApplicationContext(), OngoingActivity.class));
            }
        });
    }

    private String formatPlayerText(int index) {
        return "P" + index;
    }
}