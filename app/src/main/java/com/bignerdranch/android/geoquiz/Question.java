package com.bignerdranch.android.geoquiz;

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private int mQuestionNum;


    public Question(int textResId, boolean answerTrue, int questionNum) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mQuestionNum = questionNum;

    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

    public int getQuestionNum() {
        return mQuestionNum;
    }

    public void setQuestionNum(int questionNum) {
        mQuestionNum = questionNum;
    }
}
