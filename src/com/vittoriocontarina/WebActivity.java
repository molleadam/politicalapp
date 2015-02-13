package com.vittoriocontarina;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class WebActivity extends Activity {
	WebView mWebView;
	//Activity indicator
	ProgressBar m_ProgressBar=null;
	RelativeLayout rl;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		//Set the layout of this activity
        setContentView(R.layout.weblayout);
        
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
       // mWebView.getSettings().setUserAgentString("ua");
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");

        mWebView.setWebViewClient(new homeClient());
        mWebView.loadUrl(this.getIntent().getStringExtra("url"));
        
        Log.e("url",this.getIntent().getStringExtra("url"));
        mWebView.setBackgroundColor(R.drawable.background);
        
        this.m_ProgressBar= (ProgressBar) this.findViewById(R.id.progressBar1);
        
        rl =(RelativeLayout)this.findViewById(R.id.rlweb);
        mWebView.setWebViewClient(new WebViewClient() {

        	   public void onPageFinished(WebView view, String url) {
        	        // do your stuff here
        		   m_ProgressBar.setVisibility(View.INVISIBLE);
        		   rl.setVisibility(View.GONE);
        	    }
        	});


        
		
	}
	
	private class homeClient extends WebViewClient 
	{
        
	    

	    @Override 
	    public boolean shouldOverrideUrlLoading(WebView view, String url)
	    {   
	    	if (url.startsWith("geo:"))
	    	{
	    		
	    		/*url = url.replaceFirst("geo:", "");
	    		String [] latlon = null;
	    	    latlon = url.split(":");
	    	    Log.e("phone",latlon[0]);
	    	    Log.e("phone",latlon[1]);)*/


	    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(url));
	    		startActivity(intent);

	            return true;
	        }
			return true;
	    } 

	    @Override
	    public void onPageFinished (WebView view, String url)
	    {
	    	Log.e("phone","Finished");
	    }
	}

	

}
