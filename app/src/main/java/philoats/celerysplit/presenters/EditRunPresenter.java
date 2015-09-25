package philoats.celerysplit.presenters;

import android.content.Context;

import philoats.celerysplit.data.RunDataAccess;
import philoats.celerysplit.models.Run;
import philoats.celerysplit.models.SplitSet;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class EditRunPresenter implements Presenter {

    private Context context;
    private EditListener listener;

    private BehaviorSubject<SplitSet> currentSet;
    public Observable<SplitSet> currentSetObservable() {
        return currentSet.asObservable();
    }

    public EditRunPresenter(Context context) {
        this.context = context;
        currentSet = BehaviorSubject.create(SplitSet.empty());
    }

    public Observable<SplitSet> setSet(Run run) {
        return Observable.create(subscriber -> {
            if (run == null) {
                currentSet.onNext(SplitSet.empty());
                subscriber.onNext(SplitSet.empty());
                subscriber.onCompleted();
            } else {
                RunDataAccess access = new RunDataAccess(context);
                access.getSet(run).subscribe(set -> {
                    if (set == null) set = SplitSet.empty();
                    currentSet.onNext(set);
                    subscriber.onNext(set);
                    subscriber.onCompleted();
                });
            }
        });
    }

    public void save(String title) {
        currentSet.getValue().setTitle(title);
        RunDataAccess access = new RunDataAccess(context);
        access.updateRun(getSet());
        listener.onFinishEdited();
    }

    public void cancel() {
        listener.onCancel();
    }

    public SplitSet getSet() {
        return currentSet.getValue();
    }

    public void setListener(EditListener listener) {
        this.listener = listener;
    }

    public interface EditListener {
        void onFinishEdited();
        void onCancel();
    }
}
