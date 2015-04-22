package com.riis.pulsetracker.model;

import static java.lang.Math.round;


public class HeartRate {

    private String mGender;
    private int mAge;
    private int mBeatsPerMinute;
    private boolean belowRange = false;
    private boolean aboveRange = false;
    private boolean withinRange = false;

    public HeartRate(String gender, int age, int beatsPerMinute) {
        mGender = gender;
        mAge = age;
        mBeatsPerMinute = beatsPerMinute;
    }

    public boolean isBelowRange() {
        if (mBeatsPerMinute < getLowEndOfTargetRange()) {
            belowRange = true;
        }
        return belowRange;
    }

    public boolean isAboveRange() {

        if (mBeatsPerMinute > getHighEndOfTargetRange()) {
            aboveRange = true;
        }
        return aboveRange;
    }

    public boolean isWithinRange() {
        if (mBeatsPerMinute <= getHighEndOfTargetRange()
                && mBeatsPerMinute >= getLowEndOfTargetRange()) {
            withinRange = true;
        }
        return withinRange;
    }

    public long getMaxHeartRate() {
        return 220 - mAge;
    }

    /**
     * Maximum Heart Rate Calculation: 220 – Age
     * Target Heart Rate – Low End of Range: (220-Age)*50% (round to whole number)
     * Target Heart Rate – High End of Range: (220-Age)*85% (round to whole number)
     */

    public long getLowEndOfTargetRange() {
        return round(getMaxHeartRate() * .50);
    }

    public long getHighEndOfTargetRange() {
        return round(getMaxHeartRate() * .85);
    }

    public Long[] getHeartRateScale() {
        long bottom = 0;
        Long[] scale = {bottom, getLowEndOfTargetRange(), getHighEndOfTargetRange(), getMaxHeartRate()};
        return scale;
    }

    public int getBeatsPerMinute() {
        return mBeatsPerMinute;
    }


}
