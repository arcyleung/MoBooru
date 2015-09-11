package com.example.arthurl.mobooru;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
//    String mainsite = "http://www.mangahere.com/mangalist/";
    URL url1;
    String s1 = "http://redditbooru.com/images/?sources=17&afterDate=";
    long unixTimeInit = System.currentTimeMillis() / 1000L;
    Document doc;
    Elements redditSubs;
    ArrayList<Sub> subsList = new ArrayList<Sub>();
    String catJSONs = "";
    JSONArray catJSONa;



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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("jsonparsefailed");

        }

    }


    private class LoadJSONasync extends AsyncTask<JSONArray, Void, JSONArray> {

        protected JSONArray doInBackground(JSONArray... urls) {
            try {
                doc = Jsoup.connect(mainsite)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(6000000)
                        .get();
                redditSubs = doc.select("script");
                int i=0;
                for (Element sub : redditSubs){
                    String at = sub.toString();
                    String [] sp = at.split("-");
//                    subsList.add(new Sub(sp[0], Integer.parseInt(sp[1])));
                    System.out.println();
                    System.out.println(i+" - dbg-msg: " + redditSubs.size() + " -- " + at);
                    if (i == 2){
                        catJSONs = at;
                    }
                    i++;
                }

                catJSONs = catJSONs.substring(catJSONs.indexOf("["));
                catJSONs = catJSONs.substring(0, catJSONs.indexOf("]")+1);

                catJSONa = new JSONArray(catJSONs);

                for (int j = 0; j < catJSONa.length(); j++){
                    subsList.add(new Sub(catJSONa.getJSONObject(j).getString("name"), catJSONa.getJSONObject(j).getInt("value")));
                }
                System.out.println(subsList.size());

            } catch (Exception e){
                System.out.println("connection failed");
                e.printStackTrace();
            }

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
                startActivity(new Intent(Main.this, Settings.class).putExtra("arylst", subsList));
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
