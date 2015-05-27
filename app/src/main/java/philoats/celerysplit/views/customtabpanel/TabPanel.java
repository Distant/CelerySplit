package philoats.celerysplit.views.customtabpanel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import philoats.celerysplit.R;
import philoats.celerysplit.activities.Container;

public class TabPanel extends LinearLayout implements Tab.TabListener, Container.PageChangeListener {

    private List<Tab> tabs;
    private int curPosition = 0;
    private Container container;

    public TabPanel(Context context) {
        super(context);
    }

    public TabPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onStart(Container container) {

        this.container = container;
        container.setPageListener(this);
        this.setOrientation(LinearLayout.HORIZONTAL);
        tabs = new LinkedList<>();

        for (int i = 0; i < 3; i++) { //adapter.getCount(); i++) {
            addTab(newTab().setText("").setTabListener(this));
        }
        this.notifyDataSetChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (this.getWidth() != 0 && tabs.size() != 0)
            notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        super.removeAllViews();
        removeAllViews();
        int width = this.getWidth() / tabs.size();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).deactivate();
            this.addView(tabs.get(i).getView(), params);
            if (i==0) {tabs.get(i).setImage(R.drawable.clock);}
            else if (i==1) {tabs.get(i).setImage(R.drawable.thing);}
            else if (i==2) {tabs.get(i).setImage(R.drawable.dots);}
        }
        tabs.get(0).activate();

        tabs.get(curPosition).activate();
    }

    // TAB METHODS
    public Tab addTab(Tab tab) {
        tab.setPosition(tabs.size());
        tabs.add(tab);
        return tab;
    }

    public Tab newTab() {
        return new Tab(this.getContext());
    }

    @Override
    public void tabSelected(Tab tab) {
        setTabPosition(tab.getPosition());
        container.loadView(curPosition);
    }

    @Override
    public void tabReSelected(Tab tab) {

    }

    public void setPosition(int i)
    {
        setTabPosition(i);
        tabs.get(i).activate();
    }

    public void setTabPosition(int i){
        tabs.get(i).activate();
        tabs.get(curPosition).deactivate();
        curPosition = i;
    }

    @Override
    public void onPageChanged(int position) {
        setPosition(position);
    }
}