package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SummaryActivity extends AppCompatActivity {

    private static final String EXTRA_TOTAL_QUES_ANSWERED =
            "com.bignerdranch.android.geoquiz.total_ques_answered";

    private static final String EXTRA_TOTAL_SCORE =
            "com.bignerdranch.android.geoquiz.total_score";

    private static final String EXTRA_TOTAL_CHEAT_ATTEMPT =
            "com.bignerdranch.android.geoquiz.total_cheat_attempt";

    private TextView totalQues;
    private TextView totalScore;
    private TextView totalCheats;


    public static Intent newIntent(Context packageContext, int totalQues, int totalScore, int cheatAttempt) {
        Intent intent = new Intent(packageContext, SummaryActivity.class);
        intent.putExtra(EXTRA_TOTAL_QUES_ANSWERED, totalQues);
        intent.putExtra(EXTRA_TOTAL_SCORE, totalScore);
        intent.putExtra(EXTRA_TOTAL_CHEAT_ATTEMPT, cheatAttempt);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        int getTotalQues = getIntent().getIntExtra(EXTRA_TOTAL_QUES_ANSWERED, 0);
        int getTotalScore = getIntent().getIntExtra(EXTRA_TOTAL_SCORE, 0);
        int getTotalCheats = getIntent().getIntExtra(EXTRA_TOTAL_CHEAT_ATTEMPT, 0);


        totalQues = findViewById(R.id.total_ques);
        totalQues.setText("Total Questions Answered:" + " " + String.valueOf(getTotalQues));

        totalScore = findViewById(R.id.total_score);
        totalScore.setText("Total Score:" + " " + String.valueOf(getTotalScore));

        totalCheats = findViewById(R.id.total_cheats);
        totalCheats.setText("Total Cheat Attempt(s):" + " " + String.valueOf(getTotalCheats));
    }
}
