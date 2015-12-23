package com.skyrealm.jamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brockyy on 12/16/2015.
 */
public class listadapter extends BaseAdapter {

    ArrayList<ArrayList<String>> content;
    LayoutInflater inflater  = null;
    public listadapter(Context context, ArrayList<ArrayList<String>> populate)
    {
        content = new ArrayList<ArrayList<String>>();
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.content = populate;
    }
    @Override
    public int getCount() {
        return this.content.size();
    }

    @Override
    public Object getItem(int position) {
        return this.content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        ImageView imageView;
        TextView personone;
        TextView persontwo;
        TextView paid;
        ImageView imageView2;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item, null);

        return rowView;
    }
}
