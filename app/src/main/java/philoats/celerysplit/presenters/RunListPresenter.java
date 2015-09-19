package philoats.celerysplit.presenters;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import philoats.celerysplit.data.RunFileHelper;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.data.RunDataAccess;
import philoats.celerysplit.models.SplitSet;

import rx.functions.Action1;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RunListPresenter implements Presenter, EditRunPresenter.EditListener {

    private RunDataAccess dataAccess;
    private Action1<SplitSet> onLoadSplits;

    private BehaviorSubject<ArrayList<Run>> runSubject;
    public Observable<ArrayList<Run>> runObservable() {
        return runSubject;
    }

    public RunListPresenter(Context context, Action1<SplitSet> onLoadSplits){
        //this.parent = fragment;
        this.onLoadSplits = onLoadSplits;
        runSubject = BehaviorSubject.create();
        dataAccess = new RunDataAccess(context);
    }

    public void refresh(){
        dataAccess.getRuns().subscribe(runSubject::onNext);
    }

    public void onDetach(){
        onLoadSplits = null;
    }

    @Override
    public void onFinishCreated(String title, String[] names) {
        SplitSet set = new SplitSet(title, names);
        dataAccess.addRun(set);
        dataAccess.getRuns().subscribe(runSubject::onNext);
        //parent.transitionFromEdit();
    }

    @Override
    public void onFinishEdited(String title, String[] names, SplitSet set) {
        SplitSet newSet = new SplitSet(set.getId(), title, names, set.pbTimes, set.bestSegments);
        dataAccess.updateRun(newSet);
        dataAccess.getRuns().subscribe(runSubject::onNext);
        //parent.transitionFromEdit();
    }

    @Override
    public void onCancel() {
        //parent.transitionFromEdit();
    }

    public void onItemClick(Run run) {
        if (null != onLoadSplits) {
            dataAccess.getSet(run)
                    .subscribe(onLoadSplits::call);
        }
    }

    public void onDelete(Run run) {
        dataAccess.deleteRun(run.get_id());
        dataAccess.getRuns().subscribe(runSubject::onNext);
    }

    public void exportFile(Context context, Run run){
        RunFileHelper fileHelper = new RunFileHelper(context);
        dataAccess.getSet((run)).subscribe(fileHelper::exportFile);
    }

    public void importFile(Context context, String name){
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
