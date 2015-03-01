package bdoty.celerysplit.views;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.HashMap;

import bdoty.celerygraph.GraphView;
import bdoty.celerysplit.R;
import bdoty.celerysplit.adapters.TimerListAdapter;
import bdoty.celerysplit.models.TimerModel;

public class TimerView extends RelativeLayout {

    private static final int listOffset = 3;
    private TimerModel timerModel;
    private Button timerUnsplitButton;
    private Button timerStartButton;
    private Button timerStopButton;
    private View timerSplit;
    private ListView list;
    private Toolbar toolbar;
    private TimerListAdapter listAdapter;
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
    }

    public void onStart(TimerModel model) {
        this.timerModel = model;

        // BUTTONS
        timerUnsplitButton = (Button) findViewById(R.id.timerUnsplitButton);
        timerStartButton = (Button) findViewById(R.id.timerStartButton);
        timerStopButton = (Button) findViewById(R.id.timerStopButton);
        timerSplit = findViewById(R.id.timerSplit);
        list = (ListView) findViewById(R.id.splitList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("No Splits Loaded");

        graph = (GraphView) findViewById(R.id.graph);
        graph.init(timerModel.graphData);

        listAdapter = new TimerListAdapter(getContext(), timerModel);
        list.setAdapter(listAdapter);
        resizeToFit();
    }

    private void resizeToFit() {
        int size = list.getLastVisiblePosition() - list.getFirstVisiblePosition() + 1;

        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < size; itemPos++) {
            View item = listAdapter.getView(itemPos, null, list);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = list.getDividerHeight() *
                (size - 1);

        // Set list height.
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        list.setLayoutParams(params);
        list.setMinimumHeight(totalItemsHeight + totalDividersHeight);
        list.requestLayout();
    }

    public void reset() {
        listAdapter = new TimerListAdapter(getContext(), timerModel);
        list.setAdapter(listAdapter);
        position = 0;
        list.setSelection(position);
        listAdapter.notifyDataSetChanged();
        timerUnsplitButton.setEnabled(false);
        timerSplit.setEnabled(false);
        timerStartButton.setEnabled(true);
        timerStopButton.setText("Reset");
        timerStartButton.setText("Start");
        graph.reset();
    }

    public void stop() {
        timerStopButton.setText("Done");
        timerUnsplitButton.setEnabled(false);
        timerStartButton.setEnabled(false);
        timerSplit.setEnabled(false);
    }

    public void start() {
        timerUnsplitButton.setEnabled(true);
        timerSplit.setEnabled(true);
    }

    public void setListeners(HashMap<String, OnClickListener> listeners, OnTouchListener touch, OnTouchListener touch2) {
        timerStartButton.setOnClickListener(listeners.get("START"));
        timerStopButton.setOnClickListener(listeners.get("STOP"));
        timerUnsplitButton.setOnTouchListener(touch);
        timerSplit.setOnTouchListener(touch2);
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void updateSplits() {
        updateSplits(true);
    }

    public void updateSplits(boolean forward) {
        listAdapter.notifyDataSetChanged();
        int size = list.getLastVisiblePosition() - list.getFirstVisiblePosition() + 1;
        if (forward) {
            if (timerModel.curSplit > position + size - 1 - listOffset)
                list.setSelection(position == (list.getCount() - size + 1) ? position : (position += 1));
        } else {
            if (timerModel.curSplit < position + listOffset - 1)
                list.setSelection(position == 0 ? position : (position -= 1));
        }
        graph.notifyDataSetChanged();
        graph.moveToLast();
    }
}