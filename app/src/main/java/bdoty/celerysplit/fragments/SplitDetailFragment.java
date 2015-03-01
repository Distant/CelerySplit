package bdoty.celerysplit.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import bdoty.celerysplit.R;


public class SplitDetailFragment extends Fragment {
    private static final String SPLT_NAME_PARAM = "splitNames";

    private String[] splitNames;

    private OnFragmentInteractionListener mListener;
    private ListView lview;
    private ListAdapter adapter;

    public static SplitDetailFragment newInstance(String[] splitNames) {
        SplitDetailFragment fragment = new SplitDetailFragment();
        Bundle args = new Bundle();
        args.putStringArray(SPLT_NAME_PARAM, splitNames);
        fragment.setArguments(args);
        return fragment;
    }

    public SplitDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            splitNames = getArguments().getStringArray(SPLT_NAME_PARAM);
        }
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, splitNames);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_split_detail, container, false);
        lview = (ListView) view.findViewById(android.R.id.list);
        lview.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Spyro 2: Gateway to Glimmer");
       // ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
