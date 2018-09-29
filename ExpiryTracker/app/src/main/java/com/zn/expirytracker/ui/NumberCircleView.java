package com.zn.expirytracker.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zn.expirytracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NumberCircleView extends FrameLayout {

    private static final int DEFAULT_VALUE = 0;

    @BindView(R.id.iv_number_circle_outline)
    ImageView mIvOutline;
    @BindView(R.id.layout_number_circle)
    ConstraintLayout mCircleLayout;
    @BindView(R.id.tv_number_circle_value)
    TextView mTvValue;
    @BindView(R.id.tv_number_circle_label)
    TextView mTvLabel;

    private Context mContext;

    public NumberCircleView(Context context) {
        super(context);
        init(context);
    }

    public NumberCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NumberCircleView,
                0, 0);

        try {
            // For getting default view property values
            // https://stackoverflow.com/questions/5611411/what-is-the-default-text-size-on-android
//            TextView defaultTv = new TextView(getContext());

            // Set the value properties
            mTvValue.setText(String.valueOf(
                    a.getInt(R.styleable.NumberCircleView_value, DEFAULT_VALUE)));
//            mTvValue.setTextSize(a.getDimension(R.styleable.NumberCircleView_valueTextSize,
//                    defaultTv.getTextSize()));
//            mTvValue.setTextColor(a.getColor(R.styleable.NumberCircleView_valueTextColor,
//                    defaultTv.getCurrentTextColor()));

            // Set the label properties
            mTvLabel.setText(a.getString(R.styleable.NumberCircleView_valueLabel));
//            mTvLabel.setTextSize(a.getDimension(R.styleable.NumberCircleView_valueLabelTextSize,
//                    defaultTv.getTextSize()));
//            mTvLabel.setTextColor(a.getColor(R.styleable.NumberCircleView_valueLabelTextColor,
//                    defaultTv.getCurrentTextColor()));

            // Set the outline properties
            int outlineWidth = a.getDimensionPixelSize(R.styleable.NumberCircleView_outlineWidth,
                    mContext.getResources().getDimensionPixelSize(R.dimen.number_circle_outline_width));
            int outlineColor = a.getColor(R.styleable.NumberCircleView_outlineColor,
                    mContext.getResources().getColor(R.color.number_circle_outline_default));
            setOutlineWidthAndColor(outlineWidth, outlineColor);

            // Set the layout properties
            // Set the margins based on the outline width
            FrameLayout.LayoutParams params =
                    (FrameLayout.LayoutParams) mCircleLayout.getLayoutParams();
            params.setMargins(outlineWidth, outlineWidth, outlineWidth, outlineWidth);
            mCircleLayout.setLayoutParams(params);

            // TODO: Dynamically set outline and margins based on circle size

        } finally {
            a.recycle();
        }
    }

    public NumberCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Helper for updating the content description with current field values
     *
     * @param stringResource Resource id for the string template. Requires it contains 2 string
     *                       placeholders for the value field and the label field. Sets content
     *                       description to null if error
     */
    public void updateContentDescription(int stringResource) {
        try {
            setContentDescription(mContext.getString(stringResource,
                    mTvValue.getText(), mTvLabel.getText()));
        } catch (Exception e) {
            setContentDescription(null);
        }
    }

    /**
     * Get references to the views
     *
     * @param context
     */
    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.layout_number_circle, this);
        ButterKnife.bind(this);
    }

    /**
     * Sets the outline width and color
     * <p>
     * From: https://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically
     *
     * @param outlineWidth The width, in pixels
     * @param outlineColor The color value (note this is NOT the resource id)
     */
    public void setOutlineWidthAndColor(int outlineWidth, int outlineColor) {
        Drawable rim = mIvOutline.getBackground();
        if (rim instanceof GradientDrawable) {
            ((GradientDrawable) rim.mutate()).setStroke(outlineWidth, outlineColor);
        }
    }

    // region GETTERS

    /**
     * Gets the outline {@link ImageView} for this object
     *
     * @return
     */
    public ImageView getOutline() {
        return mIvOutline;
    }

    /**
     * Gets the circle layout container, containing the value and the label text views. This layout
     * uses {@link ConstraintLayout}
     *
     * @return
     */
    public ConstraintLayout getCircleLayout() {
        return mCircleLayout;
    }

    /**
     * Gets the value {@link TextView} for this object
     *
     * @return
     */
    public TextView getValueTextView() {
        return mTvValue;
    }

    /**
     * Gets the label {@link TextView} for this object
     *
     * @return
     */
    public TextView getLabelTextView() {
        return mTvLabel;
    }

    // endregion

}
