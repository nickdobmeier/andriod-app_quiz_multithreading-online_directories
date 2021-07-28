// Written by Nicholas Dobmeier for CS 4301.001, assignment 2, starting February 25, 2021.
//        NetID: njd170130

package com.utd.quizproject;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO
{
    private ArrayList<Quiz> quizList = null;

        // Constructor: Takes file (directory) object and organizes file data into Quiz and Question objects
    FileIO(File fileDir)
    {
        if(fileDir.isDirectory() == true)
        {
            File [] listFiles = fileDir.listFiles();
            int numFiles = listFiles.length;

            if(numFiles == 0){                                                  // if NO files are in the directory, then their is no need to initialize quizList
                return;
            }

            quizList = new ArrayList<>(numFiles);


            for(int quizNum=0; quizNum < numFiles; quizNum++)                   // if quizList.size() is ever ZERO, its because files DID exist in the directory, but NONE were QuizFiles
            {
                    // if filename does NOT start with "Quiz" OR is NOT a .txt file , skip to next file
                if(listFiles[quizNum].getName().startsWith("Quiz") == false || listFiles[quizNum].getName().endsWith(".txt") == false){
                    continue;
                }


                Scanner scanner;
                try {
                    scanner = new Scanner(listFiles[quizNum]);
                }catch (Exception ex){
                    continue;
                }

                    // pass quiz name and the FULL filename (including full path) of the quiz to the constructor
                Quiz newQuiz = new Quiz(scanner.nextLine(), listFiles[quizNum].getAbsolutePath());
                QQuestion newQuestion = null;
                int i = 0;
                while(scanner.hasNextLine())                                    // all questions must have answer choices, otherwise the question never gets added
                {
                    String lineStr = scanner.nextLine();
                    if(lineStr.length() == 0){                                  // if the current line of the quiz file is empty, SKIP to the next line
                        continue;
                    }

                    int icycle = i % 5;
                    if( icycle == 0 )                                           // every 5th line is a NEW quiz question
                    {
                        newQuestion = new QQuestion(lineStr);
                    }else{                                                      // icycle values of 1-4 are quiz answer choices
                        if(lineStr.charAt(0) == '*')
                        {
                            lineStr = lineStr.substring(1);
                            newQuestion.setCorrectChoiceIndex(icycle-1);        // if multiple lines within the same question have '*', then the LAST one will be set as the answer
                        }

                        newQuestion.addAnswer(lineStr);

                                                                                // after the FINAL answer choice for each question is added, add the Question to the Quiz
                        if(icycle == 4 && newQuestion.getCorrectChoice() != null)    // BUT only when the question DOES have a correct answer choice. Otherwise throw away the question
                        {
                            newQuiz.addQuestion(newQuestion);
                        }

                    }
                    i++;
                }

                scanner.close();                                                // close the input file stream for this particular Quiz

                if(newQuiz.getNumberOfQuestions() > 0)                          // only add the quiz if it contains ATLEAST 1 question
                {
                    quizList.add(newQuiz);
                }
            }

        }
    }

        // check if ArrayList was initialized AND that atleast 1 Quiz object is in the list
        // return FALSE no files in the local directory OR no quiz files were found in the directory OR quizzes found but none had atleast 1 question
    public boolean doQuizzesExist(){
        if(quizList == null || quizList.size() == 0){
            return false;
        }
        else{
            return true;
        }
    }

        // iterates through all the Quiz objects in the class ArrayList
            // and then returns the array of strings containing the name of EACH Quiz in the quizList
    public String [] getQuizNamesString()
    {
        if(doQuizzesExist() == false){
            return null;
        }
        String [] quizNames = new String [quizList.size()];

        for(int i=0; i < quizNames.length; i++)
        {
            quizNames[i] = quizList.get(i).getQuizName();
        }
        return quizNames;
    }


    public ArrayList<Quiz> getQuizList() {
        return quizList;
    }


    public int updateALocalFile(int quizIndex)
    {
        Quiz currQuiz = quizList.get(quizIndex);

        File file = new File(currQuiz.getFullPathName());

        if(currQuiz.getNumberOfQuestions() == 0){
            boolean didDelete = file.delete();                                  // DELETE THE FILE, no need to modify it since there are no questions
            return 0;                                                           // return 0 to indicate a file was deleted
        }

        PrintWriter printWriter = null;

        try {
                // if file does NOT exist, this will create a new one
            FileOutputStream fos = new FileOutputStream(file, false);            // append=false clears means file gets OVERWRITTEN
                //fos = new openFileOutput(file, Context.MODE_PRIVATE);
            printWriter = new PrintWriter(fos);
        } catch (IOException ex)                                                        // FileNotFoundException is a subclass of IOException
        {
            return -1;                                                                  // return -1 indicating an error/exception occured
        }

        printWriter.println(currQuiz.getQuizName());

        for(int i=0; i < currQuiz.getNumberOfQuestions(); i++)
        {
            QQuestion currQuestion = currQuiz.getQuizQuestion(i);

            printWriter.println(currQuestion.getQuestion());
            for(int g=0; g < 4; g++)
            {
                if(g == currQuestion.getCorrectChoiceIndex()){      // append a "*" to denote correct answer choice
                    printWriter.println("*" + currQuestion.getAnswerChoice(g));
                }else{
                    printWriter.println(currQuestion.getAnswerChoice(g));
                }
            }
        }

        printWriter.close();
        return 1;
    }
}
