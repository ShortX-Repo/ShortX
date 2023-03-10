/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.services.ui.widget;

import android.annotation.ColorInt;
import android.annotation.DimenRes;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.Px;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;


/**
 * A FrameLayout with a rounded corner background and shadow.
 * <p>
 * CardView uses <code>elevation</code> property on Lollipop for shadows and falls back to a
 * custom emulated shadow implementation on older platforms.
 * <p>
 * Due to expensive nature of rounded corner clipping, on platforms before Lollipop, CardView does
 * not clip its children that intersect with rounded corners. Instead, it adds padding to avoid such
 * intersection (See {@link #setPreventCornerOverlap(boolean)} to change this behavior).
 * <p>
 * Before Lollipop, CardView adds padding to its content and draws shadows to that area. This
 * padding amount is equal to <code>maxCardElevation + (1 - cos45) * cornerRadius</code> on the
 * sides and <code>maxCardElevation * 1.5 + (1 - cos45) * cornerRadius</code> on top and bottom.
 * <p>
 * Since padding is used to offset content for shadows, you cannot set padding on CardView.
 * Instead, you can use content padding attributes in XML or
 * {@link #setContentPadding(int, int, int, int)} in code to set the padding between the edges of
 * the CardView and children of CardView.
 * <p>
 * Note that, if you specify exact dimensions for the CardView, because of the shadows, its content
 * area will be different between platforms before Lollipop and after Lollipop. By using api version
 * specific resource values, you can avoid these changes. Alternatively, If you want CardView to add
 * inner padding on platforms Lollipop and after as well, you can call
 * {@link #setUseCompatPadding(boolean)} and pass <code>true</code>.
 * <p>
 * To change CardView's elevation in a backward compatible way, use
 * {@link #setCardElevation(float)}. CardView will use elevation API on Lollipop and before
 * Lollipop, it will change the shadow size. To avoid moving the View while shadow size is changing,
 * shadow size is clamped by {@link #getMaxCardElevation()}. If you want to change elevation
 * dynamically, you should call {@link #setMaxCardElevation(float)} when CardView is initialized.
 *
 * @attr ref androidx.cardview.R.styleable#CardView_cardBackgroundColor
 * @attr ref androidx.cardview.R.styleable#CardView_cardCornerRadius
 * @attr ref androidx.cardview.R.styleable#CardView_cardElevation
 * @attr ref androidx.cardview.R.styleable#CardView_cardMaxElevation
 * @attr ref androidx.cardview.R.styleable#CardView_cardUseCompatPadding
 * @attr ref androidx.cardview.R.styleable#CardView_cardPreventCornerOverlap
 * @attr ref androidx.cardview.R.styleable#CardView_contentPadding
 * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingLeft
 * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingTop
 * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingRight
 * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingBottom
 */
public class CardView extends FrameLayout {

    private static final int[] COLOR_BACKGROUND_ATTR = {android.R.attr.colorBackground};
    private static final CardViewImpl IMPL;

    static {
        IMPL = new CardViewApi21Impl();
        IMPL.initStatic();
    }

    private boolean mCompatPadding;

    private boolean mPreventCornerOverlap;

    /**
     * CardView requires to have a particular minimum size to draw shadows before API 21. If
     * developer also sets min width/height, they might be overridden.
     * <p>
     * CardView works around this issue by recording user given parameters and using an internal
     * method to set them.
     */
    int mUserSetMinWidth, mUserSetMinHeight;

    final Rect mContentPadding = new Rect();

    final Rect mShadowBounds = new Rect();

    public CardView(@NonNull Context context) {
        this(context, 20);
    }

