package com.vittoriocontarina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vittoriocontarina.AsyncRunner.RequestListener;

public class ArticlActivity extends TrackedListActivity {
	
	//Image list adapter
	private ImageListAdapter adapter;
	
	//Activity indicator
    ProgressBar m_ProgressBar=null;

	//List of videos
	private ArrayList<Object> mynews  = null;
	
	//Linear layout that will represent the loader holder
	LinearLayout ll;
	
	EasyTracker tracker;
	
	/***
	 * Asynchronous runner
	 * Replace the string with your own server url
	 * We use this Runner to retrieve date from remote server asynchronously 
	 */
	public static AsyncRunner mAsyncRunnerca; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Window with no title bar, You can remove in your project if you like Title tab
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Set layout
		setContentView(R.layout.newslist);
		
		//Init Progress bar
		m_ProgressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
		
		tracker=EasyTracker.getTracker();

		
		//Set Api location
	    String serverLocation=this.getString(R.string.server_address);
		mAsyncRunnerca = new AsyncRunner(serverLocation+"api/get_category_posts/");
		
		 //Set up title
		TextView tt = (TextView) this.findViewById(R.id.trantitle);
        String header=this.getIntent().getStringExtra("catname");
		tt.setText(header);

		//----------------- Initialize Array of Videos -------------------//
		mynews = new ArrayList<Object>(); //List of my vidoes, empty at the momment

		//Create new List Image Adapter
		this.adapter = new ImageListAdapter(this, R.layout.ytrow, mynews);
		adapter.setNotifyOnChange(true);

		//Initialize the List view
		ListView lv = getListView();

		//Set adapter to this list
		lv.setAdapter(adapter);

		//Click Listener for the ListView, Open youtube window
		lv.setOnItemClickListener(new OnItemClickListener(){

			//@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//Show Detail Gallery View
				showArticlDetails(position);
				
			}
		});

		//Connect to remote server to fetch my upload videos
		fetcArticls(this.getIntent().getStringExtra("catid"));
		
		

	}
	
	 

	/**
	    * Opnens Youtube App or other listener
	    * @param position - position of the video
	    */
		protected void showArticlDetails(int position) {
			// TODO Auto-generated method stub
			//Start activity
			
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
	 * Connect to remote server (YOUTUBE API) to retrieve JSON formated output
	 * Here we are going to fill the array of My Videos
	 * LOADS MY UPLOAD VIDEOS
	 */
	private void fetcArticls(String index) 
	{
		
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("id", index);
		//params.putString("exclude", "content");
		
		//Show progress bar
		m_ProgressBar.setVisibility(View.VISIBLE);


		//Call the API using the  AsyncRunner
		ArticlActivity.mAsyncRunnerca.requestYourApi("",params, new yourappListener());
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
			
			System.out.println(response);/**
			 * 
			 * Now try to parse the JSON RESPONSE
			 * you have to change the code bellow to suite your needs 
			 */
			try 
			{
				//Create  JSONArray of Videos
				JSONArray items = Util.parseJson(response).getJSONArray("posts");

				//Constructing the galleries
				for(int i =0;i<items.length();i++)
				{
					//Extract one gallery JSON Object
					JSONObject one_articl = items.getJSONObject(i);

					//Create the variables
					String id = one_articl.getString("id");
					String name=one_articl.getString("title");
					String url=one_articl.getString("url");
					String author=one_articl.getJSONObject("author").getString("name");
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
								if(attach.getJSONObject(j).getJSONObject("images").has("medium"))
								{
									image=attach.getJSONObject(j).getJSONObject("images").getJSONObject("medium").getString("url");
									imageSet=true;
									w=attach.getJSONObject(j).getJSONObject("images").getJSONObject("medium").getDouble("width");
									h=attach.getJSONObject(j).getJSONObject("images").getJSONObject("medium").getDouble("height");
								}
							}
						}
						
					}
					
					if(!thumbSet&&one_articl.has("thumbnail"))
					{
						thumb=one_articl.getString("thumbnail");
					}

					//Add video to the Array of images
					mynews.add(new Articl(name, thumb, date,desc, id,image,url,content,author,w,h,ArticlActivity.this));

					
				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("Articls", "Articls are loaded");
			//Show my uploads
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
	

}
