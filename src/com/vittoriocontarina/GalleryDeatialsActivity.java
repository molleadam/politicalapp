package com.vittoriocontarina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vittoriocontarina.AsyncRunner.RequestListener;

/**
 * @description
 * This Class is the Gallery Details Activity (The Grid View Activity) 
 */
public class GalleryDeatialsActivity extends Activity {

	//Simple in-memory image cache. We will manually clean it in the end
	public static HashMap<String, Drawable> globalCache=null;

	//Image list adapter
	private ImageGridAdapter adapter;
	
	//The display
	DisplayMetrics metrics;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		//Clear the cache from the image loader
		adapter.imageLoader.clearCache();
	}

	//List of Photos
	public static  ArrayList<Object> images  = null;

	/**
	 * Asynchronous runner
	 * Replace the string with your own server url
	 * We use this Runner to retrieve date from remote server asynchronously 
	 */
	public static AsyncRunner mAsyncRunner;

	//Intent to open Gallery Scroll view
	private Intent galleryScroll;

	//Linear layout that will represent the loader holder
	LinearLayout ll;

	//Animation for the loader
	AnimationDrawable frameAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//Set the Content View layout
		setContentView(R.layout.gallerydetails);
		
		//Get device display metrics
		metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		//Initialize the array  of static images
		images=new ArrayList<Object>();

		//Set up the AsyncRunnner
		//Decide the app mode Facebook Images / NextGen Gallery / Create your OWN Here
		if(GalleryActivity.wp_gallery) 
		{
			//If WP_Next_Gen_Gallery
			mAsyncRunner=new AsyncRunner(this.getString(R.string.server_address));
		}
		else
		{			
			//This is the Facebook albums
			mAsyncRunner=new AsyncRunner(getString(R.string.fql_query));
		}

		//Set the title of the application
		setTitle(getString(R.string.app_name)+":"+this.getIntent().getStringExtra("title"));


		//----------------- Initialize Array of Images -------------------//
		images = new ArrayList<Object>(); //List of images, empty at the moment 

		//Create new Grid Image Adapter
		adapter=new ImageGridAdapter(this,images);

		//Get the grid view
		GridView gridview = (GridView) findViewById(R.id.gridview);

		//Set adapter to the grid view
		gridview.setAdapter(adapter);

		//Create Listener for the grid view
		//Triggered when user click on some image
		gridview.setOnItemClickListener(

				new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,int position, long id) {

						//Open the scroll gallery with image position clicked as parameter
						openScrollGallery(position);
					}
				}
				);

		//Connect to remote server to fetch photos
		fetchPhotos();
	}


	/**
	 * Open GalleryScrollerActivity to display the images in Scrolling View
	 * @param position Position of the clicked image in the image list
	 */
	protected void openScrollGallery(int position) 
	{
		//Define the Intent that will open the Scroll Gallery
		this.galleryScroll = new Intent(this, GalleryScrollerActivity.class); 

		//Put Position and Title as Parameters to the activity we are going to open 
		this.galleryScroll.putExtra("position", position);
		this.galleryScroll.putExtra("title", this.getIntent().getStringExtra("title"));

		//Start the activity
		startActivity(galleryScroll);

	}

	/**
	 * Connect to remote server to retrieve JSON formated output
	 * Here we are going to fill the array of images
	 */
	private void fetchPhotos() 
	{
		/**
		 * Create Bundle of parameters that you will query 
		 */
		Bundle params = new Bundle();
		params.putString("format", "json");

		/**
		 * Sub path to your server api
		 *  For example your server url is "http://vordol.com/store/components/andgallery/" ->> WORDPRESS URL
		 *  And you want to get images from the NextGenGallery 
		 *  Then the path (call) is "wp-content/gallery/album/"
		 */
		String call="";
		if(GalleryActivity.wp_gallery)
		{
			call=this.getString(R.string.sub_url);
			params.putString("callback", "json");
			params.putString("api_key","true");

			//If we are in search mode
			//We can determine this by the passed parameter  isSearch
			if(this.getIntent().getBooleanExtra("isSearch", true)) //Search mode
			{
				//Add additional parameters
				//This will result in &method=search&term=term
				params.putString("method", "search"); //We are in search mode
				params.putString("term", this.getIntent().getStringExtra("term")); //Send the term as parameter

			}
			else //Else we are in standard gallery mode, where images from single gallery are displayed
			{
				//Add additional parameters
				//This will result in &method=gallery&id=id
				params.putString("method", "gallery"); //We are in gallery mode 
				params.putString("id", this.getIntent().getStringExtra("id")); //Send the gallery id as parameter
			}

		}
		else
		{
			params.putString("query", String.format(getString(R.string.fql_query_album_details), this.getIntent().getStringExtra("id")));
		}









		//Call the API using the  AsyncRunner from the MainActivity
		GalleryActivity.mAsyncRunner.requestYourApi(call,params, new yourappListener());

		//Inflate the loader view
		//GET THE Linear Top Holder
		ll = (LinearLayout) this.findViewById(R.id.topHolderLoader);

		//Inflate the image load view
		ImageView img = (ImageView) LayoutInflater.from(this).inflate(R.layout.loader_layout, null);

		//Set it's background
		img.setBackgroundResource(R.drawable.loader_animation);

		//Add image to Layout view
		ll.addView(img);

		//Get the background, which has been compiled to an AnimationDrawable object.
		frameAnimation = (AnimationDrawable) img.getBackground();
		frameAnimation.setCallback(img);
		frameAnimation.setVisible(true, true); 

		//After view is initialized, Start the loading animation
		img.post(new Starter());

	}

	public void noResults(String s)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(s)
		.setCancelable(false)
		.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				GalleryDeatialsActivity.this.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}



	/**
	 * Starts the loading animation , when resources are loaded from the file sistem
	 */
	class Starter implements Runnable 
	{
		public void run() {
			frameAnimation.start();
		}
	}


	/**
	 * Listener for the result from the issued API connection
	 */
	public class yourappListener implements RequestListener
	{

		//Triggered when everything is ok, and we have API response
		public void onComplete(String response, Object state) 
		{
			//Log the response
			//COMMENT THIS LINE OF CODE FORE SECURITY REASONS 
			Log.e("Your Api response", response);

			if(GalleryActivity.wp_gallery) //WP_NEXT_GEN
			{
				/**
				 * Now try to parse the JSON RESPONSE
				 * IF YOU ARE CONNECTING TO OTHER API THE NextGEN
				 * you have to change the code bellow to suite your needs 
				 */
				try 
				{
					//Create  JSONArray of Images
					JSONArray items = Util.parseJson(response).getJSONArray("images");

					//Constructing the photos
					for(int i =0;i<items.length();i++)
					{
						//Extract one Image JSON Object
						JSONObject one_photo = items.getJSONObject(i);

						//Create the variables
						String id = one_photo.getString("pid");
						String title=one_photo.getString("title");
						String location=one_photo.getString("thumbURL");
						String locationBig=one_photo.getString("imageURL");
						String desc=one_photo.getString("description");
						int w=one_photo.getJSONObject("meta_data").getInt("width");
						int h=one_photo.getJSONObject("meta_data").getInt("height");

						//Add image to the Array of images
						Photo p=new Photo(title, desc, location, id,locationBig,metrics.widthPixels,metrics.heightPixels);
						p.setImageWH(w, h);
						
						images.add(p);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReadError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else //Facebook albums
			{
				/**
				 * Now try to parse the JSON RESPONSE
				 */
				try 
				{
					//Create  JSONArray of Galleries
					JSONArray items = Util.parseJsonArray(response);

					//Constructing the galleries
					for(int i =0;i<items.length();i++)
					{
						//Extract one gallery JSON Object
						JSONObject one_photo = items.getJSONObject(i);


						//Create the variables
						String id = one_photo.getString("pid");
						String title="";
						String location=one_photo.getString("src");
						String locationBig=one_photo.getString("src_big");
						String desc="";
						if(one_photo.has("description"))
						{
							desc=one_photo.getString("description");
						}

						//Add image to the Array of images
						images.add(new Photo(title, desc, location, id,locationBig,metrics.widthPixels,metrics.heightPixels));

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReadError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/**
			 * Update the GUI
			 * 		SHOW the photos
			 *      HIDE Loading indicator
			 */
			runOnUiThread(new Runnable() 
			{
				public void run() 
				{
					//Notify adapter
					adapter.notifyDataSetChanged();

					//Hide loader
					ll.removeAllViews();

					//Notify adapter
					adapter.notifyDataSetChanged();

					if(images.size()==0)
					{
						noResults(getString(R.string.snr));

					}
				}
			});


		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		public void oniClubError(ReadError e, Object state) {
			// TODO Auto-generated method stub

		}
	}

}
