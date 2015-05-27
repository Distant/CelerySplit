package philoats.celerysplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import philoats.celerysplit.R;
import philoats.celerysplit.models.SplitSet;
import philoats.celerysplit.presenters.TimerPresenter;

public class EditArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resource;
    private final SplitSet set;
    private ArrayList<String> names;

    public EditArrayAdapter(Context context, int id, SplitSet set, ArrayList<String> names) {
        super(context, id);
        this.context = context;
        this.resource = id;
        this.set = set;
        this.names = names;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.left.setText(names.get(position));
        if (position < set.getCount()) {
            holder.right.setText(TimerPresenter.toTimeString(set.pbTimes[position], false));
        } else {
            holder.right.setText("-");
        }
        return view;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public String getItem(int pos){
        return names.get(pos);
    }

    public ArrayList<String> getNames() {
        return names;
    }

    private static class ViewHolder {
        public TextView left;
        public TextView right;

        public ViewHolder(View view) {
            left = (TextView) view.findViewById(R.id.textLeft);
            right = (TextView) view.findViewById(R.id.textRight);

        }
    }
}