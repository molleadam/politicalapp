package com.vittoriocontarina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vittoriocontarina.AsyncRunner.RequestListener;


public class BlogActivity extends TrackedListActivity {

	//Image list adapter
	private ImageListAdapter adapter;
	
	

	//Activity indicator
    ProgressBar m_ProgressBar=null;

	//List of videos
	private ArrayList<Object> mynews  = null;
	private ArrayList<Object> recentnews  = null;
	private ArrayList<Object> categories  = null;

	//Animation for the loader
	AnimationDrawable frameAnimation;

	//Linear layout that will represent the loader holder
	LinearLayout ll;
	
	//Create the two buttons
    //RadioButton recenttab,categoriestab;
    
    //Is data loaded
    boolean recent,categoriesflag=false;
    
    boolean isRecent=true;
    
    String lastId="";
	String newId="";
	
	boolean isShowedPush=false;

	/***
	 * Asynchronous runner
	 * Replace the string with your own server url
	 * We use this Runner to retrieve date from remote server asynchronously 
	 */
	public static AsyncRunner mAsyncRunnerr; 
	public static AsyncRunner mAsyncRunnerc;
	
	EasyTracker tracker;
	
	TimerTask scanTask;
	
	Timer t = new Timer();

	
	private static final int DISPLAY_DATA = 1;



	private static final int HELLO_ID = 1;
   
	// this handler will receive a delayed message
    private Handler mHandler = new Handler() {
        @Override
		public
        void handleMessage(Message msg) {
            // Do task here
            if (msg.what == DISPLAY_DATA) fetchRecentForUpdate();
        }
 };

 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set layout
		setContentView(R.layout.newslayout);
		
		//Init Progress bar
		m_ProgressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
		
		tracker=EasyTracker.getTracker();
		
		
		//Set Api location
	    String serverLocation=this.getString(R.string.server_address);
		mAsyncRunnerr = new AsyncRunner(serverLocation+"api/get_recent_posts/");
		//http://newsapp.nextwebart.com/test.json
	    //mAsyncRunnerr = new AsyncRunner("http://newsapp.nextwebart.com/test.json");
		mAsyncRunnerc = new AsyncRunner(serverLocation+"api/get_category_index/");
		
		 //Set up title
		TextView tt = (TextView) this.findViewById(R.id.trantitle);
        String header=getString(R.string.news);
		tt.setText(header);
		
		

		//----------------- Initialize Array of Videos -------------------//
		mynews = new ArrayList<Object>(); //List of my vidoes, empty at the momment
		recentnews = new ArrayList<Object>(); //List of my vidoes, empty at the momment
		categories = new ArrayList<Object>(); //List of my favorites, empty at the momment

		//Create new List Image Adapter
		this.adapter = new ImageListAdapter(this, R.layout.ytrow, mynews);
		adapter.setNotifyOnChange(true);

		//Initialize the List view
		ListView lv = getListView();

		//Set adapter to this list
		lv.setAdapter(adapter);
		

		//Initialize the buttons
		//recenttab=(RadioButton) findViewById(R.id.Button01);
		//recenttab.setChecked(true);
		//categoriestab=(RadioButton) findViewById(R.id.Button02);
		//categoriestab.setChecked(false);
		
		//Create All Button Click Listener, for my upload buttons
		/*recenttab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
              if(recent)
              {
            	//Show my uploads
  				setArticls();
              }
			}
		});*/

		//Create Special Button Click Listener, for my favorite buttons
		/*categoriestab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				if(categoriesflag) //if favorites are loaded
				{
					//Show favorites videos
					setCategories();
					
				}
				else
				{
					//load them
					fetchCategories();
				}

				
			}
		});*/
		
