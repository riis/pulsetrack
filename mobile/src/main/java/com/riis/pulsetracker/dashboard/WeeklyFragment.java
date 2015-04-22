package com.riis.pulsetracker.dashboard;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.riis.pulsetracker.R;
import com.riis.pulsetracker.model.FitHeartRateModel;
import com.riis.pulsetracker.model.FitHeartRateSummary;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class WeeklyFragment extends DashboardFragment {

    public WeeklyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weekly, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setTitle("PulseTracker - Weekly Dashboard");

    }


    void displayDailyChart(FitHeartRateModel model) {

    }

    void displayMinMaxAvg(FitHeartRateModel model) {
        minTextView.setText(model.getMinWeeklyBPM().toString());

        minTextView.refreshDrawableState();
        maxTextView.setText(model.getMaxWeeklyBPM().toString());
        maxTextView.refreshDrawableState();
        avgTextView.setText(model.getAvgWeeklyBPM().toString());
        avgTextView.refreshDrawableState();
    }

    void displayWeeklyChart(FitHeartRateModel model) {

        ArrayList<FitHeartRateSummary> fitDataList = model.getWeeklyList();
        if (fitDataList.size() > 0) {
            ArrayList<GraphView.GraphViewData> graphViewDataArrayList = new ArrayList<GraphView.GraphViewData>();


            for (int i = 0; i < 7 && i < fitDataList.size(); i++) {
                graphViewDataArrayList.add(
                        new GraphView.GraphViewData(i, fitDataList.get(i).getAverageBPM()));
            }

            GraphViewSeries graphViewSeries = new
                    GraphViewSeries(graphViewDataArrayList.toArray(
                    new GraphView.GraphViewData[graphViewDataArrayList.size()]));
            GraphView graphView = new BarGraphView(getActivity(), "Weekly Heart Rate");
            graphView.addSeries(graphViewSeries);

            //   ArrayList<String> timeList = new ArrayList<String>();
            //   for (int i = 0; i < 7 && i < fitDataList.size(); i++) {
            //       String dateTime = fitDataList.get(i).getDateShortForm();
            //       timeList.add(dateTime);
            //   }

            String[] days = {"Su", "M", "T", "W", "Th", "F", "Sa"};

            graphView.setHorizontalLabels(days);
            LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.weeklyLayout);
            if (layout != null) {
                layout.addView(graphView);
            }
        }

    }


}
