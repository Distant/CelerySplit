package philoats.celerysplit.views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import philoats.celerysplit.R;
import philoats.celerysplit.activities.ContainerPanel;
import philoats.celerysplit.activities.MainActivity;
import philoats.celerysplit.adapters.LayeredListAdapter;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.models.SplitSet;
import philoats.celerysplit.presenters.EditRunPresenter;
import philoats.celerysplit.presenters.RunListPresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RunListView extends CoordinatorLayout implements ContainerView, AdapterView.OnItemClickListener, LayeredListAdapter.ButtonListener {

    private ArrayAdapter<Run> listAdapter;
    private RunListPresenter runListPresenter;
    private ArrayList<Run> runs;

    private EditRunView editRunView;

    public RunListView(Context context) {
        super(context);
    }

    public RunListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RunListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialise(RunListPresenter presenter) {
        this.runListPresenter = presenter;
        this.runs = new ArrayList<>();
        ListView listView = (ListView) findViewById(android.R.id.list);
        this.listAdapter = new LayeredListAdapter(getContext(), runs, listView, this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        this.runListPresenter.runObservable().subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(newRuns -> {
                    runs.clear();
                    runs.addAll(newRuns);
                    listAdapter.notifyDataSetChanged();
                    System.out.println("runs loaded from database");
                });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("RUNS");
        toolbar.inflateMenu((R.menu.menu_load_splits));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_import) {
                if (runs.size() > 0) {
                    ArrayList<String> files = presenter.getImportFiles(getContext());
                    if (files.size() == 0) {
                        Toast.makeText(getContext(), "No files found in /CelerySplit/Import/ to import",
                                Toast.LENGTH_LONG).show();
                    } else {
                        ListSelectDialog.show(getContext(), "File to Import", files, index -> runListPresenter.importFile(getContext(), files.get(index))); // rename file
                    }
                }
                return true;
            }
            if (item.getItemId() == R.id.action_export) {
                if (runs.size() > 0) {
                    ArrayList<String> titles = new ArrayList<>();
                    for (Run run : runs) titles.add(run.getTitle());
                    ListSelectDialog.show(getContext(), "Run to Export", titles, index -> runListPresenter.exportFile(getContext(), runs.get(index)));
                }
                return true;
            }
            return false;
        });

        FloatingActionButton newSplitButton = (FloatingActionButton) findViewById(R.id.fab_add);
        newSplitButton.setOnClickListener(v -> editRunView.setSplits(null).subscribe(b -> showEdit()));

        ContainerPanel container = ((MainActivity) getContext()).getContainer();
        this.editRunView = (EditRunView) ((Activity) getContext()).getLayoutInflater().inflate(R.layout.subscreen_edit_split, container, false);

        EditRunPresenter editRunPresenter = new EditRunPresenter(getContext());
        editRunPresenter.setListener(runListPresenter);
        editRunView.initialise(editRunPresenter);
        editRunView.setOnComplete(b -> hideEdit());
    }

    @Override
    public void onEditButtonPressed(int i) {
        editRunView.setSplits(runs.get(i))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(b -> showEdit());
    }

    @Override
    public void onDeleteButtonPressed(int i) {
        runListPresenter.onDelete(runs.get(i));
    }

    @Override
    public void onSelected() {
        runListPresenter.refresh();
    }

    @Override
    // TODO add confirmation dialog
    public void onDeselected() {
        if (editRunView.getParent() != null) {
            hideEdit();
        }
        LongPressItem.reset();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean onBack() {
        if (editRunView.getParent() != null) {
            hideEdit();
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        runListPresenter.onItemClick(runs.get(position));
    }

    public void showEdit() {
        LinearLayout editContainer = (LinearLayout) findViewById(R.id.editContainer);
        editContainer.addView(editRunView);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.enter);
        editRunView.startAnimation(animation);
        editContainer.setClickable(true);
    }

    public void hideEdit() {
        LinearLayout editContainer = (LinearLayout) findViewById(R.id.editContainer);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit);
        editRunView.startAnimation(animation);
        editContainer.removeView(editRunView);
        editContainer.setClickable(false);
    }
}
