package philoats.celerysplit.activities;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import philoats.celerysplit.modules.ApplicationModule;
import dagger.ObjectGraph;

public class MainApplication extends Application{
    private ObjectGraph appGraph;

    public void onCreate(){
        super.onCreate();
        this.appGraph = ObjectGraph.create(getModules().toArray());
    }

    public List<Object> getModules(){
        return Arrays.asList(new ApplicationModule());
    }

    public ObjectGraph getAppGraph(){
        return appGraph;
    }
}
