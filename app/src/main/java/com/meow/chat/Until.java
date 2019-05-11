package com.meow.chat;

public class Until {

    static String user_cur = "";
    private static Until u;

    public static Until getInstance()
    {
        if(u == null) u = new Until();

        return u;
    }


    public void setUid(String uid)
    {
        user_cur = uid;
    }

    public String getUid()
    {
        return user_cur;
    }




}
