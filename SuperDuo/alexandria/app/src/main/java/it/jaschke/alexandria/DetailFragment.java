package it.jaschke.alexandria;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsCursor;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.provider.books.BooksSelection;

/**
 * Created by TROD on 20160106.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String BOOK_ID = "bookId";
    private String mBookId;
    public static final String VOLUME_OBJECT = "volumeObject";
    private Volume mVolume;

    private static final int DETAIL_LOADER_ID = 0;

    @Bind(R.id.details_scrollview_root) ScrollView mScrollView;
    @Bind(R.id.details_textview_title) TextView mTitleTextView;
    @Bind(R.id.details_textview_subtitle) TextView mSubtitleTextView;
    @Bind(R.id.details_textview_author_date) TextView mAuthorDateTextView;
    @Bind(R.id.details_textview_description) TextView mDescriptionTextView;
    @Bind(R.id.details_imageview_book_thumbnail) ImageView mThumbnailImageView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Extract book information from bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(DetailFragment.BOOK_ID))
                mBookId = arguments.getString(DetailFragment.BOOK_ID);
            if (arguments.containsKey(DetailFragment.VOLUME_OBJECT))
                mVolume = arguments.getParcelable(DetailFragment.VOLUME_OBJECT);
        }

        // If a volume object is found, set the UI
        if (mVolume != null) {
            VolumeInfo volumeInfo = mVolume.getVolumeInfo();
            // Titles
            mTitleTextView.setText(volumeInfo.getTitle());
            if (volumeInfo.getSubtitle() != null)
                mSubtitleTextView.setText(volumeInfo.getSubtitle());
            else
                mSubtitleTextView.setVisibility(View.GONE);
            // Authors and published date (only include year)
            String authors = "";
            if (volumeInfo.getAuthors() != null && volumeInfo.getAuthors().size() > 0) {
                for (int i = 0; i < volumeInfo.getAuthors().size(); i++) {
                    authors += volumeInfo.getAuthors().get(i) + ", ";
                }
            }
            String year = "";
            if (volumeInfo.getPublishedDate() != null)
                year = volumeInfo.getPublishedDate().substring(0, 4);
            if ((authors + year).length() > 0)
                mAuthorDateTextView.setText(authors + year);
            else
                mAuthorDateTextView.setVisibility(View.GONE);
            // Description
            String description = "";
            if (volumeInfo.getDescription() != null)
                description = volumeInfo.getDescription();
            if (description.length() > 0)
                mDescriptionTextView.setText(Html.fromHtml(description));
            else
                mDescriptionTextView.setVisibility(View.GONE);
            // Cover thumbnail
            String imageLink = "path";
            if (volumeInfo.getImageLinks() != null && volumeInfo.getImageLinks().getSmallThumbnail() != null)
                imageLink = volumeInfo.getImageLinks().getSmallThumbnail();
            Picasso.with(getContext())
                    .load(imageLink)
                    .error(R.drawable.ic_launcher)
                    .into(mThumbnailImageView);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mBookId != null) {
            return new CursorLoader(getActivity(),
                    (new BooksSelection()).uri(),
                    null,
                    BooksColumns.BOOKID + " == ? ",
                    new String[]{mBookId},
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This is only called when a loader is returned. Set the UI using the cursor if available.
        if (data != null && data.moveToFirst()) {
            BooksCursor cursor = new BooksCursor(data);

            // Titles
            mTitleTextView.setText(cursor.getTitle());
            if (cursor.getSubtitle() != null)
                mSubtitleTextView.setText(cursor.getSubtitle());
            else
                mSubtitleTextView.setVisibility(View.GONE);
            // Authors and published date (only include year)
            String authors = "";
            ContentResolver cr = getContext().getContentResolver();
            AuthorsCursor c = new AuthorsCursor(cr.query((
                            new AuthorsSelection()).uri(), new String[]{AuthorsColumns.NAME, AuthorsColumns.AUTHORVOLUMEID},
                    AuthorsColumns.AUTHORVOLUMEID + " == ? ", new String[]{mBookId},
                    null));
            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    authors += c.getName() + ", ";
                }
            }
            c.close();
            String year = "";
            if (cursor.getPublisheddate() != null)
                year = cursor.getPublisheddate().substring(0, 4);
            if ((authors + year).length() > 0)
                mAuthorDateTextView.setText(authors + year);
            else
                mAuthorDateTextView.setVisibility(View.GONE);
            // Description
            String description = "";
            if (cursor.getDescription() != null)
                description = cursor.getDescription();
            Log.d(LOG_TAG, "The description is" + description);
            if (description.length() > 0)
                mDescriptionTextView.setText(Html.fromHtml(description));
            else
                mDescriptionTextView.setVisibility(View.GONE);
            // Cover thumbnail
            String imageLink = "path";
            if (cursor.getSmallthumbnailurl() != null)
                imageLink = cursor.getSmallthumbnailurl();
            Picasso.with(getContext())
                    .load(imageLink)
                    .error(R.drawable.ic_launcher)
                    .into(mThumbnailImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Since there is no adapter, there is no need for this
    }
}
