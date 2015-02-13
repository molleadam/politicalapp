package com.vittoriocontarina;

/**
 * 
 * @author Daniel
 * This class is abstract representation of Gallery or Album
 * Each gallery has title, description,front image location and id 
 * You can add additional parameters
 */
public class Galleries 
{
	String title,description,imgLocation,id;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return description;
	}
	public void setDesc(String date) {
		this.description = date;
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

	//Gallery constructor
	public Galleries() 
	{
		super();
		this.title = "";
		this.description = "";
		this.imgLocation = "";
		this.id = "";
	}


	//Gallery constructor
	public Galleries(String title, String date, String imgLocation, String id) 
	{
		super();
		this.title = title;
		this.description = date;
		this.imgLocation = imgLocation;
		this.id = id;
	}
}
