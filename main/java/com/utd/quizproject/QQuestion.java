// Written by Nicholas Dobmeier for CS 4301.001, assignment 2, starting February 25, 2021.
//        NetID: njd170130

package com.utd.quizproject;

import java.io.Serializable;
import java.util.ArrayList;

public class QQuestion implements Serializable              // QQuestion must also implement Serializable, since it is used as a data member in the Quiz class - which are passed using Intents
{
    private String question;
    private ArrayList<String> answerChoices = new ArrayList<>(4);       // store each answer choice of this question in an ArrayList
    private int correctChoiceIndex = -1;

    QQuestion(String questionLine){
        question = questionLine;
    }



    public void setQuestion(String question){ this.question = question; }
    public String getQuestion() { return question; }


    public void addAnswer(String answer){
        answerChoices.add(answer);
    }
    public void setAnswerIndex(String answer, int index){
        if(index < answerChoices.size()) {
            answerChoices.set(index, answer);
        }
    }
    public String getAnswerChoice(int index){
        return (index < answerChoices.size()) ? ( answerChoices.get(index) ) : ( "" ) ;
    }


    public void setCorrectChoiceIndex(int correctIndex){ correctChoiceIndex = correctIndex; }

    public String getCorrectChoice()        // could delete THIS CALL IN 2nd ACTIVITY ??
    {
        return (correctChoiceIndex > -1) ? answerChoices.get(correctChoiceIndex) : (null);
    }
    public int getCorrectChoiceIndex()
    {
        return correctChoiceIndex;
    }
}
