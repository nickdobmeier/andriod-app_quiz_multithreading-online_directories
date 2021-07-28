// Dobmeier

package com.utd.quizproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class FourthActivity extends AppCompatActivity implements adapterInterface
{
    private boolean doCreateQuiz = true;    // get overwritten when EDITing a quiz
    private Quiz currentQuiz;
    private int selectedQuestionIndex;

    private EditText fileNameEditText;
    private EditText quizNameEditText;
    private EditText questionEditText;
    private EditText [] answerChoices = new EditText[4];

    private RadioGroup radioGroup;
    private HashMap<Integer, Integer> radioHashMap;

    private RecyclerView recyclerViewQuestionList;


    // Call setResult in finish(). Calling setResult in onPause() causes framework to override the return and nothing gets returned to caller actvity
    @Override
    public void finish()
    {
        // save return for when the User may press the "back" button on bottom bar of Andriod UI
        Intent intentReturn = new Intent();
        intentReturn.putExtra("UPDATED_QUIZ", currentQuiz);
        setResult(789, intentReturn);

        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        Bundle b = getIntent().getExtras();
        if(b != null)                                               // if no Extras exist, we are CREATING a new quiz
        {
            doCreateQuiz = b.getBoolean("DO_CREATE_QUIZ");     // overwrite default value of true with FALSE when EDITING a quiz
            currentQuiz = (Quiz) b.getSerializable("QUIZ");
        }

        fileNameEditText = findViewById(R.id.fileEditText1);
        quizNameEditText = findViewById(R.id.quizNameEditText1);
        questionEditText = findViewById(R.id.questionEditText1);
        answerChoices[0] = findViewById(R.id.a1EditText1);
        answerChoices[1] = findViewById(R.id.a2EditText1);
        answerChoices[2] = findViewById(R.id.a3EditText1);
        answerChoices[3] = findViewById(R.id.a4EditText1);
        radioGroup = findViewById(R.id.radioGroup2);

        radioHashMap = new HashMap<>(4);
        radioHashMap.put(R.id.radioA1, 0);
        radioHashMap.put(R.id.radioA2, 1);
        radioHashMap.put(R.id.radioA3, 2);
        radioHashMap.put(R.id.radioA4, 3);

        recyclerViewQuestionList = findViewById(R.id.recyclerView2);

        selectedQuestionIndex = 0;  // by default the FIRST question in the quiz should be selected & displayed (provided EDITING a quiz)
        if(doCreateQuiz == false)
        {
            fileNameEditText.setText(currentQuiz.getFileName());    // only needs to be filled out once when editing a quiz, as user can NOT change this value
            fileNameEditText.setEnabled(false);                     // PREVENT this field from being modified by the user
            quizNameEditText.setText(currentQuiz.getQuizName());    // only needs to be set once, as it is NEVER cleared by calls to clearOnClick()

            fillOutQuestionFields();
            setRecyclerAdapter(selectedQuestionIndex);      // when creating a NEW quiz, there are no questions to go in the RecyclerView, so no point in setting up an adapter yet in that case
        }
    }


        // save quiz data, move to next question (or a new question that can be added if previously was on the last question in the list)
    public void saveOnClick(View view)
    {
        if(areAllFieldsFull() == false){
            Toast.makeText(getApplicationContext(),"Must fill in ALL fields",Toast.LENGTH_LONG).show();
            return;
        }
        if(doCreateQuiz == true && (fileNameEditText.getText().toString().startsWith("Quiz")==false || fileNameEditText.getText().toString().endsWith(".txt")==false) ){
            Toast.makeText(getApplicationContext(),"File name must start with \"Quiz\" and end with .txt",Toast.LENGTH_LONG).show();
            return;
        }

        if(currentQuiz == null){     // the FIRST time user clicks save when the quiz is new
            currentQuiz = new Quiz(quizNameEditText.getText().toString(), fileNameEditText.getText().toString());

        }else {                     // if HAS been initialized, then save what is typed in quiz name field
            currentQuiz.setQuizName(quizNameEditText.getText().toString());

            if(doCreateQuiz == true){   // when user is creating a new quiz, they ARE allowed to keep modifying the Filename field (even after first time)
                currentQuiz.setFileName(fileNameEditText.getText().toString());
            }
        }

        QQuestion currQuestion;

        if(selectedQuestionIndex >= currentQuiz.getNumberOfQuestions()) {
            currQuestion = new QQuestion(questionEditText.getText().toString());
            currQuestion.setCorrectChoiceIndex(radioHashMap.get(radioGroup.getCheckedRadioButtonId()));     // convert the radioID into the corresponding index (using hashmap)

            for(int i=0; i < 4; i++){
                currQuestion.addAnswer(answerChoices[i].getText().toString());
            }

            currentQuiz.addQuestion(currQuestion);

        }else{
                // updating the already existing Quiz question IN-PLACE
            currQuestion = currentQuiz.getQuizQuestion(selectedQuestionIndex);
            currQuestion.setQuestion(questionEditText.getText().toString());
            currQuestion.setCorrectChoiceIndex(radioHashMap.get(radioGroup.getCheckedRadioButtonId()));
            for(int i=0; i < 4; i++){
                currQuestion.setAnswerIndex(answerChoices[i].getText().toString(), i);
            }
        }
        // re-fresh recycler data on-screen (matters when a new question added OR when a specific question line is changed)
        setRecyclerAdapter(++selectedQuestionIndex);
        // ++ to move to NEXT question

        if(selectedQuestionIndex >= currentQuiz.getNumberOfQuestions()){
            clearOnClick(null);                               // when there is NO next question to display, simply CLEAR all fields from previous question (except filename and quiz name)
        }else{
            fillOutQuestionFields();                                // when there IS an another question, diplay it
        }
    }
        // make sure ALL fields in the form are filled out
    private boolean areAllFieldsFull()
    {
        if(fileNameEditText.length() == 0){     // if the field is EMPTY, return false
            return false;
        }

        if(quizNameEditText.length() == 0){
            return false;
        }

        if(questionEditText.length() == 0){
            return false;
        }

        for(int i=0; i < answerChoices.length; i++){
            if(answerChoices[i].length() == 0){
                return false;
            }
        }

        int isChecked = radioGroup.getCheckedRadioButtonId();
        if(isChecked == -1){        // getCheckedRadioButtonId() returns -1 when NOTHING in the radio group is checked
            return false;
        }

        return true;
    }



        // clear all data fields ( except filename, quiz name, and the recycler view )
    public void clearOnClick(View view)
    {
        questionEditText.getText().clear();

        for(int i=0; i<answerChoices.length; i++)   // clear all answer choices
        {
            answerChoices[i].getText().clear();
        }

        radioGroup.clearCheck();
    }


        // delete a question from the quiz, and update the recycler view
    public void deleteOnClick(View view)
    {
        if(currentQuiz == null){                                            // return if user has just created a new quiz and presses delete
            Toast.makeText(getApplicationContext(),"There are NO saved questions to delete",Toast.LENGTH_LONG).show();
            return;
        }
        if(currentQuiz.removeQuestion(selectedQuestionIndex) == false){     // return if the user is NOT currently selected on an existing question
            Toast.makeText(getApplicationContext(),"Must select a quiz to delete",Toast.LENGTH_LONG).show();
            return;
        }
        selectedQuestionIndex = currentQuiz.getNumberOfQuestions();     // set to numberOfQuestions so that (selectedQuestionIndex >= numberOfQuestions) in saveOnClick()
        setRecyclerAdapter(selectedQuestionIndex);
        clearOnClick(null);
    }


        // create array of Strings containing each question in the quiz
    private String [] createStrArr()
    {
        int numQuestions = currentQuiz.getNumberOfQuestions();
        String[] strArr = null;

        if(numQuestions > 0)
        {
            strArr = new String[numQuestions];

            for (int i = 0; i < numQuestions; i++) {
                strArr[i] = currentQuiz.getQuizQuestion(i).getQuestion();
            }
        }
        return strArr;
    }


        // hook up the RecyclerView object to the RecyclerAdapter
        // each call to this function creates a NEW adapter
    public void setRecyclerAdapter(int defaultFirstClick)
    {
        String [] quizStrNames = createStrArr();
        RecyclerAdapter<FourthActivity> recyclerAdapter1 = new RecyclerAdapter<FourthActivity>(quizStrNames, this, defaultFirstClick);    // RecyclerAdapter constructor takes an array of Quiz name strings as one of its arguments

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewQuestionList.setLayoutManager(layoutManager);
        recyclerViewQuestionList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewQuestionList.setAdapter(recyclerAdapter1);
    }

        // pull data from a Quiz and full in each field
    public void fillOutQuestionFields()
    {
        QQuestion question = currentQuiz.getQuizQuestion(selectedQuestionIndex);

        questionEditText.setText(question.getQuestion());

        for(int i=0; i<answerChoices.length; i++)
        {
            answerChoices[i].setText(question.getAnswerChoice(i));
        }

        // check what is currently the correct answer to this question
        switch(question.getCorrectChoiceIndex()){
            case 0:
                radioGroup.check(R.id.radioA1);
                break;
            case 1:
                radioGroup.check(R.id.radioA2);
                break;
            case 2:
                radioGroup.check(R.id.radioA3);
                break;
            case 3:
                radioGroup.check(R.id.radioA4);
                break;
        }
    }



    @Override       // implenet interface method
    public void setCurrentSelectedItem(int selection){
        this.selectedQuestionIndex = selection;
    }
}
