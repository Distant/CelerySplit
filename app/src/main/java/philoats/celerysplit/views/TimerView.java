package philoats.celerysplit.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import philoats.celerygraph.GraphView;
import philoats.celerysplit.R;
import philoats.celerysplit.activities.BaseActivity;
import philoats.celerysplit.adapters.BestTimerAdapter;
import philoats.celerysplit.adapters.TimerListAdapter;
import philoats.celerysplit.enums.CompareType;
import philoats.celerysplit.enums.TimerEvent;
import philoats.celerysplit.presenters.TimerPresenter;

import rx.Subscription;
import rx.android.view.ViewObservable;

public class TimerView extends RelativeLayout implements ContainerView, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int listOffset = 2;

    private String START_TEXT;
    private String RESET_TEXT;
    private String PAUSE_TEXT;
    private String DONE_TEXT;

    private TimerPresenter timerPresenter;
    private List<Subscription> subscriptionList;
    private Toolbar toolbar;

    public Button timerUnsplitButton;
    public Button timerStartButton;
    public Button timerStopButton;
    public View timerSplit;
    private TextView timerTextView;

    private ListView list;
    private View listBottom;
    private TimerListAdapter listAdapter;
    private BestTimerAdapter listBestAdapter;

    private int position = 0;
    private GraphView graph;

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((BaseActivity) context).inject(this);
    }

    public void initialise(TimerPresenter presenter) {
        this.timerPresenter = presenter;

        // BUTTONS
        timerUnsplitButton = (Button) findViewById(R.id.timerUnsplitButton);
        timerStartButton = (Button) findViewById(R.id.timerStartButton);
        timerStopButton = (Button) findViewById(R.id.timerStopButton);
        timerSplit = findViewById(R.id.timerSplit);
        list = (ListView) findViewById(R.id.splitList);
        listBottom = findViewById(R.id.listBottom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("No Splits Loaded");

        START_TEXT = getContext().getString(R.string.screen_timer_start);
        PAUSE_TEXT = getContext().getString(R.string.screen_timer_pause);
        DONE_TEXT = getContext().getString(R.string.screen_timer_done);
        RESET_TEXT = getContext().getString(R.string.screen_timer_reset);

        subscriptionList = new ArrayList<>();

        timerTextView = (TextView) findViewById(R.id.timerTextView);

        graph = (GraphView) findViewById(R.id.graph);
        graph.init(timerPresenter.graphData, input -> {
            String text = input == 0 ? "0.0" : Float.toString(toTenths(-1 * input));
            text = (-1 * input > 0 ? "+ " : "") + text;
            return text;
        });

        subscriptionList.add(timerPresenter.onTimerStateChange().subscribe(timerEvent -> {
            switch (timerEvent) {
                case STARTED:
                    start();
                    break;
                case RESET:
                    reset();
                    break;
                case FINISHED:
                    finish();
                    break;
                case PAUSED:
                    pause();
                    break;
                default:
                    break;
            }
        }));

        subscriptionList.add(timerPresenter.onTimerTick().subscribe(timerTextView::setText));

        // STOP/RESET BUTTON
        subscriptionList.add(ViewObservable.clicks(timerStopButton)
                .subscribe(ev -> {
                    if (timerPresenter.lastEvent == TimerEvent.FINISHED) {
                        alertForSave();
                    } else timerPresenter.reset();
                }));

        // START BUTTON
        subscriptionList.add(ViewObservable.clicks(timerStartButton)
                .subscribe(ev -> {
                    timerPresenter.startButtonClicked();
                }));

        // UNSPLIT BUTTON TODO
        timerUnsplitButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (timerPresenter.getCurSplit() > 0) {
                    timerPresenter.unsplit();
                    updateSplits(false);
                }
                return true;
            }
            return false;
        });

        // SPLIT BUTTON
        timerSplit.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                timerPresenter.split().subscribe(i -> updateSplits());
                return true;
            }
            return false;
        });

        subscriptionList.add(timerPresenter.onSplitsLoaded().subscribe(aBoolean -> loadSplits()));

        Spinner compareSelector = (Spinner) findViewById(R.id.compareSelector);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.compareSelection, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        compareSelector.setAdapter(spinnerAdapter);
        compareSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timerPresenter.setCompare(position);
                if (listAdapter != null) {
                    switch (position) {
                        case 0:
                            timerPresenter.setCompare(0);
                            list.setAdapter(listAdapter);
                            break;
                        case 1:
                            timerPresenter.setCompare(1);
                            list.setAdapter(listBestAdapter);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updatePrefs(getContext().getSharedPreferences("timerPreferences", Context.MODE_PRIVATE));
    }

    @Override
    public void onAttachedToWindow(){
        getContext().getSharedPreferences("timerPreferences", Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        for (Subscription sub : subscriptionList) {
            sub.unsubscribe();
        }
        getContext().getSharedPreferences("timerPreferences", Context.MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onDeselected() {
        if (timerPresenter.lastEvent == TimerEvent.STARTED) timerPresenter.pause();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean onBack() {
        return false;
    }

    public float toTenths(float time) {
        int seconds = (int) (time / 1000);
        int dec = (int) (time / 100);
        dec %= 10;
        return (seconds) + ((float) dec) / 10;
    }

    public void loadSplits() {
        setAdapter();
        list.setVisibility(VISIBLE);
        listBottom.setVisibility(VISIBLE);
        toolbar.setTitle(timerPresenter.loadedSplits.getTitle());
        resizeToFit();
    }

    private void resizeToFit() {
    }

    public void reset() {
        setAdapter();
        position = 0;
        list.setSelection(position);
        ((ArrayAdapter<String>)list.getAdapter()).notifyDataSetChanged();

        timerUnsplitButton.setEnabled(false);
        timerSplit.setEnabled(false);
        timerStartButton.setEnabled(true);
        timerStopButton.setText(RESET_TEXT);
        timerStartButton.setText(START_TEXT);

        timerTextView.setTextColor(0xFFCCCCDD);
        graph.reset();
    }

    public void finish() {
        timerStopButton.setText(DONE_TEXT);
        timerUnsplitButton.setEnabled(false);
        timerStartButton.setEnabled(false);
        timerSplit.setEnabled(false);
        timerTextView.setTextColor(0xFFFFFF55);
    }

    public void start() {
        timerStartButton.setText(PAUSE_TEXT);
        timerUnsplitButton.setEnabled(true);
        timerSplit.setEnabled(true);
        list.setSelection(position);
    }

    public void pause(){
        timerStartButton.setText(START_TEXT);
        timerSplit.setEnabled(false);
        timerUnsplitButton.setEnabled(false);
    }

    private void setAdapter() {
        listAdapter = new TimerListAdapter(getContext(), timerPresenter.getNames(), timerPresenter.getTimeStrings(), timerPresenter.getTypes());
        listBestAdapter = new BestTimerAdapter(getContext(), timerPresenter.getNames(), timerPresenter.getBestTimeStrings(), timerPresenter.getTypes());
        list.setAdapter(timerPresenter.getCompareType() == CompareType.PBTIMES ? listAdapter : listBestAdapter);
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void updateSplits() {
        updateSplits(true);
    }

    public void updateSplits(boolean split) {
        ((ArrayAdapter<String>)list.getAdapter()).notifyDataSetChanged();
        if (split) split();
        else unsplit();
        graph.notifyDataSetChanged();
        graph.moveToLast();
    }

    public void split() {
        int size = list.getLastVisiblePosition() - list.getFirstVisiblePosition() + 1;
        if (timerPresenter.getCurSplit() > position + size - 1 - listOffset)
            list.setSelection(position == (list.getCount() - size + 1) ? position : (position += 1));
    }

    public void unsplit() {
        if (timerPresenter.getCurSplit() < position + listOffset - 1)
            list.setSelection(position == 0 ? position : (position -= 1));
    }

    public void alertForSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Save Splits?");

        builder.setPositiveButton("Save", (dialog, which) -> {
            timerPresenter.save(getContext());
            timerPresenter.reset();
        });

        builder.setNeutralButton("Discard Run", (dialog, which) -> {
            timerPresenter.reset();
            dialog.cancel();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updatePrefs(SharedPreferences sharedPreferences) {
        graph.setVisibility(sharedPreferences.getBoolean("showGraph", false)? VISIBLE : GONE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefs(sharedPreferences);
        invalidate();
    }
}