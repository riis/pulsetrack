package com.riis.pulsetrack.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.riis.pulsetrack.R;
import com.riis.pulsetrack.dashboard.DailyFragment;
import com.riis.pulsetrack.dashboard.WeeklyFragment;
import com.riis.pulsetrack.model.DataManager;
import com.riis.pulsetrack.model.FitHeartRateModel;
import com.riis.pulsetrack.profile.ProfileFragment;
import com.riis.pulsetrack.util.AppEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Observer {

    public static final String TITLE_DAILY = "Daily";
    public static final String TITLE_WEEKLY = "Weekly";
    public static final String TAG_DAILY_DASH = "DAILY_DASH";
    public static final String TAG_WEEKLY_DASH = "WEEKLY_DASH";
    public static final String TAG_PROFILE = "PROFILE_FRAGEMNT";
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final int REQUEST_OAUTH = 1;
    private static int WEEK_RANGE = 1;
    private static int MENU_OPTION_DASHBOARD = 0;
    private static int MENU_OPTION_PROFILE = 1;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean authInProgress = false;
    private GoogleApiClient mClient = null;
    //  private String mTitle = "PulseTrack - ";
    private ProfileFragment mProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        DataManager.getInstance().addObserver(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mNavigationDrawerFragment.close();
        initFitnessClient();

    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    private void initFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                new QueryDataTask().execute();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();


        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    private DataReadRequest queryFitnessWeeklyData() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        // the end time is now
        long endTime = cal.getTimeInMillis();

        // the start time is now minus n number of weeks
        //cal.add(Calendar.WEEK_OF_YEAR, WEEK_RANGE * -1);
      //  cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        cal.set(Calendar.WEEK_OF_YEAR, -10);


        long startTime = cal.getTimeInMillis();


        DataReadRequest readHeartRateRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readHeartRateRequest;
    }


    private DataReadRequest queryFitnessDailyData() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        // the end time is now
        long endTime = cal.getTimeInMillis();

        // the start time is now minus n number of weeks
        int MIDNIGHT = 0;
        cal.set(Calendar.HOUR_OF_DAY, MIDNIGHT);
      //  cal.set(Calendar.WEEK_OF_YEAR, -10);

        long startTime = cal.getTimeInMillis();


        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_HEART_RATE_BPM)
                .build();

        return readRequest;
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        if (position == MENU_OPTION_DASHBOARD) {

            boolean setup = DataManager.getInstance().isSetup();
            if (setup) {
                showDailyDashboardFromProfile(null);
            } else {
                Toast.makeText(getApplicationContext(), "A Profile is required to proceed.", Toast.LENGTH_LONG).show();
            }

        } else {
            showProfileFragment(fragmentManager);
        }
        onSectionAttached(position);
        restoreActionBar();
    }


    private void refreshFitData() {
        if (mClient != null && mClient.isConnected()) {
            new QueryDataTask().execute();
        } else if (mClient != null && !mClient.isConnecting()) {
            initFitnessClient();
            mClient.connect();
        }
    }

    private ProfileFragment getProfileFragment() {
        if (mProfileFragment == null) {
            mProfileFragment = new ProfileFragment();
        }
        return mProfileFragment;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                //   mTitle = "PulseTrack - Daily Dashboard";
                break;
            case 1:
                //    mTitle = "PulseTrack - Profile";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // actionBar.setTitle(mTitle);
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");

        TextView title = (TextView) findViewById(actionBarTitleId);
        if (title != null) {
            title.setTextColor(Color.BLACK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.dashboard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (item.getItemId() == R.id.action_dashboard) {

            if (DataManager.getInstance().isSetup()) {
                if (getProfileFragment().isVisible()) {
                    showDailyDashboardFromProfile(item);
                    return true;
                }


                if (item.getTitle().equals(TITLE_DAILY)) {
                    showDailyDashboard(item);
                } else {
                    showWeeklyDashboard(item);
                }
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "A Profile is required to proceed.", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void showProfileFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.left_to_right, R.animator.right_to_left);
        transaction.replace(R.id.container, getProfileFragment(), TAG_PROFILE);
        transaction.commit();
    }


    private void showDailyDashboardFromProfile(MenuItem item) {

        if (item != null) {
            item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_week));
            item.setTitle(TITLE_WEEKLY);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.enter, R.animator.exit);
        transaction.replace(R.id.container, new DailyFragment(), TAG_DAILY_DASH);
        transaction.commit();

        refreshFitData();

    }


    private void showDailyDashboard(MenuItem item) {

        if (item != null) {
            item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_week));
            item.setTitle(TITLE_WEEKLY);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                R.animator.card_flip_left_in, R.animator.card_flip_left_out);
        transaction.replace(R.id.container, new DailyFragment(), TAG_DAILY_DASH);
        transaction.commit();

        refreshFitData();

    }

    private void showWeeklyDashboard(MenuItem item) {

        item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_day));
        item.setTitle(TITLE_DAILY);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                R.animator.card_flip_left_in, R.animator.card_flip_left_out);
        transaction.replace(R.id.container, new WeeklyFragment(), TAG_WEEKLY_DASH);
        transaction.commit();

        refreshFitData();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof AppEvent) {
            showDailyDashboardFromProfile(null);
        }
    }

    private class QueryDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            try {
                DataReadRequest weeklyRequest = queryFitnessWeeklyData();
                DataReadRequest dailyRequest = queryFitnessDailyData();

                DataReadResult weeklyResult =
                        Fitness.HistoryApi.readData(mClient, weeklyRequest).await(1, TimeUnit.MINUTES);
                DataReadResult dailyResult =
                        Fitness.HistoryApi.readData(mClient, dailyRequest).await(1, TimeUnit.MINUTES);

                final FitHeartRateModel fitHeartrateModel = new FitHeartRateModel();


                fitHeartrateModel.storeFitWeeklyData(weeklyResult);
                fitHeartrateModel.storeFitDailyData(dailyResult);


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.getInstance().setFitHeartRateModel(fitHeartrateModel);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
