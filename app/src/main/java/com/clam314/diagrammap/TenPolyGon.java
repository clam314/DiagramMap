package com.clam314.diagrammap;

import android.content.Context;
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

public class TenPolyGon extends View {
    private final String[] itemName = new String[]{"转速","熄火","加速","超速","怠速","报警","区域超速","滑行","深度","车速"};

    private Coords[] coordses = new Coords[10];
    private int mWitch;
    private int mHeight = 150;
    private float radius;
    private final int itemNum = 10;
    private float angle = (float) ((2*Math.PI)/itemNum);
    private final float firstAngle = (float) (-Math.PI/2);
    private int colorLineData = Color.BLUE;
    private int colorLineBase = Color.GREEN;
    private int colorPath = Color.parseColor("#FF008A");
    private float mTextSize = 10;
    private List<Item> mItemList;

    public static class Item{
        private float scale;
        private String itemName;
    }

    public void setmItemList(List<Item> items){
        if(items == null || items.size() == 0) return;
        mItemList = items;
        invalidate();
    }

    private void testItem(){
        mItemList = new ArrayList<>();
        for(String s: itemName){
            Item item = new Item();
            item.itemName = s;
            item.scale = (float) Math.random()*10;
            mItemList.add(item);
        }
    }

    private class Coords{
        float x,y;
    }

    public TenPolyGon(Context context) {
        super(context);
        init();
    }

    public TenPolyGon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TenPolyGon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mHeight,getResources().getDisplayMetrics()));
        mTextSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTextSize,getResources().getDisplayMetrics()));
        radius = (mHeight - 2 * mTextSize)/2;
        testItem();
        initCoords();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas){
        canvas.save();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(colorLineBase);
        canvas.translate(canvas.getWidth()/2,canvas.getHeight()/2);

        for(Coords c : coordses){
            canvas.drawLine(0,0,c.x,c.y,paint);
        }

        for(int i = 0; i < 10 ;i++){
            if(i == itemNum - 1){
                canvas.drawLine(coordses[i].x,coordses[i].y,coordses[0].x,coordses[0].y,paint);
                canvas.drawLine(coordses[i].x/2,coordses[i].y/2,coordses[0].x/2,coordses[0].y/2,paint);
            }else {
                canvas.drawLine(coordses[i].x,coordses[i].y,coordses[i+1].x,coordses[i+1].y,paint);
                canvas.drawLine(coordses[i].x/2,coordses[i].y/2,coordses[i+1].x/2,coordses[i+1].y/2,paint);
            }
        }

        TextPaint tPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tPaint.setTextSize(mTextSize);
        for(int i = 0; i < 10; i++){
            float mx = coordses[i].x;
            float my = coordses[i].y;
            float textLength = itemName[i].length() * mTextSize;
            if( mx < 1 && mx > -1){
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
            canvas.drawText(itemName[i],mx,my,tPaint);
        }

        drawData(canvas,mItemList);
    }

    private void drawData(Canvas canvas, List<Item> items){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPath.setStyle(Paint.Style.FILL);
        paintPath.setColor(colorLineData);
        paintPath.setAlpha(255/2);
        Path path = new Path();
        paint.setColor(colorLineData);
        if(items == null || items.size() == 0) return;
        for(int i = 0; i < 10; i++){
            Item item = items.get(i);
            if(i < items.size() -1){
                Item item1 = items.get(i+1);
                float scale = item.scale;
                float scale1 = item1.scale;
                canvas.drawLine(coordses[i].x/scale,coordses[i].y/scale,coordses[i+1].x/scale1,coordses[i+1].y/scale1,paint);
                if(i==0){
                    path.moveTo(coordses[i].x/scale,coordses[i].y/scale);
                }
                path.lineTo(coordses[i+1].x/scale1,coordses[i+1].y/scale1);
            }else {
                Item item1 = items.get(0);
                float scale = item.scale;
                float scale1 = item1.scale;
                canvas.drawLine(coordses[i].x/scale,coordses[i].y/scale,coordses[0].x/scale1,coordses[0].y/scale1,paint);
                path.lineTo(coordses[0].x/scale1,coordses[0].y/scale1);
            }
        }
        canvas.drawPath(path,paintPath);
    }

    private void initCoords(){
        for(int i = 0; i < 10 ; i++){
            float mAngle = i * angle;
            getPositon(mAngle,i);
        }
    }

    private void getPositon(double degrees,int item){
        Coords coords = new Coords();
        double mDegrees = firstAngle + degrees;
        coords.y = (float) (radius * Math.sin(mDegrees));
        coords.x = (float)( radius * Math.cos(mDegrees));
        coordses[item] = coords;
    }
}
