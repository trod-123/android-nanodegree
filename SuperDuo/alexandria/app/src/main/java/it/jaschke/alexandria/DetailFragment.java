package it.jaschke.alexandria;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.model.IndustryIdentifier;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsCursor;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.provider.books.BooksSelection;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;
import it.jaschke.alexandria.provider.categories.CategoriesCursor;
import it.jaschke.alexandria.provider.categories.CategoriesSelection;
import it.jaschke.alexandria.utilities.Library;
import it.jaschke.alexandria.utilities.Network;
import it.jaschke.alexandria.utilities.UIHelper;

/**
 * Created by TROD on 20160106.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String BOOK_ID = "bookId";
    private String mBookId, mTitle, mAuthors, mInfoUrl;
    public static final String VOLUME_OBJECT = "volumeObject";
    private Volume mVolume;

    private static final int DETAIL_LOADER_ID = 0;

    @Bind(R.id.details_scrollview_root) ScrollView mScrollView;
    @Bind(R.id.details_button) FloatingActionButton mDetailsFAB;

    // Main card
    @Bind(R.id.details_textview_title) TextView mTitleTextView;
    @Bind(R.id.details_textview_subtitle) TextView mSubtitleTextView;
    @Bind(R.id.details_textview_author_date) TextView mAuthorDateTextView;
    @Bind(R.id.details_textview_description) TextView mDescriptionTextView;
    @Bind(R.id.details_imageview_book_thumbnail) ImageView mThumbnailImageView;
    @Bind(R.id.details_rating_container) LinearLayout mRatingsContainer;
    @Bind(R.id.details_ratingbar) RatingBar mRatingBar;
    @Bind(R.id.details_rating) TextView mRatingsTextView;
    @Bind(R.id.details_divider_description) View mDetailsDividerDescription;

    // Bibliographic card labels
    @Bind(R.id.details_textview_label_bib_title) TextView mLabelBibTitleTextView;
    @Bind(R.id.details_textview_label_bib_authors) TextView mLabelBibAuthorsTextView;
    @Bind(R.id.details_textview_label_bib_publisher) TextView mLabelBibPublisherTextView;
    @Bind(R.id.details_textview_label_bib_date) TextView mLabelBibDateTextView;
    @Bind(R.id.details_textview_label_bib_isbn) TextView mLabelBibIsbnTextView;
    @Bind(R.id.details_textview_label_bib_length) TextView mLabelBibLengthTextView;
    @Bind(R.id.details_textview_label_bib_categories) TextView mLabelBibCategoriesTextView;
    @Bind(R.id.details_textview_label_bib_language) TextView mLabelBibLanguageTextView;
    // Bibliographic card content
    @Bind(R.id.details_textview_bib_title) TextView mBibTitleTextView;
    @Bind(R.id.details_textview_bib_authors) TextView mBibAuthorsTextView;
    @Bind(R.id.details_textview_bib_publisher) TextView mBibPublisherTextView;
    @Bind(R.id.details_textview_bib_date) TextView mBibDateTextView;
    @Bind(R.id.details_textview_bib_isbn) TextView mBibIsbnTextView;
    @Bind(R.id.details_textview_bib_length) TextView mBibLengthTextView;
    @Bind(R.id.details_textview_bib_categories) TextView mBibCategoriesTextView;
    @Bind(R.id.details_textview_bib_language) TextView mBibLanguageTextView;


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
            prepareUi(mVolume.getVolumeInfo(), null);
            mDetailsFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Library.addToLibrary(getContext(), mVolume, mVolume.getVolumeInfo().getTitle());
                    mDetailsFAB.hide();
                }
            });
        } else {
            mDetailsFAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_white_24dp));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share :
                Network.shareText(
                        getContext(), getString(R.string.share_book, mTitle, mAuthors, mInfoUrl));
                break;
            case R.id.action_view_browser :
                Network.openInBrowser(getContext(), mInfoUrl);
                break;
        }
        return true;
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
            prepareUi(null, new BooksCursor(data));
            mDetailsFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Library.removeFromLibrary(getContext(), mBookId, mTitle);
                    mDetailsFAB.hide();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Since there is no adapter, there is no need for this
    }

    /**
     * Prepares the UI. Uses volume object or cursor, whichever is provided.
     * @param volume The volume object, containing book information. To be used from fetch.
     * @param cursor The cursor. To be used from personal library.
     */
    public void prepareUi(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        // Extract data from volume or cursor (only id and title are not nullable)
        String subtitle, publisher, year, description, isbns, imageLink, language, categories;
        int pageCount, ratingsCount;
        double averageRating;

        ContentResolver cr = getContext().getContentResolver();
        AuthorsCursor authorsCursor = null;
        CategoriesCursor categoriesCursor = null;
        if (cursor != null) {
            authorsCursor = new AuthorsCursor(cr.query((new AuthorsSelection()).uri(),
                    new String[]{AuthorsColumns.NAME, AuthorsColumns.AUTHORVOLUMEID},
                    AuthorsColumns.AUTHORVOLUMEID + " == ? ", new String[]{mBookId},
                    null));
            categoriesCursor = new CategoriesCursor(cr.query((new CategoriesSelection()).uri(),
                    new String[]{CategoriesColumns.NAME, CategoriesColumns.CATEGORYVOLUMEID},
                    CategoriesColumns.CATEGORYVOLUMEID + " == ? ", new String[]{mBookId},
                    null));
        }

        mInfoUrl = UIHelper.getInfoLink(volume, cursor);
        mTitle = UIHelper.getTitle(getContext(), volume, cursor);
        subtitle = UIHelper.getSubtitle(volume, cursor);
        mAuthors = UIHelper.getAuthors(getContext(), volume, authorsCursor);
        year = UIHelper.getDatePublished(volume, cursor);
        description = UIHelper.getDescription(volume, cursor);
        imageLink = UIHelper.getThumbnailUrl(volume, cursor);
        publisher = UIHelper.getPublisher(volume, cursor);
        isbns = UIHelper.getISBNs(volume, cursor);
        language = UIHelper.getLanguage(volume, cursor);

        categories = UIHelper.getCategories(getContext(), volume, categoriesCursor);
        pageCount = UIHelper.getPageCount(volume, cursor);
        ratingsCount = UIHelper.getRatingsCount(volume, cursor);
        averageRating = UIHelper.getRatingsAverage(volume, cursor);

        // Set the ui elements. Hide if null.

        getActivity().setTitle(mTitle);

        /*
            Main card
         */

        mTitleTextView.setText(mTitle);
        if (subtitle != null)
            mSubtitleTextView.setText(subtitle);
        else
            mSubtitleTextView.setVisibility(View.GONE);
        if ((mAuthors + year).length() > 0)
            mAuthorDateTextView.setText(mAuthors + ", " + year);
        else
            mAuthorDateTextView.setVisibility(View.GONE);
        if (description.length() > 0)
            mDescriptionTextView.setText(Html.fromHtml(description));
        else {
            mDescriptionTextView.setVisibility(View.GONE);
            mDetailsDividerDescription.setVisibility(View.GONE);
        }
        if (ratingsCount != -1) {
            mRatingBar.setRating((float) averageRating);
            mRatingsTextView.setText(getString(R.string.detail_rating, ratingsCount));
        } else {
            mRatingsContainer.setVisibility(View.GONE);
        }
        Picasso.with(getContext())
                .load(imageLink)
                .error(R.drawable.ic_launcher)
                .into(mThumbnailImageView);

        /*
            Bibliographic card
         */

        String titles = mTitle;
        if (subtitle != null)
            titles += ": " + subtitle;
        mBibTitleTextView.setText(titles);

        if (mAuthors.length() > 0)
            mBibAuthorsTextView.setText(mAuthors);
        else {
            mLabelBibAuthorsTextView.setVisibility(View.GONE);
            mBibAuthorsTextView.setVisibility(View.GONE);
        }

        if (publisher != null)
            mBibPublisherTextView.setText(publisher);
        else {
            mLabelBibPublisherTextView.setVisibility(View.GONE);
            mBibPublisherTextView.setVisibility(View.GONE);
        }

        if (year.length() > 0)
            mBibDateTextView.setText(year);
        else {
            mLabelBibDateTextView.setVisibility(View.GONE);
            mBibDateTextView.setVisibility(View.GONE);
        }

        if (isbns.length() > 0)
            mBibIsbnTextView.setText(isbns);
        else {
            mLabelBibIsbnTextView.setVisibility(View.GONE);
            mBibIsbnTextView.setVisibility(View.GONE);
        }

        if (pageCount != -1)
            mBibLengthTextView.setText(pageCount + " pages");
        else {
            mLabelBibLengthTextView.setVisibility(View.GONE);
            mBibLengthTextView.setVisibility(View.GONE);
        }

        if (categories.length() > 0)
            mBibCategoriesTextView.setText(categories);
        else {
            mLabelBibCategoriesTextView.setVisibility(View.GONE);
            mBibCategoriesTextView.setVisibility(View.GONE);
        }

        if (language != null)
            mBibLanguageTextView.setText(language);
        else {
            mLabelBibLanguageTextView.setVisibility(View.GONE);
            mBibLanguageTextView.setVisibility(View.GONE);
        }
    }
}
