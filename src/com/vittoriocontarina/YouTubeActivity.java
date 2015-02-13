package com.vittoriocontarina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vittoriocontarina.AsyncRunner.RequestListener;

public class YouTubeActivity extends TrackedListActivity {

	//Image list adapter
	private ImageListAdapter adapter;
	
	//Activity indicator
    ProgressBar m_ProgressBar=null;

	//List of videos
	private ArrayList<Object> myvideos  = null;
	private ArrayList<Object> myuploads  = null;
	private ArrayList<Object> myfavorites  = null;

	//Animation for the loader
	AnimationDrawable frameAnimation;

	//Linear layout that will represent the loader holder
	LinearLayout ll;
	
	//Create the two buttons
   // RadioButton myuploadstab,myfavoritestab;
    
    //Is data loaded
    boolean uploads,favorites=false;

	/***
	 * Asynchronous runner
	 * Replace the string with your own server url
	 * We use this Runner to retrieve date from remote server asynchronously 
	 */
	public static AsyncRunner mAsyncRunnerf; 
	public static AsyncRunner mAsyncRunneru;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set layout
		setContentView(R.layout.youtubelayout);
		
		//Init Progress bar
		m_ProgressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
		
		//Set Api location
		mAsyncRunnerf = new AsyncRunner("http://gdata.youtube.com/feeds/api/users/"+this.getString(R.string.user_favorites)+"/favorites/");
		mAsyncRunneru = new AsyncRunner("http://gdata.youtube.com/feeds/api/users/"+this.getString(R.string.user_uploads)+"/uploads/");
		
		 //Set up title
		TextView tt = (TextView) this.findViewById(R.id.trantitle);
        String header=getString(R.string.yt);
		tt.setText(header);

		//----------------- Initialize Array of Videos -------------------//
		myvideos = new ArrayList<Object>(); //List of my vidoes, empty at the momment
		myuploads = new ArrayList<Object>(); //List of my vidoes, empty at the momment
		myfavorites = new ArrayList<Object>(); //List of my favorites, empty at the momment

		//Create new List Image Adapter
		this.adapter = new ImageListAdapter(this, R.layout.ytrow, myvideos);
		adapter.setNotifyOnChange(true);

		//Initialize the List view
		ListView lv = getListView();

		//Set adapter to this list
		lv.setAdapter(adapter);
		

		//Initialize the buttons
		/*myuploadstab=(RadioButton) findViewById(R.id.Button01);
		myuploadstab.setChecked(true);
		myfavoritestab=(RadioButton) findViewById(R.id.Button02);
		myfavoritestab.setChecked(false);
		
		//Create All Button Click Listener, for my upload buttons
		myuploadstab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
              if(uploads)
              {
            	//Show my uploads
  				setUploads();
              }
			}
		});

		//Create Special Button Click Listener, for my favorite buttons
		myfavoritestab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				if(favorites) //if favorites are loaded
				{
					//Show favorites videos
					setFavorites();
					
				}
				else
				{
					//load them
					fetchMyFavorites();
				}

				
			}
		});*/
		
		//Click Listener for the ListView, Open youtube window
		lv.setOnItemClickListener(new OnItemClickListener(){

			//@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//Show Detail Gallery View
				showVideo(position);
				
			}
		});

		//Connect to remote server to fetch my upload videos
		fetchMyVideos();

	}

   /**
    * Opnens Youtube App or other listener
    * @param position - position of the video
    */
	protected void showVideo(int position) {
		// TODO Auto-generated method stub
		//Start activity
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((YTVideos)myvideos.get(position)).getId())));
		
		//Setup look, because it will recreate itself
		//myuploadstab.setChecked(true);
		//myfavoritestab.setChecked(false);
	}


	/**
	 * Connect to remote server (YOUTUBE API) to retrieve JSON formated output
	 * Here we are going to fill the array of My Videos
	 * LOADS MY UPLOAD VIDEOS
	 */
	private void fetchMyVideos() 
	{
		
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("alt", "json");
		
		//Show progress bar
		m_ProgressBar.setVisibility(View.VISIBLE);


		//Call the API using the  AsyncRunner
		YouTubeActivity.mAsyncRunneru.requestYourApi("",params, new yourappListener());
	}
	
	/**
	 * Connect to remote server (YOUTUBE API) to retrieve JSON formated output
	 * Here we are going to fill the array of My Favorites
	 * LOAD MY FAVORITES
	 */
	private void fetchMyFavorites() 
	{
		
		//Show progress bar
		m_ProgressBar.setVisibility(View.VISIBLE);
				
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("alt", "json");


		//Call the API using the  AsyncRunner
		YouTubeActivity.mAsyncRunnerf.requestYourApi("",params, new yourappListenerFavorites());
	}
	

	/**
	 * Listener for the result from the issued API connection, LOAD MY UPLOADS
	 */
	public class yourappListener implements RequestListener
	{

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
				JSONArray items = Util.parseJson(response).getJSONObject("feed").getJSONArray("entry");

				//Constructing the galleries
				for(int i =0;i<items.length();i++)
				{
					//Extract one gallery JSON Object
					JSONObject one_gallery = items.getJSONObject(i);

					//Create the variables
					String id = one_gallery.getJSONObject("media$group").getJSONArray("media$player").getJSONObject(0).getString("url");
					String title=one_gallery.getJSONObject("title").getString("$t");
					
					String desc=one_gallery.getJSONObject("content").getString("$t");
					
					//Get the tumbnail location
					String location=one_gallery.getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(0).getString("url");

					//Add video to the Array of images
					myuploads.add(new YTVideos(title, desc, location, id));

					
				}
				//Indicate that my upload videos are loaded
			    uploads=true;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Show my uploads
           setUploads();


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
	public class yourappListenerFavorites implements RequestListener
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
				JSONArray items = Util.parseJson(response).getJSONObject("feed").getJSONArray("entry");

				//Constructing the Videos
				for(int i =0;i<items.length();i++)
				{
					//Extract one gallery JSON Object
					JSONObject one_gallery = items.getJSONObject(i);

					//Create the variables
					String id = one_gallery.getJSONObject("media$group").getJSONArray("media$player").getJSONObject(0).getString("url");
					String title=one_gallery.getJSONObject("title").getString("$t");
					
					String desc=one_gallery.getJSONObject("content").getString("$t");
					
					//Get the tumbnail location
					String location=one_gallery.getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(0).getString("url");

					//Add gallery to the Array of videos
					myfavorites.add(new YTVideos(title, desc, location, id));
					
				}
				//Indicates that we have loaded the favorites
			    favorites=true;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Show Favorites
			setFavorites();
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
	public void setUploads()
	{
		Log.e("Change", "Set Uploads");
		//remove from my videos
		this.myvideos.clear();
		
		//Add uploads
		for(int i=0;i<this.myuploads.size();i++)
		{
			this.myvideos.add(this.myuploads.get(i));
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
	
	/**
	 * Show Favorites
	 */
	public void setFavorites()
	{
		Log.e("Change", "Set Favorites");
		//remove from my videos
		this.myvideos.clear();
		

		//Add uploads
		for(int i=0;i<this.myfavorites.size();i++)
		{
			this.myvideos.add(this.myfavorites.get(i));
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