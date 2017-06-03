package com.xyh.bean;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * Created by xieyuhai on 17/6/2.
 */

public class ProvinceItem {

    /**
     * 一个省份的路径
     */
    private Path path;

    private int drawColor;

    public ProvinceItem(Path path) {
        this.path = path;
    }

    /**
     * @param canvas
     * @param paint
     * @param isSelected 是否选中
     */
    public void draw(Canvas canvas, Paint paint, boolean isSelected) {
        if (isSelected) {
            //绘制背景
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8, 0, 0, 0xffffff);
            canvas.drawPath(path, paint);
            //绘制省份
            paint.clearShadowLayer();
            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(path, paint);
        } else {
            //绘制未选中 的内容

            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(path, paint);

//          绘制边界线
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xFFD0E8F4);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 点击位置
     * 是否属于某一省份
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(int x, int y) {
        RectF rectf = new RectF();

        path.computeBounds(rectf, true);
//        boolean contains = rectf.contains(x, y);
        Region region = new Region();
        region.setPath(path, new Region((int) rectf.left, (int) rectf.top, (int) rectf.right, (int) rectf.bottom));


        return region.contains(x, y);
    }


    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getDrawColor() {
        return drawColor;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }


}
