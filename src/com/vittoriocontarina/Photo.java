package com.vittoriocontarina;

import android.util.Log;

/**
 * 
 * @author Daniel
 * This class is abstract representation of Photo
 * Each gallery has title, description,smallImage location id and bigImage location 
 * You can add additional parameters
 */
public class Photo {
	String title,desc,imgLocation,id,bigImgLocation,realimage;
	int w,h;
	boolean setWH;



	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public boolean isSetWH() {
		return setWH;
	}

	public void setImageWH(int w, int h)
	{
		this.setWH=true;
		this.w=w;
		this.h=h;
	}
	public String getBigImgLocation() {
		return bigImgLocation;
	}

	public void setBigImgLocation(String bigImgLocation) {
		this.bigImgLocation = bigImgLocation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String date) {
		this.desc = date;
	}

	public String getImgLocation() {
		return imgLocation;
	}

	public void setImgLocation(String imgLocation) {
		this.imgLocation = imgLocation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Photo(String title, String date, String imgLocation, String id,
			String bigImgLocation,int w, int h) {
		super();
		this.title = title;
		this.desc = date;
		this.imgLocation = imgLocation;
		this.bigImgLocation=bigImgLocation;
		
		//Use the thimthumb only if it's enabled and we are in NextGenGallery
		if(GalleryActivity.wp_gallery&&GalleryActivity.useImageResizer)
		{
			this.bigImgLocation=GalleryActivity.timthumbLocation+"?src="+bigImgLocation+"&w="+w+"&q="+GalleryActivity.imageQuality;
		}
		
		Log.i("info",this.bigImgLocation);
		this.id = id;
		this.setWH=false;
		this.realimage=bigImgLocation;
	}



}
