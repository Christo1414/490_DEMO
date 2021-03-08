package com.example.a490_demo.classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;


import com.example.a490_demo.activities.VideoFragment;
import com.example.a490_demo.views.CustomZoom;
import com.example.a490_demo.views.ImageContainer;


public class ConnectWithPi extends AppCompatActivity {

    private static final String DEBUG_TAG = "Hello World";
    private static final String INFO_TAG = "Displacement Comparison";
    // Gesture detector variables
    private GestureDetectorCompat mDetector;

    private int THRESH = 20;
    private final int EVENT_BUFFER_SIZE = 13;
    public int EVENT_BUFFER;
    private float[][] XY_COORDS = new float [EVENT_BUFFER_SIZE][2];

    /** for MagAngle */
    private float magnitudeInitial = 0;
    private float magnitudeSample = 0;
    private float magnitudeFinal = 0;
    private float angleInitial = 0;
    private float angleSample = 0;
    private float angleFinal = 0;
    private boolean isZoomIn = false;
    private boolean isAngleIncreasing = false;
    private boolean isMagnitudeIncreasing = false;
    private int zoomType;
    private boolean Zooming;
    private int circleCounter;
    boolean isZoomIN1;
    boolean isZoomIN2;
    boolean isZoomOUT1;
    boolean isZoomOUT2;

    private String ipAddress; //
    // private CustomZoom zoomer = new CustomZoom(this);

    public ConnectWithPi(String ipAdrr){
        this.ipAddress = ipAdrr;
        this.EVENT_BUFFER = 0;
        //establishConnection();
    }


    public void action_down(float xcoord, float ycoord) {
        reset();
        this.XY_COORDS[0][0] = xcoord;
        this.XY_COORDS[0][1] = ycoord;
        //Log.info("Event count: " + EVENT_BUFFER + ". Stored XY Coords" + Arrays.deepToString(XY_COORDS));
    }
    public int action_move(float xcoord, float ycoord, CustomZoom zoomer) {
        if (this.EVENT_BUFFER == 0) {
            this.XY_COORDS = new float[this.EVENT_BUFFER_SIZE][2];
            //Log.info("Event count: " + EVENT_BUFFER + ". Stored XY Coords" + Arrays.deepToString(XY_COORDS));
        }
        this.XY_COORDS[this.EVENT_BUFFER][0] = xcoord;
        this.XY_COORDS[this.EVENT_BUFFER][1] = ycoord;
        this.EVENT_BUFFER = this.EVENT_BUFFER + 1;

        //Log.info("Event count: " + EVENT_BUFFER + ". Stored XY Coords" + Arrays.deepToString(XY_COORDS));

        if (this.EVENT_BUFFER < this.EVENT_BUFFER_SIZE){
            this.XY_COORDS[this.EVENT_BUFFER][0] = xcoord;
            this.XY_COORDS[this.EVENT_BUFFER][1] = ycoord;

        }
        else {
            int zoomDirection = MagnitudeAngleZoom(XY_COORDS);
            if (zoomDirection == 0){
                // Log.info("Linear motion detected. Sending vector to pi");
                // sendMessage();

                /** CALL UI FUNCTION HERE */
                // ImageContainer.moveImage(XY_COORDS);


                reset();
            }
            else if(zoomDirection == 1)
            {
                // Log.info("Would zoom in");
                reset();
                return 1;
            }
            else {
                // Log.info("Would zoom out");
                reset();
                return 2;
            }
        }
        return 0;
    }

    public void action_up(){
        reset();
        resetMagAngleZoom();
    }

    private void reset(){
        this.EVENT_BUFFER = 0;
        this.XY_COORDS = new float [this.EVENT_BUFFER_SIZE][2];
    }

  /*  private float getDisplacement2Points(float x1, float x2, float y1, float y2){
        return (float)Math.hypot(Math.abs(x1 - x2), Math.abs(y1-y2));
    }*/

