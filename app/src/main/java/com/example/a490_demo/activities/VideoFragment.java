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
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.RadioGroup;
import android.widget.RadioButton;
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
	// private boolean isCustom = true;
	private int zoomType = 1;

	private ToggleButton toggleButton;
	private RadioButton swirlZoom, pinchZoom, doubleTapZoom;
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
		sendToast(1); // Initial roasty toasty
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
		textureView.setZoomType(zoomType);
		textureView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent e)
			{
				switch (e.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						// stopFadeOutTimer();
						// Log.d("VideoFragment in ACTION_DOWN");
						Log.d("shantag", "VideoFragment in ACTION_DOWN");
						break;
					case MotionEvent.ACTION_UP:
						if (e.getPointerCount() == 1)
						{
							// startFadeOutTimer(false);
							Log.d("shantag", "VideoFragment started fadeouttimer");
						}
						// Log.info("VideoFragment in ACTION_UP");
						Log.d("shantag", "VideoFragment in ACTION_UP");
						break;
				}
				return false;
			}
		});
		if(zoomType == 1)
		{
			final GestureDetector gesture = new GestureDetector(getActivity(),
					new GestureDetector.SimpleOnGestureListener() {

						@Override
						public boolean onDown(MotionEvent e) {
							Log.d("shantag", "in final gesturedetector ondown");
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
							Log.d("shantag", "in view.set ACTION_DOWN");
							cwp.action_down(x, y);
							break;
						case MotionEvent.ACTION_MOVE:
							Log.d("shantag", "in view.set ACTION_MOVE");
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
							Log.d("shantag", "in view.set ACTION_UP");
							cwp.action_up();
							break;
					}
					return true;
				}
			});
		}

		// radio buttons
		swirlZoom = (RadioButton)view.findViewById(R.id.swirl_zoom);
		pinchZoom = (RadioButton)view.findViewById(R.id.pinch_zoom);
		doubleTapZoom = (RadioButton)view.findViewById(R.id.double_tap_zoom);

		swirlZoom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomType = 1;
				sendToast(zoomType);
				textureView.setZoomType(zoomType);

			}
		});

		pinchZoom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomType = 2;
				sendToast(zoomType);
				textureView.setZoomType(zoomType);

			}
		});

		doubleTapZoom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomType = 3;
				sendToast(zoomType);
				textureView.setZoomType(zoomType);
			}
		});

		return view;
	}

	private void sendToast(int zoomType){
		Context context = getActivity().getApplicationContext();
		String msg;
		switch(zoomType){
			case 1:
				msg = "\nZoom in: Draw a clockwise circle\nZoom out: Draw a counterclockwise circle";
				break;
			case 2:
				msg = "\nZoom in: Pinch outwards\nZoom out: Pinch inwards";
				break;
			case 3:
				msg = "\nZoom in: Double tap on the right\nZoom out: Double tap on the left";
				break;
			default:
				msg = "An error has occured.";
		}
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, msg, duration);
		toast.show();
	}



	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.d("shantag", "VF single tap confirmed");
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.d("shantag", "VF double tap");
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Log.d("shantag", "VF double tap event");
		return true;
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
