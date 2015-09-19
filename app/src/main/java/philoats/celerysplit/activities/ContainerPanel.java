package philoats.celerysplit.activities;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Stack;

import philoats.celerysplit.views.ContainerView;

public class ContainerPanel extends ViewPager implements Container<ContainerView> {

    private Stack<Integer> backStack = new Stack<>();
    private PageChangeListener listener;

    private boolean enabled = true;

    private ArrayList<ContainerView> views;
    private int currentPosition = 0;
    private PagerAdapter adapter;

    public ContainerPanel(Context context) {
        super(context);
    }

    public ContainerPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void start() {
        setOffscreenPageLimit(2);
        views = new ArrayList<>();
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position).getView());
                return views.get(position).getView();
            }

            public int getItemPosition(Object object) {
                return views.indexOf(object);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object o) {
                View view = (View) o;
                container.removeView(view);
                views.remove(position);
            }
        };
        this.setAdapter(adapter);
        this.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // TODO put on selected in onPageSelected to avoid returning to
            // TODO same screen with onSelected not being called (even though OnDeselected has)
            @Override
            public void onPageSelected(int position) {
                    views.get(currentPosition).onDeselected();
                    listener.onPageChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
                    if (getCurrentItem() != currentPosition) {
                        views.get(getCurrentItem()).onSelected();
                        currentPosition = getCurrentItem();
                    }
                }
            }
        });
    }

    @Override
    public void setPageListener(PageChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void registerView(ContainerView view) {
        views.add(view);
        adapter.notifyDataSetChanged();
        if (views.size() == 1) initialLoad();
    }

    private void initialLoad() {
        views.get(0).onSelected();
        backStack.push(0);
    }


    @Override
    public void loadView(int pos, boolean addToStack) {
        if (pos < views.size() && pos > -1) {
            if (listener != null) listener.onPageChanged(pos);
            if (addToStack) {
                backStack.push(currentPosition);
            }
            setCurrentItem(pos, true);
        }
    }

    public void loadView(int pos) {
        loadView(pos, false);
    }

    @Override
    public boolean onBackPressed() {
        if (views.get(currentPosition).onBack()) return true;
        if (currentPosition > 0) {
            if (backStack.size() > 0) loadView(backStack.pop(), false);
            else loadView(0, false);
            return true;
        }
        return false;
    }

    @Override
    public void show(ContainerView view) {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.enabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.enabled && super.onTouchEvent(ev);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}