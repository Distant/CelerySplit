package bdoty.celerysplit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bdoty.celerysplit.R;

public class SwipeableItem extends RelativeLayout implements View.OnTouchListener {

    private int maxWidth;
    private ListView list;
    private RelativeLayout layout;
    private RelativeLayout.LayoutParams params;
    private boolean didMove = false;
    private TextView delete;
    private TextView edit;
    private int state;
    private float x;
    private static SwipeableItem selected = null;

    private static final int NONE = 0;
    private static final int RL = 1;
    private static final int LR = 2;

    public SwipeableItem(Context context) {
        super(context);
    }

    public SwipeableItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeableItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSwipeable(ListView list)
    {
        this.layout = (RelativeLayout) findViewById(R.id.relLayout);
        this.delete = (TextView) findViewById(R.id.deleteButton);
        this.edit = (TextView) findViewById(R.id.editButton);
        this.list = list;
        state = NONE;
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (selected != this && selected != null) {
                    selected.deselect();
                }
                selected = this;
                maxWidth = delete.getWidth() + edit.getWidth();
                if (state == RL)
                {
                    doAnim(0);
                    didMove = true;
                    state = NONE;
                    delete.setClickable(false);
                    edit.setClickable(false);
                }
                x = event.getX();
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                list.requestDisallowInterceptTouchEvent(true);
                int dx = (int) (x - event.getX());
                if (dx < 0) {
                    x = event.getX();
                    dx = 0;
                }
                if (dx > 20) didMove = true;
                if (dx > ( maxWidth)/2) {
                    state = RL;
                    if (dx >= maxWidth) {
                        x -= dx - maxWidth;
                        dx = maxWidth;
                    }
                } else state = NONE;
                swipe(dx);
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                int target;
                if (state == RL)
                {
                    target = maxWidth;
                    delete.setClickable(true);
                    edit.setClickable(true);
                }
                else target = 0;
                doAnim(target);
                list.requestDisallowInterceptTouchEvent(false);
                if (!didMove) list.performItemClick(v, 0, 0);
                didMove = false;
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                return false;
            }
        }
        return true;
    }

    public void deselect() {
        state = NONE;
        delete.setClickable(false);
        edit.setClickable(false);
        try {
            doAnim(0);
        }
        catch (NullPointerException ex) {
            // error here once couldn't replicate :(
            ex.printStackTrace();
            System.out.println("MASSIVE ERROR PROBABLY COULDN'T FIND MARGIN AGAIN - SOMETHING TO DO WITH THE SLIDEY BIT");
        }
    }

    private void doAnim(int target) {
        ValueAnimator animator = ValueAnimator.ofInt(params.rightMargin, target);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.rightMargin = (int) valueAnimator.getAnimatedValue();
                params.leftMargin = (int) valueAnimator.getAnimatedValue() * -1;
                layout.setLayoutParams(params);
            }
        });
        animator.setDuration(150);
        animator.start();
    }

    public void swipe(int dx)
    {
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.rightMargin = dx;
        params.leftMargin = -dx;
        layout.setLayoutParams(params);
    }

    public int getState()
    {
        return state;
    }

    public static SwipeableItem getSelected()
    {
        return selected;
    }
}