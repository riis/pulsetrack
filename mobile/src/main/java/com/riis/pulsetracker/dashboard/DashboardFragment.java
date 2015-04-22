package com.riis.pulsetracker.dashboard;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riis.pulsetracker.R;
import com.riis.pulsetracker.model.DataManager;
import com.riis.pulsetracker.model.FitHeartRateModel;
import com.riis.pulsetracker.model.HeartRate;
import com.riis.pulsetracker.model.Profile;

import java.util.Observable;
import java.util.Observer;

import butterknife.InjectView;

public abstract class DashboardFragment extends Fragment implements Observer {


    @InjectView(R.id.minTextView)
    TextView minTextView;
    @InjectView(R.id.maxTextView)
    TextView maxTextView;
    @InjectView(R.id.avgTextView)
    TextView avgTextView;
    @InjectView(R.id.currentBpmTextView)
    TextView currentBpmTextView;
    @InjectView(R.id.middleBeginTextView)
    TextView middleBeginTextView;
    @InjectView(R.id.middleEndTextView)
    TextView middleEndTextView;
    @InjectView(R.id.endTextView)
    TextView endTextView;
    @InjectView(R.id.aboveValueText)
    TextView aboveValueText;
    @InjectView(R.id.belowValueText)
    TextView belowValueText;
    @InjectView(R.id.midValueText)
    TextView midValueText;

    @InjectView(R.id.imageViewNeedleLow)
    ImageView lowNeedle;
    @InjectView(R.id.imageViewNeedleMid)
    ImageView midNeedle;
    @InjectView(R.id.imageViewNeedleHigh)
    ImageView highNeedle;

    @InjectView(R.id.toplayout)
    RelativeLayout mTopLayout;


    private FitHeartRateModel fitHeartRateModel;


    public DashboardFragment() {
        // Required empty public constructor
    }

    abstract void displayDailyChart(FitHeartRateModel model);

    abstract void displayWeeklyChart(FitHeartRateModel model);

    abstract void displayMinMaxAvg(FitHeartRateModel model);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataManager.getInstance().addObserver(this);
        currentBpmTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        DataManager.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        DataManager.getInstance().deleteObserver(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily2, container, false);
        return rootView;
    }

    @Override
    public void update(Observable observable, Object data) {

        if (!this.isVisible()) {
            return;
        }


        if (data instanceof FitHeartRateModel) {
            fitHeartRateModel = (FitHeartRateModel) data;

           int lastBPM = Integer.valueOf(fitHeartRateModel.getLastBPM());
           String gender = Profile.GENDER_FEMALE;
           int age = 66;

           DataManager dm =  DataManager.getInstance();
           gender =  dm.getProfile(getActivity()).getGender() ;
           age =  dm.getProfile(getActivity()).getAge() ;



             mHeartRate = new HeartRate(gender, age, lastBPM);
            displayMaxHeartRateData(mHeartRate);
            displayCurrentBPM(fitHeartRateModel);

            displayMinMaxAvg(fitHeartRateModel);
            displayWeeklyChart(fitHeartRateModel);
            displayDailyChart(fitHeartRateModel);
        }
    }
HeartRate mHeartRate;

    private void displayCurrentBPM(FitHeartRateModel model) {
        String bpm = (model.getLastBPM() != null) ? model.getLastBPM() : "0";

        currentBpmTextView.setVisibility(View.VISIBLE);

        scaleView(currentBpmTextView, 0.6f, 1f,bpm);

        if(mHeartRate.isBelowRange()){
            mTopLayout.setBackgroundColor(mTopLayout.getResources().getColor(R.color.pulse_light_blue));
            currentBpmTextView.setTextColor(mTopLayout.getResources().getColor(R.color.pulse_light_blue));
        }else if(mHeartRate.isAboveRange()){
            mTopLayout.setBackgroundColor(mTopLayout.getResources().getColor(R.color.pulse_red));
            currentBpmTextView.setTextColor(mTopLayout.getResources().getColor(R.color.pulse_red));
        }else if(mHeartRate.isWithinRange()){
            mTopLayout.setBackgroundColor(mTopLayout.getResources().getColor(R.color.pulse_green));
            currentBpmTextView.setTextColor(mTopLayout.getResources().getColor(R.color.pulse_green));
        }
        currentBpmTextView.setText(bpm);
    }

    public void scaleView(View v, float startScale, float endScale, String bpm) {

        ScaleAnimation scaleAnimation = new ScaleAnimation(1,2,1,2);
        scaleAnimation.setDuration(500);


        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        scaleAnimation.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(scaleAnimation);

        scaleAnimation = new ScaleAnimation(2,1,2,1);
        scaleAnimation.setDuration(500);



        scaleAnimation.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(scaleAnimation);

    }



    private void displayMaxHeartRateData(HeartRate heartRate) {
        TextView begin = (TextView) getActivity().findViewById(R.id.beginTextView);
        // not sure what to set this value to
        begin.setText("25");

        middleBeginTextView.setText(heartRate.getLowEndOfTargetRange() + "");
        middleBeginTextView.refreshDrawableState();
        middleEndTextView.setText("" + heartRate.getHighEndOfTargetRange());
        middleEndTextView.refreshDrawableState();
        endTextView.setText(heartRate.getMaxHeartRate() + "");
        endTextView.refreshDrawableState();

        aboveValueText.setVisibility(View.INVISIBLE);
        belowValueText.setVisibility(View.INVISIBLE);
        midValueText.setVisibility(View.INVISIBLE);
        lowNeedle.setVisibility(View.INVISIBLE);
        highNeedle.setVisibility(View.INVISIBLE);
        midNeedle.setVisibility(View.INVISIBLE);

        if (heartRate.isBelowRange()) {
            belowValueText.setVisibility(View.VISIBLE);
            lowNeedle.setVisibility(View.VISIBLE);
            belowValueText.setText(Integer.toString(heartRate.getBeatsPerMinute()));
            belowValueText.refreshDrawableState();
     }

        if (heartRate.isAboveRange()) {
            aboveValueText.setVisibility(View.VISIBLE);
            highNeedle.setVisibility(View.VISIBLE);
            aboveValueText.setText(Integer.toString(heartRate.getBeatsPerMinute()));
            aboveValueText.refreshDrawableState();
     }

        if (heartRate.isWithinRange()) {
            midValueText.setVisibility(View.VISIBLE);
            midNeedle.setVisibility(View.VISIBLE);
            midValueText.setText(Integer.toString(heartRate.getBeatsPerMinute()));
            midValueText.refreshDrawableState();
   }

    }


}
