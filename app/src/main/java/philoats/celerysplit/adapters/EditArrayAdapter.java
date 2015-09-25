package philoats.celerysplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import philoats.celerysplit.R;
import philoats.celerysplit.models.SplitSet;
import philoats.celerysplit.presenters.TimerPresenter;
import philoats.celerysplit.views.LongPressItem;

public class EditArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resource;
    private final SplitSet set;
    private ListView list;
    private LayeredListAdapter.ButtonListener listener;

    public EditArrayAdapter(Context context, SplitSet set, ListView list, LayeredListAdapter.ButtonListener listener) {
        super(context, R.layout.list_item_dual_edit);
        this.context = context;
        this.resource = R.layout.list_item_dual_edit;
        this.set = set;
        this.listener = listener;
        this.list = list;
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

        holder.left.setText(set.getName(position));
        if (position < set.getCount()) {
            holder.right.setText(TimerPresenter.toTimeString(set.getPbTime(position), false));
        } else {
            holder.right.setText("-");
        }

        holder.delete.setOnClickListener(v -> listener.onDeleteButtonPressed(position));
        holder.delete.setClickable(false);

        holder.edit.setOnClickListener(v -> {
            listener.onEditButtonPressed(position);
            LongPressItem.getSelected().deselect();
        });
        holder.edit.setClickable(false);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.relLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.rightMargin = 0;
        params.leftMargin = 0;
        layout.setLayoutParams(params);

        ((LongPressItem) view).setList(list);
        return view;
    }

    @Override
    public int getCount() {
        return set.getCount();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public String getItem(int pos){
        return set.getName(pos);
    }

    public List getNames() {
        return Arrays.asList(set.getNames());
    }

    private static class ViewHolder {
        public TextView left;
        public TextView right;
        public TextView delete;
        public TextView edit;

        public ViewHolder(View view) {
            left = (TextView) view.findViewById(R.id.textLeft);
            right = (TextView) view.findViewById(R.id.textRight);
            delete = (TextView) view.findViewById(R.id.deleteButton);
            edit = (TextView) view.findViewById(R.id.editButton);
        }
    }
}