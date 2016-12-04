package com.clam314.diagrammap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clam314 on 2016/11/23
 */

public class SimpleDiagramMap extends View {

    private final static float firstAngle = (float) (-Math.PI/2);
    private int colorMap;
    private int colorLineBase;
    private int colorText;
    private int mTextSize;
    private float mTotalValue;//最大刻度
    private List<Point> points;
    private List<Item> mItemList;
    private int itemNum = 0;

    public static class Item{
        float value;
        String itemName;
    }

    private class Point {
        float x,y;
    }

    public void setItemList(List<Item> items){
        if(items == null || items.size() == 0) return;
        mItemList = items;
        itemNum = mItemList.size();
        invalidate();
    }

    public void setItemList(List<Item> items, float maximum){
        mTotalValue = maximum;
        setItemList(items);
    }


    public SimpleDiagramMap(Context context) {
        super(context);
        init();
    }

    public SimpleDiagramMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SimpleDiagramMap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void init(){
        mTextSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,getResources().getDisplayMetrics()));
        mTotalValue = 10;
        colorLineBase = Color.DKGRAY;
        colorMap =  Color.BLUE;
        colorText = Color.BLACK;
        points = new ArrayList<>();
    }

    private void initView(Context context,AttributeSet attrs){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleDiagramMap);
        mTextSize = array.getDimensionPixelSize(R.styleable.SimpleDiagramMap_textSize, 10);
        mTotalValue = array.getFloat(R.styleable.SimpleDiagramMap_maximum, 10);
        colorLineBase = array.getColor(R.styleable.SimpleDiagramMap_lineColor, Color.DKGRAY);
        colorMap = array.getColor(R.styleable.SimpleDiagramMap_mapColor, Color.BLUE);
        colorText = array.getColor(R.styleable.SimpleDiagramMap_textColor,Color.BLACK);
        array.recycle();
        points = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mHeight = Math.min(getMeasuredHeight(), getMeasuredWidth());
        initPoint(itemNum, mHeight,getMeasuredHeight() < getMeasuredWidth());
        drawMap(canvas);
    }

    private void initPoint(int itemNum, float distance, boolean isX){
        float angle = (float) ((2*Math.PI)/itemNum);
        float radius ;
        if(isX){
            //水平方向较短,计算半径时只考虑字高
            radius = (distance - 4 * mTextSize)/2;
        }else {
            //垂直方向较短,计算半径时要考虑字长的总长度
            int maxLength = 0;
            for(Item item : mItemList){
                if(item.itemName.length() > maxLength){
                    maxLength = item.itemName.length();
                }
            }
            radius = distance/2 - (maxLength + 1)*mTextSize;
        }
        points.clear();
        for(int i = 0; i < itemNum ; i++){
            Point point = new Point();
            double mDegrees = firstAngle + i * angle;
            point.y = (float) (radius * Math.sin(mDegrees));
            point.x = (float)( radius * Math.cos(mDegrees));
            points.add(point);
        }
    }

    private void drawMap(Canvas canvas){
        Paint bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bPaint.setColor(colorLineBase);
        canvas.translate(canvas.getWidth()/2,canvas.getHeight()/2);

        //各顶点与中点的连线
        for(Point c : points){
            canvas.drawLine(0,0,c.x,c.y,bPaint);
        }

        //各顶点之间的连线
        for(int i = 0; i < itemNum && itemNum > 1;i++){
            Point item = points.get(i);
            Point itemNext =  i== itemNum -1 ? points.get(0) : points.get(i + 1);
            canvas.drawLine(item.x,item.y, itemNext.x, itemNext.y,bPaint);
            canvas.drawLine(item.x/2,item.y/2, itemNext.x/2, itemNext.y/2,bPaint);

//            if(i==0){//绘制最大刻度值
//                TextPaint vPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//                vPaint.setColor(colorText);
//                vPaint.setTextSize(mTextSize*2/3);
//                canvas.drawText(String.valueOf(mTotalValue), (item.x + itemNext.x)/2, (item.y + itemNext.y)/2, vPaint);
//            }
        }

        //绘制各项文字
        TextPaint tPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setColor(colorText);
        tPaint.setTextSize(mTextSize);
        for(int i = 0; i < itemNum; i++){
            Point point = points.get(i);
            Item item = mItemList.get(i);
            float mx = point.x;
            float my = point.y;
            float textLength = item.itemName.length() * mTextSize;
            if( mx < 1 && mx > -1){//因为计算精度的问题，不能准确在垂直方向，只能指定一个范围来捕捉
                mx = - textLength/2;
                if(my < 0){
                    my = my - mTextSize/2;
                }else {
                    my = my + mTextSize*3/2;
                }
            }else if(mx > 0){
                mx = mx + mTextSize/2;
                if(my > 0){
                    my = my + mTextSize/2;
                }
            }else if(mx < 0){
                mx = mx - textLength - mTextSize/2;
                if(my > 0){
                    my = my + mTextSize/2;
                }
            }
            canvas.drawText(item.itemName,mx,my,tPaint);
        }


        //绘制各项数据的分布图
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPath.setStyle(Paint.Style.FILL);
        paintPath.setColor(colorMap);
        paintPath.setAlpha(255/2);
        Path path = new Path();
        paint.setColor(colorMap);
        for(int i = 0; i < itemNum; i++){
            int position = i < itemNum - 1 ? i+1 : 0;
            Point point = points.get(i);
            Item item = mItemList.get(i);
            Point nextPoint = points.get(position);
            Item nextItem = mItemList.get(position);

            float scale = item.value/mTotalValue;
            float scaleNext =  nextItem.value/mTotalValue;

            canvas.drawLine(point.x * scale, point.y * scale, nextPoint.x * scaleNext, nextPoint.y * scaleNext, paint);
            if(i==0){
                path.moveTo(point.x * scale, point.y * scale);
            }
            path.lineTo(nextPoint.x * scaleNext, nextPoint.y * scaleNext);
        }
        canvas.drawPath(path,paintPath);
    }

}
