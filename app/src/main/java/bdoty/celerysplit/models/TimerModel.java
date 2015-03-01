package bdoty.celerysplit.models;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bdoty.celerysplit.enums.State;

public class TimerModel {

    public static final String TIMER_DEFAULT = "0.00";

    public State curState = State.STOPPED;

    private TextView timerTextView;
    private long pauseTime;
    private long startTime = 0;
    public SplitSet loadedSplits;
    public Integer curSplit = 0;
    public long[] displayTimes;
    public List<Float> graphData;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            timerTextView.setText(toTimeString(getCurrentTime(), false));
            timerHandler.postDelayed(this, 1);
        }
    };

    public TimerModel(TextView timerTextView) {
        this.timerTextView = timerTextView;
        timerTextView.setText(TIMER_DEFAULT);
        loadedSplits = new SplitSet("No Splits Loaded", new ArrayList<String>());
        displayTimes = new long[loadedSplits.getCount()];
        graphData = new ArrayList<Float>();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        curState = State.RUNNING;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void setLoadedSplits(SplitSet splits) {
        this.loadedSplits = splits;
        displayTimes = Arrays.copyOf(loadedSplits.pbTimes, loadedSplits.getCount());
        this.reset();
    }

    public void pause() {
        pauseTime += System.currentTimeMillis() - startTime;
        timerHandler.removeCallbacks(timerRunnable);
        timerTextView.setText(toTimeString(pauseTime, false));
        startTime = 0;
        curState = State.PAUSED;
    }

    public void reset() {
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerTextView.setText(TIMER_DEFAULT);
        timerTextView.setTextColor(0xFF99BBFF);
        curState = State.STOPPED;
        pauseTime = 0;
        System.arraycopy(loadedSplits.pbTimes, 0, displayTimes, 0, loadedSplits.getCount());
        graphData.clear();
        loadedSplits.clearTemp();
        curSplit = 0;
    }

    public void stop(long time) {
        timerHandler.removeCallbacks(timerRunnable);
        curState = State.FINISHED;
        timerTextView.setText(toTimeString(time, false));
        timerTextView.setTextColor(0xFFFFFF55);
    }

    public long getCurrentTime() {
        return curState == State.PAUSED ? pauseTime : System.currentTimeMillis() - startTime + pauseTime;
    }

    public static String toTimeString(long time, boolean colored) {
        boolean neg = time < 0;
        time = Math.abs(time);
        int cents = (int) (time / 10) % 100;
        int seconds = (int) (time / 1000);
        int minutes = (seconds / 60);
        int hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        String timeString;
        if (hours == 0) {
            if (minutes == 0) {
                timeString = String.format("%d.%02d", seconds, cents);
            } else {
                timeString = String.format("%d:%02d.%02d", minutes, seconds, cents);
            }
        } else {
            timeString = String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, cents);
        }

        if (colored) {
            if (neg) timeString = "- " + timeString;
            else timeString = "+ " + timeString;
        }

        return timeString;
    }

    public void save(Context context) {
        loadedSplits.setNewPB();
        //loadedSplits.saveToFile(context);
    }

    public void split(long time) {
        loadedSplits.update(time, curSplit);
        displayTimes[curSplit] = time - loadedSplits.pbTimes[curSplit];
        graphData.add((float)time - loadedSplits.pbTimes[curSplit]);
    }

    public void unsplit() {
        curSplit--;
        loadedSplits.tempPB[curSplit] = loadedSplits.pbTimes[curSplit];
        displayTimes[curSplit] = loadedSplits.pbTimes[curSplit];
        graphData.remove(graphData.size() - 1);
    }

    public boolean isBestSeg(int position) {
        return loadedSplits.tempBest[position] > 0 && loadedSplits.pbTimes[position] > 0;
    }
}