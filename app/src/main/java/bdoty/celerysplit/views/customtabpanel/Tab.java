package bdoty.celerysplit.views.customtabpanel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bdoty.celerysplit.R;

public class Tab implements View.OnTouchListener{

    private View view;
    private View indicator;
    private TextView text;
    private ImageView imgView;
    private TabListener listener;

    private int position;
    private int activeColor;
    private boolean active = false;

    public Tab(Context context)
    {
        view = LayoutInflater.from(context).inflate(R.layout.tab, null);
        indicator = view.findViewById(R.id.indicator);
        text = (TextView) view.findViewById(R.id.text);
        imgView = (ImageView) view.findViewById(R.id.imageView2);
        view.setOnTouchListener(this);
        view.setFocusable(true);

        activeColor = context.getResources().getColor(R.color.tab_indicator);

    }

    public void setImage(int id)
    {
        imgView.setImageResource(id);
    }

    public Tab setTabListener(TabListener listener)
    {
        this.listener = listener;
        return this;
    }

    public Tab setText(CharSequence string)
    {
        text.setText(string);
        return this;
    }
    
    public void setPosition(int i)
    {
        this.position = i;
    }

    public int getPosition()
    {
        return position;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (!active)
       {
            listener.tabSelected(this);
           this.activate();
        }
        else {
            listener.tabReSelected(this);
        }
      return true;
    }

    public View getView()
    {
        return view;
    }

    public void activate()
    {
        //text.setTextColor(0xffe1e1e1);
        indicator.setBackgroundColor(activeColor);
        active= true;
    }

    public void deactivate()
    {
        //text.setTextColor(0xff555555);
        indicator.setBackgroundColor(0x00FFFFFF);
        active = false;
    }

    public interface TabListener {
        public void tabSelected(Tab tab);
        public void tabReSelected(Tab tab);
    }
}