package com.example.administrator.fb_search;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.administrator.fb_search.ResultActivity.getIdxByValue;

/**
 * Created by Administrator on 2017/4/25.
 */

public class RowAdapter extends ArrayAdapter {

    public RowAdapter(Context context, int resource) {
        super(context, resource);
    }

    List<RowData> list = new ArrayList<RowData>();
    SharedPreferences preferences;

    public void add(RowData row) {
        super.add(row);
        list.add(row);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void clear() {
        super.clear();
        list.clear();
    }

    @Override
    public RowData getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder rowHolder;
        View row;
        row = convertView;
        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_layout, parent, false);
            rowHolder = new RowHolder((TextView) row.findViewById(R.id.row_name), (ImageView) row.findViewById(R.id.row_img), (ImageButton) row.findViewById(R.id.row_favor));

            row.setTag(rowHolder);
        }
        else
            rowHolder = (RowHolder) row.getTag();



        RowData rowData = this.getItem(position);
        row.findViewById(R.id.row_btn).setTag(rowData.id+"||"+rowData.image);
        rowHolder.viewName.setText(rowData.name);
        Picasso.with(getContext()).load(rowData.image).into(rowHolder.viewImg);

        //check whether in favor
        ArrayList<String> images = new ArrayList<String>(Arrays.asList(preferences.getString("images", "").split(" , ")));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(preferences.getString("names", "").split(" , ")));
        ArrayList<String> ids = new ArrayList<String>(Arrays.asList(preferences.getString("ids", "").split(" , ")));
        if(images.get(0).equals("")) images.remove(0);
        if(names.get(0).equals("")) names.remove(0);
        if(ids.get(0).equals("")) ids.remove(0);
        if(getIdxByValue(ids, rowData.id) != -1)
            rowData.favor = true;
        else
            rowData.favor = false;

        rowHolder.viewFavor.setTag(R.id.favor, rowData.favor);
        rowHolder.viewFavor.setTag(R.id.id, rowData.id);
        rowHolder.viewFavor.setTag(R.id.name, rowData.name);
        rowHolder.viewFavor.setTag(R.id.image, rowData.image);


        if(rowData.favor)
            rowHolder.viewFavor.setImageResource(R.drawable.favorites_on);
        else
            rowHolder.viewFavor.setImageResource(R.drawable.favorites_off);

        return row;
    }

    static class RowHolder {
        TextView viewName;
        ImageButton viewFavor;
        ImageView viewImg;

        RowHolder(TextView name, ImageView img, ImageButton favor) {
            viewName = name;
            viewImg = img;
            viewFavor = favor;
        }
    }


}
