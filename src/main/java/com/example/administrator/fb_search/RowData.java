package com.example.administrator.fb_search;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/25.
 */

public class RowData {
    String image, name, id;
    Boolean favor;
    String message, post_time;
    String descript;
    ArrayList<String> imgs;
    RowData(String _image, String _name, String _id) {
        image = _image;
        name = _name;
        id = _id;
        favor = false;
    }


}
