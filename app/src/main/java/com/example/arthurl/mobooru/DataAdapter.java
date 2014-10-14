package com.example.arthurl.mobooru;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DataAdapter extends ArrayAdapter<Data> {

    Activity activity;
    int resource;
    List<Data> datas;

    public DataAdapter(Activity activity, int resource, List<Data> objects) {
        super(activity, resource, objects);

        this.activity = activity;
        this.resource = resource;
        this.datas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final DealHolder holder;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new DealHolder();
            holder.image = (DynamicHeightImageView)row.findViewById(R.id.image);
            holder.title = (TextView)row.findViewById(R.id.title);
            holder.description = (TextView)row.findViewById(R.id.description);

            row.setTag(holder);
        }
        else {
            holder = (DealHolder) row.getTag();
        }

        final Data data = datas.get(position);

        holder.image.setHeightRatio(1.0);
        holder.title.setText(data.title);
        holder.description.setText(data.desc);
        Picasso.with(this.getContext())
                .load(data.imgUrl)
                .into(holder.image);

        return row;
    }

    static class DealHolder {
        DynamicHeightImageView image;
        TextView title;
        TextView description;
    }
}
