package philoats.celerysplit.views;

import philoats.celerysplit.R;
import philoats.celerysplit.presenters.SettingsPresenter;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class SettingsView extends RelativeLayout implements ContainerView{
    private SettingsPresenter presenter;

    public SettingsView(Context context) {
        super(context);
    }

    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialise(SettingsPresenter presenter){
        this.presenter = presenter;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SETTINGS");

        CheckBox graphCheck = (CheckBox) findViewById(R.id.graph_check);
        graphCheck.setChecked(getContext().getSharedPreferences("timerPreferences", Context.MODE_PRIVATE).getBoolean("showGraph", false));
        graphCheck.setOnCheckedChangeListener((buttonView, isChecked) -> presenter.graphCheck(isChecked));
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onDeselected() {

    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean onBack() {
        return false;
    }
}
