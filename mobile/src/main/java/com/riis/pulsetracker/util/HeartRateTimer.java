package com.riis.pulsetracker.util;

import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riis.pulsetracker.R;
import com.riis.pulsetracker.model.HeartRate;


public class HeartRateTimer extends CountDownTimer {



    TextView mCurrentBPMTextView;
    int mTick = 0;
    int mValue = 0;
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public HeartRateTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    RelativeLayout mLayout;
    HeartRate mHeartRate;
    public HeartRateTimer(long millisInFuture, long countDownInterval, TextView view, Integer value, RelativeLayout layout,
                          HeartRate heartRate) {
        super(millisInFuture, countDownInterval);
        mCurrentBPMTextView = view;
        mValue = value.intValue();
        mLayout = layout;
        mHeartRate = heartRate;
    }

    @Override
    public void onTick(long millisUntilFinished) {


        mCurrentBPMTextView.setText(Integer.toString(mTick = mTick + 10));
        mCurrentBPMTextView.refreshDrawableState();
    }

    @Override
    public void onFinish() {
            mCurrentBPMTextView.setText(Integer.toString(mValue
            ));

            scaleView(mCurrentBPMTextView, 0.6f, 1f);

            if(mHeartRate.isBelowRange()){
                mLayout.setBackgroundColor(mLayout.getResources().getColor(R.color.pulse_light_blue));
                mCurrentBPMTextView.setTextColor(mLayout.getResources().getColor(R.color.pulse_light_blue));
            }else if(mHeartRate.isAboveRange()){
                mLayout.setBackgroundColor(mLayout.getResources().getColor(R.color.pulse_red));
                mCurrentBPMTextView.setTextColor(mLayout.getResources().getColor(R.color.pulse_red));
            }else if(mHeartRate.isWithinRange()){
                mLayout.setBackgroundColor(mLayout.getResources().getColor(R.color.pulse_green));
                mCurrentBPMTextView.setTextColor(mLayout.getResources().getColor(R.color.pulse_green));
            }




    }


    public void scaleView(View v, float startScale, float endScale) {

        ScaleAnimation scaleAnimation = new ScaleAnimation(1,2,1,2);
        scaleAnimation.setDuration(500);
        v.startAnimation(scaleAnimation);

        scaleAnimation = new ScaleAnimation(2,1,2,1);
        scaleAnimation.setDuration(500);



        scaleAnimation.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(scaleAnimation);
    }

}
