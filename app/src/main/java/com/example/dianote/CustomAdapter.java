package com.example.dianote;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<DataDetails>{

    private Activity context;
    private Context contextInstance;
    private List<DataDetails> dataDetailsList;

    public CustomAdapter(Activity context, List<DataDetails> dataDetailsList1) {
        super(context, R.layout.list_layout, dataDetailsList1);
        this.context = context;
        this.dataDetailsList= dataDetailsList1;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.list_layout, null, true);

        DataDetails datadetails = dataDetailsList.get(position);
        TextView point = view.findViewById(R.id.pointValueId);
        TextView date = view.findViewById(R.id.dateId);
        TextView time = view.findViewById(R.id.timeId);
        TextView eatStatus = view.findViewById(R.id.eatStatusId);
        TextView note = view.findViewById(R.id.noteTextId);

        point.setText(datadetails.getPoints());
        date.setText(datadetails.getDate());
        time.setText(datadetails.getTime());
        eatStatus.setText(datadetails.getEatTime());

        if(datadetails.getNote().matches(""))
            note.setVisibility(View.GONE);
        else
            note.setText("  "+datadetails.getNote());

        /*String pointString = datadetails.getPoints();
        double pointInt = Double.parseDouble(pointString);

        if(pointInt >8)
            point.setBackgroundTintList(contextInstance.getResources().getColorStateList(R.color.red));*/

        return view;
    }
}
