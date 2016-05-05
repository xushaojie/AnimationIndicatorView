package com.example.xushao.indicatorview;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * 指示器圆点
 */
public class IndicatorDot {

    private Rect mCheckedRect;// 选中状态位置信息
    private Rect mUnCheckedRect;// 未选中状态位置信息
    private Rect mAnimationRect;// 动画过程位置信息

    private Drawable mItemIcon;

    private boolean mSelected = false;

    public IndicatorDot() {
        mCheckedRect = new Rect();
        mUnCheckedRect = new Rect();
        mAnimationRect = new Rect();
    }

    public Rect getCheckedRect() {
        return mCheckedRect;
    }

    public Rect getUnCheckedRect() {
        return mUnCheckedRect;
    }

    public Drawable getItemIcon() {
        return mItemIcon;
    }

    public void setItemIcon(Drawable itemIcon) {
        mItemIcon = itemIcon;
    }

    public void setSelectState(boolean selected) {
        mSelected = selected;
        if (selected) {
            mItemIcon.setBounds(mCheckedRect);
        } else {
            mItemIcon.setBounds(mUnCheckedRect);
        }
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setScale(float r) {
        mAnimationRect.left = (int) (mUnCheckedRect.left + (mCheckedRect.left - mUnCheckedRect.left) * r);
        mAnimationRect.top = (int) (mUnCheckedRect.top + (mCheckedRect.top - mUnCheckedRect.top) * r);
        mAnimationRect.right = (int) (mUnCheckedRect.right + (mCheckedRect.right - mUnCheckedRect.right) * r);
        mAnimationRect.bottom = (int) (mUnCheckedRect.bottom + (mCheckedRect.bottom - mUnCheckedRect.bottom) * r);
        mItemIcon.setBounds(mAnimationRect);
    }

}
