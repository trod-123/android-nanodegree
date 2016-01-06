package it.jaschke.alexandria;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.provider.books.BooksSelection;

/**
 * Created by TROD on 20160104.
 */
public class ViewBooksFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ViewBooksFragment.class.getSimpleName();
    private static final int BOOKS_LOADER = 0;

    private EditText mSearchField;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private ViewAdapter mViewAdapter;
    private ProgressBar mLoadingSpinner;

    // For the saveInstanceState
    private static final String SIS_QUERY = "query";

    public ViewBooksFragment(){
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.title_fragment_books);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOOKS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSearchField != null) {
            outState.putString(SIS_QUERY, mSearchField.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_view_books, container, false);
        View emptyView = mRootView.findViewById(R.id.view_books_empty);

        mSearchField = (EditText) mRootView.findViewById(R.id.view_books_search_field);
        mLoadingSpinner = (ProgressBar) mRootView.findViewById(R.id.view_books_progress_spinner);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.view_books_recyclerview);
        mViewAdapter = new ViewAdapter(getContext(), new ViewAdapter.ViewAdapterOnClickHandler() {
            @Override
            public void onClick(String bookId, ViewAdapter.ViewHolder holder) {
                Log.d(LOG_TAG, "Book " + bookId + " has been clicked!!!");
            }
        }, emptyView);
        mViewAdapter.swapCursor(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mViewAdapter);

        // This is performed every time the text field value is changed
        // Use of timer to delay changed text event attributed to Marcus Pohls of futurestud.io
        //  (url: https://futurestud.io/blog/android-how-to-delay-changedtextevent-on-androids-edittext)
        mSearchField.addTextChangedListener(new TextWatcher() {
            private Timer timer;
            private static final int TIMER_DURATION = 600;

            // Called before text in EditText changes
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            // Called while text in EditText changes
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // User is currently typing: reset already started timer (if existing)
                if (timer != null) {
                    timer.cancel();
                }
            }

            // Called after text in EditText changes
            @Override
            public void afterTextChanged(final Editable s) {
                // User typed: start the timer. This allows the action below to be executed only
                //  within the temporal bounds set by the timer. The timer runs until the specified
                //  duration time (in ms). If the user continues to type, the timer is reset back
                //  to 0 and starts again.
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override public void run() {
                        // This is where actions are done
                        String query = s.toString();
                    }
                }, TIMER_DURATION);
            }
        });

        // Start the fetch fragment
        mRootView.findViewById(R.id.view_books_button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "This will start the fetch fragment.", Toast.LENGTH_SHORT).show();
            }
        });

        // Save the value currently in the search field for when the activity recreates itself
        if (savedInstanceState != null) {
            mSearchField.setText(savedInstanceState.getString(SIS_QUERY));
            mSearchField.setHint("");
        }

        return mRootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                (new BooksSelection()).uri(),
                BooksColumns.ALL_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewAdapter.swapCursor(new BooksCursor(data));
        mViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewAdapter.swapCursor(null);
    }
}
