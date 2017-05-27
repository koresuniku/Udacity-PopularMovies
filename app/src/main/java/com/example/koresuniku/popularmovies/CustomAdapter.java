package com.example.koresuniku.popularmovies;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mValues;

    public CustomAdapter(Context context, List<String> values) {
        mContext = context;
        mValues = values;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer, parent, false);

        TextView textView = (TextView) convertView.findViewById(R.id.trailer_desc);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.play_trailer);
        imageView.setImageResource(R.drawable.play);
        textView.setText(mValues.get(position));
        textView.setTextColor(R.color.listview_text);
        return convertView;
    }
}
