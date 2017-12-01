package com.example.crqi.flipover;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 回弹阻尼效果的简单实现,基于FrameLayout
 * 使用该ReboundEffectsView时应保证其只有且只有一个子View,唯一的独生子，我就叫它子View
 *
 * @author Cheny
 */
public class ReboundEffectsView extends FrameLayout {

    private View mPrinceView;// 子View
    private int mInitTop, mInitBottom;// 子View初始时上下坐标位置(相对父View,即当前ReboundEffectsView)
    private boolean isEndwiseSlide;// 是否纵向滑动
    private float mVariableY;// 手指上下滑动Y坐标变化前的Y坐标值

    private float oldPointY;
    private float viewHeight;
    private boolean isBotton;
    private boolean goBack;
    private float limitValue = 0.8f;


    public ReboundEffectsView(Context context) {
        this(context, null);
    }

    public ReboundEffectsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ReboundEffectsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setClickable(true);
    }

    /**
     * Touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (null != mPrinceView) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onActionDown(e);
                    break;
                case MotionEvent.ACTION_MOVE:
                    return onActionMove(e);
                case MotionEvent.ACTION_UP:
                    onActionUp(e);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onActionUp(e);// 当ACTION_UP一样处理
                    break;
            }
        }
        return super.onTouchEvent(e);
    }

    /**
     * 手指按下事件
     */
    private void onActionDown(MotionEvent e) {
        mVariableY = e.getY();
        oldPointY = mVariableY;
        /**
         * 保存mPrinceView的初始上下高度位置
         */
        if (mInitBottom == mInitTop && mInitBottom == 0) {
            mInitTop = mPrinceView.getTop();
            mInitBottom = mPrinceView.getBottom();
            viewHeight = mInitBottom - mInitTop;
        }
    }

    /**
     * 手指滑动事件
     */
    private boolean onActionMove(MotionEvent e) {
        float nowY = e.getY();
        float diff = (nowY - mVariableY);
        float sc = Math.min(1f, (float) (viewHeight - (nowY - oldPointY)) / (float) viewHeight);
        if ( isBotton && diff > 0) {
            // 已在底部禁止下滑
            return true;
        }
        if ((nowY - oldPointY) > viewHeight * limitValue) {
            // 触发到底部
            resetPrinceView(false);
            return true;
        }
        if (Math.abs(diff) > 0) {// 上下滑动
            // 移动子View的上下位置 ，恢复不用缩放，下滑含缩放，缩放是
            if (goBack) {
                mPrinceView.layout(mPrinceView.getLeft(), mPrinceView.getTop() + (int) diff, mPrinceView.getRight(),
                        mPrinceView.getBottom() + (int) diff);
                isBotton = false;
            } else {
                mPrinceView.setScaleY(sc);
                mPrinceView.setPivotY(mPrinceView.getBottom());
            }
            mVariableY = nowY;
            isEndwiseSlide = true;
            return true;// 消费touch事件
        }
        return super.onTouchEvent(e);
    }

    /**
     * 手指释放事件
     */
    private void onActionUp(MotionEvent e) {
        float nowY = e.getY();
        float diff = (nowY - mVariableY);
        if ((nowY - oldPointY) > viewHeight * limitValue) {
            // 触发到底部
            resetPrinceView(false);
            return;
        } else if (isEndwiseSlide) {// 是否为纵向滑动事件
            // 是纵向滑动事件，需要给子View重置位置
            resetPrinceView(true);
            isEndwiseSlide = false;
        }
    }

    /**
     * 回弹，重置子View初始的位置
     */
    private void resetPrinceView(boolean isTop) {
        if (isTop) {
            isBotton = false;
            mPrinceView.layout(mPrinceView.getLeft(),
                    mInitTop,
                    mPrinceView.getRight(),
                    mInitBottom);
            mPrinceView.setScaleY(1);
        } else {
            isBotton = true;
            goBack = true;
            mPrinceView.layout(mPrinceView.getLeft(),
                    (int) (mInitTop + (viewHeight - 150)),
                    mPrinceView.getRight(),
                    (int) (mInitBottom + (viewHeight - 150)));
            mPrinceView.setScaleY(1);
        }
    }


    /**
     * XML布局完成加载
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mPrinceView = getChildAt(0);// 获得子View，太子View
        }
    }
}
