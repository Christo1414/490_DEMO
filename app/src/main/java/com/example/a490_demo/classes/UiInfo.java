package com.example.a490_demo.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.a490_demo.R;

public class UiInfo extends ConstraintLayout {

    static private TextView connection_status;
    static private String ip_address;

    public UiInfo(@NonNull Context context) {
        super(context);
        initialize();
    }

    public UiInfo(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public UiInfo(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize(){
        inflate(getContext(), R.layout.view_info, this);

        connection_status = (TextView) findViewById(R.id.IP_Status);
        connection_status.setText("Connecting to camera");

    }

    public static void Update_IP(String IP){
        ip_address = IP;
        connection_status.setText("Connected to " + ip_address);
    }

}
