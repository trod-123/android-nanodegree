package com.thirdarm.jokesui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.thirdarm.jokes.backend.jokesApi.model.Joke;

import java.util.ArrayList;
import java.util.List;

public class JokesFragment extends Fragment {
    private static final String LOG_TAG = JokesFragment.class.getSimpleName();

    public static final String TITLE_TEXT = "title-text";
    public static final String CONTENT_TEXT = "content-text";
    public static final String SUB_TEXT = "sub-text";
    public static final String SHARE_TEXT = "share-text";
    public static final String FORMAT_TEXT = "format-text";
    public static final String SCROLL_POSITION = "scroll-position";

    private static int FLAVOR;

    private class FORMAT {
        private static final int GET = 0;
        private static final int ADD = 1;
        private static final int REMOVE = 2;
        private static final int ERROR = 3;
        private static final int NOTIFY = 4;
    }
    private int mTextFormat;

    private TextView textview_press, textview_main, textview_content, textview_sub;
    private ProgressBar loadingSpinner;
    private ScrollView mContentScrollView;

    private String mSharedMessage;

    private Joke mCurrentJoke;

    public JokesFragment() {
    }

    public static JokesFragment newInstance(int flavor) {
        FLAVOR = flavor;
        return new JokesFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE_TEXT, textview_main.getText().toString());
        outState.putString(CONTENT_TEXT, textview_content.getText().toString());
        outState.putString(SUB_TEXT, textview_sub.getText().toString());
        outState.putString(SHARE_TEXT, mSharedMessage);
        outState.putInt(FORMAT_TEXT, mTextFormat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jokes, container, false);
        mContentScrollView = (ScrollView) root.findViewById(R.id.scrollview_joke_content);

        // Set up main part of UI
        textview_press = (TextView) root.findViewById(R.id.textview_screen_press);
        textview_main = (TextView) root.findViewById(R.id.textview_joke_main);
        textview_content = (TextView) root.findViewById(R.id.textview_joke_content);
        textview_sub = (TextView) root.findViewById(R.id.textview_joke_sub);

        loadingSpinner = (ProgressBar) root.findViewById(R.id.loading_spinner);

