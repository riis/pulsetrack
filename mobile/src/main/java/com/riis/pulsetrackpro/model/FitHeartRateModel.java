package com.riis.pulsetrackpro.model;


import android.os.CountDownTimer;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class FitHeartRateModel {


    final private static String ZERO = "0";
    private static final int REQUEST_OAUTH = 1;
    private static final String FIT_DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    private ArrayList<FitHeartRateSummary> mWeeklyList;
    private ArrayList<FitHeartRateBPM> mDailyList;
    private SortedMap<String, FitHeartRateBPM> mDailyMap;
    private SortedMap<Date, FitHeartRateBPM> mTimeMap;
    private SortedSet<Integer> mDailySet;
    private TreeSet<Integer> mMaxValueSet;
    private TreeSet<Integer> mMinValueSet;
    private TreeSet<Integer> mAverageSet;

    private SortedMap<Calendar, Integer> mWeeklyMap;

    private CountDownTimer timer;

    public FitHeartRateModel() {
        mWeeklyList = new ArrayList<FitHeartRateSummary>();
    }

    public ArrayList<FitHeartRateBPM> getDailyHeartRateList() {
        mDailyList = new ArrayList<FitHeartRateBPM>(mDailyMap.values());
        return mDailyList;
    }

    public ArrayList<FitHeartRateSummary> getWeeklyList() {
        if (mWeeklyList == null) {
            mWeeklyList = new ArrayList<FitHeartRateSummary>();
        }
        return mWeeklyList;
    }

    public String getLastBPM() {

        if (mDailyMap == null || mDailyMap.size() < 1) {
            return "0";
        } else {
            FitHeartRateBPM bpm = mDailyMap.get(mDailyMap.firstKey());
            return (bpm.getFitBPMText() != null) ? bpm.getFitBPMText() : ZERO;
        }

    }

    public Integer getMaxWeeklyBPM() {
        return mMaxValueSet.last();
    }

    public Integer getMinWeeklyBPM() {
        Integer value = 0;
        if(mMaxValueSet != null && mMaxValueSet.first() != null){
            value = mMaxValueSet.first();
        }
        return value;

    }

    public Integer getAvgWeeklyBPM() {
        Integer total = 0;
        Integer average = 0;
        Integer size = mAverageSet.size();

        for (Integer value : mAverageSet) {
            total = total + value;
        }

        average = total / size;
        return average;
    }

    public String getMaxWeeklyBPMText() {
        return getMaxWeeklyBPM().toString();
    }

    public String getMinWeeklyBPMText() {
        return getMaxWeeklyBPM().toString();
    }

    public String getAvgWeeklyBPMText() {
        return getAvgWeeklyBPM().toString();
    }

    public String getMaxDailyBPM() {


        if(mDailySet.size() < 1){
            return ZERO;
        }
        return Integer.toString(mDailySet.last());
    }

    public String getMinDailyBPM() {

        if(mDailySet.size() < 1){
            return ZERO;
        }

        return Integer.toString(mDailySet.first());
    }

    public String getAvgDailyBPM() {

        Integer total = 0;
        Integer size = mDailySet.size();
        if (size < 1) {
            return ZERO;
        }

        for (Integer value : mDailySet) {
            total = total + value;
        }

        Integer average = total / size;
        return average.toString();
    }

    public SortedMap<String, FitHeartRateBPM> getDailyMap() {
        return mDailyMap;
    }

    public SortedMap<Date, FitHeartRateBPM> getTimeMap() {
        return mTimeMap;
    }

    public void storeFitDailyData(DataReadResult dataReadResult) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(FIT_DATE_FORMAT);

        mDailyMap = new TreeMap<String, FitHeartRateBPM>();
        mTimeMap = new TreeMap<Date, FitHeartRateBPM>();
        mDailySet = new TreeSet<Integer>();

        try {
            if (dataReadResult.getDataSets().size() > 0) {

                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    for (DataPoint dp : dataSet.getDataPoints()) {

                        for (Field field : dp.getDataType().getFields()) {
                            FitHeartRateBPM fitHeartRateBPM = new FitHeartRateBPM(dp);
                            mDailyMap.put(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)),
                                    fitHeartRateBPM);

                            mTimeMap.put(new Date(dp.getEndTime(TimeUnit.MILLISECONDS)), fitHeartRateBPM);

                            mDailySet.add(fitHeartRateBPM.getFitBPM());

                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isEmpty(DataReadResult readResult){

        boolean empty = false;

        if(readResult.getBuckets().size() < 1
                && readResult.getDataSets().size() < 1) {
            empty = true;
        }


    return empty;

    }

    public void storeFitWeeklyData(DataReadResult dataReadResult) {

         try {

            mMaxValueSet = new TreeSet<Integer>();
            mMinValueSet = new TreeSet<Integer>();
            mAverageSet = new TreeSet<Integer>();
            mWeeklyMap = new TreeMap<Calendar, Integer>();



            if (dataReadResult.getBuckets().size() > 0) {
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            FitHeartRateSummary summary = new FitHeartRateSummary(dp);
                            mWeeklyList.add(summary);
                            mMaxValueSet.add(summary.getMaxBPM());
                            mMinValueSet.add(summary.getMinBPM());
                            mAverageSet.add(summary.getAverageBPM());
                            mWeeklyMap.put(summary.getCalendarDate(), summary.getAverageBPM());
                        }
                    }
                }
            } else if (dataReadResult.getDataSets().size() > 0) {

                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        FitHeartRateSummary summary = new FitHeartRateSummary(dp);
                        mWeeklyList.add(summary);
                        mMaxValueSet.add(summary.getMaxBPM());
                        mMinValueSet.add(summary.getMinBPM());
                        mAverageSet.add(summary.getAverageBPM());
                        mWeeklyMap.put(summary.getCalendarDate(), summary.getAverageBPM());

                    }
                }
            }
       } catch (Exception e) {
             e.printStackTrace();
         }


    }


}
