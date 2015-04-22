package com.riis.pulsetracker.model;


import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Math.round;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FitHeartRateSummary {

    private static final String DATE_FORMAT_MILLISECONDS = "yyyy.MM.dd HH:mm:ss";
    private static final String DATE_SHORT_TIME_FORMAT = "HH:mm";
    private static final String DATE_SHORT_DAY_FORMAT = "EEE";
    public static String FIELD_NAME_AVERAGE = "average";
    public static String FIELD_NAME_MAX = "max";
    public static String FIELD_NAME_MIN = "min";
    private String mType;
    private String mStartDate;
    private String mEndDate;
    private int mAvgBPM;
    private int mMinBPM;
    private int mMaxBPM;
    private Calendar mCalendarDate;


    public Calendar getCalendarDate() {
        return mCalendarDate;
    }

    public FitHeartRateSummary(DataPoint dp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MILLISECONDS);
        mType = dp.getDataType().getName();    //com.google.heart_rate.summary
        mStartDate = dateFormat.format(dp.getStartTime(MILLISECONDS));
        mEndDate = dateFormat.format(dp.getEndTime(MILLISECONDS));

        mCalendarDate = Calendar.getInstance();

        try {
            mCalendarDate.setTime(dateFormat.parse(mStartDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        extractFitFields(dp);
    }

    private void extractFitFields(DataPoint dp) {


        try {

            for (Field field : dp.getDataType().getFields()) {
                String name = field.getName();
                String text;
                if (name.equalsIgnoreCase(FIELD_NAME_AVERAGE)) {
                    text = dp.getValue(field).toString();
                    mAvgBPM = round(Float.valueOf(text));
                } else if (name.equalsIgnoreCase(FIELD_NAME_MAX)) {
                    text = dp.getValue(field).toString();
                    mMaxBPM = round(Float.valueOf(text));
                } else if (name.equalsIgnoreCase(FIELD_NAME_MIN)) {
                    text = dp.getValue(field).toString();
                    mMinBPM = round(Float.valueOf(text));

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public String getDateShortForm() {

        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_MILLISECONDS);
        DateFormat sdf = new SimpleDateFormat(DATE_SHORT_DAY_FORMAT);
        String value = "";

        try {
            cal.setTime(df.parse(mEndDate));
            value = sdf.format(cal.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return value;
    }


    public String getStartDate() {
        return mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }


    public String getAverageBPMText() {
        return Integer.toString(mAvgBPM);
    }


    public int getMinBPM() {
        return mMinBPM;
    }

    public int getMaxBPM() {
        return mMaxBPM;
    }

    public int getAverageBPM() {
        return mAvgBPM;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getStartDate() + " - " + getEndDate());
        return sb.toString();
    }
}
