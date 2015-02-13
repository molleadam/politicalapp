package com.vittoriocontarina;

import android.app.Activity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.view.Window;

public class NewsAppActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        		
        setContentView(R.layout.splash);
          
        //The splash screen time
        		Timer timer = new Timer();
        		timer.schedule(new TimerTask() {
        			public void run() {
        				startActivity(new Intent(NewsAppActivity.this,MainActivity.class));
        				finish();
        			}
        		}, 3000);
        		
    }
}