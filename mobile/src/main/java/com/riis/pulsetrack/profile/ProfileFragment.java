package com.riis.pulsetrack.profile;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.riis.pulsetrack.R;
import com.riis.pulsetrack.model.DataManager;
import com.riis.pulsetrack.model.Profile;
import com.riis.pulsetrack.util.GuiUtil;
import com.riis.pulsetrack.util.ValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    static final int DATE_DIALOG_ID = 0;
    @InjectView(R.id.maleOption)
    RadioButton mMaleOption;
    @InjectView(R.id.femaleOption)
    RadioButton mFemaleOption;
    @InjectView(R.id.firstNameEditText)
    EditText mFirstNameEditText;
    @InjectView(R.id.lastNameEditText)
    EditText mLastNameEditText;
    @InjectView(R.id.restingHeartRate)
    EditText mRestingHeartRate;
    @InjectView(R.id.calButton)
    ImageButton mCalendarButton;
    @InjectView(R.id.textViewDOB)
    TextView birthDateTextView;
    @InjectView(R.id.saveButton)
    TextView mSaveButton;



    private Calendar mCalendar;
    // The callback received when the user "sets" the date in the Dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            try {

                updateDisplay(monthOfYear, dayOfMonth, year);
                mCalendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("mm dd yyyy", Locale.getDefault());
                mCalendar.setTime(sdf.parse(monthOfYear + " " + dayOfMonth + " " + year));// all done
                getProfile().setDOB(mCalendar);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    public ProfileFragment() {
        // Required empty public constructor
    }

    // Update the date in the TextView
    private void updateDisplay(int month, int day, int year) {
        birthDateTextView.setText(new StringBuilder()
                .append(month + 1)
                .append("-").append(day).append("-")
                .append(year).append(" "));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, rootView);
        getActivity().getActionBar().setTitle("PulseTrack - Profile");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final InputMethodManager mgr = (InputMethodManager) getActivity()
                .getSystemService(getActivity().INPUT_METHOD_SERVICE);

        mFirstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mgr.hideSoftInputFromWindow(mFirstNameEditText.getWindowToken(), 0);
                }
            }
        });

        mLastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mgr.hideSoftInputFromWindow(mLastNameEditText.getWindowToken(), 0);
                }
            }
        });

        mMaleOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFemaleOption.setChecked(!mMaleOption.isChecked());
                mgr.hideSoftInputFromWindow(mLastNameEditText.getWindowToken(), 0);
            }
        });

        mFemaleOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaleOption.setChecked(!mFemaleOption.isChecked());
                mgr.hideSoftInputFromWindow(mLastNameEditText.getWindowToken(), 0);
            }
        });


        // Set an OnClickListener on the Change The Date Button
        mCalendarButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                //  getActivity().showDialog(DATE_DIALOG_ID);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), mDateSetListener,
                        2000, 1, 1);
                dialog.show();

            }
        });


        // Set an OnClickListener on the Change The Date Button
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                saveData();
            }
        });
        displayProfileData();
    }

    private Profile getProfile() {
        return DataManager.getInstance().getProfile(getActivity());
    }

    private void displayProfileData() {
        String gender = getProfile().getGender();
        mMaleOption.setChecked(true);
        mMaleOption.setChecked((gender.equals(Profile.GENDER_MALE)) ? true : false);
        mFemaleOption.setChecked((gender.equals(Profile.GENDER_FEMALE)) ? true : false);
        mFirstNameEditText.setText(getProfile().getFirstName());
        mLastNameEditText.setText(getProfile().getLastName());
        birthDateTextView.setText(getProfile().getBirthDate());
        mRestingHeartRate.setText(getProfile().getRestingHeartRate().toString());


        if(gender.length() < 1){
            mMaleOption.setChecked(true);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    int mYear;
    int mMonth;
    int mDay;

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(getActivity(),
                        mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private void validate(TextView textView) throws ValidationException {
        if (textView.getText() == null || textView.getText().length() < 1) {
            textView.requestFocus();
            throw new ValidationException("Profile values cannot be empty.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void saveData() {

        try {
            validate(mFirstNameEditText);
            validate(mLastNameEditText);
            validate(mRestingHeartRate);
            validate(birthDateTextView);

            getProfile().setFirstName(mFirstNameEditText.getText().toString());
            getProfile().setLastName(mLastNameEditText.getText().toString());
            String value = mRestingHeartRate.getText().toString();
            getProfile().setRestingHeartRate(Integer.valueOf(value));
            getProfile().setBirthDate(birthDateTextView.getText().toString());

            getProfile().setDOB(mCalendar);

            if (mMaleOption.isChecked()) {
                getProfile().setGender(Profile.GENDER_MALE);
            } else {
                getProfile().setGender(Profile.GENDER_FEMALE);
            }

            DataManager.getInstance().saveProfile(getActivity());

        } catch (ValidationException e) {
            GuiUtil.showAlertDialog(e.getMessage(), "Profile Creation", getActivity());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            GuiUtil.showAlertDialog(e.getMessage(), "Profile Creation", getActivity());
        } catch (Exception e) {
            GuiUtil.showAlertDialog(e.getMessage(), "Profile Creation", getActivity());
            e.printStackTrace();
        }
    }


}
