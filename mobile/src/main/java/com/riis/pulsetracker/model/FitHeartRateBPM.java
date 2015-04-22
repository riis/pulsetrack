package com.riis.pulsetracker.model;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;

/**
 * Created by davidmckinnon on 11/20/14.
 */
public class FitHeartRateBPM {


    //com.google.heart_rate.bpm

    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private static final String DATE_SHORT_TIME_FORMAT = "HH:mm";
    private static final String DATE_SHORT_DAY_FORMAT = "EEE";
    public static String FIELD_BPM = "bpm";
    private String mType;
    private String mStartDate;
    private String mEndDate;
    private int mBPM;


    public FitHeartRateBPM(DataPoint dp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        mType = dp.getDataType().getName();    //com.google.heart_rate.bpm
        mStartDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
        mEndDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));

        extractFitFields(dp);
    }

    private void extractFitFields(DataPoint dp) {

        for (Field field : dp.getDataType().getFields()) {
            String name = field.getName();
            String text = null;
            if (name.equalsIgnoreCase(FIELD_BPM)) {
                text = dp.getValue(field).toString();
                mBPM = round(Float.valueOf(text));
            }
        }
    }


    public String getStartDate() {
        return mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }


    public String getTimeShortForm() {


        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        int hour = 0;
        int minute = 0;
        String value = "00:00";
        String ampm = "am";
        try {
            cal.setTime(df.parse(mStartDate));

            int hourofday = cal.get(Calendar.HOUR_OF_DAY);


            if (hourofday >= 12) {
                ampm = "pm";
            } else {
                ampm = "am";

            }

            String hourstring = "";

            if (hourofday == 0) {
                hourstring = "12";
                ampm = "am";
            } else if (hourofday < 12) {
                hourstring = hourofday + "";
                ampm = "am";
            } else if (hourofday == 12) {
                hourstring = "12";
                ampm = "pm";
            } else {
                hourstring = hourofday - 12 + "";
                ampm = "pm";
            }


            hour = cal.get(Calendar.HOUR);

            minute = cal.get(Calendar.MINUTE);
            String min = "00";
            if (minute < 10) {
                min = "0" + minute;
            } else {
                min = minute + "";
            }
            value = hourstring + ":" + min + ampm;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return value;
    }


    public String getFitBPMText() {
        return Integer.toString(mBPM);
    }

    public int getFitBPM() {
        return mBPM;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getStartDate() + " - " + getEndDate());
        return sb.toString();
    }


}
