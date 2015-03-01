package bdoty.celerysplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import bdoty.celerysplit.R;
import bdoty.celerysplit.models.TimerModel;

public class TimerListAdapter extends ArrayAdapter<String>
{
    private Context context;
    private int activeColor;
    private int inactiveText;
    private int pastelRed;
    private int pastelGreen;
    private int activeText;
    private TimerModel model;

    public TimerListAdapter(Context context, TimerModel model)
    {
        super(context, R.layout.list_item_dual);
        this.context = context;
        this.activeColor = context.getResources().getColor(R.color.split_indicator);
        this.inactiveText = context.getResources().getColor(R.color.split_inactive_text);
        this.activeText = context.getResources().getColor(R.color.off_white);
        this.pastelRed = context.getResources().getColor(R.color.pastel_red);
        this.pastelGreen = context.getResources().getColor(R.color.pastel_green);
        this.model = model;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_dual, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(model.loadedSplits.names[position]);
        Long time = model.displayTimes[position];
        if (time == 0) {holder.timeText.setText("-");System.out.println(holder.name.getText());}
        else {
            String text;
            int curSplit = model.curSplit;
            if (position < curSplit)
            {
                view.setBackgroundColor(0x00FFFFFF);
                holder.name.setTextColor(inactiveText);
                if (model.loadedSplits.pbTimes[position] > 0) {
                    text = TimerModel.toTimeString(time, true);
                    if (Character.toString(text.charAt(0)).equals("+")) {
                        holder.timeText.setTextColor(pastelRed);
                    } else if (Character.toString(text.charAt(0)).equals("-"))
                        holder.timeText.setTextColor(pastelGreen);
                } else {
                    long fullTime = model.loadedSplits.tempPB[position];
                    text = TimerModel.toTimeString(fullTime, false);
                }
                if (model.isBestSeg(position)) holder.timeText.setTextColor(0xFFFFCC44);
            } else
            {
                if (position == curSplit)
                {
                    view.setBackgroundColor(activeColor);
                    holder.name.setTextColor(activeText);
                    holder.timeText.setTextColor(activeText);
                }
                else
                {
                    view.setBackgroundColor(0x00FFFFFF);
                    holder.name.setTextColor(inactiveText);

                    holder.timeText.setTextColor(inactiveText);
                }
                text = model.loadedSplits.pbTimes[position] > 0 ? TimerModel.toTimeString(time, false) : "-";
            }
            holder.timeText.setText(text);
        }

        return view;
    }

    @Override
    public int getCount()
    {
        return model.loadedSplits.getCount();
    }

    private static class ViewHolder
    {
        public TextView name;
        public TextView timeText;

        public ViewHolder(View view)
        {
            name = (TextView) view.findViewById(R.id.textLeft);
            timeText = (TextView) view.findViewById(R.id.textRight);
        }
    }
}