    //choose which method to call here
    private int getDisplacements(float[][] coords)
    {
        int zoomType = 1;
        if(zoomType == 0)
        {
            return getDisplacementsCircumscribed(coords);
        }
        else if(zoomType == 1)
        {
            return MagnitudeAngleZoom(coords);
        }
        else
        {
            if(getDisplacementsOneDimension(coords))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    }

    //circumscribed method
    private int getDisplacementsCircumscribed(float[][] coords)
    {
        //don't do any extra work.
        for(int i = 0; i < EVENT_BUFFER_SIZE-1; i++) {
            if (coords[i + 1][0] == 0.0 && coords[i + 1][1] == 0.0) {
                if (i == 0) return 0; // Discard any lone coordinates
                break;
            }
        }

        float x1 = coords[0][0];
        float y1 = coords[0][1];

        float x2 = coords[4][0];
        float y2 = coords[4][1];

        float x3 = coords[9][0];
        float y3 = coords[9][1];

        float x12 = x1 - x2;
        float x13 = x1 - x3;

        float y12 = y1 - y2;
        float y13 = y1 - y3;

        float y31 = y3 - y1;
        float y21 = y2 - y1;

        float x31 = x3 - x1;
        float x21 = x2 - x1;

        float sx13 = (float)(Math.pow(x1, 2) -
                Math.pow(x3, 2));

        float sy13 = (float)(Math.pow(y1, 2) -
                Math.pow(y3, 2));

        float sx21 = (float)(Math.pow(x2, 2) -
                Math.pow(x1, 2));

        float sy21 = (float)(Math.pow(y2, 2) -
                Math.pow(y1, 2));

        float f = ((sx13) * (x12)
                + (sy13) * (x12)
                + (sx21) * (x13)
                + (sy21) * (x13))
                / (2 * ((y31) * (x12) - (y21) * (x13)));
        float g = ((sx13) * (y12)
                + (sy13) * (y12)
                + (sx21) * (y13)
                + (sy21) * (y13))
                / (2 * ((x31) * (y12) - (x21) * (y13)));

        float c = -(float) Math.pow(x1, 2) - (float) Math.pow(y1, 2) -
                2 * g * x1 - 2 * f * y1;

        float h = -g;
        float k = -f;
        float r = (float) Math.sqrt(((h * h) + (k * k)) - c);
        //now that we have radius and the point of the radius, can use soh cah toa to find each individual angle
        //then we can go around subtacting each angle to find the relative thetas
        //if majority of thetas are positive, CW, if majority negative, CCW
        int cw = 0, ccw = 0;
        float angle = 0;
        // Log.info("Currently checking for circular motion");
        for(int i = 0; i < EVENT_BUFFER_SIZE-1; i++)
        {
            x1 = coords[i][0];
            x2 = coords[i+1][0];
            y1 = coords[i][1];
            y2 = coords[i+1][1];
            angle = (float) Math.toDegrees(Math.atan2(y2-y1, x2-x1)) - angle;
            // Log.info("angle: " + String.valueOf(angle));
            if(angle > 0)
            {
                cw++;
            }
            else
            {
                ccw++;
            }
        }
        if(cw > ccw && cw-ccw > 2)
        {
            return 1;
        }
        else if(ccw > cw && ccw -cw > 2)
        {
            return 2;
        }
        else {
            return 0;
        }
    }

    private void resetMagAngleZoom()
    {
        Zooming = false;
        circleCounter = 0;
        isZoomIN1 = false;
        isZoomIN2 = false;
        isZoomOUT1 = false;
        isZoomOUT2 = false;
        zoomType = 0;
    }


    /** tracking from (0,0) method */
    private int MagnitudeAngleZoom(float[][] coords){
        // Returns 0 for linear, 1 for ZoomIn, 2 for ZoomOut
        int circleDirection = 0, notZooming = 0;
        float x1, y1, x2, y2;
        for(int i = 0; i < coords.length-1; i++) {

            x1 = coords[i][0];
            y1 = coords[i][1];
            x2 = coords[coords.length -1][0];
            y2 = coords[coords.length -1][1];

            // FIND MAG AND ANGLE
            magnitudeInitial = (float) Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
            magnitudeFinal = (float) Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));

            if (y1 != 0) {
                angleInitial = (float) Math.atan(x1 / y1);
            }

            if (y2 != 0) {
                angleFinal = (float) Math.atan(x2 / y2);
            }
            angleInitial = (float) Math.toDegrees(angleInitial);
            angleFinal = (float) Math.toDegrees(angleFinal);


            // DETERMINE IF USER IS TRYING TO ZOOM
            CheckZooming();
            if (!Zooming) {
                if (circleCounter >= 2) {
                    circleCounter = 0;
                    if (isZoomIN1 && isZoomIN2) {
                        circleDirection++;
                    } else if (isZoomOUT1 && isZoomOUT2) {
                        circleDirection--;
                    } else {
                        notZooming++;
                    }
                }
            } else {
                if (circleCounter >= 2) {
                    circleCounter = 0;
                    if (isZoomIN1 && isZoomIN2) {
                        circleDirection++;
                    } else if (isZoomOUT1 && isZoomOUT2) {
                        circleDirection--;
                    } else {
                        if(zoomType == 0)
                        {
                            notZooming++;
                        }
                        else if(zoomType==1)
                        {
                            circleDirection++;
                        }
                        else
                        {
                            circleDirection--;
                        }
                    }
                }
            }
        }

