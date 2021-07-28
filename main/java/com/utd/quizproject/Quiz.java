// Written by Nicholas Dobmeier for CS 4301.001, assignment 2, starting February 25, 2021.
//        NetID: njd170130

package com.utd.quizproject;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable              // implement Serializable so that this object can be passed using Intents
{
        // all data members of the class must also be Serializable types inorder to pass using Intents
    private String quizName;
    private ArrayList<QQuestion> questions = new ArrayList<>(5);            // store each Question of this Quiz in an ArrayList
    private String fileName;

    Quiz(String quizName, String fileName){         // used when reading LOCAL quizzes
        this.quizName = quizName;
        this.fileName = fileName;
    }

    Quiz(){}    // empty constructor used for reading ONLINE quizzes, AND when user is creating a new quiz



    public void setQuizName(String quizName){
        this.quizName = quizName;
    }
    public String getQuizName(){ return  quizName; }


    public void addQuestion(QQuestion newQuestion){
        questions.add(newQuestion);
    }
    public void setQuestionIndex(QQuestion newQuestion, int index) {
        if (index < questions.size()) {
            questions.set(index, newQuestion);
        }
    }
    public QQuestion getQuizQuestion(int questionIndex){
        return (questionIndex < questions.size()) ? ( questions.get(questionIndex) ) : ( null ) ;
    }

    public int getNumberOfQuestions(){
        return questions.size();
    }


    public String getFullPathName(){ return fileName; }
    public String getFileName(){ return fileName.substring(fileName.lastIndexOf("/")+1); }      // only return file name, NOT the full path
    public void setFileName(String fileName){ this.fileName = fileName; }                           // only used when saving a quiz while CREATING a new quiz


    public boolean removeQuestion(int index)
    {
        if(index >= questions.size() || index < 0){
            return false;
        }
        questions.remove(index);
        return true;
    }
}