        // Set up the get joke button
        Button buttonJoke = (Button) root.findViewById(R.id.button_joke);
        buttonJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tellJoke();
            }
        });

        // Set up the add joke button
        Button buttonAddJoke = (Button) root.findViewById(R.id.button_add_joke);
        buttonAddJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addJoke();
            }
        });

        // Set up the remove joke button and dialog
        Button buttonRemoveJoke = (Button) root.findViewById(R.id.button_remove_joke);
        buttonRemoveJoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the remove joke dialog only in the PAID version
                if (FLAVOR == Constants.FLAVOR.PAID) {
                    removeJoke();
                } else {
                    openPaidDialog();
                }
            }
        });

        // If restoring fragment, reload ui with same elements and formatting
        if (savedInstanceState != null) {
            updateTextViews(savedInstanceState.getString(SHARE_TEXT),
                    savedInstanceState.getString(TITLE_TEXT),
                    savedInstanceState.getString(CONTENT_TEXT),
                    savedInstanceState.getString(SUB_TEXT),
                    savedInstanceState.getInt(FORMAT_TEXT)
                    );
        }

        return root;
    }

    public void tellJoke() {
        startGetRandomJokeTask();
    }

    public void addJoke() {
        openAddJokeDialog();
    }

    public void removeJoke() {
        openRemoveJokeDialog();
    }

    public String getCurrentJoke() {
        return mSharedMessage;
    }

    /**
     * Helper method to set up the UI
     *
     * @param sharedMessage Message passed onto the hosting activity for social networking
     * @param main          Main string (e.g. title of joke, headliner)
     * @param content       Content string (e.g. joke itself)
     * @param sub           Footer string (e.g. joke id)
     * @param format        Formatting constant
     */
    public void updateTextViews(String sharedMessage, String main, String content, String sub,
                                int format) {
        // Set the shared message
        mSharedMessage = sharedMessage;
        // Save the current format
        mTextFormat = format;

        // Set the text
        textview_main.setText(main);
        textview_content.setText(content);
        textview_sub.setText(sub);

        // Set the formatting. If GET or ADD, make the scrollview clickable. Otherwise, disable it
        switch (format) {
            case FORMAT.GET:
                mContentScrollView.setScrollY(0);
                textview_press.setVisibility(View.VISIBLE);
                textview_main.setGravity(Gravity.CENTER);
                textview_content.setGravity(Gravity.START);
                textview_sub.setGravity(Gravity.END);
                mContentScrollView.getChildAt(0).setOnClickListener(mOnJokeClickListener);
                break;
            case FORMAT.ADD:
                textview_press.setVisibility(View.VISIBLE);
                textview_main.setGravity(Gravity.CENTER);
                textview_content.setGravity(Gravity.CENTER);
                textview_sub.setGravity(Gravity.START);
                mContentScrollView.getChildAt(0).setOnClickListener(mOnJokeClickListener);
                break;
            case FORMAT.REMOVE:
            case FORMAT.ERROR:
            case FORMAT.NOTIFY:
                textview_press.setVisibility(View.GONE);
                textview_main.setGravity(Gravity.CENTER);
                textview_content.setGravity(Gravity.CENTER);
                textview_sub.setGravity(Gravity.CENTER);
                mContentScrollView.getChildAt(0).setOnClickListener(null);
        }
    }


    /**
     * Listens for user clicks on a joke and launches a new activity showing the joke itself
     * without the added clutter
     */
    public View.OnClickListener mOnJokeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ShowJokeActivity.class);
            intent.putExtra(TITLE_TEXT, mCurrentJoke.getJokeName());
            intent.putExtra(CONTENT_TEXT, mCurrentJoke.getJoke());
            intent.putExtra(SUB_TEXT, getString(R.string.joke_get_sub, mCurrentJoke.getJokeId()));
            if (getActivity() instanceof JokeClickCallback)
                ((JokeClickCallback) getActivity()).onJokeClick(intent);
            else
                throw new RuntimeException(
                        "The hosting activity does not implement JokeClickCallback");
        }
    };

    /**
     * To be used with the hosting activity to launch a new activity showing the joke itself
     * without the added clutter.
     * Implement randomly displaying interstitial ad if free version
     */
    public interface JokeClickCallback {
        void onJokeClick(Intent intent);
    }


    /*
        GET JOKE STUFF
     */

    /**
     * Grabs and displays a random joke from GC
     */
    public void startGetRandomJokeTask() {
        // TODO: Local jokes database?
        // If no internet connection, show a nice message
        if (!Utilities.isNetworkAvailable(getContext())) {
            updateTextViews(getString(R.string.network_share_joke_get_no_internet),
                    getString(R.string.joke_no_internet_title),
                    getString(R.string.joke_get_no_internet_content), "", FORMAT.ERROR);
            return;
        }
        loadingSpinner.setVisibility(View.VISIBLE);
        new GetRandomJokeTask(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Joke joke) {
                if (joke != null) {
                    // Success
                    mCurrentJoke = joke;
                    updateTextViews(getString(R.string.network_share_joke_get,
                            joke.getJokeName(), joke.getJoke()),
                            joke.getJokeName(), joke.getJoke(),
                            getString(R.string.joke_get_sub, joke.getJokeId()),
                            FORMAT.GET);
                } else {
                    // Error
                    updateTextViews(getString(R.string.network_share_joke_get_error),
                            getString(R.string.joke_empty_title),
                            getString(R.string.joke_get_error_content),
                            getString(R.string.joke_get_error_sub), FORMAT.ERROR);
                }
                loadingSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onTaskFinished(List<Joke> jokes) {
                // This is not called in this async task
            }
        }).execute();
    }


    /*
        ADD JOKE STUFF
     */

    /**
     * Starts a dialog that prompts the user to add a joke.
     */
    public void openAddJokeDialog() {
        AddJokeDialogFragment dialog = new AddJokeDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager().beginTransaction(),
                Constants.TAGS.FRAGMENT_ADD_JOKE_DIALOG);
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Starts up the AddJokeTask to add a user-defined joke to GC
     * @param joke
     * @param jokeName
     * @param categoryIds
     */
    public void startAddJokeTask(final String joke, final String jokeName, List<Integer> categoryIds) {
        // TODO: Queue joke to be added for when user gets internet connection
        // If no internet connection, show a nice message
        if (!Utilities.isNetworkAvailable(getContext())) {
            updateTextViews(getString(R.string.network_share_joke_added_error,
                    jokeName, joke),
                    getString(R.string.joke_no_internet_title),
                    getString(R.string.joke_added_no_internet_content),
                    getString(R.string.joke_added_error_sub, jokeName, joke), FORMAT.ERROR);
            return;
        }
        loadingSpinner.setVisibility(View.VISIBLE);
        new AddJokeTask(joke, jokeName, categoryIds, new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Joke jokeObject) {
                loadingSpinner.setVisibility(View.GONE);
                // Make sure to check if there is a joke first before doing anything
                if (jokeObject != null) {
                    // Success
                    mCurrentJoke = jokeObject;
                    updateTextViews(getString(R.string.network_share_joke_added,
                            jokeObject.getJokeName(), jokeObject.getJoke()),
                            getString(R.string.joke_added_title),
                            getString(R.string.joke_added_content),
                            getString(R.string.joke_added_sub, jokeObject.getJokeName(),
                            jokeObject.getJokeId(), jokeObject.getJoke()), FORMAT.ADD);
                } else {
                    // Error
                    updateTextViews(getString(R.string.network_share_joke_added_error),
                            getString(R.string.joke_error_title),
                            getString(R.string.joke_added_error_content),
                            getString(R.string.joke_added_error_sub, jokeName, joke), FORMAT.ERROR);
                }
            }

            @Override
            public void onTaskFinished(List<Joke> jokes) {
                // This is not called in this async task
            }
        }).execute();
    }


    /*
        REMOVE JOKE STUFF
     */

    /**
     * Creates and displays a MaterialDialog object which shows a multi-selectable list of jokes
     */
    public void openRemoveJokeDialog() {
        RemoveJokeDialogFragment dialog = new RemoveJokeDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager().beginTransaction(),
                Constants.TAGS.FRAGMENT_REMOVE_JOKE_DIALOG);
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Remove jokes currently in the joke adapter's selectedIds list
     */
    public void startRemoveJokeTask(ArrayList<Integer> selectedIds) {
        // If no internet connection, show a nice message
        if (!Utilities.isNetworkAvailable(getContext())) {
            updateTextViews(getString(R.string.network_share_joke_removed_no_internet),
                    getString(R.string.joke_no_internet_title),
                    getString(R.string.joke_removed_no_internet_content), "", FORMAT.ERROR);
            return;
        }

        for (int i : selectedIds) {
            loadingSpinner.setVisibility(View.VISIBLE);
            new RemoveJokeTask(i, new TaskFinishedListener() {
                @Override
                public void onTaskFinished(Joke joke) {
                    loadingSpinner.setVisibility(View.GONE);
                    // Make sure to check if there is a joke first before doing anything
                    if (joke != null) {
                        // Success
                        updateTextViews(getString(R.string.network_share_joke_removed,
                                joke.getJokeName(), joke.getJoke()),
                                getString(R.string.joke_removed_title),
                                getString(R.string.joke_removed_content),
                                getString(R.string.joke_removed_sub,
                                        joke.getJokeName(), joke.getJokeId()),
                                FORMAT.REMOVE);
                    } else {
                        // Error
                        updateTextViews(getString(R.string.network_share_joke_removed_error),
                                getString(R.string.joke_error_title),
                                getString(R.string.joke_removed_error_content), "", FORMAT.ERROR);
                    }
                }

                @Override
                public void onTaskFinished(List<Joke> jokes) {
                    // This is not called in this async task
                }
            }).execute();
        }
    }

    /**
     * This comes up if the user is using the free version
     */
    public void openPaidDialog() {
        PaidDialogFragment dialog = new PaidDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager().beginTransaction(),
                Constants.TAGS.FRAGMENT_PAID_DIALOG);
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }


    /*
        RESET DATABASE STUFF
     */

    /**
     * Opens a dialog that prompts the user with ARE YOU SURE stuff
     */
    public void openResetDialog() {
        DatabaseResetDialogFragment dialog = new DatabaseResetDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager().beginTransaction(),
                Constants.TAGS.FRAGMENT_RESET_DATABASE_DIALOG);
        getActivity().getSupportFragmentManager().executePendingTransactions();

    }

    /**
     * Reset the jokes database to bring the good ol' jokes back
     */
    public void startResetJokeContainerTask() {
        // If no internet connection, show a nice message
        if (!Utilities.isNetworkAvailable(getContext())) {
            updateTextViews(getString(R.string.network_share_joke_error),
                    getString(R.string.joke_no_internet_title),
                    getString(R.string.joke_reset_no_internet_content), "", FORMAT.ERROR);
            return;
        }
        updateTextViews("", getString(R.string.joke_reset_ip_content), "", "", FORMAT.NOTIFY);
        loadingSpinner.setVisibility(View.VISIBLE);
        new ResetJokesDatabaseTask(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Joke joke) {
                // This is not called in this async task
            }

            @Override
            public void onTaskFinished(List<Joke> jokes) {
                loadingSpinner.setVisibility(View.GONE);
                if (jokes != null) {
                    // Success
                    updateTextViews(getString(R.string.network_share_joke_reset),
                            getString(R.string.joke_reset_title), getString(R.string.joke_reset_complete_content),
                            getString(R.string.joke_reset_sub), FORMAT.NOTIFY);
                } else {
                    // Error
                    updateTextViews(getString(R.string.network_share_joke_reset_error),
                            getString(R.string.joke_error_title), getString(R.string.joke_reset_error_content),
                            "", FORMAT.ERROR);
                }
            }
        }).execute();
    }


    /*
        ABOUT
     */

    /**
     * Creates and displays a MaterialDialog object which displays about stuff
     */
    public void openAboutDialog() {
        AboutDialogFragment dialog = new AboutDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager().beginTransaction(),
                Constants.TAGS.FRAGMENT_ABOUT_DIALOG);
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }
}
