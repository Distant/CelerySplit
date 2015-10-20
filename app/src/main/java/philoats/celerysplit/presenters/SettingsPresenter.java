package philoats.celerysplit.presenters;

import android.content.SharedPreferences;

public class SettingsPresenter implements Presenter {
    private static final String SHOW_GRAPH = "showGraph";
    private static final String SHOW_LAST_SPLIT = "showLastSplit";

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

    public void lastSplitCheck(boolean isChecked) {
        if (!sharedPrefs.contains(SHOW_LAST_SPLIT) || sharedPrefs.getBoolean(SHOW_LAST_SPLIT, false) != isChecked){
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.clear();
            editor.putBoolean(SHOW_LAST_SPLIT, isChecked);
            editor.apply();
        }
    }
}