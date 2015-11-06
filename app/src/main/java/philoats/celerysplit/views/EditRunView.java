package philoats.celerysplit.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import philoats.celerysplit.R;
import philoats.celerysplit.adapters.EditArrayAdapter;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.models.SplitSet;
import philoats.celerysplit.presenters.EditRunPresenter;
import rx.Observable;
import rx.functions.Action1;

public class EditRunView extends CoordinatorLayout implements LongPressItem.ButtonListener {

    private EditRunPresenter presenter;
    private TextView titleView;
    private Toolbar toolbar;
    private RecyclerView listView;
    private EditArrayAdapter adapter;

    private Action1<Boolean> onComplete;

    public EditRunView(Context context) {
        super(context);
    }

    public EditRunView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditRunView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onAttachedToWindow() {
    }

    public void initialise(EditRunPresenter presenter) {
        this.presenter = presenter;

        listView = (RecyclerView) findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EditArrayAdapter(presenter.getSet(), this, i -> {
            String text = presenter.getSet().getName(i);
            showEditSplitDialog(presenter.getSet(), text, i);
        });
        listView.setAdapter(adapter);

        FloatingActionButton plusButton = (FloatingActionButton) findViewById(R.id.fab_add);
        plusButton.setOnClickListener(v -> showEditSplitDialog());

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            presenter.save(titleView.getText().toString());
            onComplete.call(true);
        });

        titleView = (TextView) findViewById(R.id.titleView);

        TextView titleEditButton = (TextView) findViewById(R.id.titleViewEdit);
        titleEditButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            boolean isNew = titleView.getText() == "Set Title";
            builder.setTitle(isNew ? "Set Title" : "Edit Title");

            // Set up the input
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(isNew ? "" : titleView.getText(), TextView.BufferType.SPANNABLE);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> titleView.setText(input.getText()));
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> {
            presenter.cancel();
            onComplete.call(true);
        });

        this.presenter.currentSetObservable().subscribe(this::loadSplits);
    }

    private void showEditSplitDialog(SplitSet set, String curName, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(position == -1 ? "New Split" : "Edit Split");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(curName);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (position == -1) set.addSegment(input.getText().toString(), -1, -1);
            else set.updateSegment(position, input.getText().toString(), set.getPbTime(position), set.getBestTime(position));
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showEditSplitDialog() {
        showEditSplitDialog(presenter.getSet(), "", -1);
    }

    public Observable<Boolean> setSplits(Run run) {
        return Observable.create(subscriber -> presenter.setSet(run).subscribe(set -> {
            subscriber.onNext(true);
            subscriber.onCompleted();
        }));
    }

    private void loadSplits(SplitSet set) {
        if (set.getCount() == 0) {
            titleView.setText("Set Title");
            toolbar.setTitle("Create Splits");
        } else {
            String title = set.getTitle();
            titleView.setText(title);
            toolbar.setTitle("Edit Splits");
        }
        adapter = new EditArrayAdapter(presenter.getSet(), this, i -> {
            String text = presenter.getSet().getName(i);
            showEditSplitDialog(presenter.getSet(), text, i);
        });
        listView.setAdapter(adapter);
    }

    public void setOnComplete(Action1<Boolean> onComplete) {
        this.onComplete = onComplete;
    }

    @Override
    public void onEditButtonPressed(int i) {
        String text = presenter.getSet().getName(i);
        showEditSplitDialog(presenter.getSet(), text, i);
    }

    @Override
    public void onDeleteButtonPressed(int i) {
        presenter.getSet().deleteSegment(i);
        adapter.notifyItemRemoved(i);
        adapter.notifyItemRangeChanged(i, presenter.getSet().getCount());
    }
}
