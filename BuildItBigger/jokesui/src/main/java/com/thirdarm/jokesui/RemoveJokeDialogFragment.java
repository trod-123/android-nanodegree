package com.thirdarm.jokesui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.thirdarm.jokes.backend.jokesApi.model.Joke;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TROD on 20160417.
 */
public class RemoveJokeDialogFragment extends DialogFragment {
    private static final String LIST_STATE_KEY = "list-state-key";
    private static final String NAMES_LIST_KEY = "names-list-key";
    private static final String IDS_LIST_KEY = "ids-list-key";
    private static final String SELECTED_IDS_LIST_KEY = "selected-ids-list-key";

    private MaterialDialog mRemoveDialog;
    private RecyclerView mRecyclerView;
    private JokesAdapter mJokesAdapter;

    private TextView textview_dialog_loading;
    private ProgressBar loadingDialogSpinner;

    public RemoveJokeDialogFragment() {
    }

    @Override
    public MaterialDialog getDialog() {
        return mRemoveDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // A little trick to save relevant jokes content
        // (1) Extract the joke elements used in recyclerview ui
        // (2) With the saveInstanceState, create new joke objects, populate a list, and set list
        // for the adapter while also maintaining selected items and positive text button text
        List<Joke> jokes = mJokesAdapter.getList();
        // Save list state
        int listState = mRecyclerView.getVerticalScrollbarPosition();
        outState.putInt(LIST_STATE_KEY, listState);
        // Extract and save relevant jokes content
        if (jokes != null && jokes.size() > 0) {
            ArrayList<Integer> selectedIds = mJokesAdapter.getSelectedIds();
            ArrayList<String> jokeNames = new ArrayList<>();
            ArrayList<Integer> jokeIds = new ArrayList<>();
            for (Joke joke : jokes) {
                jokeNames.add(joke.getJokeName());
                jokeIds.add(joke.getJokeId());
            }
            outState.putStringArrayList(NAMES_LIST_KEY, jokeNames);
            outState.putIntegerArrayList(IDS_LIST_KEY, jokeIds);
            outState.putIntegerArrayList(SELECTED_IDS_LIST_KEY, selectedIds);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View removeDialogRoot = getActivity().getLayoutInflater().inflate(R.layout.dialog_removejoke, null);
        // Prepare the empty view for the dialog
        textview_dialog_loading = (TextView)
                removeDialogRoot.findViewById(R.id.dialog_remove_joke_textview_empty);
        loadingDialogSpinner = (ProgressBar)
                removeDialogRoot.findViewById(R.id.dialog_remove_joke_progress_spinner);
        loadingDialogSpinner.setIndeterminate(true);
        // Prepare the recyclerview
        mRecyclerView = (RecyclerView) removeDialogRoot.findViewById(R.id.dialog_remove_joke_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mJokesAdapter = new JokesAdapter(getContext(), new JokesAdapter.JokesAdapterOnClickListener() {
            // (3) This is the third click method that is called when user presses on a view.
            //      This calls the last method, which hosted in the housing fragment
            @Override
            public void onClick() {
                setPositiveButtonText();
            }
        }, textview_dialog_loading);
        // Set the adapter
        mRecyclerView.setAdapter(mJokesAdapter);
        // Retrieve list state and list/item positions
        if (savedInstanceState != null) {
            // Create partial joke objects to repopulate the adapter upon reconfiguration
            ArrayList<Joke> jokes = new ArrayList<>();
            ArrayList<Integer> selectedIds =
                    savedInstanceState.getIntegerArrayList(SELECTED_IDS_LIST_KEY);
            ArrayList<String> jokeNames =
                    savedInstanceState.getStringArrayList(NAMES_LIST_KEY);
            ArrayList<Integer> jokeIds =
                    savedInstanceState.getIntegerArrayList(IDS_LIST_KEY);
            if (jokeNames != null && jokeNames.size() > 0 && jokeIds != null && jokeIds.size() > 0) {
                for (int i = 0; i < jokeNames.size(); i++) {
                    jokes.add(new Joke()
                            .setJokeName(jokeNames.get(i))
                            .setJokeId(jokeIds.get(i))
                    );
                }
                mJokesAdapter.swapList(jokes);
                mJokesAdapter.setSelectedIds(selectedIds);
            }
            // Restore the list state if exists
            int listState = savedInstanceState.getInt(LIST_STATE_KEY);
            mRecyclerView.setVerticalScrollbarPosition(listState);
        } else {
            mJokesAdapter.swapList(null);
            startGetAllJokesTask();
        }

        mRemoveDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_remove_joke_title)
                .customView(removeDialogRoot, false)
                .positiveText(R.string.dialog_remove_joke_positive_button_none)
                .negativeText(R.string.dialog_remove_joke_negative_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Utilities.getJokesFragment(getActivity())
                                .startRemoveJokeTask(mJokesAdapter.getSelectedIds());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeSelectedIds();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        removeSelectedIds();
                    }
                })
                .build();
        if (mJokesAdapter.getSelectedIds() == null || mJokesAdapter.getSelectedIds().size() == 0) {
            mRemoveDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        } else {
            setPositiveButtonText();
        }
        return mRemoveDialog;
    }

    /**
     * Dynamically enable and disable the positive button if any list elements are selected
     */
    public void setPositiveButtonText() {
        MDButton positiveButton = mRemoveDialog.getActionButton(DialogAction.POSITIVE);
        if (mJokesAdapter.getSelectedIds().size() == 1) {
            positiveButton.setEnabled(true);
            positiveButton.setText(R.string.dialog_remove_joke_positive_button_single);
        } else if (mJokesAdapter.getSelectedIds().size() > 1) {
            positiveButton.setEnabled(true);
            positiveButton.setText(getString(R.string.dialog_remove_joke_positive_button,
                    mJokesAdapter.getSelectedIds().size()));
        } else {
            positiveButton.setEnabled(false);
            positiveButton.setText(R.string.dialog_remove_joke_positive_button_none);
        }
    }

    /**
     * Clears the selected ids list and resets the recyclerview views
     * Called when dialog is dismissed through any mechanisms (positive, negative, dismissed)
     */
    public void removeSelectedIds() {
        // Clear the id list
        mJokesAdapter.clearSelectedIds();
        // Reset all recyclerview views: unchecked all checked boxes and return scrolling to top
        mRecyclerView.getLayoutManager().removeAllViews();
    }

    /**
     * Used for generating the remove joke dialog list
     */
    public void startGetAllJokesTask() {
        // If no internet connection, show a message
        if (!Utilities.isNetworkAvailable(getContext())) {
            textview_dialog_loading.setText(R.string.dialog_remove_joke_list_no_internet);
            return;
        }
        textview_dialog_loading.setText(R.string.dialog_remove_joke_list_loading);
        loadingDialogSpinner.setVisibility(View.VISIBLE);
        new GetAllJokesTask(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Joke joke) {
                // This is not called in this async task
            }

            @Override
            public void onTaskFinished(List<Joke> jokes) {
                loadingDialogSpinner.setVisibility(View.INVISIBLE);
                // Make sure to check if there are any jokes first before populating
                if (jokes == null || jokes.isEmpty()) {
                    textview_dialog_loading.setText(R.string.dialog_remove_joke_list_empty);
                }
                mJokesAdapter.swapList(jokes);
                mJokesAdapter.notifyDataSetChanged();
            }
        }).execute();
    }
}
