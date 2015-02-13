package com.vittoriocontarina;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Used to adapt the images to the grid 
 * Example from Android Documentation for GRID VIEW
 */
public class ImageGridAdapter extends BaseAdapter 
{
	//Activity context
	private Context activity;

	//This can be everything, but in our case is a ArrayList of Photos
	private ArrayList<Object> items;

	//Image Loader that will  asynchronously load the images
	public ImageLoader imageLoader; 


	//Constructor of the adapter
	public ImageGridAdapter(Context c, ArrayList<Object> objects)
	{
		activity = c;
		this.items = objects;
		imageLoader=new ImageLoader(activity.getApplicationContext(),R.drawable.loadimgbig);
	}

	//Our image holder
	public static class ViewHolder
	{
		public ImageView image;
	}

	// create a new ImageView for each item referenced by the Adapter
	//@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//The image to be returned to the Grid view
    	ImageView v;
    	
    	//If View is displayed before, recycle it.
		if (convertView == null) {  // if it's not recycled, initialize some attributes
			
			//Set parameters to the image View, you can change this in your app
			v = new ImageView(activity);
			
			
			//If screen is in hight density, set the fea adapter to set the images as backgrounds
		     DisplayMetrics metrics = new DisplayMetrics();
		    ((Activity)this.activity) .getWindowManager().getDefaultDisplay().getMetrics(metrics);
		     
		     
	        if(metrics.densityDpi==240)
		     {
	       
	        	v.setLayoutParams(new GridView.LayoutParams(97, 97)); //The size of the displayed image
				v.setScaleType(ImageView.ScaleType.CENTER_CROP);
		     }
		     else if (metrics.densityDpi==160)
		     {
		    	 v.setLayoutParams(new GridView.LayoutParams(65, 65)); //The size of the displayed image
					v.setScaleType(ImageView.ScaleType.CENTER_CROP);
		     }
		     else if (metrics.densityDpi==120)
		     {
		    	 v.setLayoutParams(new GridView.LayoutParams(50, 50)); //The size of the displayed image
					v.setScaleType(ImageView.ScaleType.CENTER_CROP);
		     }
	        
			
			
			
			
		} else {
			//use the convertedView
			v = (ImageView) convertView;
		}

		Object o = items.get(position);

		//The view holder
		ViewHolder holder=new ViewHolder();
		holder.image=v;

		//Get the image location
		String location_image = ((Photo)o).getImgLocation();
		holder.image.setTag(((Photo)o).getId());
		
		//Assign image for loading
		imageLoader.DisplayImage(location_image, (Activity) activity, holder.image,((Photo)o).getId());

        //Return the image to the Grid View
		return v;
	}


	//Count the number of elements to be displayed
	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) 
	{
		return 0;
	}
}
