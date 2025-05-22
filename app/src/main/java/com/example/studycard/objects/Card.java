package com.example.studycard.objects;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Card implements Serializable {
    public int id;
    public String name;
    public String content;
    public int visible;
    public int[] period = new int[8];


    public static ArrayList<Card> CheckPeriod(ArrayList<Card> oldList, int period)
    {
        ArrayList<Card> newList = new ArrayList<>();
        for (int i=0; i<oldList.size();i++)
        {
            if(oldList.get(i).period[period]==1)
                newList.add(oldList.get(i));
        }
        return newList;
    }
    public Card ()
    {

    }

}
