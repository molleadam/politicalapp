package com.vittoriocontarina;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements
OnCheckedChangeListener {
	
	//Define 1 tab host that will host four tabs
	private TabHost tabHost;
	
	//Define the intents to be activated when user want to change the tab
    private Intent tab1Intent;
    private Intent tab2Intent;
    private Intent tab3Intent;
    private Intent tab4Intent;
    private Intent tab5Intent;
   
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set the layout of this activity. The tabs are defined there.
        setContentView(R.layout.tabs);
        
         //Initialize intents, Change it in your app with your Class names.
        this.tab1Intent = new Intent(this, BlogActivity.class); //Intent triger when users clicks on first tab
        this.tab2Intent = new Intent(this,GalleryActivity.class);
        this.tab3Intent = new Intent(this, WebActivity.class);//Intent triger when users clicks on third tab
        this.tab4Intent = new Intent(this, WebActivity.class);//Intent triger when users clicks on fourth tab
        this.tab5Intent = new Intent(this, YouTubeActivity.class);//Intent triger when users clicks on fourth tab
        
        //Set web view
        this.tab3Intent.putExtra("url", "http://m.facebook.com/"+this.getString(R.string.fb_page_in_tab));
        this.tab4Intent.putExtra("url", "https://m.twitter.com/"+this.getString(R.string.twitter));

        
        //Initialize the radio buttons that represent tab changer button
        //This buttons are defined in the layout file main.xml in the layout folder
        ((RadioButton) findViewById(R.id.rb1)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.rb2)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.rb3)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.rb4)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.rb5)).setOnCheckedChangeListener(this);
        
        //Now setup the TabHost, get TabHost From the activity
        this.tabHost=getTabHost();
        
        //Now initialize the tabs
        //Look at buildTabs() method bellow for information about parameters  
        this.tabHost.addTab(buildTabs("first_tab", R.string.tab_1_title,
                R.drawable.news_unselected, this.tab1Intent));

        this.tabHost.addTab(buildTabs("second_tab", R.string.tab_2_title,
                R.drawable.gallery_unselected, this.tab2Intent));

        this.tabHost.addTab(buildTabs("third_tab", R.string.tab_3_title,
                R.drawable.fb_unselected, this.tab3Intent));

        this.tabHost.addTab(buildTabs("fourth_tab", R.string.tab_4_title,
                R.drawable.tw_unselected, this.tab4Intent));
        
        this.tabHost.addTab(buildTabs("fifth_tab", R.string.tab_5_title,
                R.drawable.yt_unselected, this.tab5Intent));
            
    }
    
    /**
     * @description
     * Create the Tab host specific button specification like: tag, image and intent
     * @param tag A simple string that will identify this tab
     * @param resLabel The label of this tab, Ex "Home"
     * @param resIcon The icon of this tab, It must be local resource id number, Ex R.drawable.something
     * @param content The intent for this tab
     * @return TabHost.TabSpec
     */
    private TabHost.TabSpec buildTabs(String tag, int resLabel, int resIcon,
            final Intent content) {
        return this.tabHost
                .newTabSpec(tag)
                .setIndicator(getString(resLabel),
                        getResources().getDrawable(resIcon))
                .setContent(content);
    }

	@Override
	/**
	 * Trigered when users clicks on the tab
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		// TODO Auto-generated method stub
		if (isChecked) {
            switch (buttonView.getId()) {
            case R.id.rb1:   	
                this.tabHost.setCurrentTabByTag("first_tab");
                break;
            case R.id.rb2:
                this.tabHost.setCurrentTabByTag("second_tab");
                break;
            case R.id.rb3:
                this.tabHost.setCurrentTabByTag("third_tab");
                break;
            case R.id.rb4:
                this.tabHost.setCurrentTabByTag("fourth_tab");
                break;
            case R.id.rb5:
                this.tabHost.setCurrentTabByTag("fifth_tab");
                break;
            }
        }
	}
	
	/**
	 * 
	 * @param id The Facebook Page ID
	 */
	public Intent openFacebookGallery(String id)
	{
		Intent i = new Intent(MainActivity.this,GalleryActivity.class);
		i.putExtra("hasParameters", true);
		i.putExtra("isWP", false);
		i.putExtra("fbId", id);
		return i;
	}
    
    public Intent openDifferentWordPressGallery(String server_adress)
    {
    	Intent i = new Intent(MainActivity.this,GalleryActivity.class);
		i.putExtra("hasParameters", true);
		i.putExtra("isWP", true);
		i.putExtra("server_adress", server_adress);
		return i;
    }
}