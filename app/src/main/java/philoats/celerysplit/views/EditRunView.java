package philoats.celerysplit.views;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import philoats.celerysplit.R;
import philoats.celerysplit.adapters.EditArrayAdapter;
import philoats.celerysplit.data.RunDataAccess;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.models.SplitSet;
import philoats.celerysplit.presenters.EditRunPresenter;
import philoats.celerysplit.presenters.RunListPresenter;
import rx.Observable;
import rx.functions.Action1;

public class EditRunView extends RelativeLayout{

    private static final int EDIT_LIST_ITEM = R.layout.list_item_dual_edit;

    private EditRunPresenter.EditListener listener;
    private TextView titleView;
    private Toolbar toolbar;
    private ListView listView;
    private EditArrayAdapter adapter;

    private boolean inEditMode = false;
    private ArrayList<String> splitNames;
    private SplitSet currentSet;

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
    public void onAttachedToWindow(){
    }

    public void initialise(EditRunPresenter presenter) {
        splitNames = new ArrayList<>();
        splitNames.add("DEFAULT");
        currentSet = SplitSet.empty();
        adapter = new EditArrayAdapter(getContext(), EDIT_LIST_ITEM, currentSet, splitNames);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {

            String text = adapter.getItem(position);
            alertForInput(text, position);
        });

        Button plusButton = (Button) findViewById(R.id.plusButton);
        plusButton.setOnClickListener(v -> alertForInput());

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String[] namesArray = splitNames.toArray(new String[splitNames.size()]);
            if (inEditMode) listener.onFinishEdited(titleView.getText().toString(), namesArray, currentSet);
            else listener.onFinishCreated(titleView.getText().toString(), namesArray);
            onComplete.call(true);
        });

        titleView = (TextView) findViewById(R.id.titleView);
        TextView titleEditButton = (TextView) findViewById(R.id.titleViewEdit);
        titleEditButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            boolean isNew = titleView.getText()== "Set Title";
            builder.setTitle(isNew? "Set Title" : "Edit Title");

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
            listener.onCancel();
            onComplete.call(true);
        });

    }

    public void alertForInput(String curName, final int state)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(state == -1 ? "New Split" : "Edit Split");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(curName);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (state == -1) splitNames.add(input.getText().toString());
            else splitNames.set(state,input.getText().toString());
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void alertForInput()
    {
        alertForInput("", -1);
    }

    public Observable<Boolean> loadSplits(final Run run)
    {
        return Observable.create(subscriber -> {
            RunDataAccess access = new RunDataAccess(getContext());
            access.getSet(run).subscribe(set -> {
                currentSet = set;
                String title = set.getTitle();
                titleView.setText(title);
                toolbar.setTitle("Edit Splits");
                inEditMode = true;
                titleView.setText(title);
                splitNames.clear();
                Collections.addAll(splitNames, set.names);
                adapter = new EditArrayAdapter(getContext(), EDIT_LIST_ITEM, currentSet, splitNames);
                listView.setAdapter(adapter);
            });
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    public void createSplits()
    {
        titleView.setText("Set Title");
        toolbar.setTitle("Create Splits");
        inEditMode = false;
        splitNames.clear();
        currentSet = SplitSet.empty();
        adapter = new EditArrayAdapter(getContext(), EDIT_LIST_ITEM, currentSet, splitNames);
        listView.setAdapter(adapter);
    }

    public void setListener(RunListPresenter listener) {
        this.listener = listener;
    }

    public void setOnComplete(Action1<Boolean> onComplete) {
        this.onComplete = onComplete;
    }
}