    public CardView(@NonNull Context context, @DimenRes int radiusDp) {
        super(context, null, 0);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[]{
                Color.WHITE,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList backgroundColor = new ColorStateList(states, colors);

        float radius = (int) ResUtils.convertDpToPixel(radiusDp, context);
        float elevation = (int) ResUtils.convertDpToPixel(0, context);
        float maxElevation = (int) ResUtils.convertDpToPixel(4, context);
        mCompatPadding = false;
        mPreventCornerOverlap = true;
        int defaultPadding = (int) ResUtils.convertDpToPixel(12, context);
        mContentPadding.left = defaultPadding;
        mContentPadding.top = defaultPadding;
        mContentPadding.right = defaultPadding;
        mContentPadding.bottom = defaultPadding;
        mUserSetMinWidth = 0;
        mUserSetMinHeight = 0;

        IMPL.initialize(mCardViewDelegate, context, backgroundColor, radius,
                elevation, maxElevation);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        // NO OP
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // NO OP
    }

    /**
     * Returns whether CardView will add inner padding on platforms Lollipop and after.
     *
     * @return <code>true</code> if CardView adds inner padding on platforms Lollipop and after to
     * have same dimensions with platforms before Lollipop.
     */
    public boolean getUseCompatPadding() {
        return mCompatPadding;
    }

    /**
     * CardView adds additional padding to draw shadows on platforms before Lollipop.
     * <p>
     * This may cause Cards to have different sizes between Lollipop and before Lollipop. If you
     * need to align CardView with other Views, you may need api version specific dimension
     * resources to account for the changes.
     * As an alternative, you can set this flag to <code>true</code> and CardView will add the same
     * padding values on platforms Lollipop and after.
     * <p>
     * Since setting this flag to true adds unnecessary gaps in the UI, default value is
     * <code>false</code>.
     *
     * @param useCompatPadding <code>true></code> if CardView should add padding for the shadows on
     *                         platforms Lollipop and above.
     * @attr ref androidx.cardview.R.styleable#CardView_cardUseCompatPadding
     */
    public void setUseCompatPadding(boolean useCompatPadding) {
        if (mCompatPadding != useCompatPadding) {
            mCompatPadding = useCompatPadding;
            IMPL.onCompatPaddingChanged(mCardViewDelegate);
        }
    }

    /**
     * Sets the padding between the Card's edges and the children of CardView.
     * <p>
     * Depending on platform version or {@link #getUseCompatPadding()} settings, CardView may
     * update these values before calling {@link View#setPadding(int, int, int, int)}.
     *
     * @param left   The left padding in pixels
     * @param top    The top padding in pixels
     * @param right  The right padding in pixels
     * @param bottom The bottom padding in pixels
     * @attr ref androidx.cardview.R.styleable#CardView_contentPadding
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingLeft
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingTop
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingRight
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingBottom
     */
    public void setContentPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mContentPadding.set(left, top, right, bottom);
        IMPL.updatePadding(mCardViewDelegate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!(IMPL instanceof CardViewApi21Impl)) {
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            switch (widthMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minWidth = (int) Math.ceil(IMPL.getMinWidth(mCardViewDelegate));
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minWidth,
                            MeasureSpec.getSize(widthMeasureSpec)), widthMode);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    // Do nothing
                    break;
            }

            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                case MeasureSpec.AT_MOST:
                    final int minHeight = (int) Math.ceil(IMPL.getMinHeight(mCardViewDelegate));
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.max(minHeight,
                            MeasureSpec.getSize(heightMeasureSpec)), heightMode);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    // Do nothing
                    break;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        mUserSetMinWidth = minWidth;
        super.setMinimumWidth(minWidth);
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        mUserSetMinHeight = minHeight;
        super.setMinimumHeight(minHeight);
    }

    /**
     * Updates the background color of the CardView
     *
     * @param color The new color to set for the card background
     * @attr ref androidx.cardview.R.styleable#CardView_cardBackgroundColor
     */
    public void setCardBackgroundColor(@ColorInt int color) {
        IMPL.setBackgroundColor(mCardViewDelegate, ColorStateList.valueOf(color));
    }

    /**
     * Updates the background ColorStateList of the CardView
     *
     * @param color The new ColorStateList to set for the card background
     * @attr ref androidx.cardview.R.styleable#CardView_cardBackgroundColor
     */
    public void setCardBackgroundColor(@Nullable ColorStateList color) {
        IMPL.setBackgroundColor(mCardViewDelegate, color);
    }

    /**
     * Returns the background color state list of the CardView.
     *
     * @return The background color state list of the CardView.
     */
    @NonNull
    public ColorStateList getCardBackgroundColor() {
        return IMPL.getBackgroundColor(mCardViewDelegate);
    }

    /**
     * Returns the inner padding after the Card's left edge
     *
     * @return the inner padding after the Card's left edge
     */
    @Px
    public int getContentPaddingLeft() {
        return mContentPadding.left;
    }

    /**
     * Returns the inner padding before the Card's right edge
     *
     * @return the inner padding before the Card's right edge
     */
    @Px
    public int getContentPaddingRight() {
        return mContentPadding.right;
    }

    /**
     * Returns the inner padding after the Card's top edge
     *
     * @return the inner padding after the Card's top edge
     */
    @Px
    public int getContentPaddingTop() {
        return mContentPadding.top;
    }

    /**
     * Returns the inner padding before the Card's bottom edge
     *
     * @return the inner padding before the Card's bottom edge
     */
    @Px
    public int getContentPaddingBottom() {
        return mContentPadding.bottom;
    }

    /**
     * Updates the corner radius of the CardView.
     *
     * @param radius The radius in pixels of the corners of the rectangle shape
     * @attr ref androidx.cardview.R.styleable#CardView_cardCornerRadius
     * @see #setRadius(float)
     */
    public void setRadius(float radius) {
        IMPL.setRadius(mCardViewDelegate, radius);
    }

    /**
     * Returns the corner radius of the CardView.
     *
     * @return Corner radius of the CardView
     * @see #getRadius()
     */
    public float getRadius() {
        return IMPL.getRadius(mCardViewDelegate);
    }

    /**
     * Updates the backward compatible elevation of the CardView.
     *
     * @param elevation The backward compatible elevation in pixels.
     * @attr ref androidx.cardview.R.styleable#CardView_cardElevation
     * @see #getCardElevation()
     * @see #setMaxCardElevation(float)
     */
    public void setCardElevation(float elevation) {
        IMPL.setElevation(mCardViewDelegate, elevation);
    }

    /**
     * Returns the backward compatible elevation of the CardView.
     *
     * @return Elevation of the CardView
     * @see #setCardElevation(float)
     * @see #getMaxCardElevation()
     */
    public float getCardElevation() {
        return IMPL.getElevation(mCardViewDelegate);
    }

    /**
     * Updates the backward compatible maximum elevation of the CardView.
     * <p>
     * Calling this method has no effect if device OS version is Lollipop or newer and
     * {@link #getUseCompatPadding()} is <code>false</code>.
     *
     * @param maxElevation The backward compatible maximum elevation in pixels.
     * @attr ref androidx.cardview.R.styleable#CardView_cardMaxElevation
     * @see #setCardElevation(float)
     * @see #getMaxCardElevation()
     */
    public void setMaxCardElevation(float maxElevation) {
        IMPL.setMaxElevation(mCardViewDelegate, maxElevation);
    }

    /**
     * Returns the backward compatible maximum elevation of the CardView.
     *
     * @return Maximum elevation of the CardView
     * @see #setMaxCardElevation(float)
     * @see #getCardElevation()
     */
    public float getMaxCardElevation() {
        return IMPL.getMaxElevation(mCardViewDelegate);
    }

    /**
     * Returns whether CardView should add extra padding to content to avoid overlaps with rounded
     * corners on pre-Lollipop platforms.
     *
     * @return True if CardView prevents overlaps with rounded corners on platforms before Lollipop.
     * Default value is <code>true</code>.
     */
    public boolean getPreventCornerOverlap() {
        return mPreventCornerOverlap;
    }

    /**
     * On pre-Lollipop platforms, CardView does not clip the bounds of the Card for the rounded
     * corners. Instead, it adds padding to content so that it won't overlap with the rounded
     * corners. You can disable this behavior by setting this field to <code>false</code>.
     * <p>
     * Setting this value on Lollipop and above does not have any effect unless you have enabled
     * compatibility padding.
     *
     * @param preventCornerOverlap Whether CardView should add extra padding to content to avoid
     *                             overlaps with the CardView corners.
     * @attr ref androidx.cardview.R.styleable#CardView_cardPreventCornerOverlap
     * @see #setUseCompatPadding(boolean)
     */
    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        if (preventCornerOverlap != mPreventCornerOverlap) {
            mPreventCornerOverlap = preventCornerOverlap;
            IMPL.onPreventCornerOverlapChanged(mCardViewDelegate);
        }
    }

    private final CardViewDelegate mCardViewDelegate = new CardViewDelegate() {
        private Drawable mCardBackground;

        @Override
        public void setCardBackground(Drawable drawable) {
            mCardBackground = drawable;
            setBackgroundDrawable(drawable);
        }

        @Override
        public boolean getUseCompatPadding() {
            return CardView.this.getUseCompatPadding();
        }

        @Override
        public boolean getPreventCornerOverlap() {
            return CardView.this.getPreventCornerOverlap();
        }

        @Override
        public void setShadowPadding(int left, int top, int right, int bottom) {
            mShadowBounds.set(left, top, right, bottom);
            CardView.super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
                    right + mContentPadding.right, bottom + mContentPadding.bottom);
        }

        @Override
        public void setMinWidthHeightInternal(int width, int height) {
            if (width > mUserSetMinWidth) {
                CardView.super.setMinimumWidth(width);
            }
            if (height > mUserSetMinHeight) {
                CardView.super.setMinimumHeight(height);
            }
        }

        @Override
        public Drawable getCardBackground() {
            return mCardBackground;
        }

        @Override
        public View getCardView() {
            return CardView.this;
        }
    };
}
