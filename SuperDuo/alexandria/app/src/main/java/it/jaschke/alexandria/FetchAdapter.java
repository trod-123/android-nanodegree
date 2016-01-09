package it.jaschke.alexandria;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.utilities.Library;
import it.jaschke.alexandria.utilities.Network;
import it.jaschke.alexandria.utilities.LibraryHelper;

/**
 * Created by TROD on 20160104.
 */
public class FetchAdapter extends RecyclerView.Adapter<FetchAdapter.ViewHolder> {
    private static final String LOG_TAG = FetchAdapter.class.getSimpleName();

    private Context mContext;
    private List<Volume> mVolumesList;
    final private FetchAdapterOnClickHandler mClickHandler;
    final private TextView mEmptyView;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views go here
        public ImageView mThumbnail;
        public TextView mTitleTextView;
        public TextView mDateAuthorTextView;
        public TextView mDescriptionTextView;
        public ImageButton mMenuButton;

        // (1) This is the first click method that is called when user presses on a view.
        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mVolumesList.get(getAdapterPosition()), this);
        }

        public ViewHolder(View view) {
            super(view);
            mThumbnail = (ImageView) view.findViewById(R.id.books_list_imageview_book_thumbnail);
            mTitleTextView = (TextView) view.findViewById(R.id.books_list_textview_title);
            mDateAuthorTextView = (TextView) view.findViewById(R.id.books_list_textview_author_date);
            mDescriptionTextView = (TextView) view.findViewById(R.id.books_list_textview_description);
            mMenuButton = (ImageButton) view.findViewById(R.id.books_list_action_button);

            view.setOnClickListener(this);
        }
    }

    // (2) This is the second click method that is called when user presses on a view.
    public interface FetchAdapterOnClickHandler {
        void onClick(Volume volume, FetchAdapter.ViewHolder holder);
    }

    public FetchAdapter(Context context, FetchAdapterOnClickHandler handler, View empty) {
        mContext = context;
        mClickHandler = handler;
        mEmptyView = (TextView) empty;
    }

    @Override
    public FetchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.books_list_item, parent, false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return vh;
        } else
            throw new RuntimeException("The ViewGroup is not bound to RecyclerView");
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Get information from results list
        final String title, authors, infoLink;
        String subtitle, year, description, imageLink;

        final Volume volume = mVolumesList.get(position);
        VolumeInfo volumeInfo = mVolumesList.get(position).getVolumeInfo();

        title = LibraryHelper.getTitle(mContext, true, volumeInfo, null);
        authors = LibraryHelper.getAuthors(mContext, true, volumeInfo, null);
        year = LibraryHelper.getDatePublished(true, volumeInfo, null);
        description = LibraryHelper.getShortDescription(true, volume, null);
        if (description.length() == 0) {
            description = LibraryHelper.getDescription(true, volumeInfo, null);
        }
        imageLink = LibraryHelper.getThumbnailUrl(true, volumeInfo, null);
        infoLink = LibraryHelper.getInfoLink(volumeInfo, null);

        // Set view content. Hide views if null.
        holder.mTitleTextView.setText(title);

        if ((authors + year).length() > 0) {
            holder.mDateAuthorTextView.setText(authors + ", " + year);
            holder.mDateAuthorTextView.setVisibility(View.VISIBLE);
        } else
            holder.mDateAuthorTextView.setVisibility(View.GONE);

        if (description.length() > 0) {
            holder.mDescriptionTextView.setText(Html.fromHtml(description));
            holder.mDescriptionTextView.setVisibility(View.VISIBLE);
        } else
            holder.mDescriptionTextView.setVisibility(View.GONE);

        Picasso.with(mContext)
                .load(imageLink)
                .error(R.drawable.ic_launcher)
                .into(holder.mThumbnail);

        // TODO: Add a preview button and embedded book preview feature in app
        holder.mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mContext, holder.mMenuButton);
                menu.getMenuInflater().inflate(R.menu.menu_results_list_item, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_view_details :
                                ((FetchBooksFragment.ResultSelectionCallback) mContext)
                                        .onResultItemSelected(volume, holder);
                                break;
                            case R.id.action_add :
                                Library.addToLibrary(mContext, volume, title);
                                break;
                            case R.id.action_view_browser :
                                Network.openInBrowser(mContext, infoLink);
                                break;
                            case R.id.action_share :
                                Network.shareText(mContext, mContext.getString(R.string.share_book, title, authors, infoLink));
                                break;
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVolumesList != null ? mVolumesList.size() : 0;
    }

    public List<Volume> getList() {
        return mVolumesList;
    }

    public List<Volume> swapList(List<Volume> volumeList) {
        mVolumesList = volumeList;
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return mVolumesList;
    }
}
