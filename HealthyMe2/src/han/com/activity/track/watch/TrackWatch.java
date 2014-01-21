package han.com.activity.track.watch;

import org.apache.commons.lang3.time.StopWatch;

/**
 *
 * @author hanaldo
 */
public class TrackWatch {

    private static TrackWatch instance;
    private StopWatch trackWatch;
    private boolean paused;
    private boolean started;

    private TrackWatch() {
        trackWatch = new StopWatch();
        paused = false;
        started = false;
    }

    public static TrackWatch getInstance() {
        if (instance == null) {
            instance = new TrackWatch();
        }
        return instance;
    }

    public boolean isPaused() {
        return paused;
    }

    public long getTime() {
        return trackWatch.getTime();
    }

    public void pause() {
        if (started) {
            paused = true;
            trackWatch.suspend();
        }
    }

    public void resume() {
        paused = false;
        trackWatch.resume();
    }

    public void start() {
        paused = false;
        started = true;
        trackWatch.reset();
        trackWatch.start();
    }

    public void stop() {
        paused = false;
        trackWatch.stop();
    }
}
