package com.example.studycard.objects;

public class Variant {
    public String content;
    public int num;
    public int type;
    public String group="0";

    public static String replaceVariant(String oldString)
    {
        String newString =  oldString.replace("slash", "\\");
        newString = newString.replace("apostrof", "\'");

        return newString;
    }

    public static Variant createVariant(String str)
    {
        Variant variant = new Variant();
        variant.content = replaceVariant(str);

        if (str.contains("{wr}"))
        {
            variant.type=2;
            variant.content=variant.content.replace("{wr}", "");
        }
        else if(str.contains("{f}"))
        {
            variant.type=3;
            variant.content=variant.content.replace("{f}", "");
        }
        else if(str.contains("{n}"))
        {
            variant.type=4;
            variant.content=variant.content.replace("{n}", "");
            //Считаем количество доп вариантов

        }
        else if(str.contains("{t}"))
        {
            variant.type=5;
            String[] group = str.split("\\{t\\}");

            variant.content=group[0];
            variant.group=group[1];

        }
        else
        {
            variant.type=1;
            variant.group="0";
        }

        return variant;
    }
    public Variant()
    {

    }
}
