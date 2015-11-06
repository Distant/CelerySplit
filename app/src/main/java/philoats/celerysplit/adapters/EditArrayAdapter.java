package philoats.celerysplit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import philoats.celerysplit.R;
import philoats.celerysplit.models.SplitSet;
import philoats.celerysplit.presenters.TimerPresenter;
import philoats.celerysplit.views.LongPressItem;
import rx.functions.Action1;

public class EditArrayAdapter extends RecyclerView.Adapter<EditArrayAdapter.SplitViewHolder> {

    private final SplitSet set;
    private LongPressItem.ButtonListener listener;
    private Action1<Integer> itemClickAction;

    public EditArrayAdapter(SplitSet set, LongPressItem.ButtonListener listener, Action1<Integer> onItemClick) {
        this.set = set;
        this.listener = listener;
        this.itemClickAction = onItemClick;
    }

    @Override
    public SplitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dual_edit, parent, false);
        return new SplitViewHolder(v, itemClickAction);
    }

    @Override
    public int getItemCount() {
        return set.getCount();
    }

    @Override
    public void onBindViewHolder(SplitViewHolder holder, int position) {

        LongPressItem.getSelected().deselect();
        holder.delete.setOnClickListener(v -> listener.onDeleteButtonPressed(position));
        holder.edit.setOnClickListener(v -> {
            listener.onEditButtonPressed(position);
            LongPressItem.getSelected().deselect();
        });

        holder.itemView.clearFocus();

        holder.left.setText(set.getName(position));
        if (position < set.getCount()) {
            long time = set.getPbTime(position);
            holder.right.setText(time < 0 ? "-" : TimerPresenter.toTimeString(time, false));
        } else {
            holder.right.setText("-");
        }
    }

    public class SplitViewHolder extends RecyclerView.ViewHolder {

        public TextView left;
        public TextView right;
        public TextView delete;
        public TextView edit;

        public SplitViewHolder(View itemView, Action1<Integer> action) {
            super(itemView);
            left = (TextView) itemView.findViewById(R.id.textLeft);
            right = (TextView) itemView.findViewById(R.id.textRight);
            delete = (TextView) itemView.findViewById(R.id.deleteButton);
            edit = (TextView) itemView.findViewById(R.id.editButton);
            delete.setClickable(false);
            edit.setClickable(false);

            RelativeLayout layout = (RelativeLayout) itemView.findViewById(R.id.relLayout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.rightMargin = 0;
            params.leftMargin = 0;
            layout.setLayoutParams(params);

            itemView.setOnClickListener(v -> action.call(getAdapterPosition()));

            ((LongPressItem) itemView).init();
        }
    }
}