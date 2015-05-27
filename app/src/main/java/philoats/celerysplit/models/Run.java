package philoats.celerysplit.models;

public class Run {
    private long _id;
    private String title;

    public Run(long _id, String title){
        this.title = title;
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    @Override
    public String toString(){
        return title;
    }
}
