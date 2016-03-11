package philoats.celerysplit.presenters;

import android.content.Context;

import java.util.ArrayList;

import philoats.celerysplit.data.RunFileHelper;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.data.RunDataAccess;
import philoats.celerysplit.models.SplitSet;

import rx.functions.Action1;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class RunListPresenter implements Presenter, EditRunPresenter.EditListener {

    private RunDataAccess dataAccess;
    private Action1<SplitSet> onLoadSplits;

    private BehaviorSubject<RunDataEvent> runSubject;

    public Observable<RunDataEvent> runObservable() {
        return runSubject;
    }

    public RunListPresenter(Context context, Action1<SplitSet> onLoadSplits) {
        //this.parent = fragment;
        this.onLoadSplits = onLoadSplits;
        runSubject = BehaviorSubject.create();
        dataAccess = new RunDataAccess(context);
    }

    public void refresh() {
        dataAccess.getRuns().subscribe(runs -> runSubject.onNext(new RunDataEvent(runs)));
    }

    public void onDetach() {
        onLoadSplits = null;
    }

    @Override
    public void onFinishEdited() {
        dataAccess.getRuns().subscribe(runs -> runSubject.onNext(new RunDataEvent(runs)));
    }

    @Override
    public void onCancel() {
    }

    public void onItemClick(Run run) {
        if (null != onLoadSplits) {
            dataAccess.getSet(run)
                    .subscribe(onLoadSplits::call);
        }
    }

    public void onDelete(int index) {
        dataAccess.deleteRun(runSubject.getValue().getRuns().get(index).get_id());
        dataAccess.getRuns().subscribe(runs -> runSubject.onNext(new RunDataEvent(index, RunDataEvent.DELETED, runs)));
    }

    public void exportFile(Context context, Run run) {
        RunFileHelper fileHelper = new RunFileHelper(context);
        dataAccess.getSet((run)).subscribe(fileHelper::exportFile);
    }

    public void importFile(Context context, String name) {
        RunFileHelper fileHelper = new RunFileHelper(context);
        fileHelper.importFile(name).subscribe(dataAccess::addRun);
    }

    public ArrayList<String> getImportFiles(Context context) {
        RunFileHelper helper = new RunFileHelper(context);
        ArrayList<String> files;
        files = helper.getImportFiles();
        return files;
    }
}
