package com.example.xyzreader.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import com.example.xyzreader.R;
import com.example.xyzreader.util.Toolbox;

/**
 * A convenience listener that maps menu items with actions, which can be used across activities
 * that need to share article information
 */
public class ArticleActionsMenuOnClickListener implements PopupMenu.OnMenuItemClickListener {

    private Context mContext;
    private Cursor mCursor;
    private int mPosition;

    public ArticleActionsMenuOnClickListener(Context context, Cursor cursor,
                                             int cursorPosition) {
        mContext = context;
        mCursor = cursor;
        mPosition = cursorPosition;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                Toolbox.shareArticle(mContext, mCursor, mPosition);
                return true;
        }
        return false;
    }
}


