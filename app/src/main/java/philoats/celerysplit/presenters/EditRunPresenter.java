package philoats.celerysplit.presenters;

import philoats.celerysplit.models.SplitSet;

public class EditRunPresenter implements Presenter {

    public EditRunPresenter(){

    }

    public interface EditListener
    {
        public void onFinishCreated(String title, String[] names);
        public void onFinishEdited(String title, String[] names, SplitSet set);
        public void onCancel();
    }
}