        if(circleDirection > notZooming)
        {
            return 1;
        }
        else if(circleDirection < 0 && Math.abs(circleDirection) > notZooming)
        {
            return 2;
        }
        return 0;

    }

    
    private void CheckZooming() {

        //CASE 1 FOR ZOOMING IN
        if (angleFinal < angleInitial) {
            if (isAngleIncreasing) {
                if (isMagnitudeIncreasing) {
                    isZoomIN1 = true;
                    isZoomOUT1 = false;
                }
            }
            isAngleIncreasing = false;
            circleCounter++;

        }

        //CASE 1 FOR ZOOMING OUT
        if (angleFinal > angleInitial) {
            if (!isAngleIncreasing) {
                if (isMagnitudeIncreasing) {
                    isZoomOUT1 = true;
                    isZoomIN1 = false;
                }
            }
            isAngleIncreasing = true;
            circleCounter++;

        }

        //CASE 2 FOR ZOOMING OUT
        if (magnitudeFinal < magnitudeInitial) {
            if (isMagnitudeIncreasing) {
                if (isAngleIncreasing) {
                    isZoomOUT2 = true;
                    isZoomIN2 = false;
                }
            }
            isMagnitudeIncreasing = false;
            circleCounter++;

        }

        //CASE 2 FOR ZOOMING IN
        if (magnitudeFinal > magnitudeInitial) {
            if (!isMagnitudeIncreasing) {
                if (isAngleIncreasing) {
                    isZoomIN2 = true;
                    isZoomOUT2 = false;
                }
            }
            isMagnitudeIncreasing = true;
            circleCounter++;
        }
    }





    //1-D motion method
    private boolean getDisplacementsOneDimension(float[][] coords){
        float dispSum = 0;
        float netSum;
        float x1;
        float x2 = 0;
        float y1;
        float y2 = 0;
        StringBuilder xy = new StringBuilder(); // For debugging purposes

        for (int i = 0; i < coords.length - 1; i++){
            if (coords[i+1][0] == 0.0 && coords[i+1][1] == 0.0){
                if (i == 0) return false; // Discard any lone coordinates
                break;
            }
            x1 = coords[i][0];
            x2 = coords[i+1][0];
            y1 = coords[i][1];
            y2 = coords[i+1][1];
            dispSum += getDisplacement2Points(x1, x2, y1, y2);
            xy.append("\n (").append(String.valueOf(x1)).append(",").append(String.valueOf(y1)).append(") ");
        }

        x1 = coords[0][0];
        y1 = coords[0][1];

        netSum = getDisplacement2Points(x1, x2, y1, y2);
        float diff = Math.abs(netSum - dispSum);
        boolean send;
        if (diff < THRESH) send = true;
        else {
            /*if ((x1 - x2)<20 || (y1 - y2)<20){ // Limited travel along the x or y axis implies backtracking
                send = true;
            }*/
            send = false;
        }
        //Log.info("Displacement Sum: " + dispSum + ", Net Sum: " + netSum);
        //Log.info("Displacement Vector: " + xy);
        //Log.info("Start and end positions: (" + x1 + "," + y1 +" ), ( " + x2 +", "+ y2 + ")");
        //Log.info("Difference: " + diff + ", Send to pi: " + send);
        return send;
    }

    private float getDisplacement2Points(float x1, float x2, float y1, float y2) {
        return (float) Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }



}