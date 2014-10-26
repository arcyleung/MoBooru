package com.example.arthurl.mobooru;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.etsy.android.grid.StaggeredGridView;

import org.json.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class Main extends Activity {

    int pageSize = 30;
    private StaggeredGridView sgv;
    private DataAdapter adapter;
    public ArrayList<Data> datas = new ArrayList<Data>();
    JSONArray jsonObjs;

    URL url1;
    String s1 = "http://redditbooru.com/images/?sources=1&afterDate=";
    long unixTime = System.currentTimeMillis() / 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("MoBooru v. 0.1a");
        sgv = (StaggeredGridView) findViewById(R.id.gridView);
        adapter = new DataAdapter(this, R.layout.staggered, addToArry());
        sgv.setAdapter(adapter);
        try {
            url1 = new URL(s1 + unixTime);
            Scanner scan = new Scanner(url1.openStream());
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            jsonObjs = new JSONArray(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public ArrayList<Data> addToArry() {

        for (int i = 0; i < pageSize; i++) {
            Data data = new Data();
            try {
                data.imgUrl = jsonObjs.getJSONObject(i).getString("cdnUrl");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            data.title = "Image";
            data.desc = "Description";
            datas.add(data);
        }
        return datas;
    }

}
