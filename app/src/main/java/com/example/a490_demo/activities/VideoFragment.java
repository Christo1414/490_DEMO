package com.example.a490_demo.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.a490_demo.views.CustomZoom;
import com.example.a490_demo.classes.ConnectWithPi;
import com.example.a490_demo.R;


public class VideoFragment extends Fragment implements TextureView.SurfaceTextureListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener
{

	private final static float MIN_ZOOM = 0.1f;
	private final static float MAX_ZOOM = 10;
	private int isZoom = 0; // 0 for not zoom, 1 for zoom in, 2 for zoom out


	private CustomZoom textureView;
	private TextView nameView, messageView;

	private GestureDetectorCompat mDetector;
	String ipAddr = "192.168.50.99";
	ConnectWithPi cwp = new ConnectWithPi(ipAddr);


	public VideoFragment() {
		super(R.layout.fragment_video);
	}


	//******************************************************************************
	// onCreate
	//******************************************************************************
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		// configure the activity
		super.onCreate(savedInstanceState);


		mDetector = new GestureDetectorCompat(getActivity(), this);
		mDetector.setOnDoubleTapListener(this);
//        String ipAddr = "192.168.50.99";
//        ConnectWithPi cwp = new ConnectWithPi(ipAddr);

	}

	//******************************************************************************
	// onCreateView
	//******************************************************************************
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_video, container, false);

		// configure the name
		nameView = (TextView)view.findViewById(R.id.video_name);
		nameView.setText("DEMO");

//		// initialize the message
//		messageView = (TextView)view.findViewById(R.id.video_message);
//		messageView.setTextColor(getResources().getColor(R.color.bad_text));
//		messageView.setText("initializing_video");


		// set the texture listener
		// probably will have to remove the next few lines
		textureView = (CustomZoom)view.findViewById(R.id.video_surface);
		textureView.setSurfaceTextureListener(this);
		textureView.setZoomRange(MIN_ZOOM, MAX_ZOOM);
		textureView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent e)
			{
				switch (e.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						// stopFadeOutTimer();
						// Log.info("VideoFragment in ACTION_DOWN");
						break;
					case MotionEvent.ACTION_UP:
						if (e.getPointerCount() == 1)
						{
							// startFadeOutTimer(false);
						}
						// Log.info("VideoFragment in ACTION_UP");
						break;
				}
				return false;
			}
		});

		final GestureDetector gesture = new GestureDetector(getActivity(),
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onDown(MotionEvent e) {
						// Log.info("in final gesturedetector ondown");
						return true;
					}


				});

		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e)
			{
				Scroller s = new Scroller(getActivity());
				float x = e.getX();
				float y = e.getY();
				switch (e.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// Log.info("in view.set ACTION_DOWN");
						cwp.action_down(x, y);
						break;
					case MotionEvent.ACTION_MOVE:
						// Log.info("in view.set ACTION_MOVE");
						isZoom = cwp.action_move(x, y, textureView);
						switch (isZoom) {
							case 1:
								textureView.zoomIn();
								isZoom = 0;
								break;
							case 2:
								textureView.zoomOut();
								isZoom = 0;
								break;
						}
						break;
					case MotionEvent.ACTION_UP:
						// Log.info("in view.set ACTION_UP");
						cwp.action_up();
						break;
				}
				return true;
			}
		});

//		// create the snapshot button
//		snapshotButton = (Button)view.findViewById(R.id.video_snapshot);
//		snapshotButton.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View view)
//			{
//				int check = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//				if (check != PackageManager.PERMISSION_GRANTED)
//				{
//					Log.info("ask for external storage permission");
//					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//							REQUEST_WRITE_EXTERNAL_STORAGE);
//				}
//				// else
//				// {
//				// 	takeSnapshot();
//				// }
//			}
//		});

//		// move the snapshot button over to account for the navigation bar
//		if (fullScreen)
//		{
//			float scale = getContext().getResources().getDisplayMetrics().density;
//			int margin = (int)(5 * scale + 0.5f);
//			int extra = Utils.getNavigationBarHeight(getContext(), Configuration.ORIENTATION_LANDSCAPE);
//			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)snapshotButton.getLayoutParams();
//			lp.setMargins(margin, margin, margin + extra, margin);
//		}

		return view;
	}


	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {

	}

	@Override
	public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

	}
}
