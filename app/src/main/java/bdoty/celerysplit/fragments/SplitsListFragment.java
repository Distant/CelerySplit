package bdoty.celerysplit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import bdoty.celerysplit.R;
import bdoty.celerysplit.models.SplitSet;
import bdoty.celerysplit.helpers.RunFileHelper;
import bdoty.celerysplit.adapters.SwipeListAdapter;
import bdoty.celerysplit.views.SwipeableItem;

public class SplitsListFragment extends Fragment implements ListView.OnItemClickListener, EditSplitFragment.EditListener, SwipeListAdapter.ButtonListener {

    private LinkedHashMap<String, SplitSet> splitSets;
    private SplitFragmentListener listener;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private EditSplitFragment editPage;

    public static SplitsListFragment newInstance(EditSplitFragment editPage, SplitFragmentListener listener) {
        SplitsListFragment fragment = new SplitsListFragment();
        fragment.setEditPage(editPage);
        fragment.setListener(listener);
        editPage.setEditListener(fragment);
        return fragment;
    }

    private void setListener(SplitFragmentListener listener) {
        this.listener = listener;
    }

    public SplitsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splitSets = new LinkedHashMap<>();

        // TEST SETS
        /*splitSets.put("Spyro 2: Gateway to Glimmer", new SplitSet("Spyro 2: Gateway to Glimmer", new ArrayList(Arrays.asList(new String[]{"Glimmer", "Crush", "Gulp", "Fireball", "Ripto"}))));
        splitSets.put("Kingdom Hearts", new SplitSet("Kingdom Hearts", new ArrayList(Arrays.asList(new String[]{"Traverse Town", "Wonderland", "Deep Jungle", "Olympus", "Agrabah", "Atlantica"}))));
        splitSets.put("Pokemon Delta Emerald", new SplitSet("Pokemon Delta Emerald", new ArrayList(Arrays.asList(new String[]{"Florence", "Mushu", "Lipo", "Madeline", "Godrick", "Petunia", "Elmo", "Kappa", "Pooh", "Test", "Another Test", "Last Test"}))));
        */
        splitSets.put("Example 1", new SplitSet("Example 1", new ArrayList(Arrays.asList(new String[]{"Segment 1","Segment 2","Segment 3","Segment 4","Segment 5","Segment 6"}))));
        splitSets.put("Example 2", new SplitSet("Example 2", new ArrayList(Arrays.asList(new String[]{"Segment 1","Segment 2","Segment 3","Segment 4","Segment 5","Segment 6"}))));
        splitSets.put("Example 3", new SplitSet("Example 3", new ArrayList(Arrays.asList(new String[]{"Segment 1","Segment 2","Segment 3","Segment 4","Segment 5","Segment 6"}))));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splits_list, container, false);

        listView = (ListView) view.findViewById(android.R.id.list);
        // TODO make shadows for top and bottom
        listAdapter = new SwipeListAdapter(getActivity(), R.layout.list_item_layered, splitSets, listView, this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        //listView.setOnTouchListener(new SwipeListListener());

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("RUNS:");

        Button newSplitButton = (Button) view.findViewById(R.id.imageView);
        newSplitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToEdit();
                editPage.createSplits();
                //pager.setCurrentItem(pager.getAdapter().getItemPosition(editPage));
            }
        });

        return view;
    }

    private void transitionToEdit() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.add(R.id.editContainer, editPage);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void transitionFromEdit() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.remove(editPage);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = listView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public String[] keysToArray(HashMap<String, ?> map) {
        return map.keySet().toArray(new String[map.size()]);
    }

    public void setEditPage(EditSplitFragment editPage) {
        this.editPage = editPage;
    }

    @Override
    public void onSplitsCreated(String title, ArrayList<String> names) {
        SplitSet set = new SplitSet(title, names);
        splitSets.put(title, set);
        RunFileHelper helper = new RunFileHelper(getActivity());
        helper.saveToFile(set);
        listAdapter = new SwipeListAdapter(getActivity(), R.layout.list_item_layered, splitSets, listView, this);
        listView.setAdapter(listAdapter);
        transitionFromEdit();
    }

    @Override
    public void onSplitsEdited(String title, ArrayList<String> names, String oldTitle) {
        SplitSet old = splitSets.get(oldTitle);
        SplitSet set = new SplitSet(title, names, old.pbTimes, old.bestSegs);
        if (!oldTitle.equals(title)) splitSets.remove(oldTitle);
        splitSets.put(title, set);
        RunFileHelper helper = new RunFileHelper(getActivity());
        helper.saveToFile(set);
        listAdapter = new SwipeListAdapter(getActivity(), R.layout.list_item_layered, splitSets, listView, this);
        listView.setAdapter(listAdapter);
        transitionFromEdit();
    }

    @Override
    public void onCancel() {
        transitionFromEdit();
    }

    public interface SplitFragmentListener {
        public void onLoadSplits(String title, SplitSet set);

        public void onEditSplits(String title, SplitSet set);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != listener) {
            TextView textView = (TextView) view.findViewById(R.id.textLeft);
            String text = textView.getText().toString();
            listener.onLoadSplits(text, splitSets.get(text));
        }
    }

    @Override
    public void onEdit(String string) {
        transitionToEdit();
        SwipeableItem.getSelected().deselect();
        editPage.loadSplits(string, splitSets.get(string));
    }


    @Override
    public void onDelete(String string) {
        splitSets.remove(string);
        listAdapter.notifyDataSetChanged();
    }
}