package com.example.arthurl.mobooru;

import java.io.Serializable;

/**
 * Created by pspka_000 on 9/10/2015.
 */
public class Sub implements Serializable{
    public String subname = "";
    public int subID = 0;

    public Sub(String s, int n){
        subname = s;
        subID = n;
    }
}
