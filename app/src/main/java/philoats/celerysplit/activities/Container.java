package philoats.celerysplit.activities;

public interface Container<T> {
    public void registerView(T view);

    public void loadView(int pos, boolean isBack);
    public void loadView(int pos);

    public boolean onBackPressed();

    public void show(T View);

    public void setPageListener(PageChangeListener listener);

    public interface PageChangeListener {
        public void onPageChanged(int position);
    }
}