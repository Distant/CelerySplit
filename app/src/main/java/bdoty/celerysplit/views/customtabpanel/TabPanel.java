package bdoty.celerysplit.views.customtabpanel;

import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;

import bdoty.celerysplit.R;

public class TabPanel extends LinearLayout implements Tab.TabListener {

    private List<Tab> tabs;
    private ViewPager pager;
    private int curPosition = 0;

    public TabPanel(Context context) {
        super(context);
    }

    public TabPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onStart(ViewPager pager) {

        this.pager = pager;
        this.setOrientation(LinearLayout.HORIZONTAL);
        tabs = new LinkedList<Tab>();

        FragmentPagerAdapter adapter = (FragmentPagerAdapter) pager.getAdapter();
        for (int i = 0; i < 3; i++) { //adapter.getCount(); i++) {
            addTab(newTab().setText(adapter.getPageTitle(i)).setTabListener(this));
        }
        this.notifyDataSetChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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
        tabs.get(curPosition).deactivate();
        curPosition = tab.getPosition();
        pager.setCurrentItem(curPosition, true);
    }

    @Override
    public void tabReSelected(Tab tab) {

    }

    public void setPosition(int i)
    {
        tabSelected(tabs.get(i));
        tabs.get(i).activate();
    }
}