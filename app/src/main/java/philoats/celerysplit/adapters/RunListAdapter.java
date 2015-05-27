package philoats.celerysplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import philoats.celerysplit.R;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.views.SwipeableItem;

public class RunListAdapter extends ArrayAdapter<Run> {

    private Context context;
    private int resource;
    private ArrayList<Run> runs;
    private ListView list;
    private ButtonListener listener;

    public RunListAdapter(Context context, int resource, ArrayList<Run> runs, ListView list, ButtonListener listener) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.runs = runs;
        this.list = list;
        this.listener = listener;
        list.setOnFocusChangeListener((v, hasFocus) -> {
            //SwipeableItem.getSelected().deselect();
        });
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

        view.clearFocus();
        holder.name.setText(runs.get(position).toString());

        holder.delete.setOnClickListener(v -> listener.onDeleteButtonPressed(runs.get(position)));
        holder.delete.setClickable(false);

        holder.edit.setOnClickListener(v -> listener.onEditButtonPressed(runs.get(position)));
        holder.edit.setClickable(false);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.relLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        layout.setLayoutParams(params);

        ((SwipeableItem) view).setSwipeable(list);
        return view;
    }

    @Override
    public int getCount() {
        return runs.size();
    }

    @Override
    public Run getItem(int pos){
        return runs.get(pos);
    }

    public interface ButtonListener {
        public void onEditButtonPressed(Run run);
        public void onDeleteButtonPressed(Run run);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView name;
        public TextView delete;
        public TextView edit;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.textLeft);
            delete = (TextView) view.findViewById(R.id.deleteButton);
            edit = (TextView) view.findViewById(R.id.editButton);
        }
    }
}