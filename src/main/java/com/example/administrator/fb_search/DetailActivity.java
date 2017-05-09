package com.example.administrator.fb_search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.administrator.fb_search.ResultActivity.addFavor;
import static com.example.administrator.fb_search.ResultActivity.arrayListToStr;
import static com.example.administrator.fb_search.ResultActivity.getIdxByValue;
import static com.example.administrator.fb_search.ResultActivity.removeFavor;
import static com.example.administrator.fb_search.ResultActivity.resultPrefer;
import static com.example.administrator.fb_search.ResultActivity.strToJSON;

public class DetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    JSONObject detailJSON;
    DetailRowAdapter detailAdapter;
    MessageRowAdapter msgAdapter;
    ListView listView;

    String img;
    String detailId;
    String poster_name;

    CallbackManager callbackManager;
    ShareDialog shareDialog;


    boolean remove;

    static DetailActivity detailContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailbar);
        setSupportActionBar(toolbar);

        shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();

        shareDialog.registerCallback(callbackManager,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        // App code
                        Toast toast = Toast.makeText(detailContext, "Post Success!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    @Override
                    public void onCancel() {
                        // App code
                        Toast toast = Toast.makeText(detailContext, "Post Cancelled!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast toast = Toast.makeText(detailContext, "Post Error!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

        detailJSON = strToJSON(getIntent().getExtras().getString("json"));
        img = getIntent().getExtras().getString("img");
        detailId = getIntent().getExtras().getString("id");
        setTitle("More Detail");

        remove = false;
        detailContext = this;

        msgAdapter = new MessageRowAdapter(this,R.layout.msg_row);
        detailAdapter = new DetailRowAdapter(this,R.layout.detail_row);
        listView = (ListView) findViewById(R.id.detailListView);
        listView.setAdapter(detailAdapter);
        poster_name = "";


        //get detail from the json
        try {

            JSONObject data = (JSONObject) detailJSON.getJSONArray("data").get(0);
            poster_name = data.getString("name");
            if(data.has("albums")) {
                JSONArray array = data.getJSONArray("albums");
                for(int i = 0; i < array.length(); i++) {
                    RowData newRow = new RowData("", "", "");
                    newRow.descript = array.getJSONObject(i).getString("name");
                    ArrayList<String> imgs = new ArrayList<String>();
                    JSONArray photos = array.getJSONObject(i).getJSONArray("photos");
                    for(int j = 0; j < photos.length(); j++) {
                        imgs.add(photos.getJSONObject(j).getString("url"));
                    }
                    newRow.imgs = imgs;
                    detailAdapter.add(newRow);
                }
            }
            else {
                RowData newRow = new RowData("", "", "");
                newRow.descript = "No Album can be found!";
                detailAdapter.add(newRow);

            }

            if(data.has("posts")) {
                JSONArray array = data.getJSONArray("posts");
                for(int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = array.getJSONObject(i);
                        //String imgUrl = (String) obj.getJSONObject("picture").getJSONObject("data").get("url");

                        RowData newRow = new RowData("", "", (String) obj.get("id"));
                        newRow.message = (String) obj.get("message");
                        newRow.post_time = (String) obj.get("created_time");
                        newRow.name = poster_name;
                        newRow.image = img;
                        msgAdapter.add(newRow);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            else {
                RowData newRow = new RowData("", "", "");
                newRow.message = "No Post can be found!";
                newRow.post_time = "";
                newRow.name = "";
                newRow.image = "";
                msgAdapter.add(newRow);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    public void toggleImages(View v) {

        View img_layout = (View) v.getTag();
        if(img_layout.getVisibility() != View.VISIBLE)
            img_layout.setVisibility(View.VISIBLE);
        else
            img_layout.setVisibility(View.GONE);
    }


    public void displayAlbum(View v) {
        listView.setAdapter(detailAdapter);
        detailAdapter.notifyDataSetChanged();
    }

    public void displayPost(View v) {
        listView.setAdapter(msgAdapter);
        msgAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.detail, menu);

        ArrayList<String> ids = new ArrayList<String>(Arrays.asList(resultPrefer.getString("ids", "").split(" , ")));
        if(getIdxByValue(ids,detailId) != -1) {
            //change the add favor to remove favor
            menu.findItem(R.id.add_favor).setTitle("Remove Favor");
            remove = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_favor) {
            if(remove) {
                removeFavor(detailId, poster_name, img, this);
                item.setTitle("Add Favor");
            }
            else {
                addFavor(detailId, poster_name, img, this);
                item.setTitle("Remove Favor");
            }
            remove = !remove;
            return true;
        }
        else if (id == R.id.post_fb) {

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                        .build();
                shareDialog.show(linkContent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        LoginManager.getInstance().logOut();
        super.onStop();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        return true;
    }
}
