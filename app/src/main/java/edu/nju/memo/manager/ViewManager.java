package edu.nju.memo.manager;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;


import java.lang.reflect.Field;

import edu.nju.memo.view.FloatBall;
import edu.nju.memo.view.FloatMenu;
import edu.nju.memo.view.FloatState;


public class ViewManager {

    private static final String TAG = "ViewManager";

    private FloatBall floatBall;

    private FloatMenu floatMenu;

    private WindowManager windowManager;

    private ClipboardManager clipboardManager;

    private static ViewManager manager;

    private LayoutParams floatBallParams;

    private LayoutParams floatMenuParams;

    private Context context;

    private long pressTime;

    private long upTime;



    // 私有化构造函数
    private ViewManager(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        pressTime = 0;
        upTime = 0;

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        clipboardManager=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        floatBall = new FloatBall(context);
        floatMenu = new FloatMenu(context);
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            float startX;
            float startY;
            float tempX;
            float tempY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressTime = System.currentTimeMillis();
                        startX = event.getRawX();
                        startY = event.getRawY();

                        tempX = event.getRawX();
                        tempY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = event.getRawX() - startX;
                        float y = event.getRawY() - startY;
                        //计算偏移量，刷新视图
                        floatBallParams.x += x;
                        floatBallParams.y += y;
                        floatBall.setFloatState(FloatState.DRAGING);
//                        floatBall.setDragState(true);
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        upTime = System.currentTimeMillis();
                        //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (endX < getScreenWidth() / 2) {
                            floatBall.setFloatState(FloatState.LEFT);
                            endX = 0;
                        } else {
                            endX = getScreenWidth() - floatBall.width;
                            floatBall.setFloatState(FloatState.RIGHT);
                        }
                        floatBallParams.x = (int) endX;
//                        floatBall.setDragState(false);
                        windowManager.updateViewLayout(floatBall, floatBallParams);
                        //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                        //否则继续传递，将事件交给OnClickListener函数处理
                        if (Math.abs(endX - tempX) > 6 && Math.abs(endY - tempY) > 6) {
                            return true;
                        }
                        break;
                }
                return false;
            }
        };
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 响应长按事件
                if (upTime - pressTime > 1000) {
                    windowManager.removeView(floatBall);
                    showFloatMenu();
                    floatMenu.startAnimation();
                    pressTime = upTime;
                }
                // 响应点击事件
                else {
                    Toast.makeText(context, "Saved to default category", Toast.LENGTH_SHORT).show();
                    pressTime = upTime;
                }

            }
        };
        floatBall.setOnTouchListener(touchListener);
        floatBall.setOnClickListener(clickListener);
    }

    //显示浮动小球
    public void showFloatBall() {
        if (floatBallParams == null) {
            floatBallParams = new LayoutParams();
            floatBallParams.width = floatBall.width;
            floatBallParams.height = floatBall.height - getStatusHeight();
            floatBallParams.gravity = Gravity.TOP | Gravity.START;
            floatBallParams.type = LayoutParams.TYPE_TOAST;
            floatBallParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
            floatBallParams.format = PixelFormat.RGBA_8888;
        }
        Log.d(TAG, "showFloatBall: x: " + floatBallParams.x + " y: " + floatBallParams.y);
        Log.d(TAG, "showFloatBall: width: " + floatBallParams.width + "width: " + floatBallParams.height);
        windowManager.addView(floatBall, floatBallParams);
    }

    //显示底部菜单
    private void showFloatMenu() {
        if (floatMenuParams == null) {
            floatMenuParams = new LayoutParams();
            floatMenuParams.width = getScreenWidth();
            floatMenuParams.height = getScreenHeight() - getStatusHeight();
            floatMenuParams.gravity = Gravity.BOTTOM;
            floatMenuParams.type = LayoutParams.TYPE_TOAST;
            floatMenuParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
            floatMenuParams.format = PixelFormat.RGBA_8888;
        }
        floatMenu.setContent(getClipBoardContent());
        windowManager.addView(floatMenu, floatMenuParams);
    }

    //隐藏底部菜单
    public void hideFloatMenu() {
        if (floatMenu != null) {
            windowManager.removeView(floatMenu);
        }
    }

    //获取ViewManager实例
    public static ViewManager getInstance(Context context) {
        if (manager == null) {
            manager = new ViewManager(context);
        }
        return manager;
    }

    //获取屏幕宽度
    private int getScreenWidth() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    //获取屏幕高度
    private int getScreenHeight() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    //获取状态栏高度
    private int getStatusHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object object = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }
    }

    private CharSequence getClipBoardContent() {
        CharSequence content = "default";
        if (clipboardManager != null && clipboardManager.getPrimaryClip().getItemAt(0).getText() != null) {
            Log.d(TAG, "getClipBoardContent: " + clipboardManager.getPrimaryClip().getItemAt(0).getText());
            content = clipboardManager.getPrimaryClip().getItemAt(0).getText();
        }
        return content;
    }
}
