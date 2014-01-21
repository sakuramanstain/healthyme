package han.com.gps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import han.com.tts.Speak;
import han.com.utils.Values;

/**
 *
 * @author han
 */
public class ListenerGpsLocation implements LocationListener {

    private static String className = ListenerGpsLocation.class.getName();
    private Location lastPoint;
    private float progressDistance;
    private InterfaceReportGpsDistance report;

    public ListenerGpsLocation(InterfaceReportGpsDistance report) {
        this.report = report;
        progressDistance = 0;
    }

    public void setProgressDistance(float progressDistance) {
        this.progressDistance = progressDistance;
    }

    public float getProgressDistance() {
        return progressDistance;
    }

    public void clearRecord() {
        lastPoint = null;
        progressDistance = 0;
    }

    public void onLocationChanged(Location location) {
        Log.d(className, "location changed");

        if (lastPoint != null) {
            float lastDis = location.distanceTo(lastPoint);

            if (Values.MetricMode.equals("mile")) {
                lastDis *= Values.METER_FOR_FEET;
            }

            Log.d(className, "lastDis: " + lastDis);
            Log.d(className, "progressDistance: " + progressDistance + " " + Values.MetricMode);

            progressDistance += lastDis;

            Log.d(className, "progressDistance: " + progressDistance + " " + Values.MetricMode);
            report.reportDistance(progressDistance);
        }

        lastPoint = location;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(className, provider + " Status Changed");
        if (status == LocationProvider.OUT_OF_SERVICE) {
            Speak.getInstance(null).speak(provider + " is OUT OF SERVICE");
        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            //Speak.getInstance(null).speak(provider + " is TEMPORARILY UNAVAILABLE");
        }
    }

    public void onProviderEnabled(String provider) {
        Log.d(className, provider + " Provider Enabled");
    }

    public void onProviderDisabled(String provider) {
        Log.d(className, provider + " Provider Disabled");
    }
}
