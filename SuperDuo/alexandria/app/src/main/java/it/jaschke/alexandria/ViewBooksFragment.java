package it.jaschke.alexandria;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.provider.books.BooksSelection;

/**
 * Created by TROD on 20160104.
 */
public class ViewBooksFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ViewBooksFragment.class.getSimpleName();
    private static final int BOOKS_LOADER_ID = 0;

    private EditText mSearchField;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private ViewAdapter mViewAdapter;
    private ProgressBar mLoadingSpinner;

    // For the saveInstanceState
    private static final String SIS_QUERY = "query";

    // For keeping track of whether returned cursor was queried
    private boolean mQueried = false;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface BookSelectionCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onBookItemSelected(String bookId, ViewAdapter.ViewHolder vh);
    }

    /**
     * This mechanism allows activities to be notified of the + FAB click.
     */
    public interface FetchButtonClickedListener {
        void onFetchButtonClicked();
    }

    public ViewBooksFragment(){
    }

    public static ViewBooksFragment newInstance() {
        return new ViewBooksFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_books);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.title_fragment_books);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOOKS_LOADER_ID, null, this);
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
                ((BookSelectionCallback) getActivity())
                        .onBookItemSelected(bookId, holder);
            }
        }, emptyView);
        mViewAdapter.swapCursor(null);
        if (((ViewGroup.MarginLayoutParams) mSearchField.getLayoutParams()).leftMargin > 0) {
            // Number of columns determined by sw and orientation (see res/values/integers)
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    getResources().getInteger(R.integer.num_card_columns),
                    StaggeredGridLayoutManager.VERTICAL));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
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
                if (timer != null)
                    timer.cancel();
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
                        // Restarting the loader needs to be done on the ui thread
                        mRootView.post(new Runnable() {
                            @Override
                            public void run() {
                                restartLoader();
                            }
                        });
                    }
                }, TIMER_DURATION);
            }
        });

        // Start the fetch fragment
        mRootView.findViewById(R.id.view_books_button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FetchButtonClickedListener) getActivity()).onFetchButtonClicked();
            }
        });

        // Save the value currently in the search field for when the activity recreates itself
        if (savedInstanceState != null) {
            mSearchField.setText(savedInstanceState.getString(SIS_QUERY));
            mSearchField.setHint("");
        }

        return mRootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(BOOKS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Prepare the selection for the query
        String selection =
                BooksColumns.TITLE + " LIKE ? OR " +
                BooksColumns.SUBTITLE + " LIKE ? OR " +
                BooksColumns.AUTHORS + " LIKE ? OR " +
                BooksColumns.ISBN_10 + " LIKE ? OR " +
                BooksColumns.ISBN_13 + " LIKE ? OR " +
                BooksColumns.PUBLISHER + " LIKE ? ";
        String query = "%" + mSearchField.getText().toString() + "%";

        // If there is a query, load the cursor containing the results
        if(query.length() - 2 > 0) {
            mQueried = true;
            return new CursorLoader(getContext(),
                    (new BooksSelection()).uri(),
                    null,
                    selection,
                    new String[]{query, query, query, query, query, query},
                    null);
        }

        // If the query is empty, load all the books in the library
        mQueried = false;
        return new CursorLoader(getContext(),
                (new BooksSelection()).uri(),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewAdapter.swapCursor(new BooksCursor(data));
        // If the user queries, set the no results string. If there is no query, set the empty
        //  library string.
        TextView emptyView = (TextView) mRootView.findViewById(R.id.view_books_empty);
        if (mQueried)
            emptyView.setText(R.string.empty_view_books_no_results);
        else
            emptyView.setText(R.string.empty_view_books);
        mViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewAdapter.swapCursor(null);
    }
}
