// Dobmeier

package com.utd.quizproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter<E extends adapterInterface> extends RecyclerView.Adapter<RecyclerAdapter.ListRow>
{
    private String [] quizStrNames;
    private int selectedItemAdapter;                                // This variable should stay in sync with the MainActivity's version of the variable
    private E parent;

        // To create an instance of the recycler adapter, a list of Quiz names must be passed
    RecyclerAdapter(String [] quizStrNames, E parent, int defaultSelection){
        this.quizStrNames = quizStrNames;
        this.parent = parent;
        this.selectedItemAdapter = defaultSelection;                // by default NONE of the items should be selected in the beginning
    }

        // Inflates the list, and returns new ListRow item
    @NonNull
    @Override
    public ListRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_list_item, parent, false);
        return new ListRow(view);
    }


        // This is where the actual text of the TextView can be changed
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ListRow holder, int position) {
        String getName = quizStrNames[position];
        holder.quizNameItem.setText(getName);

        if(selectedItemAdapter == position){        // color GREEN only when the current position/list index EQUALS what is currently set to be the selectedItem index
            holder.itemView.setBackgroundColor(Color.GREEN);
        }else{
            holder.itemView.setBackgroundColor(Color.RED);
        }
    }


    @Override
    public int getItemCount() {
        return (quizStrNames == null) ? ( 0 ) : ( quizStrNames.length );
    }


    public int getCurrentSelection(){
        return selectedItemAdapter;
    }




        // CLASS ListRow shown in 2/22 lecture @ 55:28  &  1:00:12  marks
        // ListRow is an INNER class to RecyclerAdapter (which itself is an inner class to MainActivity)
        // this is the the ViewHolder, and allows for actually INTERACTING onscreen with each list item
        // implement click listener for every item IN the recycler view (Each row of the recycler view)
    public class ListRow extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView quizNameItem;

        public ListRow(@NonNull View itemView)
        {
            super(itemView);
            quizNameItem = itemView.findViewById(R.id.quizItem1);
            itemView.setOnClickListener(this);
        }


            // This is how we actually DO something when a list item is clicked on
        @Override
        public void onClick(View v)
        {
            notifyItemChanged(selectedItemAdapter);
            selectedItemAdapter = getLayoutPosition();
            parent.setCurrentSelectedItem(selectedItemAdapter);         // update the MainActivity's version of the selectedItem variable, ensuring it is in-sync with the adapter
            notifyItemChanged(selectedItemAdapter);
                // notify the adapter that what used to be the selected item may NO longer be the current selected item. Then get the new selected item (and now show that is is highlighted)

                // when the FOURTH activity is running, need to update the the Quiz Question that is currently being displayed
            if(parent instanceof FourthActivity){
                ((FourthActivity) parent).fillOutQuestionFields();
                // framework seems to be able to detect on its own when user has pressed the SAME selection: no need to re-display Question if user picked the question they are already currently selected on
            }
        }

    }


}
