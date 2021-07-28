// Dobmeier

package com.utd.quizproject;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

// return an ArrayList of strings containing all the quiz titles
    // later return the quiz that the user has selected??
public class AsyncQuizNameTask extends AsyncTask<String, Void, ArrayList<String>>
{
    private MainActivity mainActivity;
    private final boolean doHaveQuizNames;           // used to determine if need to request Quiz names from server or not
        // must update adapter from separate thread for cases when we have to retrieve data from server

        // non-static final variables can be assigned a value either in constructor or with the declaration

    AsyncQuizNameTask(MainActivity mainActivity, boolean doHaveQuizNames)
    {
        this.mainActivity = mainActivity;
        this.doHaveQuizNames = doHaveQuizNames;
    }


    /*
    This does the acutal work of getting the information from the web server
        @param: URLstrings is a list of a single URL
        @return: ArrayList<String> is the an arraylist of the Quiz name strings from the server
    */
    @Override
    protected ArrayList<String> doInBackground(String... URLstrings)
    {
        String quizStrURL = URLstrings[0];

        ArrayList<String> quizStrNames = null;


        if(doHaveQuizNames == false)       // only reach out to server for quiz names the FIRST time the online-radioButton is selected (AND subsequent times if a successful connection was NEVER established with the server)
        {
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
                                                                                // only if successfully connect to server does quizStrNames becomes NON-null
                    quizStrNames = new ArrayList<>(4);              // once connection is successfully established, initialize the ArrayList

                    while (scanner.hasNextLine()) {
                        String fileLineStr = scanner.nextLine();

                        if (fileLineStr.length() == 0) {                          // if the current line of the quiz name's file is empty, SKIP to the next line
                            continue;
                        }
                        if(fileLineStr.startsWith("Quiz") == false || fileLineStr.endsWith(".txt") == false){   // make sure files on server are PROPER format
                            continue;
                        }

                        quizStrNames.add(fileLineStr);                          // add quiz ONLY if the file name is in the proper format
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

        }else{
            quizStrNames = mainActivity.getOnlineQuizNames();                   // if have already previously retrieved QuizNames from the server, just get them from the MainActivty
        }

        return quizStrNames;                                                    // send the quizStrNames ArrayList back to the Main UI thread so that it can call onPostExecute()
    }


        // called and EXECUTED on the app's main UI thread AFTER the separate thread has finished executing
    @Override
    protected void onPostExecute(ArrayList<String> quizStrNames)
    {
        // super.onPostExecute(quizStrNames);   need this?

        if(quizStrNames == null){
            Toast.makeText(mainActivity.getApplicationContext(),"could NOT connect to server",Toast.LENGTH_LONG).show();
            // mainActivity.setOnlineSelected(false); is ALREADY false
        }
        else if(quizStrNames.size() == 0){
            Toast.makeText(mainActivity.getApplicationContext(),"NO quizzes found on server",Toast.LENGTH_LONG).show();
            // mainActivity.setOnlineSelected(false); is ALREADY false
        }else {

            mainActivity.setOnlineQuizNames(quizStrNames);                     // once thread finishes executing, pass the Quiz names Arraylist back to the main activity

            String[] stringArr = mainActivity.convertArrayListToArray(quizStrNames);   // convert the ArrayList to an Array so that the adapter can work with it

            mainActivity.setRecyclerAdapter(stringArr);                         // set a NEW recycler adapter so that the online quizzes are displayed

            mainActivity.setOnlineSelected(true);
            // ** could make the online radio button disappear reached server but no quizzes were found
        }
    }
}
