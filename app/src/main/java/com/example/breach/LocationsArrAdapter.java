package com.example.breach;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocationsArrAdapter extends ArrayAdapter {
    private Activity context;
    private ArrayList<String> arrLeft, arrRight;

    public LocationsArrAdapter(Activity context, ArrayList<String> arrLeft, ArrayList<String> arrRight) {
        super(context, R.layout.location_list_item, arrLeft.size() >= arrRight.size() ? arrLeft : arrRight);

        this.arrLeft = arrLeft;
        this.arrRight = arrRight;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View rowView, @NonNull ViewGroup parent) {
//        return super.getView(position, rowView, parent);
        ViewHolder myVH = new ViewHolder();

        if (rowView == null) {
            LayoutInflater myInflater = context.getLayoutInflater();
            rowView = myInflater.inflate(R.layout.location_list_item, parent, false);

            myVH.txtLeft = rowView.findViewById(R.id.textview_left);
            myVH.txtRight = rowView.findViewById(R.id.textview_right);

            rowView.setTag(myVH);
        } else {
            myVH = (ViewHolder) rowView.getTag();
        }

        myVH.txtLeft.setText(arrLeft.get(position));
        myVH.txtRight.setText(arrRight.get(position));
        return rowView;
    }

    private class ViewHolder {
        TextView txtLeft, txtRight;
    }
}
