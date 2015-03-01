package bdoty.celerysplit.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import bdoty.celerysplit.helpers.RunFileHelper;

public class SplitSet {

    public String title;
    public String[] names;
    public long[] pbTimes;
    public long[] bestSegs;
    public long[] tempPB;
    public long[] tempBest;
    private int count;

    public SplitSet(String title, ArrayList<String> names) {
        this(title, names, null, null);
    }

    public SplitSet(String title, ArrayList<String> names, long[] pbs) {
        this(title, names, pbs, null);
    }

    public SplitSet(String title, ArrayList<String> names, long[] pbs, long[] best) {
        this.count = names.size();
        this.names = names.toArray(new String[count]);
        this.pbTimes = new long[count];
        this.bestSegs = new long[count];
        if (pbs == null) {
            for (int i = 0; i < count; i++) {
                pbTimes[i] = 5000 * i + 5000;
                bestSegs[i] = -1;
            }
        } else {
            this.pbTimes = Arrays.copyOf(pbs, count);
            this.bestSegs = Arrays.copyOf(best, count);
            Arrays.fill(pbTimes, pbs.length, count, -1);
            Arrays.fill(bestSegs, pbs.length, count, -1);
        }
        this.title = title;
        tempPB = new long[count];
        tempBest = new long[count];
        clearTemp();
    }

    public int getCount() {
        return count;
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
            if (tempBest[i] > 0) bestSegs[i] = tempBest[i];
        }
    }

    public void saveToFile(Context context) {
        RunFileHelper helper = new RunFileHelper(context);
        helper.saveToFile(this);

    }

    public void update(long time, Integer curSplit) {
        tempPB[curSplit] = time;
        long tempSeg = curSplit == 0 ? time : (time - tempPB[curSplit - 1]);
        if (bestSegs[curSplit] == -1 || tempSeg < bestSegs[curSplit]) tempBest[curSplit] = tempSeg;
    }
}