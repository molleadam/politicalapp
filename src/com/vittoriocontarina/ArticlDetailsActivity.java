package com.vittoriocontarina;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.vittoriocontarina.ImageListAdapter.ViewHolder;


public class ArticlDetailsActivity extends Activity {
	public ImageLoader imageLoader; 
	public boolean displayAds=false;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
		super.onCreate(savedInstanceState);
		
		//Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set layout
		setContentView(R.layout.article);
		
		displayAds=this.getString(R.string.display_ads).compareTo("yes")==0;
		
		
		
		
		 //Set up title
		TextView tt = (TextView) this.findViewById(R.id.trantitle);
		tt.setText(this.getIntent().getStringExtra("title"));
		
		 //Set up Date
		TextView ta = (TextView) this.findViewById(R.id.authorTitle);
		ta.setText(this.getIntent().getStringExtra("date")+" | "+this.getIntent().getStringExtra("author"));

		
		//Set up contesnt
		WebView wv = (WebView) this.findViewById(R.id.webView1);
				try {
					wv.loadData(URLDecoder.decode(this.getIntent().getStringExtra("content"), "ISO-8859-1"), "text/html", null);
					//tc.setText(URLDecoder.decode(this.getIntent().getStringExtra("content"), "ISO-8859-1"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		
		//Set up imagse
		ImageView iv = (ImageView) this.findViewById(R.id.bd_logo);
		if(this.getIntent().getStringExtra("image").length()>3)
		{
			imageLoader=new ImageLoader(this.getApplicationContext(),R.drawable.loadimg);
			imageLoader.setAsBackground(true);
			imageLoader.default_picture=R.drawable.loadimgextrabig;
			//Create view Holder
			ViewHolder holder=new ViewHolder();

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)this.getIntent().getDoubleExtra("w", 320), (int) this.getIntent().getDoubleExtra("h", 320));
			iv.setLayoutParams(layoutParams);
	
			
			
			//Get the view that we are inserting the image
			iv.setTag(holder);
			holder.image=iv;
			holder.image.setTag("12345");
		
			imageLoader.DisplayImage(this.getIntent().getStringExtra("image"), this, holder.image,"12345");
		}
		else
		{
			LinearLayout ll=(LinearLayout)this.findViewById(R.id.bdlogoholder);
        	ll.setVisibility(View.GONE);
		}
		
		
		//Set up Button
		Button rf = (Button) this.findViewById(R.id.readMoreBtn);
		//Create All Button Click Listener, for my upload buttons
		rf.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						openLink();
					}
				});
		
		//Set up shareButton
				Button share = (Button) this.findViewById(R.id.shareBtn);
				//Create All Button Click Listener, for my upload buttons
				share.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								shareIt();
							}
						});
		
		//Set up ads
		if(this.displayAds)
		{
			// Create the adView

		    adView = new AdView(this, AdSize.BANNER, this.getString(R.string.ad_unit_id));

		    // Lookup your LinearLayout assuming it's been given
		    // the attribute android:id="@+id/mainLayout"
		    LinearLayout layout = (LinearLayout)findViewById(R.id.adLL);

		    // Add the adView to it
		    layout.addView(adView);
		    
		    

		    // Initiate a generic request to load it with an ad
		    adView.loadAd(new AdRequest());



		}

	}
	
	protected void shareIt() {
		// TODO Auto-generated method stub
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String title=this.getIntent().getStringExtra("title");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,title );
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+" "+this.getIntent().getStringExtra("url"));
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	private void openLink()
	{
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(this.getIntent().getStringExtra("url"))));
	}
		
	@Override
	  public void onDestroy() {
	    if (adView != null) {
	      adView.destroy();
	    }
	    super.onDestroy();
	  }

	

}
