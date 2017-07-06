package com.example.administrator.fb_search;

import android.content.Context;
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
import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class MessageRowAdapter extends ArrayAdapter {

    public MessageRowAdapter(Context context, int resource) {
        super(context, resource);
    }

    List<RowData> list = new ArrayList<RowData>();


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
            row = inflater.inflate(R.layout.msg_row, parent, false);
            rowHolder = new RowHolder((TextView) row.findViewById(R.id.row_time), (ImageView) row.findViewById(R.id.row_img),
                    (TextView) row.findViewById(R.id.row_msg), (TextView) row.findViewById(R.id.row_name));

            row.setTag(rowHolder);
        }
        else
            rowHolder = (RowHolder) row.getTag();


        RowData rowData = this.getItem(position);
        rowHolder.viewMsg.setText(rowData.message);
        rowHolder.viewTime.setText(rowData.post_time);
        rowHolder.viewName.setText(rowData.name);
        if(rowData.image != "")
            Picasso.with(getContext()).load(rowData.image).into(rowHolder.viewImg);

        return row;
    }

    static class RowHolder {
        TextView viewTime;
        TextView viewMsg;
        ImageView viewImg;
        TextView viewName;

        RowHolder(TextView time, ImageView img, TextView msg, TextView name) {
            viewTime = time;
            viewImg = img;
            viewMsg = msg;
            viewName = name;
        }
    }


}
