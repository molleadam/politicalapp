package com.vittoriocontarina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.vittoriocontarina.AsyncRunner.RequestListener;

/**
 * @description
 * This Class is the GalleryActivity (The List View Gallery Activity) 
 * THIS IS THE FIRST - START ACTIVITY
 */
public final class GalleryActivity extends TrackedListActivity {

	//Simple in-memory image cache. We will manually clean it in the end
	public static HashMap<String, Drawable> globalCache=null;

	//####### GALLERY PROPERTIES ####################
	//Do we use NextGenGallery
	public static boolean wp_gallery;
	
	//TODO: Do we use FacebookGallery, we will use this in another version
	public static boolean fb_gallery;

	//Indicate where is the setting pulled from resources, or from the intents
	public static boolean from_resources;
	//###############################################

	//#######  IMAGE RESIZER SETTINGS ###############
	//Do  we use timthumb - resizer
	public static boolean useImageResizer;

	//The location where our timthumb script is located
	//Defined here to be accessible in all classes
	public static String timthumbLocation;

	//Default image quality
	public static int imageQuality; 
	//################################################

	//############ DATA FOR THE LIST #################
	//Image list adapter
	private ImageListAdapter adapter;

	//Indicate if it loaded all the data
	private boolean all_data_loaded;

	//List of galleries
	private ArrayList<Object> galleries  = null;

	/**
	 * Asynchronous runner
	 * Replace the string with your own server url
	 * We use this Runner to retrieve date from remote server asynchronously 
	 */
	public static AsyncRunner mAsyncRunner;

	/**
	 * Asynchronous runner
	 * Fetch the icons of the albums
	 * We use this Runner to retrieve date from remote server asynchronously 
	 */
	public static AsyncRunner fb_cover_mAsyncRunner;


	//Intent to open Gallery Details view
	private Intent galleryDetails;
	//#################################################

	//Animation for the loader
	AnimationDrawable frameAnimation;

	//Linear layout that will represent the loader holder
	LinearLayout ll;



