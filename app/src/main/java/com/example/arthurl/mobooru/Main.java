package com.example.arthurl.mobooru;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.support.v7.widget.Toolbar;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Main extends AppCompatActivity {

    final String verstring = "MoBooru v. 0.2a";
    public ArrayList<Data> datas = new ArrayList<>();
    int pageSize = 30;
    String mainsite = "https://redditbooru.com";
    URL url1;
    Display display;
    int screenWidth = 0;
    int screenHeight = 0;
    int bitmapWidth = 0;
    int bitmapHeight = 0;
    // DEFAULT SETTINGS
    String favstring = "";
    Boolean showNsfw = false;
    String s1 = "https://redditbooru.com/images/?sources=" + favstring + "&afterDate=";
    long lastTime;
    Document doc;
    Elements redditSubs;
    ArrayList<Sub> subsList = new ArrayList<>();
    String catJSONs = "";
    JSONArray catJSONa;
    LoadJSONasyncInit runner;
    JSONArray jsonObjs;
    LoadMorePhotos lm;
    int current_page = 1;
    Boolean loadingMore = true;
    private StaggeredGridView sgv;
    private DataAdapter adapter;

    AppBarLayout appBarLayout;

    private Toolbar mToolbar;

    Map<Integer, Boolean> selectedSubs = new HashMap<Integer, Boolean>();
    Gson gson = new Gson();
    Type intBoolMap = new TypeToken<Map<Integer, Boolean>>(){}.getType();

    SharedPreferences prefs;


    @SuppressWarnings("deprecation")
    private static Point getDisplaySize(final Display display) {
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Legacy support
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            selectedSubs = gson.fromJson(prefs.getString("FAV_SUBS", ""+R.string.defaultsub), intBoolMap);
        }
        catch (Exception ex){
            selectedSubs.put(1, true);
        }

        setSubs();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_subs:
                        startActivity(new Intent(Main.this, Settings_subs.class).putExtra("subs", subsList));
                        return true;
//                        drawerLayout.closeDrawers();
                    case R.id.nav_about:
                        new AlertDialog.Builder(Main.this)
                                .setTitle("About")
                                .setMessage("Author: arcyleung\nSite: http://arcyleung.com")
                                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(R.mipmap.ic_launcher)
                                .show();
                        return true;

                }
                return false;
            }
        });

