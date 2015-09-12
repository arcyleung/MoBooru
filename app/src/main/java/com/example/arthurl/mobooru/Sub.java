package com.example.arthurl.mobooru;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by pspka_000 on 9/10/2015.
 */
public class Sub implements Serializable, Comparable{
    public String subname = "";
    public int subID = 0;
    public boolean selected = false;

    public Sub(String s, int n){
        subname = s.toLowerCase();
        subID = n;
    }

    public Sub(String s, int n, boolean c){
        subname = s.toLowerCase();
        subID = n;
    }

    public int compareTo(Object s2){
        Sub s = (Sub)s2;
        return subname.compareTo(s.subname);
    }
}
