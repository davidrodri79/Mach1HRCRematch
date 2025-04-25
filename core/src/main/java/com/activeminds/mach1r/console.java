package com.activeminds.mach1r;

import java.util.ArrayList;

public class console {

    public static final int MSSMAX = 19;
    ArrayList<String> mess;

    public console()
    {
        mess = new ArrayList<>();
    }

    public void add_mess(String m)
    {
        mess.add(m);
        if(mess.size() > MSSMAX) mess.remove(0);
    }
}
