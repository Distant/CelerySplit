package bdoty.celerysplit.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import bdoty.celerysplit.R;
import bdoty.celerysplit.models.SplitSet;

public class EditSplitFragment extends Fragment {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> splitNames;
    private EditListener listener;
    private String oldTitle;
    private boolean inEditMode = false;
    private TextView titleView;
    private Toolbar toolbar;

    public static EditSplitFragment newInstance() {
        return new EditSplitFragment();
    }

    public EditSplitFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splitNames = new ArrayList<>();
        splitNames.add("DEFAULT");
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, splitNames);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_edit_split, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String text = textView.getText().toString();
                alertForInput(text, position);
            }
        });

        Button plusButton = (Button) view.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertForInput();
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inEditMode) listener.onSplitsEdited(titleView.getText().toString(), splitNames, oldTitle);
                else listener.onSplitsCreated(titleView.getText().toString(), splitNames);
            }
        });

        titleView = (TextView) view.findViewById(R.id.titleView);
        titleView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Edit Title");

                // Set up the input
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(titleView.getText());
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        titleView.setText(input.getText());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                listener.onCancel();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void alertForInput(String curName, final int pos)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(pos == -1 ? "New Split" : "Edit Split");

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(curName);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pos == -1) splitNames.add(input.getText().toString());
                else splitNames.set(pos,input.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void alertForInput()
    {
        alertForInput("", -1);
    }

    public void loadSplits(String title, SplitSet set)
    {
        titleView.setText(title);
        toolbar.setTitle("Edit Splits");
        oldTitle = title;
        inEditMode = true;
        titleView.setText(title);
        splitNames.clear();
        Collections.addAll(splitNames,set.names);
        adapter.notifyDataSetChanged();
    }

    public void createSplits()
    {
        titleView.setText("Set Title");
        toolbar.setTitle("Create Splits");
        inEditMode = false;
        splitNames.clear();
        adapter.notifyDataSetChanged();
    }

    public interface EditListener
    {
       public void onSplitsCreated(String title, ArrayList<String> names);
       public void onSplitsEdited(String title, ArrayList<String> names, String oldTitle);
       public void onCancel();
    }

    public void setEditListener(EditListener listener)
    {
        this.listener = listener;
    }
}