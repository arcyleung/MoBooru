package com.example.arthurl.mobooru;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class DataAdapter extends ArrayAdapter<Data> {

    Activity activity;
    int resource;
    List<Data> datas;
    Boolean showNsfw;

    public DataAdapter(Activity activity, int resource, List<Data> objects, Boolean shwNsfw) {
        super(activity, resource, objects);

        this.activity = activity;
        this.resource = resource;
        this.datas = objects;
        this.showNsfw = shwNsfw;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final DealHolder holder;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new DealHolder();
            holder.image = (DynamicHeightImageView) row.findViewById(R.id.image);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.description = (TextView) row.findViewById(R.id.description);

            row.setTag(holder);
        } else {
            holder = (DealHolder) row.getTag();
        }

        final Data data = datas.get(position);

        holder.image.setHeightRatio(1);
        holder.title.setText(data.title);
        if (data.desc == null) {
            holder.description.setText("");
        } else {
            holder.description.setText(data.desc);
        }
        if (data.thumbImgUrl != ""){
            if (data.nsfw && !showNsfw) {
                Picasso.with(this.getContext())
                        .load(data.thumbImgUrl)
                        .transform(new RoundedTransformation(20,10))
                        .transform(new BlurTransformation(this.getContext()))
                        .into(holder.image);
                System.out.println(data.thumbImgUrl);
            } else {
                Picasso.with(this.getContext())
                        .load(data.thumbImgUrl)
                        .transform(new RoundedTransformation(20, 10))
                        .into(holder.image);
                System.out.println(data.thumbImgUrl);
            }
        } else {
            Picasso.with(this.getContext())
                    .load(new File("img/404_notfound.jpg"))
                    .transform(new RoundedTransformation(20, 10))
                    .into(holder.image);
            System.out.println(data.thumbImgUrl);
        }



        return row;
    }

    static class DealHolder {
        DynamicHeightImageView image;
        TextView title;
        TextView description;
    }
}
