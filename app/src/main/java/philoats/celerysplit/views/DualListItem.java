package philoats.celerysplit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import philoats.celerysplit.R;

public class DualListItem extends RelativeLayout {

    private TextView left;
    private TextView right;

    public DualListItem(Context context) {
        super(context);
    }

    public DualListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DualListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public TextView getTextLeft() {
        if (left == null) this.left = (TextView) findViewById(R.id.textLeft);
        return left;
    }

    public TextView getTextRight() {
        if (right == null) this.right = (TextView) findViewById(R.id.textRight);
        return right;
    }
}
