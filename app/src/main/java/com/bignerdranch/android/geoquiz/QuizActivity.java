package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

public class QuizActivity extends AppCompatActivity {

    // System Variables
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final String KEY_CHEAT_LIMIT = "limit";
    private static final String KEY_IS_QUES_ANSWERED = "is_ques_answered";
    private static final String KEY_QUES_ANSWERED_LIST = "answered_list";
    private static final String KEY_SCORE = "score";
    private static final String KEY_HAS_SUBMIT = "has_submit";

    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int REQUEST_CODE_SUMMARY = 0;


    // Widgets
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mSubmitButton;
    private Button mCheatButton;
    private Button mResetButton;
    private Button mSumButton;
    private TextView mQuestionTextView;
    private LinearLayout statusContainer;

    // Question Object Array
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true, 1),
            new Question(R.string.question_oceans, false, 2),
            new Question(R.string.question_mideast, true, 3),
            new Question(R.string.question_africa, true, 4),
            new Question(R.string.question_americas, true, 5),
            new Question(R.string.question_asia, true, 6),
    };


    // ArrayList to keep track of the question number answered
    private ArrayList<Integer> mQuestionsAnswered = new ArrayList<>();


    private ArrayList<Button> buttons = new ArrayList<>();

    private int mCurrentIndex = 0;
    private int quizGrade = 0;
    private int cheatTimes = 0;
    private boolean mIsCheater;
    private boolean mIsQuestionAnswered;
    private boolean hasSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);


        // Dynamically generate status container
        statusContainer = findViewById(R.id.status_container);

        for (Question ques : mQuestionBank) {
            Button btn = new Button(this);
            btn.setId(ques.getQuestionNum());
            btn.setText(String.valueOf(ques.getQuestionNum()));
            btn.setClickable(false);
            btn.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            buttons.add(btn);
            statusContainer.addView(btn);
        }

        // Hide submit button
        mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setVisibility(View.INVISIBLE);

        // Save onCurrentIndex Variable into savedInstanceState
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            cheatTimes = savedInstanceState.getInt(KEY_CHEAT_LIMIT, 0);
            mIsQuestionAnswered = savedInstanceState.getBoolean(KEY_IS_QUES_ANSWERED, false);
            mQuestionsAnswered = savedInstanceState.getIntegerArrayList(KEY_QUES_ANSWERED_LIST);
            quizGrade = savedInstanceState.getInt(KEY_SCORE, 0);
            hasSubmitted = savedInstanceState.getBoolean(KEY_HAS_SUBMIT, false);
        }

        if (!mQuestionsAnswered.isEmpty()) {
            for (int i = 0; i < buttons.size(); i++) {
                for (int j = 0; j < mQuestionsAnswered.size(); j++) {
                    if (buttons.get(i).getText() == String.valueOf(mQuestionsAnswered.get(j))) {
                        buttons.get(i).setEnabled(false);
                    }
                }
            }
        }

        if (mCurrentIndex == mQuestionBank.length - 1) {
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setEnabled(true);
        }


        // Display question
        mQuestionTextView = findViewById(R.id.question_text_view);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);

        // TextView Methods
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == mQuestionBank.length - 2) {
                    mSubmitButton.setVisibility(View.VISIBLE);

                }
                if (mCurrentIndex < mQuestionBank.length - 1) {
                    mCurrentIndex = mCurrentIndex + 1;
                    updateQuestion();
                    if (!mQuestionsAnswered.contains(mQuestionBank[mCurrentIndex].getQuestionNum())) {
                        enableButtons();
                    }
                }

            }
        });

        // Button Methods
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                mIsQuestionAnswered = true;
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                mIsQuestionAnswered = true;
            }
        });

        // Check if questions are answered when change state
        if (!mIsQuestionAnswered) {
            enableButtons();
        } else {
            disableButtons();
        }


        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsQuestionAnswered = false;
                if (mCurrentIndex == mQuestionBank.length - 2) {
                    mSubmitButton.setVisibility(View.VISIBLE);

                }
                if (mCurrentIndex < mQuestionBank.length - 1) {
                    mIsCheater = false;
                    mCurrentIndex = mCurrentIndex + 1;
                    updateQuestion();
                    if (!mQuestionsAnswered.contains(mQuestionBank[mCurrentIndex].getQuestionNum())) {
                        enableButtons();
                    }
                }

            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1);
                if (mCurrentIndex <= -1) {
                    mCurrentIndex = 0;
                } else {
                    updateQuestion();
                    if (mQuestionsAnswered.contains(mQuestionBank[mCurrentIndex].getQuestionNum())) {
                        disableButtons();
                    } else {
                        enableButtons();
                    }
                    mSubmitButton.setVisibility(View.INVISIBLE);

                }


            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start cheats here
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mSumButton = findViewById(R.id.summary_button);
        mSumButton.setVisibility(View.INVISIBLE);
        mSumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SummaryActivity.newIntent(QuizActivity.this, mQuestionsAnswered.size(), quizGrade, cheatTimes);
                startActivityForResult(intent, REQUEST_CODE_SUMMARY);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswers();
            }
        });

        mResetButton = findViewById(R.id.reset_button);
        mResetButton.setVisibility(View.INVISIBLE);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetQuiz();
            }
        });

        // Check state for submission attempt
        if(hasSubmitted == true){
            mSubmitButton.setEnabled(false);
            mResetButton.setVisibility(View.VISIBLE);
            mSumButton.setVisibility(View.VISIBLE);
        }


    }


    // Function to get data from Cheat Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }


    // Override life cycle methods
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() Called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        savedInstanceState.putInt(KEY_CHEAT_LIMIT, cheatTimes);
        savedInstanceState.putBoolean(KEY_IS_QUES_ANSWERED, mIsQuestionAnswered);
        savedInstanceState.putIntegerArrayList(KEY_QUES_ANSWERED_LIST, mQuestionsAnswered);
        savedInstanceState.putInt(KEY_SCORE, quizGrade);
        savedInstanceState.putBoolean(KEY_HAS_SUBMIT, hasSubmitted);
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    // Application Functions
    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        checkCheatLimit();
    }

    private void disableButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private void enableButtons() {
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);


    }

    private void checkCheatLimit() {
        if (cheatTimes == 3) {
            mCheatButton.setEnabled(false);
        } else {
            return;
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        mQuestionsAnswered.add(mQuestionBank[mCurrentIndex].getQuestionNum());

        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgement_toast;
            cheatTimes += 1;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                quizGrade += 1;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Button getStatusButton = buttons.get(mCurrentIndex);
        getStatusButton.setEnabled(false);
        disableButtons();

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void submitAnswers() {
        hasSubmitted = true;

        String totalQuestions = String.valueOf(mQuestionBank.length);
        String grade = String.valueOf(quizGrade);
        String toastGrade = grade + "/" + totalQuestions;

        float floatGrade = quizGrade;
        float totalQues = mQuestionBank.length;

        float percentage = floatGrade/totalQues * 100;


        Toast.makeText(this, String.format("%.2f",percentage), Toast.LENGTH_SHORT).show();
        if(hasSubmitted == true){
            mSubmitButton.setEnabled(false);
            mResetButton.setVisibility(View.VISIBLE);
            mSumButton.setVisibility(View.VISIBLE);
        }

    }

    private void resetQuiz() {
        mCurrentIndex = 0;
        quizGrade = 0;
        mSubmitButton.setVisibility(View.INVISIBLE);
        mSubmitButton.setEnabled(true);
        mResetButton.setVisibility(View.INVISIBLE);
        mSumButton.setVisibility(View.INVISIBLE);
        enableButtons();
        updateQuestion();
        mQuestionsAnswered.clear();
        mIsQuestionAnswered = false;
        hasSubmitted = false;

        for (Button button : buttons) {
            button.setEnabled(true);
        }
    }
}
