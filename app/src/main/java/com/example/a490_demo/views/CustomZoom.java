// Copyright © 2016-2017 Shawn Baker using the MIT License.
package com.example.a490_demo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.Image;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.util.Log;
import android.view.TextureView;

import androidx.core.view.GestureDetectorCompat;

import com.example.a490_demo.classes.ConnectWithPi;
import com.example.a490_demo.classes.UiInfo;
import com.example.a490_demo.classes.UiZoom;
import com.example.a490_demo.views.ImageContainer;

public class CustomZoom extends TextureView {
    private ScaleGestureDetector scaleDetector;
    private GestureDetectorCompat doubleTapDetector;
    private int fitWidth, fitHeight;
    private PointF fitZoom = new PointF(1, 1);
    private float zoom = 3.0f;
    private float minZoom = 0.1f;
    private float maxZoom = 10;
    // private boolean isCustom = true;
    private int zoomType = 1;

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
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        doubleTapDetector = new GestureDetectorCompat(context, new DoubleTapListener());
        //  Log.info("initialized nothing");
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
        // UiZoom.Update_Zoombar(zoom);
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
        // UiZoom.Update_Zoombar(zoom);
    }

    //******************************************************************************
    // onTouchEvent
    //******************************************************************************
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	// return panDetector.onTouchEvent(event) || scaleDetector.onTouchEvent(event) || super.onTouchEvent(event);
        // Log.info("in onTouchEvent");
        if(this.zoomType == 1) {
            Log.d("shantag", "onTouchEvent if");
            return super.onTouchEvent(event);
        } else if (this.zoomType == 2) {
            // Log.d("shantag", "onTouchEvent else");
            return scaleDetector.onTouchEvent(event) || super.onTouchEvent(event);
        } else {
            return doubleTapDetector.onTouchEvent(event) || super.onTouchEvent(event);
        }
    }

    //******************************************************************************
    // setZoomType
    // //******************************************************************************
    public void setZoomType(int zoomType)
    {
        Log.d("shantag", "setZoomType to " + Integer.toString(zoomType));
        this.zoomType = zoomType;
    }

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
        setZoom(3.0f, 0, 0);
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
        Log.d("shantag", "in setTransform");
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
        Log.d("shantag","exiting setTransform");
    }


    ////////////////////////////////////////////////////////////////////////////////
    // ScaleListener
    ////////////////////////////////////////////////////////////////////////////////
    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener
    {
        private float startZoom = 3.0f;
        private PointF center = new PointF();

        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            // Log.info("ScaleListener onScale");
            float newZoom = startZoom * detector.getScaleFactor();
            // newZoom = Math.max(minZoom, Math.min(newZoom, maxZoom));
            Log.d("shantag", "newzoom: " + Float.toString(newZoom) + " zoom: " + Float.toString(zoom));

            ImageContainer.resizeImageValue(newZoom);
            zoom = newZoom;
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            // Log.info("ScaleListener onScaleBegin");
            startZoom = zoom;
            center.x = getWidth() / 2;
            center.y = getHeight() / 2;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            // Log.info("ScaleListener onScaleEnd");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // DoubleTapListener
    ////////////////////////////////////////////////////////////////////////////////
    private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
        private PointF center = new PointF();

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("shantag", "single tap");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float thisX = e.getX();
            center.x = getWidth() / 2;
            Log.d("shantag", "double tap babey!!! " + Float.toString(thisX) + " center: " + Float.toString(center.x));
            if(thisX >= center.x) {
                ImageContainer.resizeImage(true);
            } else {
                ImageContainer.resizeImage(false);
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d("shantag", "double tap event");
            return false;
        }
    }
}
