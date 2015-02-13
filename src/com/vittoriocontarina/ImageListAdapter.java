package com.vittoriocontarina;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Used to adapt the images to the List
 * Example from Android Documentation for List VIEW
 */
public class ImageListAdapter extends ArrayAdapter<Object> 
{
	//This can be everything, but in our case is a ArrayList of Galleries and Photos
	private ArrayList<Object> items;

	//Activity Context
	private Activity activity;

	//Image Loader that will  asynchronously load the images
	public ImageLoader imageLoader; 

	//The Image manipulation 
	ImageView imageIcon;

	private String imageResizer="";


	//The constructor
	public ImageListAdapter(Context context, int textViewResourceId,ArrayList<Object> objects) 
	{
		super(context, textViewResourceId, objects);
		this.items = objects;
		this.activity = (Activity)context;
		imageLoader=new ImageLoader(activity.getApplicationContext(),R.drawable.loadimg);
		imageResizer=this.activity.getString(R.string.resizer_address);
	}

	//Our image holder
	public static class ViewHolder
	{
		public ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		//Get the Converted VIEW
		View v = convertView;

		//Get the Current Item
		Object o = items.get(position);

		//Create view Holder
		ViewHolder holder=new ViewHolder();

		//Create  LayoutInflater to Inflate the views
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Log.e("kade:","nadvor od event view GALLERY");
		//THE OBJECT WE ARE ADAPT ARE OF TYPE  Galleries
		if(o instanceof com.vittoriocontarina.Galleries)
		{	
			//Inflate view
			v = vi.inflate(R.layout.ytrow, null);

			//Get the view that we are inserting the image
			holder.image=(ImageView)v.findViewById(R.id.imageIcon);

			//Tag of the ListView row
			v.setTag(holder);

			//Setting up the text in the list
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.botomntext);

			//Set up the text
			if (tt != null) 
			{
				tt.setText(((Galleries)o).getTitle());
				bt.setText(((Galleries)o).getDesc());
			}

			//Set up the image icon
			imageIcon = (ImageView) v.findViewById(R.id.imageIcon);

			//The location (URI) of the image to be loaded
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			String location_image = ((Galleries)o).getImgLocation();
			holder.image.setTag(((Galleries)o).getId());

			//Display the image asynchronously
			if(location_image.length()>3)
			{
				imageLoader.DisplayImage(location_image, activity, holder.image,((Galleries)o).getId());
			}



		}

		if(o instanceof com.vittoriocontarina.YTVideos)
		{	
			//Inflate view
			v = vi.inflate(R.layout.videorow, null);

			//Get the view that we are inserting the image
			holder.image=(ImageView)v.findViewById(R.id.imageIcon);

			//Tag of the ListView row
			v.setTag(holder);

			//Setting up the text in the list
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			TextView tt = (TextView) v.findViewById(R.id.toptext);

			//Set up the text
			if (tt != null) 
			{
				tt.setText(((YTVideos)o).getTitle());
			}

			//Set up the image icons
			imageIcon = (ImageView) v.findViewById(R.id.imageIcon);

			//The location (URI) of the image to be loaded
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			imageLoader.default_picture=R.drawable.loadimgextrabig;
			String location_image = ((YTVideos)o).getImgLocation();
			imageLoader.setAsBackground(true);
			holder.image.setTag(((YTVideos)o).getId());


			//Display the image asynchronously
			imageLoader.DisplayImage(location_image, activity, holder.image,((YTVideos)o).getId());        
		}
		if(o instanceof com.vittoriocontarina.Articl)
		{	
			//Inflate view
			v = vi.inflate(R.layout.ytrow, null);

			//Get the view that we are inserting the image
			holder.image=(ImageView)v.findViewById(R.id.imageIcon);

			//Tag of the ListView row
			v.setTag(holder);

			//Setting up the text in the list
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.botomntext);

			//Set up the text
			if (tt != null) 
			{
				tt.setText(((Articl)o).getName());
				bt.setText(((Articl)o).getDescr());
			}

