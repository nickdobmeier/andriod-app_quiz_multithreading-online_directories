// Dobmeier

package com.utd.quizproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SecondActivity extends AppCompatActivity
{
    Quiz currentQuiz;                                                       // used by FragWrong
    private String userName;

    private TextView questionTextView;
    private TextView answerChoiceA;
    private TextView answerChoiceB;
    private TextView answerChoiceC;
    private TextView answerChoiceD;

    private short currentFragmentFlag = 1;                                  // should ony EVER store a value of 1, 2, or 3 (for fragAnswer, fragCorrect, and fragWrong)

    int currentQuestion = -1;                                               // keep track of where we are in the Quiz - used by FragWrong
    private int userScore = 0;                                              // keep track of how many questions the user answers correctly

    private HashMap<Integer, TextView> textViewAnswerChoiceHashMap;         // used to quickly deselect the previously selected TextView (avoids iterating through each TextView to determine which one was selected)
    private int currAnswerChoiceSelectedID = -1;                            // keep track of which TextView ID the user has currently selected

        // retrieves data members passed from the SecondActivity, and then place all 4 TextViews that will contain the answer choices in a HashMap
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Bundle b = getIntent().getExtras();
        currentQuiz = (Quiz) b.getSerializable("QUIZ_1");               // grab the passed Quiz under the ID "QUIZ_1"
        userName = b.getString("USER_NAME");

        questionTextView = findViewById(R.id.questionTitle1);
        answerChoiceA = findViewById(R.id.answerChoice1);
        answerChoiceB = findViewById(R.id.answerChoice2);
        answerChoiceC = findViewById(R.id.answerChoice3);
        answerChoiceD = findViewById(R.id.answerChoice4);


        textViewAnswerChoiceHashMap = new HashMap<>();                      // place all 4 answer choice TextViews in the HashMap
        textViewAnswerChoiceHashMap.put(answerChoiceA.getId(), answerChoiceA);
        textViewAnswerChoiceHashMap.put(answerChoiceB.getId(), answerChoiceB);
        textViewAnswerChoiceHashMap.put(answerChoiceC.getId(), answerChoiceC);
        textViewAnswerChoiceHashMap.put(answerChoiceD.getId(), answerChoiceD);

        moveToNextQuestion();
    }


        // update TextViews on screen with contents of the CURRENT quiz question, and deselect any answer choices that MAY be selected from a previous question
    public void moveToNextQuestion()
    {
        currentQuestion++;                                                  // currentQuestion is first initialized to -1

        if(currentQuestion == currentQuiz.getNumberOfQuestions())           // once have finished showing ALL questions in the Quiz, move to final activity
        {
            Intent intent = new Intent(this, ThirdActivity.class);                         // parameters: where coming from (this) & the filename of where going to (SecondActivity)
            intent.putExtra("QUIZ_1", currentQuiz);                   // pass the ArrayList, under the ID "QUIZ_LIST_1", to the new activity.
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("USER_SCORE", userScore);
            startActivity(intent);

            finish();                                                      // Android activities are stored in the activity stack.
            return;                                                        // any code after the finish() (and startActivity?) call will be run - we can just do a return after calling finish() to fix this
        }

        if(currAnswerChoiceSelectedID != -1)                                // if this is NOT the very first question in the quiz, remove the highlight from the answer-choice to the previous questions
        {
            TextView oldSelectedTextView = textViewAnswerChoiceHashMap.get(currAnswerChoiceSelectedID);  // instead of searching through ALL questions, go straight to the previously selected answer-choice
            oldSelectedTextView.setBackgroundColor(Color.WHITE);
            currAnswerChoiceSelectedID = -1;
        }


        if(currentFragmentFlag != 1)                                        // if this is NOT the very first question in the quiz, we need to remove the CORRECT or WRONG fragments
        {
            removeFragment();

            // reset fragmentFlag to 1 (in cases where the CORRECT or WRONG frags were just removed), so that the next addFragment() call properly adds FragAnswer
            currentFragmentFlag = 1;
        }
        addFragment();                                                      // display the "Answer" button back in the fragFrame (the Correct or Wrong fragments will have already been removed)


        String tempStr = currentQuiz.getQuizQuestion(currentQuestion).getQuestion();
        questionTextView.setText(tempStr);

            // updating the textValues of the TextView objects (not creating NEW objects) means they are still up to date when accessed from the HashMap
        tempStr = "a) " + currentQuiz.getQuizQuestion(currentQuestion).getAnswerChoice(0);
        answerChoiceA.setText(tempStr);

        tempStr = "b) " + currentQuiz.getQuizQuestion(currentQuestion).getAnswerChoice(1);
        answerChoiceB.setText(tempStr);

        tempStr = "c) " + currentQuiz.getQuizQuestion(currentQuestion).getAnswerChoice(2);
        answerChoiceC.setText(tempStr);

        tempStr = "d) " + currentQuiz.getQuizQuestion(currentQuestion).getAnswerChoice(3);
        answerChoiceD.setText(tempStr);
    }



        // when an answer choice TextView is selected, deselect any previously selected answer choice if needed, and then highlight the newly selected choice with CYAN
    public void textOnClick(View view)
    {
            // if an answer choice has NOT yet been selected, then no need to change a previously selected answer choice
        if(currAnswerChoiceSelectedID != -1) {
            TextView oldSelectedTextView = textViewAnswerChoiceHashMap.get(currAnswerChoiceSelectedID);     // instead of searching through ALL questions, go straight to the previously selected answer-choice
            oldSelectedTextView.setBackgroundColor(Color.WHITE);
        }

        TextView newSelectedTextView = (TextView) view;
        newSelectedTextView.setBackgroundColor(Color.CYAN);

        currAnswerChoiceSelectedID = newSelectedTextView.getId();
    }
    // does textOnClick() have to wait until moveToNextQuestion() finishes executing ?? **



        // display the answer button fragment in the fragFrame
    private void addFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // look into fragmentTransaction.replace() ******
        if(currentFragmentFlag == 1)
        {
            FragAnswer fragAnswer = FragAnswer.newInstance();
            fragmentTransaction.add(R.id.FragmentLayout1, fragAnswer).addToBackStack(null).commit();

        }else if(currentFragmentFlag == 2)
        {
            FragCorrect fragCorrect = FragCorrect.newInstance();
            fragmentTransaction.add(R.id.FragmentLayout1, fragCorrect).addToBackStack(null).commit();

        }else if(currentFragmentFlag == 3)
        {
            FragWrong fragWrong = FragWrong.newInstance();
            fragmentTransaction.add(R.id.FragmentLayout1, fragWrong).addToBackStack(null).commit();
        }
        // FLAG variable should ONLY EVER BE 1, 2, or 3
    }


        // REMOVE the answer button fragment, and replace with a new fragment, depending on whether the user got the answer RIGHT or WRONG
    public void setFragSequenceAfterAnswer()
    {
        if(currAnswerChoiceSelectedID == -1)    // if user has NOT selected an answer, do NOT allow them to continue yet
        {
            Toast.makeText(getApplicationContext(),"Must pick an answer",Toast.LENGTH_SHORT).show();        // prompt user that they must select an answer choice
            return;
        }

            // removes answer button fragment
        removeFragment();

        String comp = textViewAnswerChoiceHashMap.get(currAnswerChoiceSelectedID).getText().toString().substring(3);    // remove the "a) " appended to beginning of the answer choice
        if(comp.equals(currentQuiz.getQuizQuestion(currentQuestion).getCorrectChoice()) == true )                       // check if user got correct answer
        {
            userScore++;
            currentFragmentFlag = 2;
            addFragment();

        }else {
            currentFragmentFlag = 3;
            addFragment();
        }
    }

        // remove the current Fragment that is in the FragFrameLayout
    public void removeFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(currentFragmentFlag == 1)
        {
            FragAnswer fragAnswer = (FragAnswer) fragmentManager.findFragmentById(R.id.FragmentLayout1);
            fragmentTransaction.remove(fragAnswer).commit();

        }else if(currentFragmentFlag == 2)
        {
            FragCorrect fragCorrect = (FragCorrect) fragmentManager.findFragmentById(R.id.FragmentLayout1);
            fragmentTransaction.remove(fragCorrect).commit();


        }else if(currentFragmentFlag == 3)
        {
            FragWrong fragWrong = (FragWrong) fragmentManager.findFragmentById(R.id.FragmentLayout1);
            fragmentTransaction.remove(fragWrong).commit();
        }
        // FLAG variable should ONLY EVER BE 1, 2, or 3
    }

}



