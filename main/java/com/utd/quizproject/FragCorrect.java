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
 * Use the {@link FragCorrect#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragCorrect extends Fragment
{
    private Button buttonCorrect;
    private SecondActivity main;

    public FragCorrect() {
        // Required empty public constructor
    }


    public static FragCorrect newInstance()
    {
        FragCorrect fragment = new FragCorrect();
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
        View view = inflater.inflate(R.layout.fragment_correct, container, false);      // Inflate the layout for this fragment

        buttonCorrect = view.findViewById(R.id.button3);
        buttonCorrect.setOnClickListener(new BtnClickListener2());

        main = (SecondActivity) getActivity();

        return view;
    }




        // when this button is clicked, tell 2nd activity to move to next question
    private class BtnClickListener2 implements Button.OnClickListener{
        @Override
        public void onClick(View v)
        {
            main.moveToNextQuestion();
        }
    }

}
