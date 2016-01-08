package it.jaschke.alexandria.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Locale;

import it.jaschke.alexandria.R;
import it.jaschke.alexandria.model.IndustryIdentifier;
import it.jaschke.alexandria.model.Volume;
import it.jaschke.alexandria.model.VolumeInfo;
import it.jaschke.alexandria.provider.authors.AuthorsColumns;
import it.jaschke.alexandria.provider.authors.AuthorsCursor;
import it.jaschke.alexandria.provider.authors.AuthorsSelection;
import it.jaschke.alexandria.provider.books.BooksCursor;
import it.jaschke.alexandria.provider.categories.CategoriesColumns;
import it.jaschke.alexandria.provider.categories.CategoriesCursor;
import it.jaschke.alexandria.provider.categories.CategoriesSelection;

/**
 * Created by TROD on 20160108.
 */
public class UIHelper {

    /**
     * Gets the Google Books url. Must NOT be null.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getInfoLink(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null) {
            return volume.getInfoLink();
        }
        if (cursor != null) {
            return cursor.getInfolink();
        }
        return null;
    }

    /**
     * Gets the title. Must NOT be null.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getTitle(Context context, @Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null) {
            return volume.getTitle();
        }
        if (cursor != null) {
            return cursor.getTitle();
        }
        return context.getString(R.string.library_book_no_name);
    }

    /**
     * Gets the subtitle. Null if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getSubtitle(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getSubtitle() != null) {
            return volume.getSubtitle();
        }
        if (cursor != null && cursor.getSubtitle() != null) {
            return cursor.getSubtitle();
        }
        return null;
    }

    /**
     * Gets the authors. Empty string if none.
     * @param context
     * @param volume
     * @param cursor
     * @return
     */
    public static String getAuthors(@NonNull Context context, @Nullable VolumeInfo volume, @Nullable AuthorsCursor cursor) {
        String authors = "";
        if (volume != null && volume.getAuthors() != null && volume.getAuthors().size() > 0) {
            List<String> authorsList = volume.getAuthors();
            int size = authorsList.size();
            for (int i = 0; i < size; i++) {
                if (size == 1) {
                    // Simply return the author if only one
                    return authorsList.get(i);
                } else if (size == 2 || i < size - 2) {
                    // Omit the comma if on last two authors
                    authors += context.getString(R.string.list_duple, authorsList.get(i), authorsList.get(++i));
                } else if (i < size - 1) {
                    // Add comma if there are at least 2 more authors to go
                    authors += authorsList.get(i) + ", ";
                } else {
                    // Append "and" if on last author
                    authors += context.getString(R.string.list_and) + authorsList.get(i);
                }
            }
        }
        if (cursor != null && cursor.moveToFirst()) {
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (size == 1) {
                    // Simply return the author if only one
                    authors += cursor.getName();
                } else if (size == 2 || i < size - 2) {
                    // Omit the comma if on last two authors
                    String firstAuthor = cursor.getName();
                    cursor.moveToNext();
                    i++;
                    authors += context.getString(R.string.list_duple, firstAuthor, cursor.getName());
                } else if (i < size - 1) {
                    // Add comma if there are at least 2 more authors to go
                    authors += cursor.getName() + ", ";
                } else {
                    // Append "and" if on last author
                    authors += context.getString(R.string.list_and) + cursor.getName();
                }
            }
            cursor.close();
        }
        if (authors.length() == 0) {
            authors = context.getString(R.string.library_book_no_author);
        }
        return authors;
    }

    /**
     * Gets the published date. Empty string if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getDatePublished(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getPublishedDate() != null) {
            return volume.getPublishedDate().substring(0, 4);
        }
        if (cursor != null && cursor.getPublisheddate() != null) {
            return cursor.getPublisheddate().substring(0, 4);
        }
        return "";
    }

    /**
     * Gets the short description. Empty string if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getShortDescription(@Nullable Volume volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getSearchInfo() != null &&
                volume.getSearchInfo().getTextSnippet() != null) {
            return volume.getSearchInfo().getTextSnippet();
        }
        if (cursor != null && cursor.getDescriptionsnippet() != null) {
            return cursor.getDescriptionsnippet();
        }
        return "";
    }

    /**
     * Gets the long description. Empty string if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getDescription(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getDescription() != null) {
            return volume.getDescription();
        }
        if (cursor != null && cursor.getDescription() != null) {
            return cursor.getDescription();
        }
        return "";
    }

    /**
     * Gets the thumbnail url. Invalid url if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getThumbnailUrl(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getImageLinks() != null &&
                volume.getImageLinks().getSmallThumbnail() != null) {
            return volume.getImageLinks().getSmallThumbnail();
        }
        if (cursor != null && cursor.getThumbnailurl() != null) {
            return cursor.getThumbnailurl();
        }
        return "link";
    }

    /**
     * Gets the publisher. Null if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getPublisher(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getPublisher() != null) {
            return volume.getPublisher();
        }
        if (cursor != null && cursor.getPublisher() != null) {
            return cursor.getPublisher();
        }
        return null;
    }

    /**
     * Gets the ISBNs in presentable form. Empty string if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getISBNs(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        String isbn_10 = null;
        String isbn_13 = null;
        if (volume != null && volume.getIndustryIdentifiers() != null) {
            for (IndustryIdentifier indId : volume.getIndustryIdentifiers()) {
                if (indId.getType().equals(Library.ISBN_10))
                    isbn_10 = indId.getIdentifier();
                else if (indId.getType().equals(Library.ISBN_13))
                    isbn_13 = indId.getIdentifier();
            }
        }
        if (cursor != null) {
            if (cursor.getIsbn10() != null) {
                isbn_10 = cursor.getIsbn10();
            }
            if (cursor.getIsbn13() != null) {
                isbn_13 = cursor.getIsbn13();
            }
        }
        String isbns = "";
        if (isbn_10 != null && isbn_13 != null)
            isbns += isbn_10 + ", " + isbn_13;
        else if (isbn_10 != null)
            isbns += isbn_10;
        else if (isbn_13 != null)
            isbns += isbn_13;
        return isbns;
    }

    /**
     * Gets the main language. Null if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static String getLanguage(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getLanguage() != null) {
            return new Locale(volume.getLanguage()).getDisplayLanguage();
        }
        if (cursor != null && cursor.getLanguage() != null) {
            return new Locale(cursor.getLanguage()).getDisplayLanguage();
        }
        return null;
    }

    /**
     * Gets the categories. Empty string if none.
     * @param context
     * @param volume
     * @param cursor
     * @return
     */
    public static String getCategories(@NonNull Context context, @Nullable VolumeInfo volume, @Nullable CategoriesCursor cursor) {
        String categories = "";
        if (volume != null && volume.getCategories() != null && volume.getCategories().size() > 0) {
            List<String> categoriesList = volume.getCategories();
            int size = categoriesList.size();
            for (int i = 0; i < size; i++) {
                if (size == 1) {
                    // Simply return the category if only one
                    return categoriesList.get(i);
                } else if (size == 2 || i < size - 2) {
                    // Omit the comma if on last two categories
                    categories += context.getString(R.string.list_duple, categoriesList.get(i), categoriesList.get(++i));
                } else if (i < size - 1) {
                    // Add comma if there are at least 2 more categories to go
                    categories += categoriesList.get(i) + ", ";
                } else {
                    // Append "and" if on last category
                    categories += context.getString(R.string.list_and) + categoriesList.get(i);
                }
            }
        }
        if (cursor != null && cursor.moveToFirst()) {
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (size == 1) {
                    // Simply return the category if only one
                    categories += cursor.getName();
                } else if (size == 2 || i < size - 2) {
                    // Omit the comma if on last two categories
                    String firstCategory = cursor.getName();
                    cursor.moveToNext();
                    i++;
                    categories += context.getString(R.string.list_duple, firstCategory, cursor.getName());
                } else if (i < size - 1) {
                    // Add comma if there are at least 2 more categories to go
                    categories += cursor.getName() + ", ";
                } else {
                    // Append "and" if on last category
                    categories += context.getString(R.string.list_and) + cursor.getName();
                }
            }
            cursor.close();
        }
        return categories;
    }

    /**
     * Gets the page count. -1 if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static int getPageCount(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getPageCount() != null) {
            return volume.getPageCount();
        }
        if (cursor != null && cursor.getPagecount() != null) {
            return cursor.getPagecount();
        }
        return -1;
    }

    /**
     * Gets the ratings count. -1 if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static int getRatingsCount(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getRatingsCount() != null) {
            return volume.getRatingsCount();
        }
        if (cursor != null && cursor.getRatingscount() != null) {
            return cursor.getRatingscount();
        }
        return -1;
    }

    /**
     * Gets the average rating. -1 if none.
     * @param volume
     * @param cursor
     * @return
     */
    public static double getRatingsAverage(@Nullable VolumeInfo volume, @Nullable BooksCursor cursor) {
        if (volume != null && volume.getAverageRating() != null) {
            return volume.getAverageRating();
        }
        if (cursor != null && cursor.getAveragerating() != null) {
            return cursor.getAveragerating();
        }
        return -1;
    }
}
