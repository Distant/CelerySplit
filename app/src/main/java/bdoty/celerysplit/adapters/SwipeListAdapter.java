package bdoty.celerysplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import bdoty.celerysplit.R;
import bdoty.celerysplit.views.SwipeableItem;

public class SwipeListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private HashMap<String, ?> sets;
    private String[] objects;
    private ListView list;
    private ButtonListener listener;

    public SwipeListAdapter(Context context, int resource, HashMap<String, ?> sets, ListView list, ButtonListener listener) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.sets = sets;
        this.objects = sets.keySet().toArray(new String[sets.size()]);
        this.list = list;
        this.listener = listener;
        list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                SwipeableItem.getSelected().deselect();
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = null;
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
        holder.name.setText(objects[position]);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDelete(objects[position]);
            }
        });
        holder.delete.setClickable(false);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEdit(objects[position]);
            }
        });
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
        return objects.length;
    }

    public interface ButtonListener {
        public void onEdit(String string);

        public void onDelete(String string);
    }

    @Override
    public void notifyDataSetChanged() {
        this.objects = sets.keySet().toArray(new String[sets.size()]);
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