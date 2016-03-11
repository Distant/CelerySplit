package philoats.celerysplit.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import philoats.celerysplit.data.RunDataAccess;

public class SplitSet {
    private final long _id;

    private String title;
    private ArrayList<Segment> segments;
    public long[] bestSegmentsCum;

    public long[] tempPB;
    public long[] tempBest;

    public SplitSet(SplitSet set) {
        this(set.getId(), set.getTitle(), set.getNames(), set.getPbTimes(), set.getBestTimes());
    }

    public SplitSet(String title, String[] names) {
        this(-1, title, names, null, null);
    }

    public SplitSet(String title, String[] names, long[] pbs) {
        this(-1, title, names, pbs, null);
    }

    public SplitSet(String title, String[] names, long[] pbs, long[] best) {
        this(-1, title, names, pbs, best);
    }

    public SplitSet(long _id, String title, String[] names, long[] pbs, long[] best) {
        this._id = _id;
        int count = names.length;
        segments = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            segments.add(new Segment(names[i], pbs == null ? -1 : pbs[i], best == null ? -1 : best[i]));
        }

        bestSegmentsCum = new long[count];
        long total = 0;
        for (int i = 0; i < count; i++) {
            total += getBestTimes()[i];
            bestSegmentsCum[i] = total;
        }

        this.title = title;
        tempPB = new long[count];
        tempBest = new long[count];
        clearTemp();
    }

    public int getCount() {
        return segments.size();
    }

    public long getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long[] getPbTimes() {
        long[] p = new long[getCount()];
        for (int i = 0; i < getCount(); i++) {
            p[i] = segments.get(i).pb;
        }
        return p;
    }

    public long[] getBestTimes() {
        long[] p = new long[getCount()];
        for (int i = 0; i < getCount(); i++) {
            p[i] = segments.get(i).best;
        }
        return p;
    }

    public String[] getNames() {
        String[] n = new String[getCount()];
        for (int i = 0; i < getCount(); i++) {
            n[i] = segments.get(i).name;
        }
        return n;
    }

    public String getName(int position) {
        return segments.get(position).name;
    }

    public Long getPbTime(int position) {
        return segments.get(position).pb;
    }

    public Long getBestTime(int position) {
        return segments.get(position).best;
    }

    public void clearTemp() {
        Arrays.fill(tempPB, -1);
        Arrays.fill(tempBest, -1);
    }

    public void setNewPB() {
        for (int i = 0; i < getCount(); i++) {
            segments.get(i).pb = tempPB[i];
        }
        for (int i = 0; i < getCount(); i++) {
            if (tempBest[i] > 0) segments.get(i).best = tempBest[i];
        }
    }

    public void saveToFile(Context context) {
        RunDataAccess data = new RunDataAccess(context);
        data.updateRun(this);
    }

    public void update(long time, int curSplit) {
        tempPB[curSplit] = time;
        long tempSeg = curSplit == 0 ? time : (time - tempPB[curSplit - 1]);
        if (segments.get(curSplit).best == -1 || tempSeg < segments.get(curSplit).best)
            tempBest[curSplit] = tempSeg;
    }

    @Override
    public String toString() {
        return title + ": " + getCount() + " splits. ";
    }

    public static SplitSet empty() {
        return new SplitSet("", new String[]{});
    }

    public void addSegment(String name, long pb, long best) {
        segments.add(new Segment(name, pb, best));
    }

    public void addSegment(int position, String name, long pb, long best) {
        segments.add(position, new Segment(name, pb, best));
    }

    public void updateSegment(int position, String name, long pb, long best) {
        segments.set(position, new Segment(name, pb, best));
    }

    public void deleteSegment(int position) {
        segments.remove(position);
    }

    private class Segment {

        private String name;
        private long pb;
        private long best;

        private Segment(String name, long pb, long best) {
            this.name = name;
            this.pb = pb;
            this.best = best;
        }
    }
}