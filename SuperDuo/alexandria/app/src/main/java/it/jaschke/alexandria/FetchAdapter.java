package it.jaschke.alexandria;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.utilities.Library;
import it.jaschke.alexandria.utilities.Network;

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
        // Get information from results list and set view content. Hide views if null.
        final Volume volume = mVolumesList.get(position);
        VolumeInfo volumeInfo = mVolumesList.get(position).getVolumeInfo();
        // Title of book
        final String title;
        if (volumeInfo.getTitle() != null)
            title = volumeInfo.getTitle();
        else
            title = mContext.getString(R.string.library_book_no_name);
        holder.mTitleTextView.setText(title);
        // Authors and published date (only include year)
        String authors = "";
        if (volumeInfo.getAuthors() != null && volumeInfo.getAuthors().size() > 0) {
            int size = volumeInfo.getAuthors().size();
            for (int i = 0; i < size; i++) {
                if (size == 1)
                    authors = volumeInfo.getAuthors().get(i);
                else if (i < size - 1)
                    // Only add comma if there is another author coming up next
                    authors += volumeInfo.getAuthors().get(i) + ", ";
                else
                    // Append "and" if last author and there are multiple authors
                    authors += mContext.getString(R.string.list_and) + volumeInfo.getAuthors().get(i);
            }
        }
        final String authorsFinal;
        if (authors.length() > 0)
            authorsFinal = authors;
        else
            authorsFinal = mContext.getString(R.string.library_book_no_author);
        String year = "";
        if (volumeInfo.getPublishedDate() != null)
            year = volumeInfo.getPublishedDate().substring(0, 4);
        if ((authors + year).length() > 0) {
            holder.mDateAuthorTextView.setText(authors + ", " + year);
            holder.mDateAuthorTextView.setVisibility(View.VISIBLE);
        } else
            holder.mDateAuthorTextView.setVisibility(View.GONE);
        // Description
        String description = "";
        if (volume.getSearchInfo() != null)
            description = volume.getSearchInfo().getTextSnippet();
        else if (volumeInfo.getDescription() != null)
            description = volumeInfo.getDescription();
        if (description.length() > 0) {
            holder.mDescriptionTextView.setText(Html.fromHtml(description));
            holder.mDescriptionTextView.setVisibility(View.VISIBLE);
        } else
            holder.mDescriptionTextView.setVisibility(View.GONE);
        // Cover thumbnail
        String imageLink = "path";
        if (volumeInfo.getImageLinks() != null && volumeInfo.getImageLinks().getSmallThumbnail() != null)
            imageLink = volumeInfo.getImageLinks().getSmallThumbnail();
        Picasso.with(mContext)
                .load(imageLink)
                .error(R.drawable.ic_launcher)
                .into(holder.mThumbnail);
        // Action menu button
        // TODO: Add a preview button and embedded book preview feature in app
        final String infoLink;
        // Even though most likely there will be a link to Google's Book page for this book
        if (volumeInfo.getInfoLink() != null)
            infoLink = volumeInfo.getInfoLink();
        else
            infoLink = "";
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
                                Network.shareText(mContext, mContext.getString(R.string.share_book, title, authorsFinal, infoLink));
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
