package com.example.arthurl.mobooru;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;


public class Main extends Activity {

    private static final int SAMPLE_DATA_ITEM_COUNT = 5;
    private StaggeredGridView sgv;
    private DataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("MoBooru v. 0.1a");
        sgv = (StaggeredGridView) findViewById(R.id.gridView);
        adapter = new DataAdapter(this, R.layout.staggered, generateSampleData());
        sgv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public static ArrayList<Data> generateSampleData() {
        final ArrayList<Data> datas = new ArrayList<Data>();
        for (int i = 0; i < SAMPLE_DATA_ITEM_COUNT; i++) {
            Data data = new Data();
            data.imgUrl = "http://icons.iconarchive.com/icons/uiconstock/socialmedia/512/Reddit-icon.png";
            data.title = "Image";
            data.desc = "Description";
            datas.add(data);
        }
        return datas;
    }
}
