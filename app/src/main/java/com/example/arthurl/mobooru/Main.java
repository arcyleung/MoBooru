package com.example.arthurl.mobooru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.etsy.android.grid.StaggeredGridView;

import org.json.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Main extends Activity {

    int pageSize = 30;
    private StaggeredGridView sgv;
    private DataAdapter adapter;
    public ArrayList<Data> datas = new ArrayList<Data>();
    JSONArray jsonObjs = new JSONArray();

    final String verstring = "MoBooru v. 0.1a";

    String mainsite = "http://redditbooru.com";
    URL url1;
    String s1 = "http://redditbooru.com/images/?sources=17&afterDate=";
    long unixTimeInit = System.currentTimeMillis() / 1000L;
    Document doc;
    Elements redditSubs;
    ArrayList<Sub> subsList = new ArrayList<Sub>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadJSONasync runner = new LoadJSONasync();
        try {
            jsonObjs = runner.execute(jsonObjs).get();
            adapter = new DataAdapter(this, R.layout.staggered, addToArry());
            setTitle(verstring);
            sgv = (StaggeredGridView) findViewById(R.id.gridView);
            sgv.setAdapter(adapter);
            doc = Jsoup.connect(mainsite).get();
            redditSubs = doc.select("ul li label");
            for (Element sub : redditSubs){
                String at = sub.attr("for");
                System.out.println(at);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("jsonparsefailed");

        }

    }


    private class LoadJSONasync extends AsyncTask<JSONArray, Void, JSONArray> {

        protected JSONArray doInBackground(JSONArray... urls) {
            try {
                url1 = new URL(s1 + unixTimeInit);

                Scanner scan = new Scanner(url1.openStream());
                String str = "";
                while (scan.hasNext())
                    str += scan.nextLine();
                scan.close();

                jsonObjs = new JSONArray(str);

            } catch (Exception e) {
                System.out.println("JSON parse failed");
                e.printStackTrace();
            }

            return jsonObjs;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            jsonObjs = result;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public ArrayList<Data> addToArry() {

        for (int i = 0; i < pageSize; i++) {
            Data data = new Data();
            try {
                data.thumbImgUrl = "http://redditbooru.com/"+jsonObjs.getJSONObject(i).getString("thumb")+"_300_300.jpg";
                data.imgUrl = jsonObjs.getJSONObject(i).getString("cdnUrl");
                data.width = jsonObjs.getJSONObject(i).getInt("width");
                data.height = jsonObjs.getJSONObject(i).getInt("height");
                data.nsfw = jsonObjs.getJSONObject(i).getBoolean("nsfw");
                data.title = jsonObjs.getJSONObject(i).getString("title");
                data.desc = jsonObjs.getJSONObject(i).getString("sourceUrl");
                data.rat = data.width/data.height;
            } catch (Exception e) {
                System.out.println("JSON parse failed2");
                e.printStackTrace();
            }
            if (data.desc.equals("null")) {
                data.desc = "";
            }
            if (data.thumbImgUrl.equals("null")) {
                data.thumbImgUrl = "";
            }
            datas.add(data);
        }
        return datas;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.action_about:
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage("Author: pspkazy\nSite: http://github.com/pspkazy\n2014-2015")
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
