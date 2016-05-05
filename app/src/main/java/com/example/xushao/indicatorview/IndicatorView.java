package com.example.xushao.indicatorview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 指示器视图类
 * <br>功能详细描述:该指示器存在默认间隔和最大显示长度。
 * 当达到最大显示长度之前，项与项之间使用默认间隔；当到达最大显示长度，项与项之间的间隔会相对减少.
 * <p/>
 * 属性动画的运行机制是通过不断地对值进行操作来实现的，而初始值和结束值之间的动画过渡就是由ValueAnimator这个类来负责计算的.
 */
public class IndicatorView extends ViewGroup {

    private static final int DOT_SCALE_DURATION_TIME = 720;
    private static final int DOT_SCALE_START_DELAY = 100;// 缩放变大的指示器动画与缩放变小的指示器动画之间的时间间隔

    private boolean mIsAnimate = false;
    private TimeInterpolator mBounceInterpolator;
    private AnimatorSet mAnimatorSet;// 将动画效果组合起来使用

    private List<IndicatorDot> mDots = new ArrayList<>();
    private int mCount;

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBounceInterpolator = new BounceInterpolator();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initDotsLayout(left, top, right, bottom);
    }

    private void initDotsLayout(int left, int top, int right, int bottom) {
        if (mDots.isEmpty()) {
            return;
        }
        int count = mDots.size();
        int dotSize = getResources().getDimensionPixelSize(R.dimen.indicator_dot_size);// 每个点的尺寸
        int w = right - left;
        int h = bottom - top;
        int maxDisplayW = w * 3 / 4;// 最大显示长度
        int defaultVal = dotSize;// 项与项之间的默认间隔
        int firstDotL;// 首个项的初始位置
        // 初步预算显示长度
        int displayW = dotSize * count + defaultVal * (count - 1);
        // 超出最大显示长度的情况
        if (displayW > maxDisplayW) {
            firstDotL = (w - maxDisplayW) / 2;
            if (count > 1) {
                defaultVal = (maxDisplayW - dotSize * count) / (count - 1);
            }
        } else {
            firstDotL = (w - displayW) / 2;
        }
        int dotL;
        int dotT = (h - dotSize) / 2;
        int dotR;
        int dotB = dotT + dotSize;
        for (int i = 0; i < count; i++) {
            dotL = (dotSize + defaultVal) * i + firstDotL;
            dotR = dotL + dotSize;
            IndicatorDot dot = mDots.get(i);
            dot.getCheckedRect().set(dotL, dotT, dotR, dotB);
            dot.getUnCheckedRect().set(scaleRect(dot.getCheckedRect()));
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDotsLayout(0, 0, w, h);
        int count = mDots.size();
        for (int i = 0; i < count; i++) {
            IndicatorDot dot = mDots.get(i);
            dot.setSelectState(dot.isSelected());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = mDots.size();
        for (int i = 0; i < count; i++) {
            if (mDots.get(i).getItemIcon().getBounds().left == 0) {
                continue;
            }
            mDots.get(i).getItemIcon().draw(canvas);
        }
    }

    /**
     * 创建指示器
     */
    private IndicatorDot createDot() {
        Drawable icon = getResources().getDrawable(R.drawable.ic_indicator_dot);
        IndicatorDot dot = new IndicatorDot();
        dot.setItemIcon(icon);
        return dot;
    }

    /**
     * ValueAnimator:内部使用一种时间循环的机制来计算值与值之间的动画过渡，我们只需要将初始值和结束值提供给ValueAnimator，
     * 并且告诉它动画所需运行的时长，那么ValueAnimator就会自动帮我们完成从初始值平滑地过渡到结束值这样的效果。
     * 除此之外，ValueAnimator还负责管理动画的播放次数、播放模式、以及对动画设置监听器等.
     */
    public void onPageChanged(final int newPage, final int oldPage) {
        resetDot();
        initDotsLayout(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mDots.size(); i++) {
            mDots.get(i).getUnCheckedRect().set(scaleRect(mDots.get(i).getCheckedRect()));
            mDots.get(i).setSelectState(false);
        }
        // 默认
        if (newPage == oldPage && newPage == 0) {
            mDots.get(0).setSelectState(true);
            invalidate();
        } else {
            if (mIsAnimate) {
                mAnimatorSet.end();
            }
            ValueAnimator dotShrink = new ValueAnimator();
            dotShrink.setFloatValues(0f, 1f);
            dotShrink.setDuration(DOT_SCALE_DURATION_TIME);
            dotShrink.setInterpolator(mBounceInterpolator);
            dotShrink.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float r = (Float) animation.getAnimatedValue();
                    mDots.get(oldPage).setScale(1 - r);// 缩小
                    invalidate();
                }
            });
            dotShrink.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    for (int i = 0; i < mDots.size(); i++) {
                        mDots.get(i).setSelectState(false);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mDots.get(oldPage).setSelectState(false);// 缩小
                }
            });
            // 指示器收缩的属性动画，此ValueAnimator的作用是让oldPage屏处的指示器和newPage屏出的指示器不同步的动画
            ValueAnimator dotExpand = new ValueAnimator();
            dotExpand.setFloatValues(0f, 1.0f);
            dotExpand.setDuration(DOT_SCALE_DURATION_TIME);
            dotExpand.setInterpolator(mBounceInterpolator);
            dotExpand.setStartDelay(DOT_SCALE_START_DELAY);// 两个指示器的点放大和缩小之间的延迟
            dotExpand.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float r = (Float) animation.getAnimatedValue();
                    mDots.get(newPage).setScale(r);// 放大
                    invalidate();
                }
            });
            dotExpand.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    for (int i = 0; i < mDots.size(); i++) {
                        mDots.get(i).setSelectState(false);
                    }
                    mDots.get(newPage).setSelectState(true);// 放大
                    invalidate();
                }
            });
            // 动画集
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(dotShrink, dotExpand);
            mAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimate = false;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mIsAnimate = true;
                }
            });
            mAnimatorSet.start();
        }
    }

    private void resetDot() {
        mDots.clear();
        for (int i = 0; i < mCount; i++) {
            IndicatorDot dot = createDot();
            mDots.add(dot);
        }
    }

    private Rect scaleRect(Rect rect) {
        float scale = 0.65f;
        final int left = rect.left;
        final int top = rect.top;
        final int width = rect.width();
        final int height = rect.height();
        int newW = (int) (width * scale);
        int newH = (int) (height * scale);
        int newL = left + (width - newW) / 2;
        int newT = top + (height - newH) / 2;
        return new Rect(newL, newT, newL + newW, newT + newH);
    }

    public void setCount(int count) {
        mCount = count;
    }

}
