package philoats.celerysplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import philoats.celerysplit.R;
import philoats.celerysplit.enums.SegmentType;

public class TimerListAdapter extends ArrayAdapter<String> {
    private Context context;
    private int activeColor;
    private int inactiveText;
    private int pastelRed;
    private int pastelGreen;
    private int activeText;
    private String[] names;
    private String[] times;
    private ArrayList<SegmentType> types;

    //TODO stuff colours

    public TimerListAdapter(Context context,String[] names, String[] times, ArrayList<SegmentType> types) {
        super(context, R.layout.list_item_dual);
        this.context = context;
        this.activeColor = context.getResources().getColor(R.color.split_indicator);
        this.inactiveText = context.getResources().getColor(R.color.split_inactive_text);
        this.activeText = context.getResources().getColor(R.color.off_white);
        this.pastelRed = context.getResources().getColor(R.color.pastel_red);
        this.pastelGreen = context.getResources().getColor(R.color.pastel_green);
        this.names = names;
        this.times = times;
        this.types = types;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_dual, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(names[position]);
        String timeString = times[position];
        holder.timeText.setText(timeString);

        if (position < types.size()) {
            view.setBackgroundColor(0x00FFFFFF);
            holder.name.setTextColor(inactiveText);
            if (timeString.equals("-")) {
                holder.timeText.setTextColor(inactiveText);
            } else {
                switch (types.get(position)) {
                    case BEHIND_LOSING:
                        holder.timeText.setTextColor(pastelRed);
                        break;
                    case AHEAD_GAINING:
                        holder.timeText.setTextColor(pastelGreen);
                        break;
                    case BEST_SEGMENT:
                        holder.timeText.setTextColor(0xFFFFCC44);
                        break;
                    case NEW_SPLIT:
                        break;
                }
            }
        }
        else if (position == types.size()){
            view.setBackgroundColor(activeColor);
            holder.timeText.setTextColor(activeText);
            holder.name.setTextColor(activeText);
        }

        else {
            view.setBackgroundColor(0x00FFFFFF);
            holder.timeText.setTextColor(inactiveText);
            holder.name.setTextColor(inactiveText);
        }

        return view;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    private static class ViewHolder {
        public TextView name;
        public TextView timeText;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.textLeft);
            timeText = (TextView) view.findViewById(R.id.textRight);
        }
    }

    public void replaceData(String[] data){
        times = data;
        notifyDataSetChanged();
    }
}