	//Preferences
	public static SharedPreferences gallerySettings; //Get the preferences

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);


		//Set the Content View layout
		setContentView(R.layout.activity);

		//Initialize the timthumb script
		timthumbLocation=this.getString(R.string.resizer_address);
		useImageResizer=true;

		imageQuality=Integer.parseInt(this.getString(R.string.image_quality));

		//Indicate that the data is not loaded
		all_data_loaded=false;

		//Set Type indicator
		Log.e("Parameters:",this.getIntent().toString()+"");
		if(this.getIntent().getBooleanExtra("hasParameters", false))
		{
			//WE HAVE RECEIVED PARAMETERS
			from_resources=false;
			Log.e("Parameter WP:",this.getIntent().getBooleanExtra("isWP", true)+"");
			
			//WE HAVE RECEIVDE NEXTGEN PARAMETERS
			if(this.getIntent().getBooleanExtra("isWP", true))
			{
				wp_gallery=true;
				//If WP_Next_Gen_Gallery
				mAsyncRunner=new AsyncRunner(this.getIntent().getStringExtra("server_address"));
			}
			else
			{
				wp_gallery=false;
				//This is the Facebook albums
				mAsyncRunner=new AsyncRunner(getString(R.string.fql_query));
				fb_cover_mAsyncRunner=new AsyncRunner(getString(R.string.fql_query));

				//Remove the search bar. We can't use search function in facebook
				LinearLayout sb = (LinearLayout) this.findViewById(R.id.SearchBar);
				sb.setVisibility(View.GONE);

			}
		}
		else
		{
			from_resources=true;
			wp_gallery=this.getString(R.string.type).compareTo("nextgen")==0;

			//Set up the AsyncRunnner
			//Decide the app mode Facebook Images / NextGen Gallery / Create your OWN Here
			if(wp_gallery) 
			{
				//If WP_Next_Gen_Gallery
				mAsyncRunner=new AsyncRunner(this.getString(R.string.server_address));
			}
			else
			{			
				//This is the Facebook albums
				mAsyncRunner=new AsyncRunner(getString(R.string.fql_query));
				fb_cover_mAsyncRunner=new AsyncRunner(getString(R.string.fql_query));

				//Remove the search bar. We can't use search function in facebook
				LinearLayout sb = (LinearLayout) this.findViewById(R.id.SearchBar);
				sb.setVisibility(View.GONE);
			}
		}




		//----------------- Initialize Array of Galleries -------------------//
		galleries = new ArrayList<Object>(); //List of galleries, empty at the momment

		//Create new List Image Adapter
		this.adapter = new ImageListAdapter(this, R.layout.ytrow, galleries);
		adapter.setNotifyOnChange(true);

		//Preferences, will help us to detetmine when to clear the cache
		gallerySettings = getSharedPreferences("GalleryPreferences", MODE_PRIVATE);

		//Initialize preferences, update preferences
		SharedPreferences.Editor prefEditor = (getSharedPreferences("GalleryPreferences", MODE_PRIVATE)).edit(); 
		prefEditor.putFloat("times", gallerySettings.getFloat("times", 0)+1);
		prefEditor.commit();
		Log.i("Info:","This is the "+(int)gallerySettings.getFloat("times", 0)+"th time this gallery is oppeneed");

		//Clear cache
		if(((int)gallerySettings.getFloat("times", 0)+"").compareTo(getString(R.string.clean_cache))==0)
		{
			prefEditor = (getSharedPreferences("GalleryPreferences", MODE_PRIVATE)).edit(); 
			prefEditor.putFloat("times", 0);
			prefEditor.commit();
			this.adapter.imageLoader.clearCache();
			Log.i("Info:","The cache is cleared");
		}



		//Initialize the List view
		ListView lv = getListView();

		//Set adapter to this list
		lv.setAdapter(adapter);

		//This is the Search text
		final EditText et = (EditText) findViewById(R.id.SearchTerm);


		//Search Button
		final ImageButton button = (ImageButton) findViewById(R.id.SearchButton);

		//Create Search Button Click Listener
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(et.getText().toString().length()>0) //Query active
				{
					//Show details Image View, with the search parameter
					showDetails(et.getText().toString());
				}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.sq), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});


		//Click Listener for the ListView
		lv.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//Show Detail Gallery View
				showDetails(position);
			}
		});

		//Connect to remote server to fetch galleries
		fetchGalleries();

	}

	/**
	 * Open GalleryDetailActivity to display the images in Grid View, Images from single Gallery
	 * @param position Position of the clicked image in the image list
	 */
	protected void showDetails(int position) 
	{
		//Initialize the intent to open the Detail GalleryView (Grid View)
		this.galleryDetails = new Intent(this, GalleryDeatialsActivity.class);

		//Send the id of the gallery, title and tell it that this is not in Search mode, "isSearch":false
		galleryDetails.putExtra("id",((Galleries)galleries.get(position)).getId());
		galleryDetails.putExtra("isSearch", false);
		galleryDetails.putExtra("title",((Galleries)galleries.get(position)).getTitle());

		//Start activity
		startActivity(galleryDetails);
	}

	/**
	 * Open GalleryDetailActivity to display the images in Grid View, Images from search term
	 * @param term The search term
	 */
	protected void showDetails(String term) 
	{
		//Initialize the intent to open the Detail GalleryView (Grid View)
		this.galleryDetails = new Intent(this, GalleryDeatialsActivity.class); //Intent triger when users clicks on some gallery

		//Send the search term, title and tell it that this is  in Search mode, "isSearch":true
		galleryDetails.putExtra("term",term);
		galleryDetails.putExtra("title",term);
		galleryDetails.putExtra("isSearch", true);

		//Start activity
		startActivity(galleryDetails);
	}

	/**
	 * Connect to remote server to retrieve JSON formated output
	 * Here we are going to fill the array of galleries
	 */
	private void fetchGalleries() 
	{

		/**
		 * Create Bundle of parameters that you will query , Both fot Facebook and NextGen
		 */
		Bundle params = new Bundle();

		/**
		 * Sub path to your server api
		 * Sub path to Facebook Page Graph API
		 *  For example your server url is "http://vordol.com/store/components/andgallery/" ->> WORDPRESS URL
		 *  For example facebook page url of albums "https://graph.facebook.com/PAGE_ID/albums"
		 *  And you want to get images from the NextGenGallery / Facebook
		 *  Then the path (call) is "wp-content/gallery/album/" / albums
		 */
		String call="";
		if(wp_gallery)//If we use the wp gallery
		{
			call=this.getString(R.string.sub_url);

			//Add parameters
			params.putString("callback", "json");
			params.putString("method", "autocomplete");
			params.putString("type", "gallery");
			params.putString("format", "json");
			params.putString("api_key","true");
		}
		else //Facebook
		{
			if(from_resources)
			{
				//Add parameters
				params.putString("format", "json");
				params.putString("query", String.format(getString(R.string.fql_query_albums), getString(R.string.fb_page_id)));

				Bundle params2 = new Bundle();
				//Add parameters
				params2.putString("format", "json");
				params2.putString("query", String.format(getString(R.string.fql_query_cover_photos), getString(R.string.fb_page_id)));
				GalleryActivity.fb_cover_mAsyncRunner.requestYourApi(call,params2, new yourappListener());
			}
			else
			{
				//Add parameters
				params.putString("format", "json");
				params.putString("query", String.format(getString(R.string.fql_query_albums), this.getIntent().getStringExtra("fbId")));

				Bundle params2 = new Bundle();
				//Add parameters
				params2.putString("format", "json");
				params2.putString("query", String.format(getString(R.string.fql_query_cover_photos), this.getIntent().getStringExtra("fbId")));
				GalleryActivity.fb_cover_mAsyncRunner.requestYourApi(call,params2, new yourappListener());
			}

		}

		//Call the API using the  AsyncRunner
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

			if(wp_gallery) //WP_NEXT_GEN
			{
				all_data_loaded=true;
				/**
				 * Now try to parse the JSON RESPONSE
				 * IF YOU ARE CONNECTING TO OTHER API THE NextGEN
				 * you have to change the code bellow to suite your needs 
				 */
				try 
				{
					//Create  JSONArray of Galleries
					JSONArray items = Util.parseJsonArray(response);

					//Constructing the galleries
					for(int i =0;i<items.length();i++)
					{
						//Extract one gallery JSON Object
						JSONObject one_gallery = items.getJSONObject(i);

						//Create the variables
						String id = one_gallery.getString("id");
						String title=one_gallery.getString("value");
						String desc=one_gallery.getString("desc");

						//If gallery have front image only
						if(one_gallery.getString("tumb").compareTo("false")!=0)
						{
							//Get the tumbnail location
							String location=one_gallery.getJSONObject("tumb").getString("thumbURL");

							//Add gallery to the Array of images
							galleries.add(new Galleries(title, desc, location, id));
						}
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
                    Log.e("Image size",(items.length()+1)+"---");
					if(galleries.size()==0)//Create gallery, the first time, the data is not loaded
					{
						for(int i=0;i<(items.length());i++)
						{
							galleries.add(new Galleries());
						}
					}
					else
					{
						//Now it's loaded
						all_data_loaded=true;
					}

					//Constructing the galleries
					for(int i=0;i<items.length();i++)
					{
						
						
						Galleries g=(Galleries) galleries.get(i);
						
						//Extract one gallery JSON Object
						JSONObject one_gallery = items.getJSONObject(i);						
						Log.e("Images","hereee");
						if(one_gallery.has("aid"))//This is album
						{
							//Create the variabless
							String id = one_gallery.getString("aid");
							String title=one_gallery.getString("name");
							String desc="";
							if(one_gallery.has("description"))
							{
								desc=one_gallery.getString("description");
							}
							
							//Set
							//galleries.add(new Galleries(title, desc, null, id));
							g.setId(id);
							g.setDesc(desc);
							g.setTitle(title);
						}
						else //This is album photo
						{
							String location=one_gallery.getString("src_small");
							//galleries.add(new Galleries("", "", location, ""));
							g.setImgLocation(location);
						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReadError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			if(all_data_loaded)
			{
				/**
				 * Update the GUI
				 * 		SHOW the galleries
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
					}
				});
			}




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