		//Click Listener for the ListView, Open youtube window
		lv.setOnItemClickListener(new OnItemClickListener(){

			//@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//Show Detail Gallery View
				
				if(isRecent)
				{
					showArticlDetails(position);
				}
				else
				{
					showCategory(position);
				}
				
			}
		});
		
		//Set up shareButton
		ImageButton share = (ImageButton) this.findViewById(R.id.refreshbtn);
		//Create All Button Click Listener, for my upload buttons
		share.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						refresh();
					}
				});

		//Connect to remote server to fetch my upload videos
		fetchRecent();
		
		
				
				
		scanTask = new TimerTask() {
		    public void run() {
		    	mHandler.sendEmptyMessageAtTime(DISPLAY_DATA, 500);
		    }};
		t.schedule(scanTask, 60000, 60000);  // here is t.schedule( , delay, period); 

		
		


	}
	

 

protected void refresh() {
		// TODO Auto-generated method stub
	   this.mynews.clear();
		if(isRecent)
		{
			this.recentnews.clear();
			fetchRecent();
			
			
		}
		else
		{
			this.categories.clear();
			fetchCategories();
		}
	}

/**
    * Opnens Youtube App or other listener
    * @param position - position of the video
    */
	protected void showArticlDetails(int position) {
		// TODO Auto-generated method stub
		int index=Integer.parseInt(((Articl)mynews.get(position)).getId());
		tracker.trackEvent(
	            "Clicks",  // Category
	            "Article",  // Action
	            ((Articl)mynews.get(position)).getName(), // Label
	            index);       // Value
	
		  Intent i =new Intent(this, ArticlDetailsActivity.class);
          i.putExtra("title",((Articl)mynews.get(position)).getName());
          i.putExtra("content",((Articl)mynews.get(position)).getContent());
          i.putExtra("image",((Articl)mynews.get(position)).getImage());
          i.putExtra("url",((Articl)mynews.get(position)).getUrl());
          i.putExtra("author",((Articl)mynews.get(position)).getAuthor());
          i.putExtra("date",((Articl)mynews.get(position)).getDate());
          i.putExtra("w",((Articl)mynews.get(position)).getW());
          i.putExtra("h",((Articl)mynews.get(position)).getH());
          startActivity(i);
	}
	
	
	/**
	    * @param position - position of the video
	    */
		protected void showCategory(int position) {
			// TODO Auto-generated method stub
			//Start activity
			
			int index=Integer.parseInt(((Category)mynews.get(position)).getId());

			tracker.trackEvent(
		            "Clicks",  // Category
		            "Category",  // Action
		            ((Category)mynews.get(position)).getName(), // Label
		            index);       // Value
			
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((YTVideos)mynews.get(position)).getId())));
            Intent i =new Intent(this, ArticlActivity.class);
            i.putExtra("catid",((Category)mynews.get(position)).getId());
            i.putExtra("catname",((Category)mynews.get(position)).getName());
            startActivity(i);
		}
	
	

    
	/**
	 * Connect to remote server (YOUTUBE API) to retrieve JSON formated output
	 * Here we are going to fill the array of My Videos
	 * LOADS MY UPLOAD VIDEOS
	 */
	private void fetchRecent() 
	{
		
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("alt", "json");
		//params.putString("exclude", "content");
		
		//Show progress bar
		m_ProgressBar.setVisibility(View.VISIBLE);

		
		//Call the API using the  AsyncRunner
		BlogActivity.mAsyncRunnerr.requestYourApi("",params, new yourappListener(false));
	}
	
	private void fetchRecentForUpdate() 
	{
		
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("alt", "json");
		//params.putString("exclude", "content");
		
		//Show progress bar
		//m_ProgressBar.setVisibility(View.VISIBLE);


		//Call the API using the  AsyncRunner
		BlogActivity.mAsyncRunnerr.requestYourApi("",params, new yourappListener(true));
	}
	
	/**
	 * Connect to remote server (YOUTUBE API) to retrieve JSON formated output
	 * Here we are going to fill the array of My Favorites
	 * LOAD MY FAVORITES
	 */
	private void fetchCategories() 
	{
		
		//Show progress bar
		m_ProgressBar.setVisibility(View.VISIBLE);
				
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("alt", "json");
		params.putString("exclude", "content");


		//Call the API using the  AsyncRunner
		BlogActivity.mAsyncRunnerc.requestYourApi("",params, new yourappListenerCategories());
	}
	

	/**
	 * Listener for the result from the issued API connection, LOAD MY UPLOADS
	 */
	public class yourappListener implements RequestListener
	{
		Boolean fromUpdate;
		Boolean displayNotification=false;
		
		

		public yourappListener(Boolean fromUpdate) {
			super();
			this.fromUpdate = fromUpdate;
		}

		//Triggered when everything is ok, and we have API response
		//@Override
		public void onComplete(String response, Object state) 
		{
			
			/**
			 * Now try to parse the JSON RESPONSE
			 * you have to change the code bellow to suite your needs 
			 */
			try 
			{
				//Create  JSONArray of Videos
				JSONArray items = Util.parseJson(response).getJSONArray("posts");
                Boolean featuredFound=false;
				//Constructing the galleries
				for(int i =0;i<items.length();i++)
				{
					//Extract one gallery JSON Object
					JSONObject one_articl = items.getJSONObject(i);

					//Create the variables
					String id = one_articl.getString("id");
					String name=one_articl.getString("title");
					String url=one_articl.getString("url");
					String author="contarina"; //one_articl.getJSONObject("author").getString("name")
					String date=one_articl.getString("date");
					String desc=one_articl.getString("excerpt");
					String content=one_articl.getString("content");
					
					//Get the tumbnail location
					String thumb="";
					Boolean thumbSet=false;
					String image="";
					@SuppressWarnings("unused")
					Boolean imageSet=false;
					double w=320;
					double h=200;
					if(one_articl.has("attachments"))
					{
						
						
						JSONArray attach = one_articl.getJSONArray("attachments");
						for(int j =0;j<attach.length();j++)
						{
							if(attach.getJSONObject(j).getString("mime_type").contains("image"))
							{
								if(attach.getJSONObject(j).getJSONObject("images").has("thumbnail"))
								{
									thumb=attach.getJSONObject(j).getJSONObject("images").getJSONObject("thumbnail").getString("url");
									thumbSet=true;
								}
								if(attach.getJSONObject(j).getJSONObject("images").has("full"))
								{
									image=attach.getJSONObject(j).getJSONObject("images").getJSONObject("full").getString("url");
									imageSet=true;
					
									w=attach.getJSONObject(j).getJSONObject("images").getJSONObject("full").getDouble("width");
									h=attach.getJSONObject(j).getJSONObject("images").getJSONObject("full").getDouble("height");
								}
							}
						}
						
					}
					if(i==0&&fromUpdate)
					{
						newId=id;
					}
					if(i==0&&!fromUpdate)
					{
						lastId=id;
					}
					if(!thumbSet&&one_articl.has("thumbnail"))
					{
						thumb=one_articl.getString("thumbnail");
					}
					
					if(!featuredFound&&image!="")
					{
						//Add featured articl to the Array of images
						recentnews.add(0, new FeaturedArticl(name, thumb, date,desc, id,image,url,content,author,w,h,BlogActivity.this));
						featuredFound=true;
					}
					else
					{
						//Add articl to the Array of images
						recentnews.add(new Articl(name, thumb, date,desc, id,image,url,content,author,w,h,BlogActivity.this));
					}
							

					

					
				}
				//Indicate that my upload videos are loaded
			    recent=true;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Show my uploads
			if(fromUpdate)
			{
				if(recentnews.size()>0)
				{
					Log.e("New id:",newId);
					Log.e("Last id:",lastId);
					displayNotification=lastId.compareTo(newId)==0;
					int index=0;
					for(int j=0;j<recentnews.size();j++)
					{
						if(newId.compareTo(((Articl)recentnews.get(j)).getId())==0)
						{
							index=j;
						}
					}
					if(!displayNotification&&!isShowedPush)
					{
						String id=((Articl)recentnews.get(index)).getId();
						String Title=((Articl)recentnews.get(index)).getName();
						String Desc=((Articl)recentnews.get(index)).getDescr();
						isShowedPush=true;
						displayTheNotification(id,Title,Desc);
					}
				}
			}
			else
			{
				setArticls();
			}
           


		}

		//@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void oniClubError(ReadError e, Object state) {
			// TODO Auto-generated method stub

		}
	}
	
	/**
	 * Listener for the result from the issued API connection
	 */
	public class yourappListenerCategories implements RequestListener
	{

		//Triggered when everything is ok, and we have API response
		//@Override
		public void onComplete(String response, Object state) 
		{
			/**
			 * Now try to parse the JSON RESPONSE
			 * IF YOU ARE CONNECTING TO OTHER API THE NextGEN
			 * you have to change the code bellow to suite your needs 
			 */
			try 
			{
				//Create  JSONArray of Galleries
				JSONArray items = Util.parseJson(response).getJSONArray("categories");

				//Constructing the Videos
				for(int i =0;i<items.length();i++)
				{
					//Extract one gallery JSON Object
					JSONObject one_category = items.getJSONObject(i);

					//Create the variables
					String id = one_category.getString("id");
					String title=one_category.getString("title");
					
					//Add gallery to the Array of videos
					categories.add(new Category(title, id));
					
				}
				//Indicates that we have loaded the favorites
			    categoriesflag=true;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Show Favorites
			setCategories();
		}
		

		//@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		//@Override
		public void oniClubError(ReadError e, Object state) {
			// TODO Auto-generated method stub

		}
	}
	
 
	/**
	 * Show uploads
	 */
	public void setArticls()
	{
		Log.e("Change", "Set Articls");
		//remove from my videos
		this.mynews.clear();
		isRecent=true;
		
		//Add uploads
		for(int i=0;i<this.recentnews.size();i++)
		{
			this.mynews.add(this.recentnews.get(i));
		}
		

		/**
		 * Update the GUI
		 * 		SHOW the videos
		 *      HIDE Loading indicator
		 */
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				
				//Notify adapter
				adapter.notifyDataSetChanged();
				m_ProgressBar.setVisibility(View.INVISIBLE);
				
			}
		});
		
	}
	
	public void displayTheNotification(String id, String title, String desc) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		   Log.e("Status","Displaying notification");
		   
		   String ns = Context.NOTIFICATION_SERVICE;
		   NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		 
		   int icon = R.drawable.icon;
		   CharSequence tickerText = title;
		   long when = System.currentTimeMillis();
		   Notification notification = new Notification(icon, tickerText, when);
		   notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		   
		   Context context = getApplicationContext();
		   CharSequence contentTitle = title;
		   CharSequence contentText = desc;
		   Intent notificationIntent = new Intent(this, BlogActivity.class);
		   PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		   notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			
		   mNotificationManager.notify(HELLO_ID, notification);
		
	}


	/**
	 * Show Favorites
	 */
	public void setCategories()
	{
		Log.e("Change", "Set Categories");
		//remove from my videos
		this.mynews.clear();
		isRecent=false;

		//Add uploads
		for(int i=0;i<this.categories.size();i++)
		{
			this.mynews.add(this.categories.get(i));
		}

		/**
		 * Update the GUI
		 * 		SHOW the favorites
		 *      HIDE Loading indicator
		 */
		runOnUiThread(new Runnable() 
		{
			public void run() 
			{
				//Notify adapter
				adapter.notifyDataSetChanged();
				m_ProgressBar.setVisibility(View.INVISIBLE);
				
			}
		});
		
	}
	
	

}