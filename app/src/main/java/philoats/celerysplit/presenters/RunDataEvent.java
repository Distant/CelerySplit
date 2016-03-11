package philoats.celerysplit.presenters;

import java.util.ArrayList;

import philoats.celerysplit.models.Run;

public class RunDataEvent {

    public static final int NEW_RUNS = 0;
    public static final int ADDED = 1;
    public static final int DELETED = 2;
    public static final int UPDATED = 3;

    private int index;
    private int eventType;
    private ArrayList<Run> runs;

    public RunDataEvent(int index, int eventType, ArrayList<Run> runs){
        this.index = index;
        this.eventType = eventType;
        this.runs = runs;
    }

    public RunDataEvent(ArrayList<Run> runs){
        this.index = -1;
        this.eventType = NEW_RUNS;
        this.runs = runs;
    }

    public int getIndex(){
        return index;
    }

    public int getEventType() {
        return eventType;
    }

    public ArrayList<Run> getRuns() {
        return runs;
    }
}
