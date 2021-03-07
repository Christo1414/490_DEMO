package com.example.a490_demo.views;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a490_demo.R;

public class ImageContainer extends RelativeLayout {

    static private ImageView image;
    static private RelativeLayout layout;
    static private float scalefactor = 3.0f;


    public ImageContainer(@NonNull Context context) {
        super(context);
        initialize();
    }

    public ImageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public ImageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();

    }



    private void initialize(){
        inflate(getContext(), R.layout.container_image, this);
        image = findViewById(R.id.image_view);
        layout = findViewById(R.id.image_container);

        image.setImageResource(R.drawable.photo);
        layout.setScaleX(scalefactor);
        layout.setScaleY(scalefactor);
    }

    public static void resizeImage(boolean IN){

        if(IN){ scalefactor += 0.1f; }
        else{ scalefactor -= 0.1f; }

        if(scalefactor < 0f){
            scalefactor = 0;
        }
        if(scalefactor > 10f){
            scalefactor = 10f;
        }
        layout.setScaleX(scalefactor);
        layout.setScaleY(scalefactor);

    }

    public static void moveImage(float[][] coords){
        float xVector, yVector, xPosition, yPosition;

        xVector = coords[coords.length -1][0] - coords[0][0];
        yVector = coords[coords.length -1][1] - coords[0][1];

        xPosition = image.getX();
        yPosition = image.getY();

        image.setX(xPosition + xVector/5);
        image.setY(yPosition + yVector/5);

    }


}
