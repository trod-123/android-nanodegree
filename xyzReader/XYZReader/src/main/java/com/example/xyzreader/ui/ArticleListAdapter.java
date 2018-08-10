package com.example.xyzreader.ui;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.util.Toolbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleListViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private Context mContext;
    private Cursor mCursor;
    private ArticleListClickListener mClickListener;

    interface ArticleListClickListener {
        void onClick(ImageView iv, long itemId);
    }

    public ArticleListAdapter(Context context, Cursor cursor, ArticleListClickListener listener) {
        mContext = context;
        mCursor = cursor;
        mClickListener = listener;
    }

    public ArticleListAdapter(Context context, ArticleListClickListener listener) {
        mContext = context;
        mClickListener = listener;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_article, parent, false);
        return new ArticleListViewHolder(view);
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Timber.e(ex);
            Timber.i("passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticleListViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.authorView.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        String bodyPreview = mCursor.getString(ArticleLoader.Query.BODY);
        int bodyCharLimit = holder.itemView.getContext().getResources().getInteger(R.integer.body_preview_upper_limit);
        if (bodyPreview.length() > bodyCharLimit) {
            // limit the body preview to lessen load on OS
            bodyPreview = bodyPreview.substring(0, bodyCharLimit);
        }
        holder.bodyPreviewView.setText(bodyPreview);
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString())
            );
        } else {
            holder.subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate))
            );
        }

        RequestListener<Bitmap> listener = new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                holder.pbThumbnail.setVisibility(View.GONE);
                Timber.e(e, "There was a problem loading the list image thumbnail");
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                holder.pbThumbnail.setVisibility(View.GONE);
                return false;
            }
        };
        holder.pbThumbnail.setVisibility(View.VISIBLE);
        Toolbox.loadThumbnailFromUrl(holder.itemView.getContext(),
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                holder.thumbnailView, listener);

        // set-up article actions button
        holder.ibActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbox.showArticleActionsMenuPopup(mContext, v, mCursor, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public class ArticleListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.article_thumbnail)
        ImageView thumbnailView;
        @BindView(R.id.details_article_title)
        TextView titleView;
        @BindView(R.id.article_date)
        TextView subtitleView;
        @BindView(R.id.details_article_author)
        TextView authorView;
        @BindView(R.id.article_body_preview)
        TextView bodyPreviewView;
        @BindView(R.id.ib_action_menu)
        ImageButton ibActions;
        @BindView(R.id.pb_article_thumbnail)
        ProgressBar pbThumbnail;

        ArticleListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onClick(thumbnailView,
                    ArticleListAdapter.this.getItemId(getAdapterPosition()));
        }
    }
}
