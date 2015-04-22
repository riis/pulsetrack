package com.riis.pulsetrack.dashboard;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.ValueDependentColor;
import com.riis.pulsetrack.R;
import com.riis.pulsetrack.model.FitHeartRateBPM;
import com.riis.pulsetrack.model.FitHeartRateModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import butterknife.ButterKnife;

import static java.lang.Math.round;

public class DailyFragment extends DashboardFragment {

    public DailyFragment() {
        // required fragment constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_daily2, container, false);
        ButterKnife.inject(this, rootView);
        getActivity().getActionBar().setTitle("PulseTrack - Daily Dashboard");
        return rootView;
    }


    void displayDailyChart(FitHeartRateModel model) {


        ArrayList<FitHeartRateBPM> heartRateList = new ArrayList<FitHeartRateBPM>();
        for (Map.Entry<Date, FitHeartRateBPM> entry : model.getTimeMap().entrySet()) {
            heartRateList.add(entry.getValue());
        }

        if (heartRateList.size() > 0) {
            FitHeartRateBPM firstBPM = heartRateList.get(0);
            FitHeartRateBPM midBPM = heartRateList.get(round(heartRateList.size() / 2));
            FitHeartRateBPM lastBPM = heartRateList.get(heartRateList.size() - 1);

            GraphView.GraphViewData[] graphViewDataArray = new GraphView.GraphViewData[3];

            graphViewDataArray[0] = new GraphView.GraphViewData(0, firstBPM.getFitBPM());
            graphViewDataArray[1] = new GraphView.GraphViewData(1, midBPM.getFitBPM());
            graphViewDataArray[2] = new GraphView.GraphViewData(2, lastBPM.getFitBPM());

            String[] labelsArray = new String[3];
            labelsArray[0] = firstBPM.getTimeShortForm();
            labelsArray[1] = midBPM.getTimeShortForm();
            labelsArray[2] = lastBPM.getTimeShortForm();


            GraphViewSeries.GraphViewSeriesStyle seriesStyle = new GraphViewSeries.GraphViewSeriesStyle();
            seriesStyle.setValueDependentColor(new ValueDependentColor() {
                @Override
                public int get(GraphViewDataInterface data) {
                    // the higher the more red
                    return Color.rgb(248, 126, 48);
                }
            });

            GraphViewSeries graphViewSeries = new GraphViewSeries("BeatsPerMinute",
                    new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(248, 126, 48), 3),
                    graphViewDataArray);

            GraphView graphView = new BarGraphView(getActivity(), "BPM by Time");
            graphView.addSeries(graphViewSeries);
            graphView.setHorizontalLabels(labelsArray);
            graphView.getGraphViewStyle().setNumVerticalLabels(3);
            graphView.setScalable(true);
            graphView.setShowLegend(false);

            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.dailyGraphLayout);
            if (layout != null) {
                layout.addView(graphView);
            }
        }
    }

    void displayWeeklyChart(FitHeartRateModel model) {
    }

    void displayMinMaxAvg(FitHeartRateModel model) {
        minTextView.setText(model.getMinDailyBPM());
        maxTextView.setText(model.getMaxDailyBPM());
        avgTextView.setText(model.getAvgDailyBPM());
    }

}
