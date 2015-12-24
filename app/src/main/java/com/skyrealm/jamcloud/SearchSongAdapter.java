package com.skyrealm.jamcloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Brockyy on 12/23/2015.
 */
public class SearchSongAdapter extends BaseAdapter {
    ArrayList<ArrayList<String>> track_info;
    private static LayoutInflater inflater=null;
    public SearchSongAdapter(Context context, ArrayList<ArrayList<String>> track_info)
    {
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.track_info = track_info;
    }

    @Override
    public int getCount() {
        return this.track_info.size();
    }

    @Override
    public Object getItem(int position) {
        return track_info.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView songNameTextView, artistNameTextView;
        ImageView trackIconImageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        Holder holder = new Holder();
        rowView = inflater.inflate(R.layout.song_list_items, null);
        holder.songNameTextView = (TextView) rowView.findViewById(R.id.songName);
        holder.artistNameTextView = (TextView) rowView.findViewById(R.id.artistNameTextView);

        holder.songNameTextView.setText(track_info.get(position).get(0));
        holder.artistNameTextView.setText(track_info.get(position).get(2));


        return rowView;
    }
}