/*  the new solution seems to let the UI be much snappier on the emulator (for whatever reason - perhaps since CORRECT and WRONG fragments themselves are performing less calls to the SecondActivity?)

    // display the answer button fragment in the fragFrame
    private void setFragAnswer(){
        FragAnswer fragAnswer = FragAnswer.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.FragmentLayout1, fragAnswer).addToBackStack(null).commit();
    }


        // REMOVE the answer button fragment, and replace with a new fragment, depending on whether the user got the answer RIGHT or WRONG
    public void setFragSequenceAfterAnswer()
    {
            // removes answer button fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragAnswer fragAnswer = (FragAnswer) fragmentManager.findFragmentById(R.id.FragmentLayout1);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragAnswer).commit();


        fragmentManager = getSupportFragmentManager();      // reset fragmentManager variable before ADDING new fragment

        String comp = textViewAnswerChoiceHashMap.get(currAnswerChoiceSelectedID).getText().toString().substring(3);
        if(comp.equals(currentQuiz.getQuizQuestion(currentQuestion).getCorrectChoice()) == true )       // check if user got correct answer
        {
            userScore++;

            FragCorrect fragCorrect = FragCorrect.newInstance();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.FragmentLayout1, fragCorrect).addToBackStack(null).commit();
        }else {
            FragWrong fragWrong = FragWrong.newInstance();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.FragmentLayout1, fragWrong).addToBackStack(null).commit();
        }
    }

        // remove the CORRECT Answer button fragment
    public void removeCorrectFrag()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragCorrect fragCorrect = (FragCorrect) fragmentManager.findFragmentById(R.id.FragmentLayout1);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragCorrect).commit();
    }
        // remove the WRONG answer button & textView fragment
    public void removeWrongFrag()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragWrong fragWrong = (FragWrong) fragmentManager.findFragmentById(R.id.FragmentLayout1);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragWrong).commit();
    }


 */
