package com.zzp.dtrip.view;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.huawei.hms.mlsdk.common.LensEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * 手势识别所使用的View,不需要改动
 */
public class GraphicOverlay extends View {
    private final Object mLock = new Object();

    private int mPreviewWidth;

    private float mWidthScaleFactor = 1.0f;

    private int mPreviewHeight;

    private float mHeightScaleFactor = 1.0f;

    private int mFacing = LensEngine.BACK_LENS;

    private Set<Graphic> mGraphics = new HashSet<>();
    /**
     * 要在图形叠加层中呈现的自定义图形对象的基类。 子类
     * 并实现 {@link Graphic#draw(Canvas)} 方法来定义
     * 图形元素。 使用 {@link GraphicOverlay#add(Graphic)} 将实例添加到叠加层。
     */
    public abstract static class Graphic {
        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        /**
         * 在提供的画布上绘制图形。 绘图应使用以下方法
         * 转换为绘制图形的视图坐标：
         * <ol>
         * <li>{@link Graphic#scaleX(float)} 和 {@link Graphic#scaleY(float)} 调整大小
         * 提供的从预览比例到视图比例的值。</li>
         * <li>{@link Graphic#translateX(float)} 和 {@link Graphic#translateY(float)} 调整
         * 从预览坐标系到视图坐标系的坐标。</li>
         * </ol>
         *
         * @param canvas 绘制画布
         */
        public abstract void draw(Canvas canvas);

        /**
         * 将所提供值的水平值从预览比例调整到视图
         * 规模。
         */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }
        public float unScaleX(float horizontal) {
            return horizontal / mOverlay.mWidthScaleFactor;
        }
        /**
         * 将所提供值的垂直值从预览比例调整为视图比例。
         */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }
        public float unScaleY(float vertical) {
            return vertical / mOverlay.mHeightScaleFactor;
        }
        /**
         * 调整 x 坐标从预览坐标系到视图坐标
         * 系统。
         */
        public float translateX(float x) {
            if (mOverlay.mFacing == LensEngine.FRONT_LENS) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * 将 y 坐标从预览坐标系调整为视图坐标
         * 系统。
         */
        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 /**
     * 从叠加层中删除所有图形。
     */
    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }

    /**
     * 增加一个图形到叠加层
     */
    public void add(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

    /**
     * 从叠加层移除一个图形
     */
    public void remove(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }

    /**
     * 设置相机属性的大小和朝向，告知如何变换
     * 图像坐标稍后。
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }
    /**
     * 使用其关联的图形对象绘制叠加层。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }
            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }

        }
    }
}
