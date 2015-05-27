package philoats.celerysplit.models;

public class Segment {
    private long _id;
    private long runID;
    private String name;
    private long pbTime;
    private long bestSeg;
    private int index;

    public Segment(){

    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getRunID() {
        return runID;
    }

    public void setRunID(long runID) {
        this.runID = runID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPbTime() {
        return pbTime;
    }

    public void setPbTime(long pbTime) {
        this.pbTime = pbTime;
    }

    public long getBestSeg() {
        return bestSeg;
    }

    public void setBestSeg(long bestSeg) {
        this.bestSeg = bestSeg;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString(){
        return index + ": " + name + " Time: " + pbTime + " From Run: " + _id;
    }
}
