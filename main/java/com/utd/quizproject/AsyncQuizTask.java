// Dobmeier

package com.utd.quizproject;

import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class AsyncQuizTask extends AsyncTask<String, Void, Quiz>
{
    private final Quiz newOnlineQuiz;           // final means can't change the variable's reference, but CAN still modify the object itself
                                                // NEED ANOTHER THREAD CLASS IN ORDER TO READ QUIZ Question DATA... OTHERWISE HAVE TO RETURN ArrayList<String> from doInBackground() in AsyncQuizNameTask.....

    AsyncQuizTask(Quiz newOnlineQuiz)
    {
        this.newOnlineQuiz = newOnlineQuiz;
    }


        // Upon successfully establishing a connection with the server, the seperate thread will update the newOnlineQuiz (passed to constructor) with the data of a specific quiz on the server
    @Override
    protected Quiz doInBackground(String... URLstrings)
    {
        String quizStrURL = URLstrings[0];


        URL objURL = null;
        HttpURLConnection httpConnection = null;
        int responseCode = -1;
        InputStream responseInputStream = null;
        Scanner scanner = null;

        try {

            objURL = new URL(quizStrURL);                                   // might throw MalformedURLException
            httpConnection = (HttpURLConnection) objURL.openConnection();   // might throw IOException & NULLPointerException
            responseCode = httpConnection.getResponseCode();                // IOException – if an error occurred connecting to the server

            if (responseCode == HttpURLConnection.HTTP_OK)   // HTTP_OK == 200
            {
                responseInputStream = httpConnection.getInputStream();
                scanner = new Scanner(responseInputStream);



                    // newOnlineQuiz is NEVER NULL because it is was instantiated in MainActivtiy and passed to this thread's constructor
                newOnlineQuiz.setQuizName(scanner.nextLine());               // if successfully connect to server, add the name to the Quiz (this assumes it has a name)
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
                            newOnlineQuiz.addQuestion(newQuestion);
                        }

                    }
                    i++;
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //String msg = ex.getMessage();
            //System.out.println(msg);
        } finally {      // always make sure to perform the following commands, success or not
            try {
                scanner.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            try {
                responseInputStream.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            try {
                httpConnection.disconnect();                                // close connection to turn the radio off and save device battery
            } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    // ex.printStackTrace();
            }
        }

        return newOnlineQuiz;
    }
}
