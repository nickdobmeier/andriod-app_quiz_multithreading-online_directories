// Written by Nicholas Dobmeier for CS 4301.001, assignment 2, starting February 25, 2021.
//        NetID: njd170130

package com.utd.quizproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity
{
    private Quiz currentQuiz;
    private String userName;
    private int userScore;

    private TextView textViewName;
    private TextView textViewScore;
    private TextView textViewQuizName;

        // retrieves data members passed from the SecondActivity, and then displays the username, score, and quiz name
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Bundle b = getIntent().getExtras();
        currentQuiz = (Quiz) b.getSerializable("QUIZ_1");      // grab the passed Quiz under the ID "QUIZ_1"
        userName = b.getString("USER_NAME");
        userScore = b.getInt("USER_SCORE");

        textViewName = findViewById(R.id.userName1);
        textViewScore = findViewById(R.id.userScore1);
        textViewQuizName = findViewById(R.id.quizName1);

        String tempStr = textViewName.getText().toString() + userName;
        textViewName.setText(tempStr);

        tempStr = textViewScore.getText().toString() + userScore + " / " + currentQuiz.getNumberOfQuestions();
        textViewScore.setText(tempStr);

        tempStr = textViewQuizName.getText().toString() + currentQuiz.getQuizName();
        textViewQuizName.setText(tempStr);
    }

        // transition back to the very first activity when button is pressed on-screen
    public void resetBtnOnClick(View view)
    {
        //Intent intent = new Intent(this, MainActivity.class);                         // parameters: where coming from (this) & the filename of where going to (MainActivity)
        //startActivity(intent);
        finish();                                                                       // Android activities are stored in the activity stack.
    }

}