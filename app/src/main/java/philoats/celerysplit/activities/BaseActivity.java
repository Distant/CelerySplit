package philoats.celerysplit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public abstract class BaseActivity extends AppCompatActivity{
    private ObjectGraph graph;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        graph = ((MainApplication) getApplication()).getAppGraph();
        graph.inject(this);
    }

    @Override protected void onDestroy(){
        graph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Arrays.asList();
    }


    public void inject(Object object) {
        graph.inject(object);
    }
}
