package com.example.arthurl.mobooru;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * Created by Arthur on 10/9/2014.
 */
public class ImageAdapter extends BaseAdapter {
    int[] images = {};
    public ImageAdapter(Context applicationContext) {
    }

    @Override
    public int getCount() {
        //number of dataelements to be displayed
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
