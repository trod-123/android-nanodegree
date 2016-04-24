package com.thirdarm.jokesui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.thirdarm.jokes.backend.jokesApi.model.Joke;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to populate the view jokes list. Only reads joke name and id.
 */
public class JokesAdapter extends RecyclerView.Adapter<JokesAdapter.ViewHolder> {
    private static final String LOG_TAG = JokesAdapter.class.getSimpleName();

    private Context mContext;
    private List<Joke> mJokes;
    final private JokesAdapterOnClickListener mClickHandler;
    final private TextView mEmptyView;

    // To keep track of which jokes are selected
    private ArrayList<Integer> mSelectedIds = new ArrayList<>();

    // A viewHolder instance is created for EACH item in the recycler view.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views go here
        public TextView mJokeTextView;
        public TextView mJokeIdTextView;
        public CheckBox mCheckBox;

        // (1) This is the first click method that is called when user presses on a view
        // This handles clicks to the view itself (i.e. anywhere but the checkbox). A separate
        // listener is used for direct checkbox clicks (see below in the ViewHolder constructor)
        @Override
        public void onClick(View v) {
            handleButtonClicks(mCheckBox, false, this);
        }

        public ViewHolder(View view) {
            super(view);
            mJokeTextView = (TextView) view.findViewById(R.id.jokes_list_textview_joke);
            mJokeIdTextView = (TextView) view.findViewById(R.id.jokes_list_textview_joke_id);
            mCheckBox = (CheckBox) view.findViewById(R.id.jokes_list_checkbox);
            // Setting a separate click listener for direct checkbox click
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // To refer to the parent class, just state the classname, and .this
                    handleButtonClicks(mCheckBox, true, ViewHolder.this);
                }
            });

            view.setOnClickListener(this);
        }
    }

    // (2) This is the second click method that is called when user presses on a view
    public interface JokesAdapterOnClickListener {
        void onClick();
    }

    public JokesAdapter(Context context, JokesAdapterOnClickListener handler, View empty) {
        mContext = context;
        mClickHandler = handler;
        mEmptyView = (TextView) empty;
    }

    // This contains grab n' go code. Create viewholder for each item
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.jokes_item, parent, false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return vh;
        } else
            throw new RuntimeException("The ViewGroup is not bound to RecyclerView");
    }

    // Where the good stuff happens. Populate viewholder elements.
    // This is called each time an item comes to view.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the joke elements
        Joke joke = mJokes.get(position);
        String jokeName = joke.getJokeName();
        int jokeId = joke.getJokeId();
        // Set the joke elements
        holder.mJokeTextView.setText(jokeName);
        holder.mJokeIdTextView.setText(mContext.getString(R.string.joke_get_sub, jokeId));
        // Set the checkbox if selected (for use after recycling has happened)
        if (mSelectedIds.contains(jokeId)) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }
        //Log.d(LOG_TAG, "Currently selected ids: " + mSelectedIds);
    }

    /**
     * For handling all button (and checkbox) clicks.
     * @param checkBox Checkbox contained in the view
     * @param boxDirectClick If user directly clicked on checkbox (reverse)
     * @param vh Viewholder used to call the click listener
     */
    public void handleButtonClicks(CheckBox checkBox, boolean boxDirectClick, ViewHolder vh) {
        Joke joke = mJokes.get(vh.getAdapterPosition());
        if (!boxDirectClick) {
            // If the user had NOT clicked on the checkbox (i.e. anywhere else in the view)
            if (checkBox.isChecked()) {
                // If the joke is currently checked, remove it from the list
                checkBox.setChecked(false);
                mSelectedIds.remove(joke.getJokeId());
            } else {
                // Otherwise, add it to the list
                checkBox.setChecked(true);
                mSelectedIds.add(joke.getJokeId());
            }
        } else {
            // If the user DID click on the checkbox (i.e. not anywhere else in the view)
            if (checkBox.isChecked()) {
                mSelectedIds.add(joke.getJokeId());
            } else {
                mSelectedIds.remove(joke.getJokeId());
            }
        }
        mClickHandler.onClick();
        //Log.d(LOG_TAG, "Currently selected ids: " + mSelectedIds);
    }

    @Override
    public int getItemCount() {
        return mJokes != null ? mJokes.size() : 0;
    }

    public List<Joke> getList() {
        return mJokes;
    }

    public List<Joke> swapList(List<Joke> jokes) {
        mJokes = jokes;
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return mJokes;
    }

    public ArrayList<Integer> getSelectedIds() {
        return mSelectedIds;
    }

    public void setSelectedIds(ArrayList<Integer> selectedIds) {
        this.mSelectedIds = selectedIds;
    }

    public void clearSelectedIds() {
        mSelectedIds.clear();
        //Log.d(LOG_TAG, "Cleared selected ids. Current state: " + mSelectedIds);
    }
}
