package com.example.administrator.fb_search;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.fb_search.DetailActivity.detailContext;

/**
 * Created by Administrator on 2017/4/25.
 */

public class DetailRowAdapter extends ArrayAdapter {

    public DetailRowAdapter(Context context, int resource) {
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
            row = inflater.inflate(R.layout.detail_row, parent, false);
            rowHolder = new RowHolder((Button) row.findViewById(R.id.row_detail), (LinearLayout)row.findViewById(R.id.detailAlbum));

            row.setTag(rowHolder);
        }
        else
            rowHolder = (RowHolder) row.getTag();



        RowData rowData = this.getItem(position);

        rowHolder.btn.setText(rowData.descript);
        rowHolder.btn.setTag(rowHolder.images);
        if(rowHolder.init) {
            for (String img : rowData.imgs) {
                ImageView newImg = new ImageView(detailContext);
                Picasso.with(getContext()).load(img).into(newImg);
                rowHolder.images.addView(newImg);
            }
        }
        rowHolder.init = false;
        //need to check whether in favor
        //rowHolder.viewFavor.setText(rowData.id);
        return row;
    }

    static class RowHolder {
        boolean init;
        Button btn;
        LinearLayout images;
        RowHolder(Button _btn, LinearLayout list) {
            btn = _btn;
            images = list;
            init = true;
        }
    }


}
