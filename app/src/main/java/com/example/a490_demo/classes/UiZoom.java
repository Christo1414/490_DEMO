package com.example.a490_demo.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.a490_demo.R;
import com.example.a490_demo.views.ImageContainer;

public class UiZoom extends ConstraintLayout {

    static private RelativeLayout zoombar_layout;
    static private View zoombar_fill;

    static private float zoombar_range;
    static private float zoom_percentage = (float) 0.5;


    public UiZoom(@NonNull Context context) {
        super(context);
        initialize();
    }

    public UiZoom(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public UiZoom(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        inflate(getContext(), R.layout.view_zoom, this);

        zoombar_layout = (RelativeLayout) findViewById(R.id.ZoomBarLayout);
        zoombar_fill = (View) findViewById(R.id.ZoomBarView);
        zoombar_range = pxFromDp(this, 300);
        Update_Zoombar(ImageContainer.scalefactor / 10);

    }

    public static void Update_Zoombar(float percentage){
        // Call when zooming

        zoombar_layout.setVisibility(View.VISIBLE);

        /** Update ZOOM percentage HERE with global variable or parameter */
        zoom_percentage = percentage;

        if(zoom_percentage < 0) {
            zoombar_fill.setY(zoombar_range);
        }else if(zoom_percentage > 1){
            zoombar_fill.setY(0);
        }else {
            zoombar_fill.setY((zoombar_range) * (1 - zoom_percentage));
        }
    }

    public static float pxFromDp(final UiZoom context, final float dp) {
        // To find boundary conditions
        return dp * context.getResources().getDisplayMetrics().density;
    }


}
