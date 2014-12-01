package com.riis.pulsetrack.util;

import com.google.android.gms.fitness.data.DataPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class CalendarUtil {



    public static int getAge(Calendar dob) {

        Integer age = 0;
        Calendar today = Calendar.getInstance();
        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        return age.intValue();
    }
}
