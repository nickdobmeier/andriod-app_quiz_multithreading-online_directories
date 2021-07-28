package com.utd.quizproject;

public interface adapterInterface
{
    public void setCurrentSelectedItem(int selection);

    // if both MainActivity and FourthActivity implement this interface, then they both can use the same RecyclerAdapter class code

}
