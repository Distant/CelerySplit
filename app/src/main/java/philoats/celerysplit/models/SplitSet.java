package philoats.celerysplit.models;

import android.content.Context;

import java.util.Arrays;

import philoats.celerysplit.data.RunDataAccess;

public class SplitSet {
    private final long _id;
    private final int count;

    public String title;
    public String[] names;

    public long[] pbTimes;
    public long[] bestSegments;
    public long[] bestSegmentsCum;

    public long[] tempPB;
    public long[] tempBest;

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
        this.count = names.length;
        this.names = names;
        this.pbTimes = new long[count];
        this.bestSegments = new long[count];
        if (pbs == null) {
            for (int i = 0; i < count; i++) {
                pbTimes[i] = -1;
                bestSegments[i] = -1;
            }
        } else {
            this.pbTimes = Arrays.copyOf(pbs, count);
            this.bestSegments = Arrays.copyOf(best, count);
            Arrays.fill(pbTimes, pbs.length, count, -1);
            Arrays.fill(bestSegments, pbs.length, count, -1);
        }

        bestSegmentsCum = new long[count];
        long total = 0;
        for (int i = 0; i < count; i++){
            total += bestSegments[i];
            bestSegmentsCum[i] = total;
        }

        this.title = title;
        tempPB = new long[count];
        tempBest = new long[count];
        clearTemp();
    }

    public int getCount() {
        return count;
    }

    public long getId(){
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void clearTemp() {
        Arrays.fill(tempPB, -1);
        Arrays.fill(tempBest, -1);
    }

    public void setNewPB() {
        System.arraycopy(tempPB, 0, pbTimes, 0, tempPB.length);
        for (int i = 0; i < count; i++) {
            if (tempBest[i] > 0) bestSegments[i] = tempBest[i];
        }
    }

    public void saveToFile(Context context) {
        RunDataAccess data = new RunDataAccess(context);
        data.updateRun(this);
    }

    public void update(long time, int curSplit) {
        tempPB[curSplit] = time;
        long tempSeg = curSplit == 0 ? time : (time - tempPB[curSplit - 1]);
        if (bestSegments[curSplit] == -1 || tempSeg < bestSegments[curSplit]) tempBest[curSplit] = tempSeg;
    }

    @Override
    public String toString(){
        return title + ": " + count + " splits. ";
    }

    public static SplitSet empty() {
        return new SplitSet("", new String[]{});
    }
}