package com.vittoriocontarina;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Toast;

/**
 * @description
 * This Class is the Gallery Scroll Activity
 */
public class GalleryScrollerActivity extends Activity 
{
	//Gallery View variable
	private Gallery g;

	String folder;
	String filename;

	//Position of the image
	int position=0;

	//The adapter for the Gallery View
	private ImageListAdapter adapter;

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		//Clear the cache from the image loader
		adapter.imageLoader.clearCache();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gallery_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.download_image_button:
		{
			downloadImage(false);

		}
		case R.id.set_as_wallpaper:
		{
			setWallPaper();
		}

		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//Set the Content View layout
		setContentView(R.layout.galleryscroll);

		//Set the title of the application
		setTitle(getString(R.string.app_name)+":"+this.getIntent().getStringExtra("title"));

		//Find the view that represents the Gallery Component 
		g = (Gallery) findViewById(R.id.scrollgallery);

		//Create listener for gallery. Triggered when user changes (scrolls) photo 
		g.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				// Display a Toast with the image number
				CharSequence text = (arg2+1)+"/"+GalleryDeatialsActivity.images.size();
				position=arg2;
				displayToast(text,Toast.LENGTH_SHORT);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});


		//Create listener for gallery. Triggered when user clisck photo 
		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
			{
				// Display a Toast with the image description
				CharSequence text = getString(R.string.description)+((Photo)GalleryDeatialsActivity.images.get(arg2)).getDesc();

				if(getString(R.string.show_description_at_click).compareTo("true")==0&&text.length()>getString(R.string.description).length()+2)
				{		
					displayToast(text,Toast.LENGTH_LONG);
				}
			}
		});

		//Define the image adapter
		this.adapter = new ImageListAdapter(this,0,GalleryDeatialsActivity.images);
		g.setAdapter(this.adapter);

		//Get the image position from passed parameter
		position=this.getIntent().getIntExtra("position", 0);

		//Set selected photo
		g.setSelection(position);




		Button download=(Button) findViewById(R.id.Button01);

		//Create All Button Click Listener
		download.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				downloadImage(false);
			}


		});


		Button setWp=(Button) findViewById(R.id.Button02);

		//Create All Button Click Listener
		setWp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setWallPaper();
			}


		});

		

	}
	/**
	 * Set as Wallpaper
	 */
	protected void setWallPaper() {

		Photo p=((Photo)GalleryDeatialsActivity.images.get(position));

		String url=p.bigImgLocation;
		boolean imagehaveWH=p.isSetWH();


		//If we use resizer , resize the image to correct window resolution
		if(imagehaveWH)
		{
			//Find the device width and height
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			//Find the photo mode, Vertical or Horisontal image, you may decide to take different actions
			@SuppressWarnings("unused")
			boolean landscapeMode=true;
			if((p.getW()/p.getH())>0) //The image is Portrait
			{
				landscapeMode=false;
			}

			
			String toReplace=this.getString(R.string.server_address);
			url=url.replace(toReplace, ""); //Now we have the url of the image

			

			url=this.getString(R.string.resizer_address)+"?src="+url;

			url=url+"&q=100&h="+metrics.heightPixels;

			Log.i("image_resized",url);


		}
		displayToast(getString(R.string.seted_wallpaper),Toast.LENGTH_SHORT);

		new Thread(new Runnable() {
			public void run() {
				adapter.imageLoader.setWallaper(((Photo)GalleryDeatialsActivity.images.get(position)).realimage,getApplicationContext());
			}
		}).start();






	}

	/**
	 * Download Image
	 * @param b 
	 */
	public String downloadImage(boolean toPublish) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		String url=((Photo)GalleryDeatialsActivity.images.get(position)).realimage;

		folder="";
		if(toPublish)
		{
			folder="Pictures/";
		}
		else
		{
			folder=getString(R.string.save_folder);
		}

		int start=((Photo)GalleryDeatialsActivity.images.get(position)).realimage.lastIndexOf("/");
		filename=((Photo)GalleryDeatialsActivity.images.get(position)).realimage.substring(start);
		Log.e("filename:",filename);

		new Thread(new Runnable() {
			public void run() {
			    adapter.imageLoader.saveImage(((Photo)GalleryDeatialsActivity.images.get(position)).realimage,folder,filename,GalleryScrollerActivity.this);

			}
		}).start();


		if(!toPublish)
		{
			displayToast(getString(R.string.downloaded_image),Toast.LENGTH_SHORT);
		}

		return folder+filename;

	}

	/**
	 * 
	 * @param text Text To be displayed
	 * @param length Toast.LENGTH_LONG or Toast.LENGTH_SHORT
	 */
	public void displayToast(CharSequence text,int length)
	{
		Toast toast = Toast.makeText(getApplicationContext(), text, length);
		toast.show();
	}

}
