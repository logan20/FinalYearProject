package com.logan20.trackfitfinal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by kwasi on 4/4/2017.
 */

public class ViewHeartrateFragment extends Fragment implements WatchListener{
    private static final int PROJECTION = 30;
    private static long TIMEOUT = 1000;
    private TextView tv;
    private LineChart lc;
    private ArrayList<Entry> entryList;
    private int x;
    private Runnable updater;
    private View v;
    private int currVal;
    private boolean canUpdate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_view_heartrate,null);
        lc = (LineChart)v.findViewById(R.id.lc_chart);
        tv = ((TextView)v.findViewById(R.id.tv_heartRate));
        entryList = new ArrayList<>();
        HeartRateListenerService.setListener(this);
        x=0;
        canUpdate=false;
        updater = new Runnable() {
            @Override
            public void run() {
                if (canUpdate){
                    onHeartRateChange(currVal);
                }
                v.postDelayed(this,TIMEOUT);
            }
        };
        v.postDelayed(updater,TIMEOUT);
        return v;
    }

    @Override
    public void onWatchDataReceive() {

    }

    @Override
    public void onHeartRateChange(final int newVal) {
        synchronized (this){
            canUpdate=true;
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(String.valueOf(newVal));
                }
            });
            currVal = newVal;
            entryList.add(new Entry(x,newVal));
            x++;
            LineDataSet lineDataSet = new LineDataSet(entryList,"");;
            if (entryList.size()>PROJECTION){
                lineDataSet = new LineDataSet(entryList.subList(entryList.size()-PROJECTION,entryList.size()-1),"");
            }
            lc.setData(new LineData(lineDataSet));
            lc.post(new Runnable() {
                @Override
                public void run() {
                    lc.invalidate();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HeartRateListenerService.setListener(null);
    }
}
