package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.data.ScoresProjections;

/**
 * TODO: Convert this into an Adapter for RecyclerView
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {

    private static final String LOG_TAG = ScoresAdapter.class.getSimpleName();

    public double detail_match_id = 0;

    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    // The dataset
    private Cursor mCursor;
    final private Context mContext;
    final private ScoresAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mHomeNameTextView;
        public TextView mAwayNameTextView;
        public TextView mScoreTextView;
        public TextView mDateTextView;
        public ImageView mHomeCrestImageView;
        public ImageView mAwayCrestImageView;
        public ViewGroup mDetailFragmentContainer;

        public double match_id;

        @Override public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            mClickHandler.onClick(mCursor.getDouble(ScoresProjections.COL_MATCH_ID), this);
        }

        public ViewHolder(View view) {
            super(view);
            mHomeNameTextView = (TextView) view.findViewById(R.id.home_name);
            mAwayNameTextView = (TextView) view.findViewById(R.id.away_name);
            mScoreTextView = (TextView) view.findViewById(R.id.score_textview);
            mDateTextView = (TextView) view.findViewById(R.id.data_textview);
            mHomeCrestImageView = (ImageView) view.findViewById(R.id.home_crest);
            mAwayCrestImageView = (ImageView) view.findViewById(R.id.away_crest);
            mDetailFragmentContainer = (ViewGroup) view.findViewById(R.id.details_fragment_container);

            view.setOnClickListener(this);
        }
    }

    public ScoresAdapter(Context c, ScoresAdapterOnClickHandler handler, View empty)
    {
        mContext = c;
        mClickHandler = handler;
        mEmptyView = empty;
    }

    public interface ScoresAdapterOnClickHandler {
        void onClick(double match_id, ScoresAdapter.ViewHolder holder);
    }

    @Override public ScoresAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.scores_list_item, parent, false);
            ViewHolder mHolder = new ViewHolder(view);
            view.setTag(mHolder);
            return mHolder;
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
    }

//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent)
//    {
//        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
//        ViewHolder mHolder = new ViewHolder(mItem);
//        mItem.setTag(mHolder);
//        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
//        return mItem;
//    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.mHomeNameTextView.setText(mCursor.getString(ScoresProjections.COL_HOME_NAME));
        holder.mAwayNameTextView.setText(mCursor.getString(ScoresProjections.COL_AWAY_NAME));
        holder.mDateTextView.setText(mCursor.getString(ScoresProjections.COL_TIME) + mCursor.getString(ScoresProjections.COL_DATE));
        holder.mScoreTextView.setText(Utilities.getScores(mContext,
                mCursor.getInt(ScoresProjections.COL_HOME_GOALS), mCursor.getInt(ScoresProjections.COL_AWAY_GOALS)));
        holder.match_id = mCursor.getDouble(ScoresProjections.COL_MATCH_ID);

        // TODO: Use Picasso to display crest (maybe pre-fetch all the team crests?)
        holder.mHomeCrestImageView.setImageResource(Utilities.getTeamCrestByTeamName(
                mCursor.getString(ScoresProjections.COL_HOME_NAME)));
        holder.mAwayCrestImageView.setImageResource(Utilities.getTeamCrestByTeamName(
                mCursor.getString(ScoresProjections.COL_AWAY_NAME)
        ));

        // This is for the detail fragment layouts
        LayoutInflater vi = (LayoutInflater) mContext.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View detailFragmentView = vi.inflate(R.layout.detail_fragment, null);
        if (holder.match_id == detail_match_id) {
            holder.mDetailFragmentContainer.addView(detailFragmentView, 0,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            TextView matchDayTextView = (TextView) detailFragmentView.findViewById(R.id.matchday_textview);
            matchDayTextView.setText(Utilities.getMatchDay(mContext, mCursor.getInt(ScoresProjections.COL_MATCH_DAY),
                    mCursor.getInt(ScoresProjections.COL_LEAGUE_NAME)));

            TextView leagueTextView = (TextView) detailFragmentView.findViewById(R.id.league_textview);
            leagueTextView.setText(Utilities.getLeague(mContext, mCursor.getInt(ScoresProjections.COL_LEAGUE_NAME)));

            // For the share button
            Button share_button = (Button) detailFragmentView.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    mContext.startActivity(
                            createShareForecastIntent(
                                    // TODO: Make a string resource for this
                                    holder.mHomeNameTextView.getText() + " " +
                                            holder.mScoreTextView.getText() + " " +
                                            holder.mAwayNameTextView.getText() + " "
                            )
                    );
                }
            });
        }
        else holder.mDetailFragmentContainer.removeAllViews();
    }

//    @Override
//    public void bindView(View view, final Context context, Cursor cursor)
//    {
//        // Get the view holder and bind text to each of its elements
//        final ViewHolder mHolder = (ViewHolder) view.getTag();
//
//        mHolder.mHomeNameTextView.setText(cursor.getString(COL_HOME_NAME));
//        mHolder.mAwayNameTextView.setText(cursor.getString(COL_AWAY_NAME));
//        mHolder.mDateTextView.setText(cursor.getString(COL_TIME));
//        mHolder.mScoreTextView.setText(Utilities.getScores(context, cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
//        mHolder.match_id = cursor.getDouble(COL_MATCH_ID);
//
//        mHolder.mHomeCrestImageView.setImageResource(Utilities.getTeamCrestByTeamName(
//                cursor.getString(COL_HOME_NAME)));
//        mHolder.mAwayCrestImageView.setImageResource(Utilities.getTeamCrestByTeamName(
//                cursor.getString(COL_AWAY_NAME)
//        ));
//
//        //Log.v(FetchScoreTask.LOG_TAG,mHolder.mHomeNameTextView.getText() + " Vs. " + mHolder.mAwayNameTextView.getText() +" id " + String.valueOf(mHolder.match_id));
//        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
//
//        // This is for the detail fragment layouts
//        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = vi.inflate(R.layout.detail_fragment, null);
//        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
//        if(mHolder.match_id == detail_match_id)
//        {
//            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");
//
//            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                    , ViewGroup.LayoutParams.MATCH_PARENT));
//            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
//            match_day.setText(Utilities.getMatchDay(context, cursor.getInt(COL_MATCH_DAY),
//                    cursor.getInt(COL_LEAGUE_NAME)));
//            TextView league = (TextView) v.findViewById(R.id.league_textview);
//            league.setText(Utilities.getLeague(context, cursor.getInt(COL_LEAGUE_NAME)));
//
//            // For the share button
//            Button share_button = (Button) v.findViewById(R.id.share_button);
//            share_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v)
//                {
//                    //add Share Action
//                    context.startActivity(createShareForecastIntent(mHolder.mHomeNameTextView.getText()+" "
//                    +mHolder.mScoreTextView.getText()+" "+mHolder.mAwayNameTextView.getText() + " "));
//                }
//            });
//        }
//        else
//        {
//            container.removeAllViews();
//        }
//
//    }

    @Override public int getItemCount() {
        if (mCursor != null) {
            Log.d(LOG_TAG, "THE COUNT OF VIEWS IN CURSOR: " + mCursor.getCount());
            return mCursor.getCount();
        } else return 0;
    }


    /*
        These two methods pretty much fulfill the requirements for a cursor adapter, allowing this
         adapter also function as a cursor adapter with the getting and the swapping of cursors
         that cursor adapters are able to do.
     */

    public Cursor getCursor() {
        return mCursor;
    }

    public Cursor swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return mCursor;
    }

    // For the share button
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
