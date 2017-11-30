package com.example.ljj.texttagdemo.tool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.method.ArrowKeyMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ljj.texttagdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>TagGroup</code> is a special layout with a set of tags.
 * This group has two modes:
 * <p>
 * 1. APPEND mode
 * 2. DISPLAY mode
 * </p>
 * Default is DISPLAY mode. When in APPEND mode, the group is capable of input for append new tags
 * and delete tags.
 * <p>
 * When in DISPLAY mode, the group is only contain NORMAL state tags, and the tags in group
 * is not focusable.
 * </p>
 *
 * @author Jun Gu (http://2dxgujun.com)
 * @version 2.0
 * @since 2015-2-3 14:16:32
 */
public class TagGroupNoEdit extends ViewGroup {
    private final int default_border_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_text_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_background_color = Color.WHITE;
    private final int default_dash_border_color = Color.rgb(0xAA, 0xAA, 0xAA);
    private final int default_input_hint_color = Color.argb(0x80, 0x00, 0x00, 0x00);
    private final int default_input_text_color = Color.argb(0xDE, 0x00, 0x00, 0x00);
    private final int default_checked_border_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_checked_text_color = Color.WHITE;
    private final int default_checked_marker_color = Color.WHITE;
    private final int default_checked_background_color = Color.rgb(0x49, 0xC1, 0x20);
    private final int default_pressed_background_color = Color.rgb(0xED, 0xED, 0xED);
    private final float default_border_stroke_width;
    private final float default_text_size;
    private final float default_horizontal_spacing;
    private final float default_vertical_spacing;
    private final float default_horizontal_padding;
    private final float default_vertical_padding;

    /**
     * The text to be displayed when the text of the INPUT tag is empty.
     */
    private CharSequence inputHint;

    /**
     * The tag outline border color.
     */
    private int borderColor;

    /**
     * The tag text color.
     */
    private int textColor;

    /**
     * The tag background color.
     */
    private int backgroundColor;

    /**
     * The dash outline border color.
     */
    private int dashBorderColor;

    /**
     * The  input tag hint text color.
     */
    private int inputHintColor;

    /**
     * The input tag type text color.
     */
    private int inputTextColor;

    /**
     * The checked tag outline border color.
     */
    private int checkedBorderColor;

    /**
     * The check text color
     */
    private int checkedTextColor;

    /**
     * The checked marker color.
     */
    private int checkedMarkerColor;

    /**
     * The checked tag background color.
     */
    private int checkedBackgroundColor;

    /**
     * The tag background color, when the tag is being pressed.
     */
    private int pressedBackgroundColor;

    /**
     * The tag outline border stroke width, default is 0.5dp.
     */
    private float borderStrokeWidth;

    /**
     * The tag text size, default is 13sp.
     */
    private float textSize;

    /**
     * The horizontal tag spacing, default is 8.0dp.
     */
    private int horizontalSpacing;

    /**
     * The vertical tag spacing, default is 4.0dp.
     */
    private int verticalSpacing;

    /**
     * The horizontal tag padding, default is 12.0dp.
     */
    private int horizontalPadding;

    /**
     * The vertical tag padding, default is 3.0dp.
     */
    private int verticalPadding;

    private int maxChooseCount;

    private boolean isChildCenter = false;

    /**
     * Listener used to dispatch tag change event.
     */
    private OnTagChangeListener mOnTagChangeListener;

    /**
     * Listener used to dispatch tag click event.
     */
    private OnTagClickListener mOnTagClickListener;

    /**
     * Listener used to handle tag click event.
     */
    private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();

    public TagGroupNoEdit(Context context) {
        this(context, null);
    }

    public TagGroupNoEdit(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tagGroupStyle);
    }

    public TagGroupNoEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_border_stroke_width = dp2px(0.5f);
        default_text_size = sp2px(13.0f);
        default_horizontal_spacing = dp2px(8.0f);
        default_vertical_spacing = dp2px(4.0f);
        default_horizontal_padding = dp2px(12.0f);
        default_vertical_padding = dp2px(3.0f);

        // Load styled attributes.
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagGroup, defStyleAttr, R.style.TagGroupNoEdit);
        try {
            borderColor = a.getColor(R.styleable.TagGroup_atg_borderColor, default_border_color);
            textColor = a.getColor(R.styleable.TagGroup_atg_textColor, default_text_color);
            backgroundColor = a.getColor(R.styleable.TagGroup_atg_backgroundColor, default_background_color);
            dashBorderColor = a.getColor(R.styleable.TagGroup_atg_dashBorderColor, default_dash_border_color);
            checkedBorderColor = a.getColor(R.styleable.TagGroup_atg_checkedBorderColor, default_checked_border_color);
            checkedTextColor = a.getColor(R.styleable.TagGroup_atg_checkedTextColor, default_checked_text_color);
            checkedMarkerColor = a.getColor(R.styleable.TagGroup_atg_checkedMarkerColor, default_checked_marker_color);
            checkedBackgroundColor = a.getColor(R.styleable.TagGroup_atg_checkedBackgroundColor, default_checked_background_color);
            pressedBackgroundColor = a.getColor(R.styleable.TagGroup_atg_pressedBackgroundColor, default_pressed_background_color);
            borderStrokeWidth = a.getDimension(R.styleable.TagGroup_atg_borderStrokeWidth, default_border_stroke_width);
            textSize = a.getDimension(R.styleable.TagGroup_atg_textSize, default_text_size);
            horizontalSpacing = (int) a.getDimension(R.styleable.TagGroup_atg_horizontalSpacing, default_horizontal_spacing);
            verticalSpacing = (int) a.getDimension(R.styleable.TagGroup_atg_verticalSpacing, default_vertical_spacing);
            horizontalPadding = (int) a.getDimension(R.styleable.TagGroup_atg_horizontalPadding, default_horizontal_padding);
            verticalPadding = (int) a.getDimension(R.styleable.TagGroup_atg_verticalPadding, default_vertical_padding);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int parentWidth = r - l;

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;
        int oneLineCount = 0;
        int height = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            height = child.getMeasuredHeight();
            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line

                    if (isChildCenter) {
                        // 将已经加载出来的几个按钮水平居中显示出来
                        adjustLine(oneLineCount, i, parentWidth - (childLeft - parentLeft - horizontalSpacing), childTop, childTop + height);
                    }
                    oneLineCount = 0;
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + horizontalSpacing;
                oneLineCount++;
            }
        }

        if (isChildCenter) {
            adjustLine(oneLineCount, count, parentWidth - (childLeft - parentLeft - horizontalSpacing), childTop, childTop + height);
        }

    }

    /**
     * @param lineCount 一行子view的个数
     * @param i         在所有的子view中，第i个的时候超出了行宽，需要换行
     */
    private void adjustLine(int lineCount, int i, int childWidth, int top, int bottom) {
        int totalLeft = childWidth / 2; // 填充view距离左侧间距
        for (int lineNum = (i - lineCount); lineNum < i; lineNum++) {
            View lineChildView = getChildAt(lineNum);
            int totalRight = totalLeft + lineChildView.getMeasuredWidth();
            lineChildView.layout(totalLeft, top, totalRight, bottom);
            totalLeft += lineChildView.getMeasuredWidth() + horizontalSpacing;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.tags = getTags();
        ss.checkedTagsCount = getCheckedTagCount();
        ss.checkedTagsIndex = getCheckedTagsIndex();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setTags(ss.tags);

        String[] checkedTagsIndex = ss.checkedTagsIndex;
        for (int i = 0; i < checkedTagsIndex.length; i++) {
            String indexString = checkedTagsIndex[i];
            int index = Integer.parseInt(indexString);
            TagView checkedTagView = getTagAt(index);
            if (checkedTagView != null) {
                checkedTagView.setChecked(true);
            }
        }
    }

    /**
     * Return the last NORMAL state tag view in this group.
     *
     * @return the last NORMAL state tag view or null if not exists
     */
    protected TagView getLastNormalTagView() {
        final int lastNormalTagIndex = getChildCount() - 1;
        TagView lastNormalTagView = getTagAt(lastNormalTagIndex);
        return lastNormalTagView;
    }

    /**
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getTags() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (tagView.mState == TagView.STATE_NORMAL) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList.toArray(new String[tagList.size()]);
    }

    public void setChildCenter(boolean childCenter) {
        isChildCenter = childCenter;
    }

    public void setMaxChooseNum(int maxChooseCount) {
        this.maxChooseCount = maxChooseCount;
    }

    /**
     * @see #setTags(String...)
     */
    public void setTags(List<String> tagList) {
        setTags(tagList.toArray(new String[tagList.size()]));
    }

    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(String... tags) {
        removeAllViews();
        for (final String tag : tags) {
            appendTag(tag);
        }
    }

    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from.
     * @return the tag view at the specified position or null if the position
     * does not exists within this group.
     */
    protected TagView getTagAt(int index) {
        return (TagView) getChildAt(index);
    }

    /**
     * Returns the checked tag view in the group.
     *
     * @return the checked tag view or null if not exists.
     */
    private int getCheckedTagCount() {

        int checkedCount = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final TagView tag = getTagAt(i);
            if (tag.isChecked) {
                checkedCount = checkedCount + 1;
            }
        }
        return checkedCount;
    }


    private String[] getCheckedTagsIndex() {

        final int count = getChildCount();
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(i + "");
            }
        }

        return tagList.toArray(new String[tagList.size()]);
    }


    public String[] getCheckedTags() {

        final int count = getChildCount();
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }

        return tagList.toArray(new String[tagList.size()]);
    }