			//Set up the image icon
			imageIcon = (ImageView) v.findViewById(R.id.imageIcon);

			//The location (URI) of the image to be loaded
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			String location_image = ((Articl)o).getThumb();
			holder.image.setTag(((Articl)o).getId());

			//Display the image asynchronously
			if(((Articl)o).getThumb()!="")
			{
				imageLoader.DisplayImage(location_image, activity, holder.image,((Articl)o).getId());
				imageLoader.default_picture=R.drawable.loadimgextrabig;
			}
			else
			{
				LinearLayout ll=(LinearLayout)v.findViewById(R.id.imageHolderLayout);
				ll.setVisibility(View.GONE);
			}


		}
		if(o instanceof com.vittoriocontarina.FeaturedArticl)
		{	
			//Inflate view
			v = vi.inflate(R.layout.fearow, null);




			//Get the view that we are inserting the image
			holder.image=(ImageView)v.findViewById(R.id.feaImage);
			imageIcon = (ImageView) v.findViewById(R.id.feaImage);
			Log.e("Image w",((FeaturedArticl)o).getW()+"");
			Log.e("Image h",((FeaturedArticl)o).getH()+"");

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)((FeaturedArticl)o).getW(), (int)((FeaturedArticl)o).getH());
			imageIcon.setLayoutParams(layoutParams);

			



			//Tag of the ListView row
			v.setTag(holder);

			//Setting up the text in the list
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			TextView tt = (TextView) v.findViewById(R.id.theFeaTitle);

			//Set up the text
			if (tt != null) 
			{
				tt.setText(((Articl)o).getName());
			}


			//The location (URI) of the image to be loaded
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			String location_image = ((FeaturedArticl)o).getImage();
			holder.image.setTag(((Articl)o).getId());
			imageLoader.default_picture=R.drawable.loadimgextrabig;
			imageLoader.setAsBackground(true);

			//Display the image asynchronously
			imageLoader.DisplayImage(location_image, activity, holder.image,((Articl)o).getId());

		}
		if(o instanceof com.vittoriocontarina.Category)
		{	
			//Inflate view
			v = vi.inflate(R.layout.catrow, null);



			//Tag of the ListView row
			v.setTag(holder);

			//Setting up the text in the list
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			TextView tt = (TextView) v.findViewById(R.id.toptext);

			//Set up the text
			if (tt != null) 
			{
				tt.setText(((Category)o).getName());
			}
		}

		//THE OBJECT WE ARE ADAPT ARE OF TYPE  Photo
		else if(o instanceof com.vittoriocontarina.Photo)
		{
			//Current image view
			v = new ImageView(activity);
			holder.image=(ImageView)v;

			//Tag of the image
			v.setTag(holder);

			DisplayMetrics metrics = new DisplayMetrics();
			((Activity)this.activity) .getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Log.e("Density",metrics.widthPixels+"");
			if(metrics.densityDpi==240&&((Activity)this.activity) .getWindowManager().getDefaultDisplay().getOrientation()==0)
			{
				Log.e("orient",((Activity)this.activity) .getWindowManager().getDefaultDisplay().getOrientation()+"");

				((ImageView)v).setLayoutParams(new Gallery.LayoutParams(480, 720));
				//imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
			else if (metrics.densityDpi==160)
			{
				//imageIcon.setLayoutParams(new ListView.LayoutParams(320, 200));
				//imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}

			//((ImageView)v).setScaleType(ImageView.ScaleType.FIT_XY);


			//Set up the image icon
			imageIcon = (ImageView)v;

			//The location (URI) of the image to be loaded
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			String location_image = ((Photo)o).getBigImgLocation();
			holder.image.setTag(((Photo)o).getId());

			//Set new default image for loading
			//YOU CAN CHANGE THIS IN YOUR PROJECT
			imageLoader.changeDefaultImage(R.drawable.big_transparent);

			//Display the image asynchronously
			imageLoader.DisplayImage(location_image, activity, holder.image,((Photo)o).getId());

		}


		//Return the VIEW
		return v;
	}


}
