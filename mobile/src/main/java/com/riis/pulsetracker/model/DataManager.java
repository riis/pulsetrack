package com.riis.pulsetracker.model;

import android.app.Activity;

import com.riis.pulsetracker.util.AppEvent;

import java.util.Observable;


public class DataManager extends Observable {

    private static DataManager mDataManager = null;
    private FitHeartRateModel mFitHeartRateModel;
    private Profile mProfile;

    private DataManager() {
    }

    public static DataManager getInstance() {
        if (mDataManager == null) {
            mDataManager = new DataManager();
        }
        return mDataManager;
    }

    public boolean isSetup() {

        if (mProfile == null || mProfile.getAge() < 1) {
            return false;
        }

        return true;
    }

    public void saveProfile(Activity activity) {
        getProfile(activity).saveData(activity);

        setChanged();
        notifyObservers(mProfile);
        clearChanged();

        setChanged();
        notifyObservers(new AppEvent());
        clearChanged();
    }



    public Profile getProfile(Activity activity) {
        if (mProfile == null) {
            mProfile = new Profile(activity);
        }
        return mProfile;
    }

    public void setFitHeartRateModel(FitHeartRateModel model) {
        mFitHeartRateModel = model;
        setChanged();
        notifyObservers(mFitHeartRateModel);
        clearChanged();
    }
}
