package com.example.administrator.fb_search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;

public class ResultActivity extends AppCompatActivity {
    ArrayList<JSONObject> jsons;
    ListView listView;
    RowAdapter rowAdapter;
    RowAdapter favorAdapter;
    //JSONObject json;
    String json;
    Context context;
    MainActivity main;
    String currentType;
    String detailImg;
    String detailId;


    static SharedPreferences resultPrefer;

    protected void setMain(MainActivity m) {
        main = m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        jsons = new ArrayList<JSONObject>();
        rowAdapter = new RowAdapter(this,R.layout.row_layout);
        rowAdapter.preferences = this.getSharedPreferences("favorData", Context.MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(rowAdapter);

        resultPrefer = this.getSharedPreferences("favorData", Context.MODE_PRIVATE);

        context = this;
        setTitle("Result");

        //load json data
        if(getIntent().hasExtra("json0")) {
            int i = 0;
            json = "";
            while (json != null) {
                json = getIntent().getExtras().getString("json" + String.valueOf(i));
                if (json != null) {
                    jsons.add(strToJSON(json));
                    i++;
                } else
                    break;
            }
            currentType = "user";
            switchTab(findViewById(R.id.userTab));
        }
        else if(getIntent().hasExtra("favor")) {
            currentType = "favor";
            displayFavor();
        }

    }

    public static void removeFavor(String id, String name, String image, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "favorData", Context.MODE_PRIVATE);
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(sharedPref.getString("images", "").split(" , ")));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(sharedPref.getString("names", "").split(" , ")));
        ArrayList<String> ids = new ArrayList<String>(Arrays.asList(sharedPref.getString("ids", "").split(" , ")));
        if(images.get(0).equals("")) images.remove(0);
        if(names.get(0).equals("")) names.remove(0);
        if(ids.get(0).equals("")) ids.remove(0);
        int idx = getIdxByValue(ids, id);
        if(idx != -1) {
            ids.remove(idx);
            names.remove(idx);
            images.remove(idx);
            Toast toast = Toast.makeText(context, "Favorite removed!", Toast.LENGTH_SHORT);
            toast.show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("images", arrayListToStr(images));
            editor.putString("names", arrayListToStr(names));
            editor.putString("ids", arrayListToStr(ids));
            editor.commit();
        }

    }


    public static void addFavor(String id, String name, String image, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "favorData", Context.MODE_PRIVATE);
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(sharedPref.getString("images", "").split(" , ")));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(sharedPref.getString("names", "").split(" , ")));
        ArrayList<String> ids = new ArrayList<String>(Arrays.asList(sharedPref.getString("ids", "").split(" , ")));
        if(images.get(0).equals("")) images.remove(0);
        if(names.get(0).equals("")) names.remove(0);
        if(ids.get(0).equals("")) ids.remove(0);

        if(getIdxByValue(ids, id) == -1) {
            images.add(image);
            names.add(name);
            ids.add(id);
            Toast toast = Toast.makeText(context, "Favorite added!", Toast.LENGTH_SHORT);
            toast.show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("images", arrayListToStr(images));
            editor.putString("names", arrayListToStr(names));
            editor.putString("ids", arrayListToStr(ids));
            editor.commit();
        }

    }


    public void toggleFavor(View v) {
        ImageButton favorBtn = (ImageButton) v.findViewById(R.id.row_favor);

        if((boolean)favorBtn.getTag(R.id.favor)) {
            favorBtn.setTag(R.id.favor, false);
            favorBtn.setImageResource(R.drawable.favorites_off);
            removeFavor((String) favorBtn.getTag(R.id.id), (String) favorBtn.getTag(R.id.name),
                    (String) favorBtn.getTag(R.id.image), this);
        }
        else {
            favorBtn.setTag(R.id.favor, true);
            favorBtn.setImageResource(R.drawable.favorites_on);
            addFavor((String) favorBtn.getTag(R.id.id), (String) favorBtn.getTag(R.id.name),
                    (String) favorBtn.getTag(R.id.image), this);
        }

        if(currentType.equals("favor")) {
            displayFavor();
        }

    }

    public static int getIdxByValue(ArrayList<String> list, String val) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(val)) return i;
        }
        return -1;
    }

    public static String arrayListToStr(ArrayList<String> list) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < list.size(); i++) {
            str.append(list.get(i));
            if(i < list.size() -1) str.append(" , ");
        }
        return str.toString();
    }


    public void displayRowJSON(JSONObject jsonObject, String type) {
        try {
            rowAdapter.clear();
            rowAdapter.notifyDataSetChanged();
            currentType = (String) type;

            JSONArray arr = jsonObject.getJSONArray("data");
            JSONArray subArr = arr.getJSONObject(0).getJSONArray("data");
            for (int i = 0; i < subArr.length(); i++) {
                JSONObject obj = subArr.getJSONObject(i);
                String imgUrl = (String) obj.getJSONObject("picture").getJSONObject("data").get("url");

                RowData newRow = new RowData(imgUrl, (String) obj.get("name"), (String) obj.get("id"));
                rowAdapter.add(newRow);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchTab(View v) {
        if(v == null || v.getTag() == null) return;

        for(JSONObject jsonObject : jsons) {
            try {
                if (jsonObject.get("type").equals(v.getTag())) {
                    displayRowJSON(jsonObject, (String) v.getTag());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void displayFavor() {
        favorAdapter = new RowAdapter(this,R.layout.row_layout);
        favorAdapter.preferences = this.getSharedPreferences("favorData", Context.MODE_PRIVATE);
        SharedPreferences sharedPref = context.getSharedPreferences(
                "favorData", Context.MODE_PRIVATE);
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(sharedPref.getString("images", "").split(" , ")));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(sharedPref.getString("names", "").split(" , ")));
        ArrayList<String> ids = new ArrayList<String>(Arrays.asList(sharedPref.getString("ids", "").split(" , ")));
        if(images.get(0).equals("")) images.remove(0);
        if(names.get(0).equals("")) names.remove(0);
        if(ids.get(0).equals("")) ids.remove(0);
        //should check whether all have equal length
        for(int i = 0; i < ids.size(); i++) {
            String imgUrl = images.get(i);
            RowData newRow = new RowData(imgUrl, names.get(i), ids.get(i));
            favorAdapter.add(newRow);
        }
        listView.setAdapter(favorAdapter);
        favorAdapter.notifyDataSetChanged();


    }


    public void getDetail(View v) {
        String tag = (String) v.findViewById(R.id.row_btn).getTag();
        detailImg = tag.split("\\|\\|")[1];
        detailId = tag.split("\\|\\|")[0];
        new DetailAjaxCall().execute(tag.split("\\|\\|")[0]);
    }

    public void displayDetail(String json) {
        if(json == null) return;
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("json", json);
        intent.putExtra("img", detailImg);
        intent.putExtra("id", detailId);
        startActivity(intent);
    }

    public static JSONObject strToJSON(String json) {
        JSONObject res = null;
        try {
            res = new JSONObject(json);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public int getJSONIdxByType(String type) {
        for(int i = 0; i < jsons.size(); i++) {
            try {
                if (jsons.get(i).get("type").equals(type)) {
                    return i;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void nextPage(View v) {
        if(v == null || currentType == null) return;
        for(JSONObject jsonObject : jsons) {
            try {
                if (jsonObject.get("type").equals(currentType)) {
                    JSONArray arr = jsonObject.getJSONArray("data");
                    JSONObject page = arr.getJSONObject(0).getJSONObject("paging");
                    if(page != null && page.get("next") != null) {
                        new PageCall().execute((String) page.get("next"));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void prevPage(View v) {
        if(v == null || currentType == null) return;
        for(JSONObject jsonObject : jsons) {
            try {
                if (jsonObject.get("type").equals(currentType)) {
                    JSONArray arr = jsonObject.getJSONArray("data");
                    JSONObject page = arr.getJSONObject(0).getJSONObject("paging");
                    if(page != null && page.get("previous") != null) {
                        new PageCall().execute((String) page.get("previous"));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class PageCall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String finalRes = "";
            try {
                URL url = new URL(params[0]);
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
                finalRes = str.toString();
                reader.close();
                stream.close();
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return finalRes;
        }

        @Override
        protected void onPostExecute(String result) {
            String newjson = "{type:"+currentType+",data:["+result+"]}";
            JSONObject newJSON = strToJSON(newjson);
            int idx = getJSONIdxByType(currentType);
            if(idx != -1) jsons.set(idx, newJSON);
            else return;
            displayRowJSON(newJSON, currentType);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }



    public class DetailAjaxCall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

                String finalRes = "";
                String urlStr = "http://cs571wrx.us-west-1.elasticbeanstalk.com/hw8_function.php?id=" + params[0];
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
                    finalRes = str.toString();
                    reader.close();
                    stream.close();
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            return finalRes;
        }

        @Override
        protected void onPostExecute(String result) {
            displayDetail(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    protected void onResume()
    {
        if(currentType.equals("favor"))
            displayFavor();
        else if(currentType.equals("user"))
            switchTab(findViewById(R.id.userTab));
        else if(currentType.equals("page"))
            switchTab(findViewById(R.id.pageTab));
        else if(currentType.equals("event"))
            switchTab(findViewById(R.id.eventTab));
        else if(currentType.equals("place"))
            switchTab(findViewById(R.id.placeTab));
        else if(currentType.equals("group"))
            switchTab(findViewById(R.id.groupTab));

        // TODO Auto-generated method stub
        super.onResume();
    }


}



