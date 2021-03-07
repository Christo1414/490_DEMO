// Copyright © 2016-2017 Shawn Baker using the MIT License.
package com.example.a490_demo.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a490_demo.R;
import com.example.a490_demo.views.ImageContainer;

public class VideoActivity extends AppCompatActivity
{


	//******************************************************************************
	// onCreate
	//******************************************************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// configure the activity
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);


		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.setReorderingAllowed(true)
					.add(R.id.frame_container, VideoFragment.class, null)
					.commit();
		}
	}

}
