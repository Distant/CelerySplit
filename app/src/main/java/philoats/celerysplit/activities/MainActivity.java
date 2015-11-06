package philoats.celerysplit.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;

import java.io.File;

import javax.inject.Inject;

import philoats.celerysplit.R;
import philoats.celerysplit.presenters.RunListPresenter;
import philoats.celerysplit.presenters.SettingsPresenter;
import philoats.celerysplit.presenters.TimerPresenter;
import philoats.celerysplit.views.RunListView;
import philoats.celerysplit.views.SettingsView;
import philoats.celerysplit.views.TimerView;
import philoats.celerysplit.views.customtabpanel.TabPanel;

public class MainActivity extends BaseActivity {
    private ContainerPanel container;

    @Inject
    TimerPresenter timerPresenter;
    private TimerView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String path = Environment.getExternalStorageDirectory().toString() + "/CelerySplit/Import";
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        container = (ContainerPanel) findViewById(R.id.container);
        container.start();

        TabPanel tabPanel = (TabPanel) findViewById(R.id.tabPanel);
        tabPanel.onStart(container);

        timerView = (TimerView) getLayoutInflater().inflate(R.layout.screen_timer, container, false);
        timerView.initialise(timerPresenter);
        container.registerView(timerView);

        RunListPresenter runListPresenter = new RunListPresenter(this, (splitSet -> {
            timerPresenter.setLoadedSplits(splitSet);
            timerView.reset();
            container.loadView(0);
        }));

        RunListView runListView = (RunListView) getLayoutInflater().inflate(R.layout.screen_runs_list, container, false);
        runListView.initialise(runListPresenter);
        container.registerView(runListView);

        SettingsPresenter settingsPresenter = new SettingsPresenter(getSharedPreferences("timerPreferences", Context.MODE_PRIVATE));
        SettingsView settingsView = (SettingsView) getLayoutInflater().inflate(R.layout.screen_settings, container, false);
        settingsView.initialise(settingsPresenter);
        container.registerView(settingsView);
    }

    public ContainerPanel getContainer() {
        return container;
    }

    @Override
    public void onBackPressed() {
        if (!container.onBackPressed()) super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
}