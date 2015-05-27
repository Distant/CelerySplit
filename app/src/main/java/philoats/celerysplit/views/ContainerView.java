package philoats.celerysplit.views;

import android.view.View;

public interface ContainerView {
    public void onSelected();
    public void onDeselected();
    public View getView();
    public boolean onBack();
}