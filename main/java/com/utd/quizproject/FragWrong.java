// Written by Nicholas Dobmeier for CS 4301.001, assignment 2, starting February 25, 2021.
//        NetID: njd170130

package com.utd.quizproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragWrong#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragWrong extends Fragment
{
    private Button buttonWrong;
    private TextView textViewWrong;
    private SecondActivity main;

    public FragWrong() {
        // Required empty public constructor
    }


    public static FragWrong newInstance()
    {
        FragWrong fragment = new FragWrong();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

        // inflate the fragment, and locate the button by its ID. Then, designate the onClickListener for the button
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_wrong, container, false);        // Inflate the layout for this fragment

        buttonWrong = view.findViewById(R.id.button4);
        buttonWrong.setOnClickListener(new BtnClickListener3());

        main = (SecondActivity) getActivity();

        textViewWrong = view.findViewById(R.id.correctAnswer1);                                     // display the CORRECT answer on-screen, in addition to the button
        String correctAnswerStr = "The correct answer is:\n" + main.currentQuiz.getQuizQuestion(main.currentQuestion).getCorrectChoice();
        textViewWrong.setText(correctAnswerStr);

        return view;
    }




        // when this button is clicked, tell 2nd activity to move to next question
    private class BtnClickListener3 implements Button.OnClickListener{
        @Override
        public void onClick(View v)
        {
            main.moveToNextQuestion();
        }
    }
}