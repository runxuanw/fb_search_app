package com.example.administrator.fb_search;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText searchText;
    String[] json_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        searchText = (EditText) findViewById(R.id.searchText);
        Button button = (Button) findViewById(R.id.searchBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    AjaxCall searchAjax = new AjaxCall();
                    String search = searchText.getText().toString();
                    searchAjax.execute(search);//.get();
                    //JSONObject json = new JSONObject(jsonStr);
                    //Log.d("tag", json.toString());


            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_favorites) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("favor", "true");
            startActivity(intent);

        }  else if (id == R.id.nav_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class AjaxCall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String[] type = {"user","page","place","event","group"};
            json_results = new String[type.length];
            String finalRes = "";
            for(int i = 0; i < type.length; i++) {
                String urlStr = "http://cs571wrx.us-west-1.elasticbeanstalk.com/hw8_function.php?type=" + type[i] + "&keyword=" + params[0];
                if(type[i].equals("place"))
                    urlStr += "&lng=-118.2872&lat=34.0215";
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream stream = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder str = new StringBuilder();
                    String res = "";
                    while (res != null) {
                        str.append(res);
                        res = reader.readLine();
                    }
                    json_results[i] = str.toString();

                    reader.close();
                    stream.close();
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return finalRes;
        }

        @Override
        protected void onPostExecute(String result) {
            displayResult();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    public void displayResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        for(int i = 0; i < json_results.length; i++) {
            intent.putExtra("json"+String.valueOf(i), json_results[i]);
        }
        startActivity(intent);
    }



    public void clearSearch(View v) {
        EditText text = (EditText) findViewById(R.id.searchText);
        text.setText("");
    }

}
