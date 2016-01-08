package it.jaschke.alexandria;

import android.content.ContentResolver;
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

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsCursor;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.utilities.Library;
import it.jaschke.alexandria.utilities.Network;
import it.jaschke.alexandria.utilities.UIHelper;

/**
 * Created by TROD on 20160104.
 */
public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private static final String LOG_TAG = ViewAdapter.class.getSimpleName();

    private Context mContext;
    private BooksCursor mBooksCursor;
    final private ViewAdapterOnClickHandler mClickHandler;
    final private TextView mEmptyView;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views go here
        public ImageView mThumbnail;
        public TextView mTitleTextView;
        public TextView mDateAuthorTextView;
        public TextView mDescriptionTextView;
        public ImageButton mMenuButton;

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mBooksCursor.moveToPosition(position);
            mClickHandler.onClick(mBooksCursor.getBookid(), this);
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

    public interface ViewAdapterOnClickHandler {
        void onClick(String bookId, ViewAdapter.ViewHolder holder);
    }

    public ViewAdapter(Context context, ViewAdapterOnClickHandler handler, View empty) {
        mContext = context;
        mClickHandler = handler;
        mEmptyView = (TextView) empty;
    }

    @Override
    public ViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        mBooksCursor.moveToPosition(position);
        final String bookId = mBooksCursor.getBookid();

        ContentResolver cr = mContext.getContentResolver();
        AuthorsCursor authorsCursor = new AuthorsCursor(cr.query((new AuthorsSelection()).uri(),
                new String[]{AuthorsColumns.NAME, AuthorsColumns.AUTHORVOLUMEID},
                AuthorsColumns.AUTHORVOLUMEID + " == ? ", new String[]{bookId},
                null));

        // Get information from results list
        final String title, authors, infoLink;
        String subtitle, year, description, imageLink;

        title = UIHelper.getTitle(mContext, null, mBooksCursor);
        authors = UIHelper.getAuthors(mContext, null, authorsCursor);
        year = UIHelper.getDatePublished(null, mBooksCursor);
        description = UIHelper.getShortDescription(null, mBooksCursor);
        if (description.length() == 0) {
            description = UIHelper.getDescription(null, mBooksCursor);
        }
        imageLink = UIHelper.getThumbnailUrl(null, mBooksCursor);
        infoLink = UIHelper.getInfoLink(null, mBooksCursor);

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
                menu.getMenuInflater().inflate(R.menu.menu_view_list_item, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_view_details :
                                ((ViewBooksFragment.BookSelectionCallback) mContext)
                                        .onBookItemSelected(bookId, holder);
                                break;
                            case R.id.action_remove :
                                Library.removeFromLibrary(mContext, bookId, title);
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
        return mBooksCursor != null ? mBooksCursor.getCount() : 0;
    }

    public BooksCursor getCursor() {
        return mBooksCursor;
    }

    public BooksCursor swapCursor(BooksCursor booksCursor) {
        mBooksCursor = booksCursor;
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        return mBooksCursor;
    }
}
