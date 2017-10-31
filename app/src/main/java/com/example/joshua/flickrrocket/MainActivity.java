package com.example.joshua.flickrrocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    ArrayList<String> pictureUrls = new ArrayList<String>();
    SearchView srcView;
    ImageView imgView;
    JSONArray pictures;
    String json = "";
    int counter = 0;
    static String STARTING_URL = "http://api.flickr.com/services/rest/?format=json" +
            "&sort=random&method=flickr.photos.search&tags=rocket" +
            "&tag_mode=all&api_key=0e2b6aaf8a6901c264acb91f151a3350" +
            "&nojsoncallback=1";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DownloadUrl().execute(STARTING_URL);

    }

    public void getNextPicture(View view) {
        imgView = (ImageView) findViewById(R.id.ImageView00);
        new DownloadImage().execute(STARTING_URL);
        }

    class DownloadUrl extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... myurl) {

            InputStream inputStream = null;
            String result = null;

            try {
                URL url = new URL(myurl[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                inputStream.close();
                result = sb.toString();
            }
            catch (Exception e) {
                // Oops
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                if(pictures == null)
                {
                    JSONObject tmp = new JSONObject(json);
                    JSONObject data = tmp.getJSONObject("pictures");
                    pictures = data.getJSONArray("photo");
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imgView = (ImageView) findViewById(R.id.ImageView00);
            imgView.setVisibility(View.INVISIBLE);
            ProgressBar prgBar = (ProgressBar) findViewById(R.id.LoadingBar00);
            prgBar.setVisibility(View.VISIBLE);
            prgBar.animate();
        }

        protected Bitmap doInBackground(String... params)
        {
            InputStream inputStream = null;
            Bitmap bmp = null;
            try{
                URL url = new URL(params[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bmp = BitmapFactory.decodeStream(bufferedInputStream);
                bufferedInputStream.close();
                inputStream.close();
            } catch (Exception e) {

            }
            return bmp;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            ProgressBar prgBar = (ProgressBar) findViewById(R.id.LoadingBar00);
            prgBar.setVisibility(View.VISIBLE);
            prgBar.animate();
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            imgView = (ImageView) findViewById(R.id.ImageView00);
            imgView.setVisibility(View.VISIBLE);
            try {
                imgView.setImageBitmap(Bitmap.createScaledBitmap(image,
                        imgView.getWidth(), imgView.getHeight(), true));
            } catch (Exception e) {

            }
            ProgressBar prgBar = (ProgressBar) findViewById(R.id.LoadingBar00);
            prgBar.setVisibility(View.INVISIBLE);
            Animation fade = new AlphaAnimation(0.0f, 1.0f);
            fade.setDuration(500);
            imgView.setAnimation(fade);
        }
    }
}
