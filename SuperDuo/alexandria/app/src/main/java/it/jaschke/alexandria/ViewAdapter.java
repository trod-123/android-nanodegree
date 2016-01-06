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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsCursor;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.utilities.Library;

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
        } else {
            throw new RuntimeException("The ViewGroup is not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mBooksCursor.moveToPosition(position);

        // Get information from results list and set view content. Hide views if null.
        final String bookId = mBooksCursor.getBookid();
        // Title of book
        String title = mBooksCursor.getTitle();
        holder.mTitleTextView.setText(title);
        // Authors and published date (only include year)
        String authors = "";
        ContentResolver cr = mContext.getContentResolver();
        AuthorsCursor c = new AuthorsCursor(cr.query((
                new AuthorsSelection()).uri(), new String[]{AuthorsColumns.NAME, AuthorsColumns.AUTHORVOLUMEID},
                AuthorsColumns.AUTHORVOLUMEID + " == ? ", new String[]{bookId},
                null));
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                authors += c.getName() + ", ";
            }
        }
        c.close();
        String year = "";
        if (mBooksCursor.getPublisheddate() != null) {
            year = mBooksCursor.getPublisheddate().substring(0, 4);
        }
        if ((authors + year).length() > 0) {
            holder.mDateAuthorTextView.setText(authors + year);
            holder.mDateAuthorTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mDateAuthorTextView.setVisibility(View.GONE);
        }
        // Description
        String description = "";
        if (mBooksCursor.getDescriptionsnippet() != null) {
            description = mBooksCursor.getDescriptionsnippet();
        } else if (mBooksCursor.getDescription() != null) {
            description = mBooksCursor.getDescription();
        }
        if (description.length() > 0) {
            holder.mDescriptionTextView.setText(Html.fromHtml(description));
            holder.mDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mDescriptionTextView.setVisibility(View.GONE);
        }
        // Cover thumbnail
        String imageLink = "path";
        if (mBooksCursor.getSmallthumbnailurl() != null) {
            imageLink = mBooksCursor.getSmallthumbnailurl();
        }
        Picasso.with(mContext)
                .load(imageLink)
                .error(R.drawable.ic_launcher)
                .into(holder.mThumbnail);
        // Action menu button
        // TODO: Do this programmatically. Hide the button if there is no link.
        // TODO: Add a preview button and embedded book preview feature in app
        final String infoLink;
        if (mBooksCursor.getInfolink() != null) {
            infoLink = mBooksCursor.getInfolink();
        } else {
            infoLink = "";
        }
        holder.mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                PopupMenu menu = new PopupMenu(mContext, holder.mMenuButton);
                menu.getMenuInflater().inflate(R.menu.menu_view_list_item, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(mContext.getString(R.string.action_view_details))) {
                            Toast.makeText(mContext, "Will view details for item " + bookId, Toast.LENGTH_SHORT).show();
                        } else if (item.getTitle().equals(mContext.getString(R.string.action_remove))) {
                            Library.removeFromLibrary(mContext, bookId);
                        } else if (item.getTitle().equals(mContext.getString(R.string.action_view_browser))) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(infoLink));
                            mContext.startActivity(intent);
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
