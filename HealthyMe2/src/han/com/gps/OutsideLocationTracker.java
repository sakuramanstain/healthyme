package han.com.gps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import han.com.tts.Speak;

/**
 *
 * @author han
 */
public class OutsideLocationTracker {

    private static String className = OutsideLocationTracker.class.getName();
    private LocationManager locationManager;
    private ListenerGpsLocation listener;
    private GpsStatus.Listener gpsStatusListener;
    private Activity activity;
    private ProgressDialog waitingGPS;
    private DialogInterface.OnCancelListener waitingGpsDialogListener;
    private InterfaceTrackingReadyReport reportInitiation;
    private boolean gpsFixed;

    public OutsideLocationTracker(Activity activity) {
        this.activity = activity;
        initLocationProvider();
        gpsFixed = false;
        waitingGpsDialogListener = new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Speak.getInstance(null).speak("gps cancelled");
                if (!gpsFixed) {
                    end();
                }
            }
        };
        gpsStatusListener = new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) {
                if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
                    Log.d(className, "GPS_EVENT_FIRST_FIX");
                    GpsIsFixed();
                } else if (event == GpsStatus.GPS_EVENT_STARTED) {
                    Log.d(className, "GPS_EVENT_STARTED");
                } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
                    Log.d(className, "GPS_EVENT_STOPPED");
                    Speak.getInstance(null).speak("gps stopped");
                }
            }
        };
    }

    private void initLocationProvider() {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            enableLocationSettings();
        }
    }

    public void startListenLocation(ListenerGpsLocation listener) {
        gpsFixed = false;
        Speak.getInstance(null).speak("please wait for gps signal to start");
        waitingGPS = ProgressDialog.show(activity, "Looking for GPS...", "Please wait for GPS to start", false, true, waitingGpsDialogListener);

        locationManager.addGpsStatusListener(gpsStatusListener);
        this.listener = listener;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 10, listener);
        Log.d(className, "location tracking started");
    }

    public void end() {
        if (listener != null) {
            locationManager.removeUpdates(listener);
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
    }

    public void setReportInitiation(InterfaceTrackingReadyReport reportInitiation) {
        this.reportInitiation = reportInitiation;
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(settingsIntent);
    }

    private void GpsIsFixed() {
        if (waitingGPS != null) {
            Speak.getInstance(null).speak("gps is located");
            gpsFixed = true;
            waitingGPS.dismiss();
            waitingGPS = null;
            if (reportInitiation != null) {
                reportInitiation.gpsIsReady();
            }
        }
    }
}
