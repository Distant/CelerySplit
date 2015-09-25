package philoats.celerysplit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import philoats.celerysplit.R;

public class LongPressItem extends RelativeLayout implements View.OnTouchListener {

    private int maxWidth;
    private ListView list;
    private RelativeLayout layout;
    private RelativeLayout.LayoutParams params;
    private boolean didMove = false;
    private TextView delete;
    private TextView edit;
    private int state;
    private float x;
    private static LongPressItem selected = null;

    private static final int NONE = 0;
    private static final int RL = 1;

    public LongPressItem(Context context) {
        super(context);
    }

    public LongPressItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongPressItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setList(ListView list) {
        this.layout = (RelativeLayout) findViewById(R.id.relLayout);
        this.delete = (TextView) findViewById(R.id.deleteButton);
        this.edit = (TextView) findViewById(R.id.editButton);
        this.list = list;
        state = NONE;
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        setOnTouchListener(this);
    }

    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            Select();
        }
    });

    private void Select() {
        int target;
        target = maxWidth;
        delete.setClickable(true);
        edit.setClickable(true);
        didMove = true;
        if (selected != this && selected != null) {
            selected.deselect();
        }
        selected = this;
        doAnim(target);
    }

    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.midnight_grey_light));
                maxWidth = delete.getWidth() + edit.getWidth();
                if (state == RL) {
                    doAnim(0);
                    didMove = true;
                    state = NONE;
                    delete.setClickable(false);
                    edit.setClickable(false);
                }
                x = event.getX();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                int dx = (int) (x - event.getX());
                if (Math.abs(dx) > 10) {
                    didMove = true;
                    layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.midnight_grey));
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.midnight_grey));
                if (!didMove) list.performItemClick(v, list.getPositionForView(v), 0);
                didMove = false;
                return true;
            }
            case MotionEvent.ACTION_CANCEL: {
                layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.midnight_grey));
                return false;
            }
        }
        return true;
    }

    public void deselect() {
        state = NONE;
        delete.setClickable(false);
        edit.setClickable(false);
        layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.midnight_grey));
        try {
            doAnim(0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void doAnim(final int target) {
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        ValueAnimator anim = ValueAnimator.ofInt(params.rightMargin, target);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            params = (LayoutParams) layout.getLayoutParams();
            params.rightMargin = val;
            params.leftMargin = 0 - val;
            layout.setLayoutParams(params);
        });
        anim.setDuration(130);
        anim.start();
    }

    public static LongPressItem getSelected() {
        return selected;
    }

    public static void reset() {
        if (selected != null) selected.deselect();
    }
}