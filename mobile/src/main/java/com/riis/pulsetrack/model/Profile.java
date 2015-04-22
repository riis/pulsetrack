package com.riis.pulsetrack.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.riis.pulsetrack.util.CalendarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Profile {

    public static String GENDER_MALE = "Male";
    public static String GENDER_FEMALE = "Female";
    private static String PREF_FIRST_NAME = "firstname";
    private static String PREF_LAST_NAME = "lastname";
    private static String PREF_GENDER = "gender";
    private static String PREF_DOB = "dob";
    private static String PREF_RESTING_RATE = "restingrate";
    private String mFirstName;
    private String mLastName;
    private String mGender;
    private Calendar mDOB;
    private String mBirthDate;
    private Integer mRestingHeartRate;
    private int mAge;
    private SharedPreferences mPrefs;


    public Profile(Activity activity) {
        readExistingData(activity);
    }

    public String getBirthDate() {
        return mBirthDate;
    }

    public void setBirthDate(String date) {
        mBirthDate = date;
    }

     void saveData(Activity activity) {

        SharedPreferences.Editor editor = getSharedPreference(activity).edit();
        editor.putString(PREF_FIRST_NAME, getFirstName());
        editor.putString(PREF_LAST_NAME, getLastName());
        editor.putString(PREF_GENDER, getGender());
        editor.putString(PREF_DOB, mBirthDate);
        editor.putInt(PREF_RESTING_RATE, getRestingHeartRate());
        editor.commit();

    }

    private SharedPreferences getSharedPreference(Activity activity) {
        if (mPrefs == null) {
            mPrefs = activity.getPreferences(Context.MODE_PRIVATE);
        }
        return mPrefs;
    }

    private void readExistingData(Activity activity) {

        try {

            mFirstName = getSharedPreference(activity).getString(PREF_FIRST_NAME, "");
            mLastName = getSharedPreference(activity).getString(PREF_LAST_NAME, "");
            mGender = getSharedPreference(activity).getString(PREF_GENDER, "");
            mBirthDate = getSharedPreference(activity).getString(PREF_DOB, "");
            mRestingHeartRate = getSharedPreference(activity).getInt(PREF_RESTING_RATE, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("mm-dd-yyyy", Locale.getDefault());
            mDOB = Calendar.getInstance();
            mDOB.setTime(sdf.parse(mBirthDate));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public int getAge() {

        try {
                SimpleDateFormat sdf = new SimpleDateFormat("mm-dd-yyyy", Locale.getDefault());
                mDOB = Calendar.getInstance();

            mDOB.setTime(sdf.parse(mBirthDate));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mAge = CalendarUtil.getAge(mDOB);


        return mAge;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public void setDOB(Calendar dob) {
        mDOB = dob;
    }

    public Integer getRestingHeartRate() {
        return mRestingHeartRate;
    }

    public void setRestingHeartRate(Integer rhr) {
        mRestingHeartRate = rhr;
    }

}
