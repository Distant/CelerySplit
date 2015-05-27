package philoats.celerysplit.presenters;
import android.content.Context;

import java.util.ArrayList;

import philoats.celerysplit.models.Run;
import philoats.celerysplit.data.RunDataAccess;
import philoats.celerysplit.models.SplitSet;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class RunListPresenter implements Presenter, EditRunPresenter.EditListener {

    private RunDataAccess dataAccess;
    private SplitFragmentListener listener;

    private BehaviorSubject<ArrayList<Run>> runSubject;
    public Observable<ArrayList<Run>> runObservable() {
        return runSubject;
    }

    public RunListPresenter(Context context,  RunListPresenter.SplitFragmentListener listener){
        //this.parent = fragment;
        this.listener = listener;
        runSubject = BehaviorSubject.create();
        dataAccess = new RunDataAccess(context); // INJECT
    }

    public void refresh(){
        dataAccess.getRuns().subscribe(runSubject::onNext);
    }

    public void onDetach(){
        listener = null;
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
        if (null != listener) {
            dataAccess.getSet(run)
                    .subscribe(listener::onLoadSplits);
        }
    }

    public void onDelete(Run run) {
        dataAccess.deleteRun(run.get_id());
        dataAccess.getRuns().subscribe(runSubject::onNext);
    }

    public interface SplitFragmentListener {
        public void onLoadSplits(SplitSet set);
    }
}
