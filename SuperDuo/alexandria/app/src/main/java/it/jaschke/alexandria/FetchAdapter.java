package it.jaschke.alexandria;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;

/**
 * Created by TROD on 20160104.
 */
public class FetchAdapter extends RecyclerView.Adapter<FetchAdapter.ViewHolder> {
    private static final String LOG_TAG = FetchAdapter.class.getSimpleName();

    private Context mContext;
    private List<Volume> mVolumesList;
    private int mPosition;
    final private FetchAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views go here
        public ImageView mThumbnail;
        public TextView mTitleTextView;
        public TextView mDateAuthorTextView;
        public TextView mDescriptionTextView;

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mPosition, this);
        }

        public ViewHolder(View view) {
            super(view);
            mThumbnail = (ImageView) view.findViewById(R.id.results_list_imageview_book_thumbnail);
            mTitleTextView = (TextView) view.findViewById(R.id.results_list_textview_title);
            mDateAuthorTextView = (TextView) view.findViewById(R.id.results_list_textview_author_date);
            mDescriptionTextView = (TextView) view.findViewById(R.id.results_list_textview_description);

            view.setOnClickListener(this);
        }
    }

    public interface FetchAdapterOnClickHandler {
        void onClick(int position, FetchAdapter.ViewHolder holder);
    }

    public FetchAdapter(Context context, FetchAdapterOnClickHandler handler, View empty) {
        mContext = context;
        mClickHandler = handler;
        mEmptyView = empty;
    }

    @Override
    public FetchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.results_list_item, parent, false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return vh;
        } else {
            throw new RuntimeException("The ViewGroup is not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mPosition = position;

        // Get information from results list and set view content. Hide views if null.
        Volume volume = mVolumesList.get(position);
        if (volume != null) {
            VolumeInfo volumeInfo = mVolumesList.get(position).getVolumeInfo();
            if (volumeInfo != null) {
                // Title of book
                String title = "";
                if (volumeInfo.getTitle() != null) {
                    title = volumeInfo.getTitle();
                    holder.mTitleTextView.setText(title);
                    holder.mTitleTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.mTitleTextView.setVisibility(View.GONE);
                }
                // Authors and published date (only include year)
                String authors = "";
                if (volumeInfo.getAuthors() != null && volumeInfo.getAuthors().size() > 0) {
                    for (int i = 0; i < volumeInfo.getAuthors().size(); i++) {
                        authors += volumeInfo.getAuthors().get(i) + ", ";
                    }
                }
                String year = "";
                if (volumeInfo.getPublishedDate() != null) {
                    year = volumeInfo.getPublishedDate().substring(0, 4);
                }
                if ((authors + year).length() > 0) {
                    holder.mDateAuthorTextView.setText(authors + year);
                    holder.mDateAuthorTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.mDateAuthorTextView.setVisibility(View.GONE);
                }
                // Description
                String description = "";
                if (volume.getSearchInfo() != null) {
                    description = volume.getSearchInfo().getTextSnippet();
                } else if (volumeInfo.getDescription() != null){
                    description = volumeInfo.getDescription();
                }
                if (description.length() > 0) {
                    holder.mDescriptionTextView.setText(description);
                    holder.mDescriptionTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.mDescriptionTextView.setVisibility(View.GONE);
                }
                // Cover thumbnail
                String imageLink = "";
                if (volumeInfo.getImageLinks() != null && volumeInfo.getImageLinks().getSmallThumbnail() != null) {
                    imageLink = volumeInfo.getImageLinks().getSmallThumbnail();
                }
                Glide.with(mContext)
                        .load(imageLink)
                        .error(R.drawable.ic_launcher)
                        .into(holder.mThumbnail);
            } else {
                Log.d(LOG_TAG, "The volumeInfo object is null.");
            }
        } else {
            Log.d(LOG_TAG, "The volume object is null.");
        }
    }

    @Override
    public int getItemCount() {
        return mVolumesList != null ? mVolumesList.size() : 0;
    }

    public List<Volume> getList() {
        return mVolumesList;
    }

    public List<Volume> swapList(List<Volume> volumeList) {
        if (volumeList != null) {
            Log.d(LOG_TAG, "In swapList. The size of the volumeList is " + volumeList.size());
        } else {
            Log.d(LOG_TAG, "In swapList. The volumeList is null.");
        }
        mVolumesList = volumeList;
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return mVolumesList;
    }
}
