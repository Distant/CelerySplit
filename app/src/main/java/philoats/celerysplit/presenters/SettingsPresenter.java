package philoats.celerysplit.presenters;

import android.content.SharedPreferences;

public class SettingsPresenter implements Presenter {
    private static final String SHOW_GRAPH = "showGraph";

    private SharedPreferences sharedPrefs;

    public SettingsPresenter(SharedPreferences prefs){
        this.sharedPrefs = prefs;
    }

    public void graphCheck(boolean isChecked) {
        if (!sharedPrefs.contains(SHOW_GRAPH) || sharedPrefs.getBoolean(SHOW_GRAPH, false) != isChecked){
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.clear();
            editor.putBoolean(SHOW_GRAPH, isChecked);
            editor.apply();
        }
    }
}