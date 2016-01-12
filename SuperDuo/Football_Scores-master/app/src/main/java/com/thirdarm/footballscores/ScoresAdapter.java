package com.thirdarm.footballscores;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thirdarm.footballscores.provider.fixture.FixtureCursor;
import com.thirdarm.footballscores.provider.fixture.Status;
import com.thirdarm.footballscores.utilities.ItemChoiceManager;
import com.thirdarm.footballscores.utilities.Utilities;

/**
 * TODO: Convert this into an Adapter for RecyclerView
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {

    private static final String LOG_TAG = ScoresAdapter.class.getSimpleName();

    public double detail_match_id = 0;

    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    // The dataset
    private FixtureCursor mCursor;
    private int mSelectedPosition;
    final private Context mContext;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View mParentView;
        public TextView mHomeNameTextView;
        public TextView mAwayNameTextView;
        public TextView mHomeScoreTextView;
        public TextView mAwayScoreTextView;
        public TextView mTimeTextView;
        public TextView mStatusTextView;
        public ImageView mHomeCrestImageView;
        public ImageView mAwayCrestImageView;
        public TextView mLeagueNameTextView;
        public TextView mMatchDayTextView;
        public ImageButton mShareButton;

        public int match_id;

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mCursor.moveToPosition(position);
            mSelectedPosition = position;
            mICM.onClick(this);
        }

        public ViewHolder(View view) {
            super(view);
            mParentView = view;
            view.setClickable(true);
            mHomeNameTextView = (TextView) view.findViewById(R.id.scores_list_item_textview_home_name);
            mAwayNameTextView = (TextView) view.findViewById(R.id.scores_list_item_textview_away_name);
            mHomeScoreTextView = (TextView) view.findViewById(R.id.scores_list_item_textview_home_score);
            mAwayScoreTextView = (TextView) view.findViewById(R.id.scores_list_item_textview_away_score);
            mTimeTextView = (TextView) view.findViewById(R.id.scores_list_item_textview_time);
            mStatusTextView = (TextView) view.findViewById(R.id.scores_list_item_textview_status);
            mHomeCrestImageView = (ImageView) view.findViewById(R.id.scores_list_item_imageview_home_crest);
            mAwayCrestImageView = (ImageView) view.findViewById(R.id.scores_list_item_imageview_away_crest);
            mLeagueNameTextView = (TextView) view.findViewById(R.id.detail_textview_league);
            mMatchDayTextView = (TextView) view.findViewById(R.id.detail_textview_matchday);
            mShareButton = (ImageButton) view.findViewById(R.id.detail_button_share);

            view.setOnClickListener(this);
        }
    }

    public ScoresAdapter(Context c, View empty) {
        mContext = c;
        mEmptyView = empty;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }


    @Override
    public ScoresAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
//        ViewHolder mParentView = new ViewHolder(mItem);
//        mItem.setTag(mParentView);
//        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
//        return mItem;
//    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        mICM.onBindViewHolder(holder, position);

        // Set the team names. Use code names. If no code name, use short name.
        //  If there is no short name, use the default name.
        String homeTeamName = mCursor.getAteamShortname();
        String awayTeamName = mCursor.getBteamShortname();
        String homeTeamCode = mCursor.getAteamCode();
        String awayTeamCode = mCursor.getBteamCode();
        if (homeTeamName == null) {
            homeTeamName = mCursor.getAteamName();
            if (homeTeamCode == null) {
                homeTeamCode = homeTeamName;
            }
        } else {
            if (homeTeamCode == null) {
                homeTeamCode = homeTeamName;
            }
        }
        if (awayTeamName == null) {
            awayTeamName = mCursor.getBteamName();
            if (awayTeamCode == null) {
                awayTeamCode = awayTeamName;
            }
        } else {
            if (awayTeamCode == null) {
                awayTeamCode = awayTeamName;
            }
        }
        holder.mHomeNameTextView.setText(homeTeamCode);
        holder.mAwayNameTextView.setText(awayTeamCode);

        // Set the match time
        String matchTime = mContext.getString(R.string.date_time, mCursor.getDate(), mCursor.getTime());
        holder.mTimeTextView.setText(matchTime);

        //
        String leagueName = Utilities.getLeague(mContext, mCursor.getLeagueid());

        // Set the scores
        if (mCursor.getHomegoals() != -1 && mCursor.getAwaygoals() != -1) {
            // Cursors that have scores should be visible score views
            holder.mHomeScoreTextView.setVisibility(View.VISIBLE);
            holder.mAwayScoreTextView.setVisibility(View.VISIBLE);
            // Only set goals if not null
            // NOTE: WHEN SETTING TEXT TO TEXTVIEWS, TEXT MUST BE A STRING. NOT AN INT.
            int homeGoals = mCursor.getHomegoals();
            int awayGoals = mCursor.getAwaygoals();
            holder.mHomeScoreTextView.setText("" + homeGoals);
            holder.mAwayScoreTextView.setText("" + awayGoals);
            // Set the scores colors
            int homeColor, awayColor, winColor, loseColor;
            String matchStatus, winStatus, notedTeam;
            if (mCursor.getStatus() == Status.FINISHED) {
                matchStatus = mContext.getString(R.string.status_finished);
                holder.mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                winColor = mContext.getResources().getColor(R.color.primary_text);
                loseColor = mContext.getResources().getColor(R.color.tertiary_text);
            } else {
                matchStatus = mContext.getString(R.string.status_timed);
                holder.mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.secondary_text));
                winColor = mContext.getResources().getColor(R.color.primary_text);
                loseColor = mContext.getResources().getColor(R.color.secondary_text);
            }
            if (homeGoals > awayGoals) {
                homeColor = winColor;
                awayColor = loseColor;
                winStatus = mContext.getString(R.string.scores_winner);
                notedTeam = homeTeamName;
            } else if (homeGoals < awayGoals) {
                homeColor = loseColor;
                awayColor = winColor;
                winStatus = mContext.getString(R.string.scores_winner);
                notedTeam = awayTeamName;
            } else {
                homeColor = loseColor;
                awayColor = loseColor;
                winStatus = mContext.getString(R.string.scores_tied);
                notedTeam = "";
            }
            holder.mStatusTextView.setText(matchStatus);
            holder.mHomeNameTextView.setTextColor(homeColor);
            holder.mHomeScoreTextView.setTextColor(homeColor);
            holder.mAwayNameTextView.setTextColor(awayColor);
            holder.mAwayScoreTextView.setTextColor(awayColor);

            // Set content description
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                holder.mParentView.setContentDescription(mContext.getString(R.string.a11y_full_match_description,
                                matchStatus, leagueName, mCursor.getMatchday(), matchTime,
                                awayTeamName, homeTeamName, awayGoals, homeGoals,
                                winStatus, notedTeam)
                );
            }
        } else {
            // Cursors that don't have scores should be hidden score views
            holder.mStatusTextView.setText(mContext.getString(R.string.status_upcoming));
            holder.mStatusTextView.setTextColor(mContext.getResources().getColor(R.color.tertiary_text));
            int textColor = mContext.getResources().getColor(R.color.primary_text);
            holder.mHomeNameTextView.setTextColor(textColor);
            holder.mAwayNameTextView.setTextColor(textColor);
            holder.mHomeScoreTextView.setVisibility(View.GONE);
            holder.mAwayScoreTextView.setVisibility(View.GONE);

            // Set content description
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                holder.mParentView.setContentDescription(mContext.getString(R.string.a11y_upcoming_match_description,
                                leagueName, mCursor.getMatchday(), matchTime,
                                awayTeamName, homeTeamName)
                );
            }
        }
        holder.match_id = mCursor.getMatchid();

        // Set the crests
        String homeCrestUrl = Utilities.convertCrestUrl(mCursor.getAteamCresturl());
        Glide.with(mContext)
                .load(homeCrestUrl)
                .error(R.drawable.ic_launcher)
                .into(holder.mHomeCrestImageView);

        String awayCrestUrl = Utilities.convertCrestUrl(mCursor.getBteamCresturl());
        Glide.with(mContext)
                .load(awayCrestUrl)
                .error(R.drawable.ic_launcher)
                .into(holder.mAwayCrestImageView);

        // This is for the detail fragment layouts
        holder.mMatchDayTextView.setText(Utilities.getMatchDay(mContext, mCursor.getMatchday(),
                mCursor.getLeagueid()));

        holder.mLeagueNameTextView.setText(Utilities.getLeague(mContext, mCursor.getLeagueid()));

        // For the share button
        holder.mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(
                        createShareForecastIntent(
                                // TODO: Make a string resource for this
                                holder.mHomeNameTextView.getText() + " " +
                                        holder.mHomeScoreTextView.getText() + " - " +
                                        holder.mAwayScoreTextView.getText() + " " +
                                        holder.mAwayNameTextView.getText() + " "
                        )
                );
            }
        });
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

//    @Override
//    public void bindView(View view, final Context context, Cursor cursor)
//    {
//        // Get the view holder and bind text to each of its elements
//        final ViewHolder mParentView = (ViewHolder) view.getTag();
//
//        mParentView.mHomeNameTextView.setText(cursor.getString(COL_HOME_NAME));
//        mParentView.mAwayNameTextView.setText(cursor.getString(COL_AWAY_NAME));
//        mParentView.mDateTextView.setText(cursor.getString(COL_TIME));
//        mParentView.mScoreTextView.setText(Utilities.getScores(context, cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
//        mParentView.match_id = cursor.getDouble(COL_MATCH_ID);
//
//        mParentView.mHomeCrestImageView.setImageResource(Utilities.getTeamCrestByTeamName(
//                cursor.getString(COL_HOME_NAME)));
//        mParentView.mAwayCrestImageView.setImageResource(Utilities.getTeamCrestByTeamName(
//                cursor.getString(COL_AWAY_NAME)
//        ));
//
//        //Log.v(FetchScoreTask.LOG_TAG,mParentView.mHomeNameTextView.getText() + " Vs. " + mParentView.mAwayNameTextView.getText() +" id " + String.valueOf(mParentView.match_id));
//        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
//
//        // This is for the detail fragment layouts
//        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = vi.inflate(R.layout.detail_fragment, null);
//        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
//        if(mParentView.match_id == detail_match_id)
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
//                    context.startActivity(createShareForecastIntent(mParentView.mHomeNameTextView.getText()+" "
//                    +mParentView.mScoreTextView.getText()+" "+mParentView.mAwayNameTextView.getText() + " "));
//                }
//            });
//        }
//        else
//        {
//            container.removeAllViews();
//        }
//
//    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
//            Log.d(LOG_TAG, "There are " + mCursor.getCount() + " views.");
            return mCursor.getCount();
        } else return 0;
    }


    /*
        These two methods pretty much fulfill the requirements for a cursor adapter, allowing this
         adapter also function as a cursor adapter with the getting and the swapping of cursors
         that cursor adapters are able to do.
     */

    public FixtureCursor getCursor() {
        return mCursor;
    }

    public FixtureCursor swapCursor(FixtureCursor scoresCursor) {
        mCursor = scoresCursor;
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
