package it.jaschke.alexandria.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.List;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsContentValues;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksColumns;
import it.jaschke.alexandria.provider.books.BooksContentValues;
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
        String bookId = volume.getId();

        if (volume.getVolumeInfo() != null) {
            // Extract data from volume (only id and title are not nullable)
            String infoUrl, title, subtitle, authorsString, publishedDate, descriptionSnippet,
                    description, smallThumbnailUrl, thumbnailUrl, publisher, isbn_10, isbn_13, language,
                    categoriesString;
            List<String> authors, categories;
            int pageCount, ratingsCount;
            double averageRating;

            VolumeInfo volumeInfo = volume.getVolumeInfo();
            infoUrl = LibraryHelper.getInfoLink(volumeInfo, null);
            title = LibraryHelper.getTitle(context, false, volumeInfo, null);
            subtitle = LibraryHelper.getSubtitle(volumeInfo, null);
            authors = LibraryHelper.getAuthorsList(volumeInfo);
            // To be stored in the book object itself to make it searchable
            authorsString = LibraryHelper.getAuthors(context, false, volumeInfo, null);
            publishedDate = LibraryHelper.getDatePublished(false, volumeInfo, null);
            descriptionSnippet = LibraryHelper.getShortDescription(false, volume, null);
            description = LibraryHelper.getDescription(false, volumeInfo, null);
            smallThumbnailUrl = LibraryHelper.getThumbnailUrl(false, volumeInfo, null);
            thumbnailUrl = LibraryHelper.getThumbnailUrl(false, volumeInfo, null);
            publisher = LibraryHelper.getPublisher(volumeInfo, null);
            String[] isbns = LibraryHelper.getListISBNs(volumeInfo, null);
            isbn_10 = isbns[0];
            isbn_13 = isbns[1];
            language = LibraryHelper.getLanguage(false, volumeInfo, null);
            categories = LibraryHelper.getCategoriesList(volumeInfo);
            categoriesString = LibraryHelper.getCategories(context, false, volumeInfo, null);
            pageCount = LibraryHelper.getPageCount(volumeInfo, null);
            ratingsCount = LibraryHelper.getRatingsCount(volumeInfo, null);
            averageRating = LibraryHelper.getRatingsAverage(volumeInfo, null);

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
            bCv.putCategories(categoriesString);
            bCv.putSmallthumbnailurl(smallThumbnailUrl);
            bCv.putThumbnailurl(thumbnailUrl);
            bCv.putLanguage(language);
            bCv.putInfolink(infoUrl);
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
