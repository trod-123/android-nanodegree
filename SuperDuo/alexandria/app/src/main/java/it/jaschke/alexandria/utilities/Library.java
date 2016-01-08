package it.jaschke.alexandria.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.IndustryIdentifier;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsContentValues;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.books.BooksContentValues;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.provider.books.BooksSelection;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;
import it.jaschke.alexandria.provider.categories.CategoriesContentValues;
import it.jaschke.alexandria.provider.categories.CategoriesSelection;

/**
 * Created by TROD on 20160106.
 *
 * Class for handling user's local libraries
 */
public class Library {

    private static final String LOG_TAG = Library.class.getSimpleName();

    public static final String ISBN_10 = "ISBN_10";
    public static final String ISBN_13 = "ISBN_13";

    public static long addToLibrary(Context context, Volume volume, String bookTitle) {
        long id = -1;

        // Extract data from volume (only id and title are not nullable)
        String bookId = null;               String title = null;
        String subtitle = null;             String publisher = null;
        String publishedDate = null;        String description = null;
        String isbn_10 = null;              String isbn_13 = null;
        String printType = null;            String maturityRating = null;
        String smallThumbnailUrl = null;    String thumbnailUrl = null;
        String language = null;             String previewUrl = null;
        String infoUrl = null;              String canonicalVolumeUrl = null;
        String descriptionSnippet = null;   String authorsString = "";
        String categoriesString = "";

        List<String> authors = null;        List<String> categories = null;

        int pageCount = -1;                 int ratingsCount = -1;

        double averageRating = -1;

        bookId = volume.getId();
        if (volume.getVolumeInfo() != null) {
            VolumeInfo volumeInfo = volume.getVolumeInfo();
            if (volumeInfo.getTitle() != null)
                title = volumeInfo.getTitle();
            else
                title = context.getString(R.string.library_book_no_name);
            if (volumeInfo.getSubtitle() != null)
                subtitle = volumeInfo.getSubtitle();
            if (volumeInfo.getAuthors() != null && volumeInfo.getAuthors().size() > 0) {
                authors = volumeInfo.getAuthors();
                for (String author : authors) {
                    authorsString += author + " ";
                }
            }
            if (volumeInfo.getPublisher() != null)
                publisher = volumeInfo.getPublisher();
            if (volumeInfo.getPublishedDate() != null)
                publishedDate = volumeInfo.getPublishedDate();
            if (volumeInfo.getDescription() != null)
                description = volumeInfo.getDescription();
            if (volumeInfo.getIndustryIdentifiers() != null) {
                for (IndustryIdentifier indId : volumeInfo.getIndustryIdentifiers()) {
                   if (indId.getType().equals(ISBN_10))
                       isbn_10 = indId.getIdentifier();
                   else if (indId.getType().equals(ISBN_13))
                       isbn_13 = indId.getIdentifier();
                }
            }
            if (volumeInfo.getPageCount() != null)
                pageCount = volumeInfo.getPageCount();
            if (volumeInfo.getPrintType() != null)
                printType = volumeInfo.getPrintType();
            if (volumeInfo.getCategories() != null && volumeInfo.getCategories().size() > 0) {
                categories = volumeInfo.getCategories();
                for (String category : categories) {
                    categoriesString += category + " ";
                }
            }
            if (volumeInfo.getAverageRating() != null)
                averageRating = volumeInfo.getAverageRating();
            if (volumeInfo.getRatingsCount() != null)
                ratingsCount = volumeInfo.getRatingsCount();
            if (volumeInfo.getMaturityRating() != null)
                maturityRating = volumeInfo.getMaturityRating();
            if (volumeInfo.getImageLinks() != null) {
                if (volumeInfo.getImageLinks().getSmallThumbnail() != null)
                    smallThumbnailUrl = volumeInfo.getImageLinks().getSmallThumbnail();
                if (volumeInfo.getImageLinks().getThumbnail() != null)
                    thumbnailUrl = volumeInfo.getImageLinks().getThumbnail();
            }
            if (volumeInfo.getLanguage() != null)
                language = volumeInfo.getLanguage();
            if (volumeInfo.getPreviewLink() != null)
                previewUrl = volumeInfo.getPreviewLink();
            if (volumeInfo.getInfoLink() != null)
                infoUrl = volumeInfo.getInfoLink();
            if (volumeInfo.getCanonicalVolumeLink() != null)
                canonicalVolumeUrl = volumeInfo.getCanonicalVolumeLink();
        }
        if (volume.getSearchInfo() != null && volume.getSearchInfo().getTextSnippet() != null)
            descriptionSnippet = volume.getSearchInfo().getTextSnippet();

        // Prepare content values

        BooksContentValues bCv = new BooksContentValues();
        bCv.putBookid(bookId);
        bCv.putTitle(title);
        bCv.putSubtitle(subtitle);
        bCv.putAuthors(authorsString);
        bCv.putPublisher(publisher);
        bCv.putPublisheddate(publishedDate);
        bCv.putDescription(description);
        bCv.putIsbn10(isbn_10);
        bCv.putIsbn13(isbn_13);
        bCv.putPrinttype(printType);
        bCv.putCategories(categoriesString);
        bCv.putMaturityrating(maturityRating);
        bCv.putSmallthumbnailurl(smallThumbnailUrl);
        bCv.putThumbnailurl(thumbnailUrl);
        bCv.putLanguage(language);
        bCv.putPreviewlink(previewUrl);
        bCv.putInfolink(infoUrl);
        bCv.putCanonicalvolumelink(canonicalVolumeUrl);
        bCv.putDescriptionsnippet(descriptionSnippet);
        bCv.putPagecount(pageCount);
        bCv.putRatingscount(ratingsCount);
        bCv.putAveragerating(averageRating);

        // Update if already in library. Add otherwise.

        ContentResolver cr = context.getContentResolver();
        // Book
        Cursor c = cr.query((new BooksSelection()).uri(), new String[]{BooksColumns.BOOKID},
                BooksColumns.BOOKID + " == ? ", new String[]{bookId}, null);
        if (c.moveToFirst()) {
            id = bCv.update(cr, (new BooksSelection().bookid(bookId)));
            Toast.makeText(context, context.getString(R.string.library_update_book, bookTitle), Toast.LENGTH_SHORT).show();
        } else {
            id = ContentUris.parseId(bCv.insert(cr));
            Toast.makeText(context, context.getString(R.string.library_add_book, bookTitle), Toast.LENGTH_SHORT).show();
        }
        c.close();
        // Authors
        if (authors != null) {
            for (String author : authors) {
                AuthorsContentValues aCv = new AuthorsContentValues();
                aCv.putName(author);
                aCv.putAuthorvolumeid(bookId);
                c = cr.query((new AuthorsSelection()).uri(), new String[]{AuthorsColumns.NAME, AuthorsColumns.AUTHORVOLUMEID},
                        AuthorsColumns.NAME + " == ? AND " + AuthorsColumns.AUTHORVOLUMEID + " == ? ", new String[]{author, bookId}, null);
                if (c.moveToFirst()) {
                    // do nothing
                } else {
                    aCv.insert(cr);
                }
                c.close();
            }
        }
        // Categories
        if (categories != null) {
            for (String category : categories) {
                CategoriesContentValues cCv = new CategoriesContentValues();
                cCv.putName(category);
                cCv.putCategoryvolumeid(bookId);
                c = cr.query((new CategoriesSelection()).uri(), new String[]{CategoriesColumns.NAME, CategoriesColumns.CATEGORYVOLUMEID},
                        CategoriesColumns.NAME + " == ? AND " + CategoriesColumns.CATEGORYVOLUMEID + " == ? ", new String[]{category, bookId}, null);
                if (c.moveToFirst()) {
                    // do nothing
                } else {
                    cCv.insert(cr);
                }
                c.close();
            }
        }

        return id;
    }

    public static void removeFromLibrary(Context context, String volumeId, String bookTitle) {
        String[] selectionArgs = new String[]{volumeId};
        ContentResolver cr = context.getContentResolver();
        cr.delete((new BooksSelection()).uri(), BooksColumns.BOOKID + " == ? ", selectionArgs);
        cr.delete((new AuthorsSelection()).uri(), AuthorsColumns.AUTHORVOLUMEID + " == ? ", selectionArgs);
        cr.delete((new CategoriesSelection()).uri(), CategoriesColumns.CATEGORYVOLUMEID + " == ? ", selectionArgs);
        Toast.makeText(context, context.getString(R.string.library_delete_book, bookTitle), Toast.LENGTH_SHORT).show();
    }
}
