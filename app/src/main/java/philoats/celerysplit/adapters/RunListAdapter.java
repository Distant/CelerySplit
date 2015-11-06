package philoats.celerysplit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import philoats.celerysplit.R;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.views.LongPressItem;
import rx.functions.Action1;

public class RunListAdapter extends RecyclerView.Adapter<RunListAdapter.RunViewHolder> {

    private ArrayList<Run> runs;
    private LongPressItem.ButtonListener listener;
    private Action1<Integer> itemClickAction;

    public RunListAdapter(ArrayList<Run> runs, LongPressItem.ButtonListener listener, Action1<Integer> onItemClick) {
        this.runs = runs;
        this.listener = listener;
        this.itemClickAction = onItemClick;
    }

    @Override
    public RunViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layered, parent, false);
        return new RunViewHolder(v, itemClickAction);
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    @Override
    public void onBindViewHolder(RunViewHolder holder, int position) {
        holder.name.setText(runs.get(position).toString());
        holder.delete.setOnClickListener(v -> listener.onDeleteButtonPressed(position));
        holder.edit.setOnClickListener(v -> {
            listener.onEditButtonPressed(position);
            LongPressItem.getSelected().deselect();
        });
    }

    public class RunViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView delete;
        public TextView edit;

        public RunViewHolder(View itemView, Action1<Integer> action) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textLeft);
            delete = (TextView) itemView.findViewById(R.id.deleteButton);
            delete.setClickable(false);
            edit = (TextView) itemView.findViewById(R.id.editButton);
            edit.setClickable(false);

            itemView.setOnClickListener(v -> action.call(getAdapterPosition()));

            RelativeLayout layout = (RelativeLayout) itemView.findViewById(R.id.relLayout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.rightMargin = 0;
            params.leftMargin = 0;
            layout.setLayoutParams(params);

            ((LongPressItem) itemView).init();
        }
    }
}
