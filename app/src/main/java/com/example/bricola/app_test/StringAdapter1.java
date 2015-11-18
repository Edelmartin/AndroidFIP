package com.example.bricola.app_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Fischer on 19/11/2015.
 */
public class StringAdapter1 extends ArrayAdapter<String>
{

    View.OnTouchListener yTouchListener;

    public StringAdapter1(Context context, ArrayList<String> values, View.OnTouchListener listener)
    {
        super(context, R.layout.list_item, values);
        yTouchListener = listener;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View d = inflater.inflate(R.layout.list_item, parent, false);

        TextView b = (TextView) d.findViewById(R.id.list_tv1);
        b.setText(getItem(position));

        d.setOnTouchListener(yTouchListener);

        return d;
    }

}
