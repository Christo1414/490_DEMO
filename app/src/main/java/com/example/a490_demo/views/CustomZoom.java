// Copyright © 2016-2017 Shawn Baker using the MIT License.
package com.example.a490_demo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import com.example.a490_demo.classes.ConnectWithPi;
import com.example.a490_demo.classes.UiInfo;
import com.example.a490_demo.classes.UiZoom;
import com.example.a490_demo.views.ImageContainer;

public class CustomZoom extends TextureView {
    // private GestureDetectorCompat swirlDetector; //panDetector;
    private int fitWidth, fitHeight;
    private PointF fitZoom = new PointF(1, 1);
    private float zoom = 1;
    private float minZoom = 0.1f;
    private float maxZoom = 10;


    //******************************************************************************
    // CustomZoom
    //******************************************************************************
    public CustomZoom(Context context) {
        super(context);
        initialize(context);
    }

    //******************************************************************************
    // CustomZoom
    //******************************************************************************
    public CustomZoom(Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    //******************************************************************************
    // CustomZoom
    //******************************************************************************
    public CustomZoom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    //******************************************************************************
    // initialize
    //******************************************************************************
    private void initialize(Context context) {
        // create the gesture recognizers
        // swirlDetector = new GestureDetectorCompat(context, new SwirlListener());
        // Log.info("initialized nothing");
    }

    //******************************************************************************
    // zoomIn
    //******************************************************************************
    public void zoomIn() {
        // Log.info("in zoomIn");
        zoom += 0.1;
        setZoom(zoom, getWidth() / 2, getHeight() / 2);

        if (zoom >= 10) {
            // Log.info("in zoomIn if case");
            zoom = 10;
            // swirlZoom(true);
        }
        // Log.info("exiting zoomIn");

        /** CALL UI FUNCTION HERE */
        ImageContainer.resizeImage(true);
        UiZoom.Update_Zoombar(zoom);
    }

    //******************************************************************************
    // zoomOut
    //******************************************************************************
    public void zoomOut() {
        // Log.info("in zoomOut");
        zoom -= 0.1;
        setZoom(zoom, getWidth() / 2, getHeight() / 2);

        if (zoom >= 10) {
            // Log.info("in zoomOut if case");
            zoom = 10;
            // swirlZoom(true);
        }
        // Log.info("exiting zoomOut");

        /** CALL UI FUNCTION HERE */
        ImageContainer.resizeImage(false);
        UiZoom.Update_Zoombar(zoom);
    }

    //******************************************************************************
    // onTouchEvent
    //******************************************************************************
    // @Override
    // public boolean onTouchEvent(MotionEvent event)
    // {
    // 	// return panDetector.onTouchEvent(event) || scaleDetector.onTouchEvent(event) || super.onTouchEvent(event);
    //     Log.info("in onTouchEvent");
    //     return super.onTouchEvent(event);
    // }

    //******************************************************************************
    // setZoomRange
    // //******************************************************************************
    public void setZoomRange(float minZoom, float maxZoom)
    {
        // Log.info("in setZoomRange");
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    //******************************************************************************
    // setVideoSize
    //******************************************************************************
    public void setVideoSize(int videoWidth, int videoHeight) {
        // Log.info("in setVideoSize");
        // get the aspect ratio
        float aspectRatio = (float) videoHeight / videoWidth;

        // get the view size
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // get the fitted size
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            fitWidth = viewWidth;
            fitHeight = (int) (viewWidth * aspectRatio);
        } else {
            fitWidth = (int) (viewHeight / aspectRatio);
            fitHeight = viewHeight;
        }

        // get the fitted zoom
        fitZoom.x = (float) fitWidth / viewWidth;
        fitZoom.y = (float) fitHeight / viewHeight;

        // clear the transform
        setZoom(1, 0, 0);
    }

    //******************************************************************************
    // setZoom
    //******************************************************************************
    public void setZoom(float newZoom, PointF newPan) {
        // Log.info("in setZoom (two params)");
        zoom = newZoom;
        setTransform();
    }

    //******************************************************************************
    // setZoom
    //******************************************************************************
    public void setZoom(float newZoom, float newPanX, float newPanY) {
        // Log.info("in setZoom (three params)");
        setZoom(newZoom, new PointF(newPanX, newPanY));
    }

    //******************************************************************************
    // setTransform
    //******************************************************************************
    private void setTransform() {
        // Log.info("in setTransform");
        // get the view size
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // scale relative to the center
        Matrix transform = new Matrix();
        transform.postScale(fitZoom.x * zoom, fitZoom.y * zoom, viewWidth / 2, viewHeight / 2);

        // add the panning
        // if (pan.x != 0 || pan.y != 0)
        // {
        //     transform.postTranslate(pan.x, pan.y);
        // }

        // set the transform
        setTransform(transform);
        invalidate();
        // Log.info("exiting setTransform");
    }


    ////////////////////////////////////////////////////////////////////////////////
    // SwirlListener
    ////////////////////////////////////////////////////////////////////////////////
    // public class SwirlListener extends GestureDetector.SimpleOnGestureListener
    // {
        // @Override
        // public boolean onDown(MotionEvent e)
        // {
        //     Log.info("in SwirlListener onDown");
        //     return true;
        // }

        // @Override
        // public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        // {
        //     Log.info("in SwirlListener onScroll");
        //     zoom += 0.1;
        //     setZoom(zoom, getWidth() / 2, getHeight() / 2);

        //     if(zoom >= 10) {
        //         Log.info("in SwirlListener if case");
        //         zoom = 10;
        //         // swirlZoom(true);
        //     }
        //     Log.info("exiting SwirlListener onScroll");
        //     return false;
        // }

        // @Override
        // public boolean onDoubleTap(MotionEvent e)
        // {
        //     Log.info("in SwirlListener onDoubleTap");
		// 	setZoom(1, 0, 0);
        //     return true;
        // }
    // }
}
