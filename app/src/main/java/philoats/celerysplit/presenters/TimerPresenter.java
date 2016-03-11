package philoats.celerysplit.presenters;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import philoats.celerysplit.enums.CompareType;
import philoats.celerysplit.enums.SegmentType;
import philoats.celerysplit.enums.TimerEvent;
import philoats.celerysplit.models.SplitSet;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class TimerPresenter implements Presenter, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TIMER_DEFAULT = "0.00";

    public TimerEvent lastEvent;
    private BehaviorSubject<TimerEvent> timerState = BehaviorSubject.create();

    public Observable<TimerEvent> onTimerStateChange() {
        return timerState;
    }

    private BehaviorSubject<String> timerString = BehaviorSubject.create();

    public Observable<String> onTimerTick() {
        return timerString;
    }

    public SplitSet loadedSplits;
    private BehaviorSubject<Boolean> splitsLoaded = BehaviorSubject.create();

    public Observable<Boolean> onSplitsLoaded() {
        return splitsLoaded;
    }

    private BehaviorSubject<Boolean> showGraphSubject = BehaviorSubject.create();

    public Observable<Boolean> showGraph() {
        return showGraphSubject;
    }

    private BehaviorSubject<Boolean> showLastSplitSubject = BehaviorSubject.create();

    public Observable<Boolean> showLastSplitObservable() {
        return showLastSplitSubject;
    }

    public boolean showLastSplit() {
        return showLastSplitSubject.getValue();
    }

    private long pauseTime;
    private long startTime = 0;
    private int curSplit = 0;
    private CompareType compareType = CompareType.PBTIMES;

    public int getCurSplit() {
        return curSplit;
    }

    public long[] displayTimes;
    private String[] timeStrings;
    private String[] bestTimeStrings;
    private ArrayList<SegmentType> types;

    public List<Float> graphData;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            timerString.onNext(toTimeString(getCurrentTime()));
            timerHandler.postDelayed(this, 1);
        }
    };

    public TimerPresenter() {

        loadedSplits = new SplitSet("No Splits Loaded", new String[0]);
        displayTimes = new long[loadedSplits.getCount()];
        timeStrings = new String[displayTimes.length];
        bestTimeStrings = new String[displayTimes.length];
        graphData = new ArrayList<>();
        types = new ArrayList<>();

        timerString.onNext(TIMER_DEFAULT);
    }

    public void setLoadedSplits(SplitSet splits) {
        if (splits.getCount() > 0) {
            this.loadedSplits = splits;
            resetDisplayTimes(splits);
            this.reset();
            setTimerState(TimerEvent.RESET);
            splitsLoaded.onNext(true);
        }
    }

    private void resetDisplayTimes(SplitSet splits) {
        displayTimes = Arrays.copyOf(splits.getPbTimes(), splits.getCount());
        timeStrings = new String[displayTimes.length];
        bestTimeStrings = new String[displayTimes.length];
        for (int i = 0; i < displayTimes.length; i++) {
            timeStrings[i] = displayTimes[i] < 0 ? "-" : toTimeString(displayTimes[i]);
        }

        for (int i = 0; i < splits.getCount(); i++) {
            bestTimeStrings[i] = toTimeString(splits.bestSegmentsCum[i]);
        }

        types.clear();
    }

    public void startButtonClicked() {
        if (lastEvent == TimerEvent.STARTED) {
            pause();
        } else {
            start();
        }
    }

    public void start() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        setTimerState(TimerEvent.STARTED);
    }

    public void pause() {
        pauseTime += System.currentTimeMillis() - startTime;
        timerHandler.removeCallbacks(timerRunnable);
        timerString.onNext(toTimeString(pauseTime));
        startTime = 0;
        setTimerState(TimerEvent.PAUSED);

    }

    public void reset() {
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerString.onNext(TIMER_DEFAULT);
        pauseTime = 0;
        resetDisplayTimes(loadedSplits);
        graphData.clear();
        loadedSplits.clearTemp();
        curSplit = 0;
        setTimerState(TimerEvent.RESET);
    }

    public void finish(long time) {
        timerHandler.removeCallbacks(timerRunnable);
        timerString.onNext(toTimeString(time));
        setTimerState(TimerEvent.FINISHED);
    }

    public long getCurrentTime() {
        return lastEvent == TimerEvent.PAUSED ? pauseTime : System.currentTimeMillis() - startTime + pauseTime;
    }

    public Observable<Void> split() {
        return Observable.create(subscriber -> {

            if (getCurSplit() < loadedSplits.getCount()) {
                long time = getCurrentTime();
                loadedSplits.update(time, curSplit);
                displayTimes[curSplit] = time - loadedSplits.getPbTimes()[curSplit];
                float pbTime = loadedSplits.getPbTimes()[curSplit];

                if (pbTime > 0) {
                    graphData.add((float) time - pbTime);
                }

                Long timeString = displayTimes[curSplit];
                long bestSegmentTime = time - loadedSplits.bestSegmentsCum[curSplit];
                if (loadedSplits.getPbTimes()[curSplit] < 0) {
                    timeStrings[curSplit] = toTimeString(loadedSplits.tempPB[curSplit]);
                    bestTimeStrings[curSplit] = toTimeString(loadedSplits.tempPB[curSplit]);
                    types.add(SegmentType.NEW_SPLIT);
                } else {
                    timeStrings[curSplit] = toTimeString(timeString, true);
                    bestTimeStrings[curSplit] = toTimeString(bestSegmentTime, true);
                    if (isBestSeg(curSplit))
                        types.add(SegmentType.BEST_SEGMENT);
                    else if (timeString < 0) types.add(SegmentType.AHEAD_GAINING);
                    else types.add(SegmentType.BEHIND_LOSING);
                }

                if (getCurSplit() == loadedSplits.getCount() - 1) {
                    finish(time);
                }

                subscriber.onNext(null);
                curSplit++;
            }
        });
    }

    public void unsplit() {
        curSplit--;
        loadedSplits.tempPB[curSplit] = loadedSplits.getPbTimes()[curSplit];
        displayTimes[curSplit] = loadedSplits.getPbTimes()[curSplit];
        timeStrings[curSplit] = displayTimes[curSplit] < 0 ? "-" : toTimeString(displayTimes[curSplit]);
        types.remove(types.size() - 1);
        float pbTime = loadedSplits.getPbTimes()[curSplit];
        if (pbTime > 0) graphData.remove(graphData.size() - 1);
    }

    public boolean isBestSeg(int position) {
        return loadedSplits.tempBest[position] > 0 && loadedSplits.getPbTimes()[position] > 0;
    }

    public void setCompare(int position) {
        switch (position) {
            case 0:
                compareType = CompareType.PBTIMES;
                break;
            case 1:
                compareType = CompareType.BESTSEGMENTS;
                break;
        }
    }

    public void setTimerState(TimerEvent newTimerEvent) {
        lastEvent = newTimerEvent;
        timerState.onNext(lastEvent);
    }

    public static String toTimeString(long time) {
        return toTimeString(time, false);
    }

    public void save(Context context) {
        loadedSplits.setNewPB();
        loadedSplits.saveToFile(context);
    }

    // returns string form of the absolute value, unless displaySign is true
    public static String toTimeString(long time, boolean displaySign) {
        boolean neg = time < 0;
        time = Math.abs(time);
        int cents = (int) (time / 10) % 100;
        int seconds = (int) (time / 1000);
        int minutes = (seconds / 60);
        int hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        StringBuilder builder = new StringBuilder();

        if (displaySign) {
            if (neg) builder.append("- ");
            else builder.append("+ ");
        }

        if (hours == 0) {
            if (minutes == 0) {
                builder.append(String.format("%d.%02d", seconds, cents));
            } else {
                builder.append(String.format("%d:%02d.%02d", minutes, seconds, cents));
            }
        } else {
            builder.append(String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, cents));
        }
        return builder.toString();
    }

    public String[] getBestTimeStrings() {
        return bestTimeStrings;
    }

    public String[] getTimeStrings() {
        return timeStrings;
    }

    public ArrayList<SegmentType> getTypes() {
        return types;
    }

    public String[] getNames() {
        return loadedSplits.getNames();
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public String getName(int i) {
        return loadedSplits.getName(i);
    }

    public String getPbTime(int i) {
        return toTimeString(loadedSplits.getPbTime(i), false);
    }

    public int getCount() {
        return loadedSplits.getCount();
    }

    public void getPrefs(SharedPreferences sharedPreferences){
        showGraphSubject.onNext(sharedPreferences.getBoolean("showGraph", false));
        showLastSplitSubject.onNext(sharedPreferences.getBoolean("showLastSplit", false));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("showGraph"))
            showGraphSubject.onNext(sharedPreferences.getBoolean("showGraph", false));
        else if (key.equals("showLastSplit"))
            showLastSplitSubject.onNext(sharedPreferences.getBoolean("showLastSplit", false));
    }
}