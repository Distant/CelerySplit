package bdoty.celerysplit.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import bdoty.celerysplit.R;
import bdoty.celerysplit.models.SplitSet;
import bdoty.celerysplit.enums.State;
import bdoty.celerysplit.models.TimerModel;
import bdoty.celerysplit.views.TimerView;

public class TimerFragment extends Fragment {

    private TimerModel timerModel;
    private TimerView timerView;

    public static TimerFragment newInstance() {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TimerFragment() {
        // Required empty constructor
    }

    public void setLoadedSplits(SplitSet splits) {
        timerModel.setLoadedSplits(splits);
        timerView.reset();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        timerView = (TimerView) inflater.inflate(R.layout.fragment_timer, container, false);
        super.onCreate(savedInstanceState);

        TextView timerTextView = (TextView) timerView.findViewById(R.id.timerTextView);
        timerModel = new TimerModel(timerTextView);
        timerView.onStart(timerModel);

        HashMap<String, View.OnClickListener> listeners = new HashMap<>();
        listeners.put("START", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (timerModel.curState == State.RUNNING) {
                    timerModel.pause();
                    b.setText("Start");
                } else {
                    timerModel.start();
                    timerView.start();
                    b.setText("Pause");
                }
            }
        });

        listeners.put("STOP", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerModel.curState == State.FINISHED) {
                    alertForSave();
                } else reset();
            }
        });

        // UNSPLIT BUTTON
        View.OnTouchListener touch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (timerModel.curSplit > 0) {
                        timerModel.unsplit();
                        timerView.updateSplits(false);
                    }
                    return true;
                }
                return false;
            }
        };

        // SPLIT BUTTON
        View.OnTouchListener splitView = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (timerModel.curSplit < timerModel.loadedSplits.getCount()) {
                        long time = timerModel.getCurrentTime();
                        timerModel.split(time);
                        timerView.updateSplits();
                        if (timerModel.curSplit == timerModel.loadedSplits.getCount() - 1)
                            stop(time);
                        timerModel.curSplit++;
                    }
                    return true;
                }
                return false;
            }
        };

        timerView.setListeners(listeners, touch, splitView);
        return timerView;
    }

    private void reset() {
        timerModel.reset();
        timerView.reset();
    }

    public void stop(long time) {
        timerModel.stop(time);
        timerView.stop();
    }

    public void saveSplits() {
        timerModel.save(getActivity());
    }

    public void alertForSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Save Splits?");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSplits();
                reset();
            }
        });

        builder.setNeutralButton("Discard Run", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reset();
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //TODO put title in model
    public void setTitle(String title) {
        timerView.setTitle(title);
    }
}