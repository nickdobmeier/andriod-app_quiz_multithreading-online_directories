// Dobmeier

package com.utd.quizproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements adapterInterface
{
    private int selectedItemMainAct = -1;           // by default NONE of the items should be selected in the beginning. Should stay in sync with the Adapter's version of the variable
    private FileIO fileIO;                          // container object to perform all input/output and store Quiz data

    private RecyclerView recyclerViewQuizList;
    private EditText editText1;

    private String userInputName;                   // used to store the user's name

        // variables added for 2nd phase of project
    private ArrayList<String> onlineQuizNames = null;
    private boolean isOnlineSelected = false;
    private final String baseURL = "https://personal.utdallas.edu/~john.cole/Data/";
    RadioGroup radioGroup;

        // 3rd phase
    Button createButton;
    Button editButton;
    boolean isNameFieldInvalid = true;    // at start of program, no name is filled in


        // upon program load, local quizzes are read from the app's local file directory, and View items that will be needed are located using findViewById()
    @Override
    protected void onCreate(Bundle savedInstanceState)          // onCreate() is called by the FRAMEWORK when your program is loaded. So therefore the framework passes the savedInstanceState object
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                 // specific items in the layout can only be located with findViewById() after this is called

        fileIO = new FileIO(getFilesDir());                     // Perform all file i/o needed for program. Pass the necessary file directory to the constructor


        editText1 = findViewById(R.id.getName1);                // find specific EditText View   (sub-classes of View class: Button, TextView, EditText, etc...)
        recyclerViewQuizList = findViewById(R.id.recyclerView1);
        radioGroup = findViewById(R.id.radioGroup1);


        if(fileIO.doQuizzesExist() == true)                    // if no LOCAL quizzes were found, do NOT set the recycler Adapter, instead display an error toast message
        {
            setRecyclerAdapter(fileIO.getQuizNamesString());        // initiate & sync the RecyclerAdapter. Each list item in the RecyclerView will store a String of the Quiz name
        }else{
            Toast.makeText(getApplicationContext(),"No LOCAL quizzes found",Toast.LENGTH_LONG).show();
        }

        createButton = findViewById(R.id.button2);  // both buttons are INVISIBLE by default
        editButton = findViewById(R.id.button3);
        editText1.addTextChangedListener(new ListenerOnTextChange(createButton, editButton));
    }



        // when user attempts to go to a quiz, verify they have input name and made a selection before proceeding. Then, if online is selected, fetch that specific quiz from the server
    public void buttonOnClick(View view)
    {
        userInputName = editText1.getText().toString();

        if( (userInputName.length() > 0) && (selectedItemMainAct != -1) )   // user CANNOT proceed unless a name is filled in AND a list item is selected
        {
                // Intent is used for inter-activity communication
            Intent intent = new Intent(this, SecondActivity.class);                         // parameters: where coming from (this) & the filename of where going to (SecondActivity)
            intent.putExtra("USER_NAME", userInputName);


            if(isOnlineSelected == true)
            {
                Quiz newOnlineQuiz = new Quiz();
                intent.putExtra("QUIZ_1", newOnlineQuiz);                                               // pass the Quiz object, meanwhile separate thread will update its contents

                Toast.makeText(getApplicationContext(),"fetching ONLINE quiz",Toast.LENGTH_SHORT).show();

                AsyncQuizTask asyncQuizTask = new AsyncQuizTask(newOnlineQuiz)
                {
                        protected void onPostExecute(Quiz quizA)
                        {
                                // do NOT go to next activity if the Quiz has NO questions - which can occur if there was
                                //     an error at any point during the separate thread OR if the quiz on the server actually had NO questions
                            if(quizA.getNumberOfQuestions() != 0){
                                startActivity(intent);                                                          // once separate thread is FINISHED, start new activity
                            }else{
                                Toast.makeText(getApplicationContext(),"could NOT get that ONLINE quiz",Toast.LENGTH_LONG).show();
                            }
                        }
                };
                String quizFile = baseURL + onlineQuizNames.get(selectedItemMainAct);
                asyncQuizTask.execute(quizFile);

            }else{
                intent.putExtra("QUIZ_1", fileIO.getQuizList().get(selectedItemMainAct));                   // pass the ArrayList, under the ID "QUIZ_LIST_1", to the new activity.
                startActivity(intent);                                                                              // in fileIO.getQuizList(), quizzes with NO questions were never added in the first place
            }

                // ** could reset user's name from input and deselect the user's quiz choice
        }else{
            //editText1.setBackgroundColor(Color.rgb(255,180,11));                                                      // call users to name box (likley to be empty)
            Toast.makeText(getApplicationContext(),"enter name AND select a quiz",Toast.LENGTH_LONG).show();        // prompt user of problem
        }
    }



        // hook up the RecyclerView object to the RecyclerAdapter
        // each call to this function creates a NEW adapter
    public void setRecyclerAdapter(String [] quizStrNames)
    {
            // by default NONE of the items should be selected in the beginning
        selectedItemMainAct = -1;       // need this when a previous recycler view adapter is replaced by a new one (toggling online/local)
        RecyclerAdapter<MainActivity> recyclerAdapter1 = new RecyclerAdapter<MainActivity>(quizStrNames, this, selectedItemMainAct);    // RecyclerAdapter constructor takes an array of Quiz name strings as one of its arguments

            // ** could make the Recycler Adapter work with String ArrayLists instead of just String arrays so that we can avoid converting ArrayLists into Arrays...

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewQuizList.setLayoutManager(layoutManager);
        recyclerViewQuizList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewQuizList.setAdapter(recyclerAdapter1);

            // do need to DELETE the memory of the previous adapter (if it exists) ??
    }

    @Override
    public void setCurrentSelectedItem(int selection){
        this.selectedItemMainAct = selection;
    }


    public void setOnlineQuizNames(ArrayList<String> quizNames){
        onlineQuizNames = quizNames;
    }


        // when user clicks ONLINE radio button (only one of the buttons can ever be selected at a time): create the separate thread to assist with display the online quizzes
    public void radioOnlineOnClick(View view)
    {
        if(isOnlineSelected == false)                               // if LOCAL quizzes are currently being displayed on-screen
        {
            boolean doHaveQuizNames = (onlineQuizNames != null);    // only reach out to server for quiz names the FIRST time the online-radioButton is selected (AND subsequent times if a successful connection was NEVER established with the server)
            AsyncQuizNameTask AsyncQuizNameTask = new AsyncQuizNameTask(this, doHaveQuizNames);

            String quizNamesFile = baseURL + "Quizzes.txt";

                // CONSIDERED: for cases when the separate networking thread or "radioLocalOnClick()" calls this function: selectedItemMainAct is reset to -1 before setRecyclerAdapter() is called
                    // CANT because selecting another quiz (in the main UI thread) before the separate thread is finished with the networking would just override any changes to selectedItemMainAct

            AsyncQuizNameTask.execute(quizNamesFile);               // depending on the value of "doHaveQuizNames", the thread may or may not reach out to the server
                                                                    //    but the thread will ALWAYS update the recyclerView adapter

            // isOnlineSelected = true;                             // set to TRUE only when separate thread is successful

            createButton.setVisibility(View.INVISIBLE);             // buttons should ALWAYS be invisible when working in ONLINE mode
            editButton.setVisibility(View.INVISIBLE);
        }
    }

        // when user clicks LOCAL radio button, verify that it is not already selected, and then if there are LOCAL quizzes available, display them
    public void radioLocalOnClick(View view)
    {
        if(isOnlineSelected == true)                                // if ONLINE quizzes are currently being displayed on-screen
        {
            if (fileIO.doQuizzesExist() == true)                    // only swap recycler view if quizzes actually exist
            {
                setRecyclerAdapter(fileIO.getQuizNamesString());    // initiate & sync the RecyclerAdapter. Each list item in the RecyclerView will store a String of the Quiz name
                isOnlineSelected = false;

                if(isNameFieldInvalid == false) {     // display buttons only IF the user has previously input "professor" (NON-case sensitive)
                    createButton.setVisibility(View.VISIBLE);
                    editButton.setVisibility(View.VISIBLE);
                }

            }else {
                Toast.makeText(getApplicationContext(),"No LOCAL quizzes found",Toast.LENGTH_LONG).show();
                radioGroup.check(R.id.radioOnline2);                // reset check back to online since no LOCAL quizzes were available
            }
        }
    }


        // convert and ArrayList od string to an Array of Strings
    public String [] convertArrayListToArray(ArrayList<String> arrayList)
    {
        // null ArrayList is NEVER passed

        String [] stringArr = new String [arrayList.size()];

        for(int i=0; i < stringArr.length; i++)
        {
            stringArr[i] = arrayList.get(i);
        }
        return stringArr;
    }


    public ArrayList<String> getOnlineQuizNames() {
        return onlineQuizNames;
    }

    public void setOnlineSelected(boolean onlineSelected) {
        isOnlineSelected = onlineSelected;
    }


        //when create button is clicked
    public void createQuizOnClick(View view)
    {
        if(isOnlineSelected == false) {
            Intent intent = new Intent(this, FourthActivity.class);                         // parameters: where coming from (this) & the filename of where going to (SecondActivity)
            //intent.putExtra("QUIZZES", fileIO.getQuizList());                   // pass the ArrayList, under the ID "QUIZ_LIST_1", to the new activity.
            startActivityForResult(intent, 1001);
        }
    }
        // when edit button is clicked
    public void editQuizOnClick(View view)
    {
        if(selectedItemMainAct != -1 && isOnlineSelected == false)                   // user MUST select a quiz to edit before they can edit it...
        {
            Intent intent = new Intent(this, FourthActivity.class);                         // parameters: where coming from (this) & the filename of where going to (SecondActivity)
            intent.putExtra("QUIZ", fileIO.getQuizList().get(selectedItemMainAct));                   // pass the ArrayList, under the ID "QUIZ_LIST_1", to the new activity.
            intent.putExtra("DO_CREATE_QUIZ", false);
            startActivityForResult(intent, 1002);
        }else{
            Toast.makeText(getApplicationContext(),"Select a quiz",Toast.LENGTH_LONG).show();       // prompt the user that they must first select a quiz
        }
    }

        // when user comes BACK from fourth activity, this is called even BEFORE onResume
        //      - only called when Activity's return that were invoked using startActivityForResult()
        //      - update main-menu's RecyclerView here...
        // ** The resultCode will be RESULT_CANCELED if the activity didn't return any result, explicitly returned that, or crashed during its operation
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);  // why need this?

        if(resultCode == 789)                                   // then the return from FourthActivity was successful
        {
            int quizIndex = selectedItemMainAct;                // updating adapter RESETS selectedItemMainAct, so we need to save its value

            if (requestCode == 1001)                            // CREATED a NEW quiz
            {
                Quiz newQuiz = (Quiz) data.getSerializableExtra("UPDATED_QUIZ");

                if(newQuiz == null){    // if newQuiz is null, the user created a quiz but then never saved anything, so we do not need to make changes to anything
                    return;
                }

                newQuiz.setFileName(getFilesDir().getAbsolutePath()+"/" + newQuiz.getFileName());   // prefix the FULL path to the file name so that it can be properly saved locally
                fileIO.getQuizList().add(newQuiz);

                quizIndex = fileIO.getQuizList().size()-1;      // update since the NEW quiz was placed at the END of the QuizList

            } else if (requestCode == 1002){                    // EDITED an existing quiz

                Quiz updatedQuiz = (Quiz) data.getSerializableExtra("UPDATED_QUIZ");
                fileIO.getQuizList().set(quizIndex, updatedQuiz);
                // idea: if wanted to be efficient, could calculate a total "Hash code" for updatedQuiz, and compare it to the original copy held by fileIO; if no changes were made, do NOT need to write to memory
            }

            int didDeleteFile = fileIO.updateALocalFile(quizIndex);                 // update the LOCAL file corresponding to the quiz that was modified/created

            if(didDeleteFile == 0)                              // remove Quiz if it had NO questions (and thus was already remove from file directory)
            {
                fileIO.getQuizList().remove(quizIndex);
            }

            setRecyclerAdapter(fileIO.getQuizNamesString());    // update RecyclerView in case any of the Quiz-Names were changed, or Quizzes were deleted/added
        }
    }


        // used to determine if user has entered the name "professor" or not (if YES, display Create & Edit buttons)
    private class ListenerOnTextChange implements TextWatcher {
        private final Button createbutton;
        private final Button editbutton;        // final keyword means the variable must always reference the SAME object

        ListenerOnTextChange(Button createbutton, Button editbutton) {
            super();
            this.createbutton = createbutton;
            this.editbutton = editbutton;
        }

        @Override
        public void afterTextChanged(Editable s){ }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){ }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){    // find length of text INCLUDING the character/deletion the user just made
                // length check FIRST is so that we can SHORT-circuit and thus not have to check the expensive string comparison every time
            if( (s.length() == 9) && (s.toString().toLowerCase().equals("professor") == true) )
            {
                isNameFieldInvalid = false;             // set to true ONLY when the field contains "professor" (NON-case sensitive)

                if((isOnlineSelected == false)) {       // but only DISPLAY if in LOCAL mode
                    createbutton.setVisibility(View.VISIBLE);
                    editbutton.setVisibility(View.VISIBLE);
                }
            }else{
                createbutton.setVisibility(View.INVISIBLE);
                editbutton.setVisibility(View.INVISIBLE);
                isNameFieldInvalid = true;
            }
        }
    }


    /*     // ** could make the radio button disappear if no quizzes of that medium (online or local) are found
    public void setRadioButton1Invisible(){
        RadioButton radioButton1 = findViewById(R.id.radioOnline2);
        radioButton1.setVisibility(View.GONE);
    }*/

}
