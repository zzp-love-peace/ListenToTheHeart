package com.zzp.dtrip.view;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.huawei.hms.mlsdk.gesture.MLGesture;
import com.zzp.dtrip.util.TtsUtil;

import java.util.List;
/**
 * 手势识别所使用的View,不需要改动，已经使得该功能能够不重复说出手势
 */

public class HandGestureGraphic extends GraphicOverlay.Graphic {

    private final List<MLGesture> results;
//    private final List<MyMLGesture> myResults;

    private Paint circlePaint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint rectPaint;
    private final Rect rect;
//    private RectF rectF;//绘制圆角矩阵
    private static String record = "没有";

    /**
     * 修改线条属性
     * @param overlay
     * @param results
     */
    public HandGestureGraphic(GraphicOverlay overlay, List<MLGesture> results) {
        super(overlay);
        this.results = results;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
//        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setAntiAlias(true);


        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(5f);
        textPaint.setTextSize(100);

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        linePaint.setAntiAlias(true);

        rectPaint = new Paint();
//        rectPaint.setColor(Color.BLUE);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(50f);
//        rectPaint.setStrokeCap(Paint.Cap.ROUND);
        rectPaint.setStrokeJoin(Paint.Join.ROUND);
        LinearGradient linearGradient = new LinearGradient(100,100,200,100,Color.WHITE,Color.GRAY, Shader.TileMode.MIRROR);//设置渐变渐变区域属性
        rectPaint.setShader(linearGradient);//设置线性渐变
        rectPaint.setAntiAlias(true);

        rect = new Rect();
    }


//
//        public HandGestureGraphic(GraphicOverlay overlay, List<MyMLGesture> myResults) {
//        super(overlay);
//        this.myResults = myResults;
//
//        circlePaint = new Paint();
//        circlePaint.setColor(Color.RED);
////        circlePaint.setStyle(Paint.Style.FILL);
//        circlePaint.setAntiAlias(true);
//
//        circlePaint.setStyle(Paint.Style.STROKE);//尝试
//        circlePaint.setStrokeWidth(5f);
//
//        textPaint = new Paint();
//        textPaint.setColor(Color.YELLOW);
//        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setStrokeWidth(5f);
//        textPaint.setTextSize(100);
//
//        linePaint = new Paint();
//        linePaint.setColor(Color.GREEN);
//        linePaint.setStyle(Paint.Style.STROKE);
//        linePaint.setStrokeWidth(4f);
//        linePaint.setAntiAlias(true);
//
//        rectPaint = new Paint();
//        rectPaint.setColor(Color.BLUE);
//        rectPaint.setStyle(Paint.Style.STROKE);
//        rectPaint.setStrokeWidth(5f);
//        rectPaint.setAntiAlias(true);
//
////        rect = new Rect();
//
//        rectF = new RectF();//自定义圆角矩形
////        rectF.left = 50;
////        rectF.right = 250;
////        rectF.top = 200;
////        rectF.bottom = 300;
//    }





    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < results.size(); i++) {
            MLGesture mlGesture = results.get(i);

            canvas.drawRect(rect, rectPaint);
            Rect rect = translateRect(mlGesture.getRect());
            if (rect.right < rect.left) {
                int x = rect.left;
                rect.left = rect.right;
                rect.right = x;
            }

//            LinearGradient linearGradient = new LinearGradient(100,100,200,100,Color.WHITE,Color.GRAY,Shader.TileMode.CLAMP);
//            rectPaint.setShader(linearGradient);
            canvas.drawRect(rect, rectPaint);
            // 注意。如果绘制时坐标点需要与原图一一对应，需要使用translateX和translateY进项坐标转换
            canvas.drawText(getChineseDescription(mlGesture.getCategory()),
                    translateX((mlGesture.getRect().left + mlGesture.getRect().right) / 2f),//无法更改现有图形
                    translateY((mlGesture.getRect().top + mlGesture.getRect().bottom) / 2f),
                    textPaint);

        }
    }

//
//    @Override
//    public void draw(Canvas canvas) {
//        for (int i = 0; i < myResults.size(); i++) {
//            MyMLGesture myMLGesture = myResults.get(i);
//
//            canvas.drawRoundRect(rectF,10,10, rectPaint);
////            canvas.drawRect(rect, circlePaint);尝试
////            canvas.drawRect(rectF,rectPaint);
//            RectF rectF = translateRectF(myMLGesture.getRectF());
//            if (rectF.right < rectF.left) {
//                float x = rectF.left;
//                rectF.left = rectF.right;
//                rectF.right = x;
//            }
//            canvas.drawRoundRect(rectF,10,10, rectPaint);
//            // 注意。如果绘制时坐标点需要与原图一一对应，需要使用translateX和translateY进项坐标转换
//            canvas.drawText(getChineseDescription(myMLGesture.getCategory()),
//                    translateX((myMLGesture.getRectF().left + myMLGesture.getRectF().right) / 2f),//无法更改现有图形
//                    translateY((myMLGesture.getRectF().top + myMLGesture.getRectF().bottom) / 2f),
//                    textPaint);
//        }
//
//    }


    private String getChineseDescription(int gestureCategory) {
        String chineseDescription;
        switch (gestureCategory) {
            case MLGesture.ONE:
                chineseDescription = "数字1";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.SECOND:
                chineseDescription = "数字2";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.THREE:
                chineseDescription = "数字3";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.FOUR:
                chineseDescription = "数字4";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.FIVE:
                chineseDescription = "数字5";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.SIX:
                chineseDescription = "数字6";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.SEVEN:
                chineseDescription = "数字7";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.EIGHT:
                chineseDescription = "数字8";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.NINE:
                chineseDescription = "数字9";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.DISS:
                chineseDescription = "差评";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.FIST:
                chineseDescription = "握拳";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.GOOD:
                chineseDescription = "点赞";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.HEART:
                chineseDescription = "单手比心";
                play(chineseDescription);
                record = chineseDescription;
                break;
            case MLGesture.OK:
                chineseDescription = "确认";
                play(chineseDescription);
                record = chineseDescription;
                break;
            default:
                chineseDescription = "其他手势";
                break;

        }
        return chineseDescription;
    }

    public Rect translateRect(Rect rect) {
        float left = translateX(rect.left);
        float right = translateX(rect.right);
        float bottom = translateY(rect.bottom);
        float top = translateY(rect.top);
        if (left > right) {
            float size = left;
            left = right;
            right = size;
        }
        if (bottom < top) {
            float size = bottom;
            bottom = top;
            top = size;
        }
        return new Rect((int) left, (int) top, (int) right, (int) bottom);
    }

//
//    /**
//     * 改变矩阵图形
//     * @param rectF
//     * @return
//     */
//    public RectF translateRectF(RectF rectF) {
//        float left = translateX(rectF.left);
//        float right = translateX(rectF.right);
//        float bottom = translateY(rectF.bottom);
//        float top = translateY(rectF.top);
//        if (left > right) {
//            float size = left;
//            left = right;
//            right = size;
//        }
//        if (bottom < top) {
//            float size = bottom;
//            bottom = top;
//            top = size;
//        }
//        return new RectF((int) left, (int) top, (int) right, (int) bottom);
//    }


    public void play(String chineseDescription){
        if(!record.equals(chineseDescription)) {
            new Thread(() -> TtsUtil.INSTANCE.playString(chineseDescription)).start();
            try {
                Thread.sleep(1000); //设置手势识别采样间隔（语音播报时间间隔）
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}