//        favstring = prefs.getString("FAV_SUBS", "" + R.string.defaultsub).replaceAll(",", "%2C");
        showNsfw = prefs.getBoolean("SHOW_NSFW", false);

        display = getWindowManager().getDefaultDisplay();
        screenWidth = getDisplaySize(display).x;
        screenHeight = getDisplaySize(display).y;

        s1 = "https://redditbooru.com/images/?sources=" + favstring;
        runner = new LoadJSONasyncInit();

        try {
            JSONArray jsonObjs = new JSONArray();
            jsonObjs = runner.execute(jsonObjs).get();
            ArrayList<Data> tmp = addToArry(jsonObjs);
            adapter = new DataAdapter(this, R.layout.staggered, tmp, showNsfw);
            setTitle(verstring);
            sgv = (StaggeredGridView) findViewById(R.id.gridView);
            sgv.setAdapter(adapter);
            sgv.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    lm = new LoadMorePhotos();
                    lm.execute();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                System.out.println("Enabling nested scrolling");
                sgv.setNestedScrollingEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sgv.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                InteractiveImageView zoomImageView = new InteractiveImageView(getActivity());
                Dialog dialog = new Dialog(getActivity());
                try {

                    Bitmap img = new DownloadImage(zoomImageView).execute(datas.get(position).imgUrl).get();
                    bitmapWidth = img.getWidth();
                    bitmapHeight = img.getHeight();

//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.popup_imgview);
                    dialog.getWindow().setBackgroundDrawable(null);

                    InteractiveImageView image = (InteractiveImageView) dialog.findViewById(R.id.imageview);
                    image.setImageBitmap(img);


                    dialog.getWindow().setLayout(screenWidth, screenHeight);
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ViewCompat.setNestedScrollingEnabled(sgv,true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sgv.setNestedScrollingEnabled(true);
        }
        System.out.println("[DBG] Enabled nested scrolling");

    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }


    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
    }

    public void setSubs(){
        favstring = "";
            for (int sub : selectedSubs.keySet()){
                if (selectedSubs.get(sub)){
                    favstring += sub + "%2C";
                }
            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public Context getActivity() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public ArrayList<Data> addToArry(JSONArray ja) {
        if (loadingMore) {

            //IMPLEMENT STOP LOADING ONCE ARRAYSIZE < PAGESIZE
            if (ja == null) {
                loadingMore = false;
            } else {
                if (ja.length() < pageSize) {
                    pageSize = ja.length();
                }
                for (int i = 0; i < pageSize; i++) {
                    Data data = new Data();
                    try {
                        data.thumbImgUrl = ja.getJSONObject(i).getString("thumb") + "_300_300.jpg";
                        data.imgUrl = ja.getJSONObject(i).getString("cdnUrl");
                        data.width = ja.getJSONObject(i).getInt("width");
                        data.height = ja.getJSONObject(i).getInt("height");
                        data.nsfw = ja.getJSONObject(i).getBoolean("nsfw");
                        data.title = ja.getJSONObject(i).getString("title");
                        data.desc = ja.getJSONObject(i).getString("sourceUrl");
                        data.rat = data.width / data.height;
                        if (i == pageSize - 1) {
                            lastTime = Long.parseLong(ja.getJSONObject(i).getString("dateCreated"));
                        }
                    } catch (Exception e) {
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
            }
        }
        return datas;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_subs:
//                startActivity(new Intent(Main.this, Settings_subs.class).putExtra("subs", subsList));
//                return true;
//            case R.id.nav_about:
//                new AlertDialog.Builder(this)
//                        .setTitle("About")
//                        .setMessage("Author: arcyleung\nSite: http://arcyleung.com")
//                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        InteractiveImageView bmImage;
        ProgressDialog pDialog;

        DownloadImage(InteractiveImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Downloading...");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            pDialog.dismiss();
            bmImage.setImageDrawable(new BitmapDrawable(getResources(), result));
        }
    }

    private class LoadJSONasyncInit extends AsyncTask<JSONArray, Void, JSONArray> {

        protected JSONArray doInBackground(JSONArray... urls) {
            try {
                doc = Jsoup.connect(mainsite)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(6000000)
                        .get();
                redditSubs = doc.select("script");
                int i = 0;
                for (Element sub : redditSubs) {
                    String at = sub.toString();
                    if (i == 2) {
                        catJSONs = at;
                    }
                    i++;
                }

                catJSONs = catJSONs.substring(catJSONs.indexOf("["));
                catJSONs = catJSONs.substring(0, catJSONs.indexOf("]") + 1);

                catJSONa = new JSONArray(catJSONs);

                for (int j = 0; j < catJSONa.length(); j++) {
                    subsList.add(new Sub(catJSONa.getJSONObject(j).getString("name"), catJSONa.getJSONObject(j).getInt("value")));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String str = "";

            try {
                s1 = "https://redditbooru.com/images/?sources=" + favstring + "&afterDate=";
                url1 = new URL(s1 + lastTime);

                Scanner scan = new Scanner(url1.openStream());
                while (scan.hasNext())
                    str += scan.nextLine();
                scan.close();

                jsonObjs = new JSONArray(str);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObjs;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            jsonObjs = result;
        }
    }

    private class LoadMorePhotos extends AsyncTask<Void, Void, Void> {
        JSONArray tmp;

        @Override
        protected Void doInBackground(Void... arg0) {
            // SET LOADING MORE "TRUE"
            loadingMore = true;

            // INCREMENT CURRENT PAGE
            current_page += 1;

            try {
                s1 = "https://redditbooru.com/images/?sources=" + favstring + "&afterDate=";
                url1 = new URL(s1 + lastTime);

                Scanner scan = new Scanner(url1.openStream());
                String str = "";
                while (scan.hasNext())
                    str += scan.nextLine();
                scan.close();

                tmp = new JSONArray(str);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // get listview current position - used to maintain scroll position
//            int currentPosition = sgv.getFirstVisiblePosition();

            // APPEND NEW DATA TO THE ARRAYLIST AND SET THE ADAPTER TO THE
            // LISTVIEW
            datas = addToArry(tmp);
            adapter.datas = datas;
            adapter.notifyDataSetChanged();


            // SET LOADINGMORE "FALSE" AFTER ADDING NEW FEEDS TO THE EXISTING
            // LIST
            loadingMore = false;
        }

    }
}