//    protected TagView getCheckedTag() {
//        final int checkedTagIndex = getCheckedTagIndex();
//        if (checkedTagIndex != -1) {
//            return getTagAt(checkedTagIndex);
//        }
//        return null;
//    }


    /**
     * Register a callback to be invoked when this tag group is changed.
     *
     * @param l the callback that will run
     */
    public void setOnTagChangeListener(OnTagChangeListener l) {
        mOnTagChangeListener = l;
    }

    /**
     * @see #appendInputTag(String)
     */
    protected void appendInputTag() {
        appendInputTag(null);
    }

    /**
     * Append a INPUT tag to this group. It will throw an exception if there has a previous INPUT tag.
     *
     * @param tag the tag text.
     */
    protected void appendInputTag(String tag) {
        final TagView newInputTag = new TagView(getContext(), TagView.STATE_INPUT, tag);
        newInputTag.setOnClickListener(mInternalTagClickListener);
        addView(newInputTag);
    }

    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    protected void appendTag(CharSequence tag) {
        final TagView newTag = new TagView(getContext(), TagView.STATE_NORMAL, tag);
        newTag.setOnClickListener(mInternalTagClickListener);
        addView(newTag);
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new TagGroupNoEdit.LayoutParams(getContext(), attrs);
    }

    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
    }

    protected void deleteTag(TagView tagView) {
        removeView(tagView);
        if (mOnTagChangeListener != null) {
            mOnTagChangeListener.onDelete(TagGroupNoEdit.this, tagView.getText().toString());
        }
    }

    /**
     * Interface definition for a callback to be invoked when a tag group is changed.
     */
    public interface OnTagChangeListener {
        /**
         * Called when a tag has been appended to the group.
         *
         * @param tag the appended tag.
         */
        void onAppend(TagGroupNoEdit tagGroup, String tag);

        /**
         * Called when a tag has been deleted from the the group.
         *
         * @param tag the deleted tag.
         */
        void onDelete(TagGroupNoEdit tagGroup, String tag);
    }

    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnTagClickListener {
        /**
         * Called when a tag has been clicked.
         *
         * @param tag The tag text of the tag that was clicked.
         */
        void onTagClick(String tag);
    }

    /**
     * Per-child layout information for layouts.c
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    /**
     * For {@link TagGroupNoEdit} save and restore state.
     */
    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int tagCount;
        String[] tags;
        int checkedTagsCount;
        String[] checkedTagsIndex;
        String input;

        public SavedState(Parcel source) {
            super(source);
            tagCount = source.readInt();
            tags = new String[tagCount];
            source.readStringArray(tags);
            checkedTagsIndex = new String[checkedTagsCount];
            source.readStringArray(checkedTagsIndex);
            input = source.readString();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            tagCount = tags.length;
            dest.writeInt(tagCount);
            dest.writeStringArray(tags);
            dest.writeInt(checkedTagsCount);
            dest.writeStringArray(checkedTagsIndex);
            dest.writeString(input);
        }
    }

    /**
     * The tag view click listener for internal use.
     */
    class InternalTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final TagView tag = (TagView) v;

            // If the clicked tag is unchecked, uncheck the previous checked tag if exists,
            // then check the clicked tag.
            if (tag.isChecked) {
                tag.setChecked(false);
                if (mOnTagClickListener != null) {
                    mOnTagClickListener.onTagClick(tag.getText().toString());
                }
                return;
            }


            if (maxChooseCount > 0) {
                final int checkedTagCount = getCheckedTagCount();
                if (checkedTagCount >= maxChooseCount) {
                    Toast.makeText(getContext(), "最多" + maxChooseCount + "个", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            tag.setChecked(true);
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(tag.getText().toString());
            }
        }
    }

    /**
     * The tag view which has two states can be either NORMAL or INPUT.
     */
    class TagView extends TextView {
        public static final int STATE_NORMAL = 1;
        public static final int STATE_INPUT = 2;

        /**
         * The offset to the text.
         */
        private static final int CHECKED_MARKER_OFFSET = 3;

        /**
         * The stroke width of the checked marker
         */
        private static final int CHECKED_MARKER_STROKE_WIDTH = 4;

        /**
         * The current state.
         */
        private int mState;

        /**
         * Indicates the tag if checked.
         */
        private boolean isChecked = false;

        /**
         * Indicates the tag if pressed.
         */
        private boolean isPressed = false;

        private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private Paint mCheckedMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        /**
         * The rect for the tag's left corner drawing.
         */
        private RectF mLeftCornerRectF = new RectF();

        /**
         * The rect for the tag's right corner drawing.
         */
        private RectF mRightCornerRectF = new RectF();

        /**
         * The rect for the tag's horizontal blank fill area.
         */
        private RectF mHorizontalBlankFillRectF = new RectF();

        /**
         * The rect for the tag's vertical blank fill area.
         */
        private RectF mVerticalBlankFillRectF = new RectF();

        /**
         * The rect for the checked mark draw bound.
         */
        private RectF mCheckedMarkerBound = new RectF();

        /**
         * Used to detect the touch event.
         */
        private Rect mOutRect = new Rect();

        /**
         * The path for draw the tag's outline border.
         */
        private Path mBorderPath = new Path();

        /**
         * The path effect provide draw the dash border.
         */
        private PathEffect mPathEffect = new DashPathEffect(new float[]{10, 5}, 0);

        {
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(borderStrokeWidth);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
            mCheckedMarkerPaint.setStyle(Paint.Style.FILL);
            mCheckedMarkerPaint.setStrokeWidth(CHECKED_MARKER_STROKE_WIDTH);
            mCheckedMarkerPaint.setColor(checkedMarkerColor);
        }


        public TagView(Context context, final int state, CharSequence text) {
            super(context);
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            setLayoutParams(new TagGroupNoEdit.LayoutParams(
                    TagGroupNoEdit.LayoutParams.WRAP_CONTENT,
                    TagGroupNoEdit.LayoutParams.WRAP_CONTENT));

            setGravity(Gravity.CENTER);
            setText(text);
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            mState = state;

            setClickable(true);
            setFocusable(state == STATE_INPUT);
            setFocusableInTouchMode(state == STATE_INPUT);
            setHint(state == STATE_INPUT ? inputHint : null);
            setMovementMethod(state == STATE_INPUT ? ArrowKeyMovementMethod.getInstance() : null);

            // Interrupted long click event to avoid PAUSE popup.
            setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return state != STATE_INPUT;
                }
            });
            invalidatePaint();
        }

        /**
         * Set whether this tag view is in the checked state.
         *
         * @param checked true is checked, false otherwise
         */
        public void setChecked(boolean checked) {
            isChecked = checked;
            invalidatePaint();
        }


        @Override
        protected boolean getDefaultEditable() {
            return true;
        }

        /**
         * Indicates whether the input content is available.
         *
         * @return True if the input content is available, false otherwise.
         */
        public boolean isInputAvailable() {
            return getText() != null && getText().length() > 0;
        }

        private void invalidatePaint() {

            mBorderPaint.setPathEffect(null);
            if (isChecked) {
                mBorderPaint.setColor(checkedBorderColor);
                mBackgroundPaint.setColor(checkedBackgroundColor);
                setTextColor(checkedTextColor);
            } else {
                mBorderPaint.setColor(borderColor);
                mBackgroundPaint.setColor(backgroundColor);
                setTextColor(textColor);
            }


            if (isPressed) {
                mBackgroundPaint.setColor(pressedBackgroundColor);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawArc(mLeftCornerRectF, -180, 90, true, mBackgroundPaint);
            canvas.drawArc(mLeftCornerRectF, -270, 90, true, mBackgroundPaint);
            canvas.drawArc(mRightCornerRectF, -90, 90, true, mBackgroundPaint);
            canvas.drawArc(mRightCornerRectF, 0, 90, true, mBackgroundPaint);
            canvas.drawRect(mHorizontalBlankFillRectF, mBackgroundPaint);
            canvas.drawRect(mVerticalBlankFillRectF, mBackgroundPaint);
            canvas.drawPath(mBorderPath, mBorderPaint);
            super.onDraw(canvas);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            int left = (int) borderStrokeWidth;
            int top = (int) borderStrokeWidth;
            int right = (int) (left + w - borderStrokeWidth * 2);
            int bottom = (int) (top + h - borderStrokeWidth * 2);

            int d = bottom - top;

            mLeftCornerRectF.set(left, top, left + d, top + d);
            mRightCornerRectF.set(right - d, top, right, top + d);

            mBorderPath.reset();
            mBorderPath.addArc(mLeftCornerRectF, -180, 90);
            mBorderPath.addArc(mLeftCornerRectF, -270, 90);
            mBorderPath.addArc(mRightCornerRectF, -90, 90);
            mBorderPath.addArc(mRightCornerRectF, 0, 90);

            int l = (int) (d / 2.0f);
            mBorderPath.moveTo(left + l, top);
            mBorderPath.lineTo(right - l, top);

            mBorderPath.moveTo(left + l, bottom);
            mBorderPath.lineTo(right - l, bottom);

            mBorderPath.moveTo(left, top + l);
            mBorderPath.lineTo(left, bottom - l);

            mBorderPath.moveTo(right, top + l);
            mBorderPath.lineTo(right, bottom - l);

            mHorizontalBlankFillRectF.set(left, top + l, right, bottom - l);
            mVerticalBlankFillRectF.set(left + l, top, right - l, bottom);

            int m = (int) (h / 2.5f);
            h = bottom - top;
            mCheckedMarkerBound.set(right - m - horizontalPadding + CHECKED_MARKER_OFFSET,
                    top + h / 2 - m / 2,
                    right - horizontalPadding + CHECKED_MARKER_OFFSET,
                    bottom - h / 2 + m / 2);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mState == STATE_INPUT) {
                // The INPUT tag doesn't change background color on the touch event.
                return super.onTouchEvent(event);
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    getDrawingRect(mOutRect);
                    isPressed = true;
                    invalidatePaint();
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mOutRect.contains((int) event.getX(), (int) event.getY())) {
                        isPressed = false;
                        invalidatePaint();
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isPressed = false;
                    invalidatePaint();
                    invalidate();
                    break;
                }
            }
            return super.onTouchEvent(event);
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            return new ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
        }

        /**
         * Solve edit text delete(backspace) key detect, see<a href="http://stackoverflow.com/a/14561345/3790554">
         * Android: Backspace in WebView/BaseInputConnection</a>
         */
        private class ZanyInputConnection extends InputConnectionWrapper {
            public ZanyInputConnection(InputConnection target, boolean mutable) {
                super(target, mutable);
            }

            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
                if (beforeLength == 1 && afterLength == 0) {
                    // backspace
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }
        }
    }
}