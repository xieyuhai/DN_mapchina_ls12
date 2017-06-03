package view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xyh.bean.ProvinceItem;
import com.xyh.xieyuhai.dn_mapchina_ls12.PathParser;
import com.xyh.xieyuhai.dn_mapchina_ls12.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.content.ContentValues.TAG;

/**
 * Created by xieyuhai on 17/6/2.
 */

public class MapView extends View {

    List<ProvinceItem> list = new ArrayList<>();


    private RectF totalRectf;
    private int viewWidth;
    private int viewHeight;

    //    private int[] colorArray = {Color.argb(), Color.LTGRAY, Color.BLUE, Color.YELLOW};
    private Random random;

    private Paint mPaint;

    private float scale = 1.3f;
//    private float scaleWidth = 1.3f;
//    private float scaleHeight = 1.3f;

    private GestureDetectorCompat mGestureDetectorCompat;
    /**
     * 选中的省份
     */
    private ProvinceItem mSelectedProvinceItem;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    private void init(Context context) {
        mMinWidth = getResources().getDimensionPixelSize(R.dimen.map_min_width);
        mMinHeight = getResources().getDimensionPixelSize(R.dimen.map_min_height);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        random = new Random();

        loadDataThread.start();

        mGestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {

                handlerTouch(e.getX(), e.getY());

                return true;
            }
        });
    }

    /**
     * 处理触摸事件的方法
     *
     * @param x
     * @param y
     */
    private void handlerTouch(float x, float y) {
        if (list != null) {
            ProvinceItem temp = null;
            for (ProvinceItem item : list) {
                //除以放大系数
                if (item.isTouch((int) (x / scale), (int) (y / scale))) {
                    temp = item;
                }
            }
            if (temp != null) {
                mSelectedProvinceItem = temp;
                postInvalidate();
            }
        }
    }

    Thread loadDataThread = new Thread(new Runnable() {
        @Override
        public void run() {
            InputStream inputStream = getContext().getResources().openRawResource(R.raw.china);
//            InputStream inputStream = getContext().getResources().openRawResource(R.raw.taiwanhigh);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);
                Element documentElement = doc.getDocumentElement();
                NodeList items = documentElement.getElementsByTagName("path");


                float left = -1;
                float top = -1;
                float right = -1;
                float bottom = -1;

                int len = items.getLength();
                for (int i = 0; i < len; i++) {
                    Element ele = (Element) items.item(i);
                    String pathData = ele.getAttribute("android:pathData");
                    Log.e(TAG, "run: " + pathData);
                    Path path = PathParser.createPathFromPathData(pathData);


                    //适配 start
                    RectF rectF = new RectF();
                    path.computeBounds(rectF, true);
                    //取到最左边的path的极限值

                    left = left == -1 ? rectF.top : Math.min(rectF.left, left);
                    top = top == -1 ? rectF.top : Math.min(rectF.top, top);

                    bottom = bottom == -1 ? rectF.bottom : Math.max(rectF.bottom, bottom);
                    right = right == -1 ? rectF.right : Math.max(rectF.right, right);
                    //  end
                    ProvinceItem provinceItem = new ProvinceItem(path);
                    list.add(provinceItem);
                }

                totalRectf = new RectF(left, top, right, bottom);
                handler.sendEmptyMessage(1);
                Log.e(TAG, "run: " + len);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });


    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            if (list != null && !list.isEmpty()) {

                float scaleWidth = viewWidth / totalRectf.width();
                float scaleHeight = viewHeight / totalRectf.height();
                scale = Math.min(scaleWidth, scaleHeight);

                Log.e(TAG, "scaleWidth: " + scaleWidth);
                Log.e(TAG, "scaleHeight: " + scaleHeight);
                Log.e(TAG, "scale: " + scale);

                int totalNumber = list.size();

                for (int i = 0; i < totalNumber; i++) {
                    int color;
                    switch (i) {
                        case 1:
//                            color = colorArray[0];
                            color = Color.argb(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
                            break;
                        case 2:
//                            color = colorArray[1];
                            color = Color.argb(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
                            break;
                        case 3:
//                            color = colorArray[2];
                            color = Color.argb(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
                            break;
                        case 4:
//                            color = colorArray[3];
                            color = Color.argb(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
                            break;
                        default:
//                            color = colorArray[new Random().nextInt(4)];
                            color = Color.argb(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
                            break;
                    }
//                    给每一个省份设置颜色
                    list.get(i).setDrawColor(color);
                }


                postInvalidate();
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (list != null) {
//            保存
            canvas.save();
            canvas.scale(scale, scale);
            for (ProvinceItem item : list) {
                //绘制未被选中的
                if (item != mSelectedProvinceItem) {
                    item.draw(canvas, mPaint, false);
                }
            }
            if (mSelectedProvinceItem != null) {
                //绘制选中的
                mSelectedProvinceItem.draw(canvas, mPaint, true);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetectorCompat.onTouchEvent(event);
//        return super.onTouchEvent(event);
    }


    private int mMinWidth;
    private int mMinHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        viewWidth = width;
        viewHeight = height;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                viewWidth = Math.max(width, mMinWidth);
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                viewWidth = mMinWidth;
                break;
        }
        //得到参考值
        int compupteHeight = mMinHeight * viewWidth / mMinWidth;


        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                viewHeight = height;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                viewHeight = Math.max(mMinHeight, compupteHeight);
                break;
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));

    }

}
