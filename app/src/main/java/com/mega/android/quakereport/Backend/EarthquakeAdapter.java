package com.mega.android.quakereport.Backend;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mega.android.quakereport.DT.Earthquake;
import com.mega.android.quakereport.R;
import com.mega.android.quakereport.Util.Utils;
import android.graphics.drawable.GradientDrawable;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private int lastPosition = -1;
    private static final String TAG = EarthquakeAdapter.class.getSimpleName();
    private  Context mContext = null;
    private ArrayList<Earthquake> dataSet;

    // View lookup cache
    private static class ViewHolder {
        TextView tvMag ;
        TextView tvPlace ;
        TextView tvDate ;
        TextView tvTime;
        TextView tvLocation;
    }

    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquakeList) {
        super(context, R.layout.earthquakelayout, earthquakeList);
        this.mContext = context;
        this.dataSet = earthquakeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        Earthquake dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;// view lookup cache stored in tag
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.earthquakelayout, parent, false);

            viewHolder.tvMag = (TextView)convertView.findViewById(R.id.tvmag);
            viewHolder.tvPlace =(TextView)convertView.findViewById(R.id.tvplace);
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tvdate);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tvtime);
            viewHolder.tvLocation =(TextView)convertView.findViewById(R.id.tvlocation);

            result = convertView;
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //Animation ///////////////////////////////////////////////////////////////////////////////////////
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        ////////////////////////////////////////////////////////////////////////////////////////////
        //if I didn't write this casting it will be shown as SQUARE
        GradientDrawable magnitudeCircle = (GradientDrawable) viewHolder.tvMag.getBackground();
        magnitudeCircle.setColor(Utils.getMagnitudeColor(dataModel.getMag(),mContext));
        //Set the text
        viewHolder.tvMag.setText(Utils.getFormatterFloat(dataModel.getMag()));

        //Split location info
        String string[] = Utils.splitter(dataModel.getPlace()) ;
        viewHolder.tvLocation.setText(string[0]);
        viewHolder.tvPlace.setText(string[1]);

        //Set the data to the controls.
        viewHolder.tvDate.setText(Utils.getDate(dataModel.getDateTime()));
        viewHolder.tvTime.setText(Utils.getTime(dataModel.getDateTime()));

        return convertView;
    }
}
