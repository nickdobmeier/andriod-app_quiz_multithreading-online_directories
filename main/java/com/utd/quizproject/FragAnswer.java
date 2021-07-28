// Dobmeier

package com.utd.quizproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragAnswer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragAnswer extends Fragment
{
    private Button buttonAnswer;
    private SecondActivity main;

    public FragAnswer() {
        // Required empty public constructor
    }


    public static FragAnswer newInstance()
    {
        FragAnswer fragment = new FragAnswer();
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
        View view = inflater.inflate(R.layout.fragment_answer, container, false);       // Inflate the layout for this fragment

        buttonAnswer = view.findViewById(R.id.button2);
        buttonAnswer.setOnClickListener(new BtnClickListener1());

        main = (SecondActivity) getActivity();

        return view;
    }




        // when this button is clicked, initiate sequence to swap answer fragment with either the CORRECT or WRONG fragments
    private class BtnClickListener1 implements Button.OnClickListener{
        @Override
        public void onClick(View v)
        {
            main.setFragSequenceAfterAnswer();
        }
    }


}
