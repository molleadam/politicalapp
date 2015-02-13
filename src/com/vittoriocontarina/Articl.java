package com.vittoriocontarina;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Articl {
String name,thumb,date,descr,id,image,url,content,author;
double w,h;

public Articl(String name, String thumb, String date, String descr, String id,
		String image, String url, String content,String author,double w,double h,Context c) {
	super();
	this.name = name;
	this.thumb = thumb;
	this.date = date.substring(0, 16);
	this.descr = descr;
	this.id = id;
	this.image = image;
	this.url = url;
	this.content = content;
	this.author=author;
	
	if(this.image.length()>3)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)c) .getWindowManager().getDefaultDisplay().getMetrics(metrics);
		

		Log.e("Image w",w+"");
		Log.e("Image h",h+"");

		double index=metrics.widthPixels/w;
		int desiredHeight=(int) (h*index);

		this.image=((Activity)c).getString(R.string.resizer_address)+"?src="+image+"&w"+metrics.widthPixels+"&h="+desiredHeight+"&zc=1&q=100";
		this.w=metrics.widthPixels;
		this.h=desiredHeight;

		Log.d("TIMTHUMB","-----------IMAGE INFO -----------");
		Log.d("TIMTHUMB","Articl:"+name);
		Log.d("TIMTHUMB","Orginal W:"+w);
		Log.d("TIMTHUMB","Orginal H:"+h);
		Log.d("TIMTHUMB"," Device W:"+metrics.widthPixels);
		Log.d("TIMTHUMB","    Index:"+index);
		Log.d("TIMTHUMB","Desired H:"+desiredHeight);
		Log.d("TIMTHUMB","Image Location:"+this.image);
	}
	
	
}


public String getContent() {
	return content;
}


public void setContent(String content) {
	this.content = content;
}


public String getUrl() {
	return url;
}


public void setUrl(String url) {
	this.url = url;
}


public String getImage() {
	return image;
}


public void setImage(String image) {
	this.image = image;
}


public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getThumb() {
	return thumb;
}

public void setThumb(String thumb) {
	this.thumb = thumb;
}

public String getDate() {
	return date;
}

public void setDate(String date) {
	this.date = date;
}

public String getDescr() {
	return descr;
}

public void setDescr(String descr) {
	this.descr = descr;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}


public String getAuthor() {
	return author;
}




public double getW() {
	return w;
}


public void setW(double w) {
	this.w = w;
}


public double getH() {
	return h;
}


public void setH(double h) {
	this.h = h;
}


public void setAuthor(String author) {
	this.author = author;
}



}
