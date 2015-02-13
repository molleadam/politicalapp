package com.vittoriocontarina;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class is constructed from various tutorials I have read
 * It has the best methods for Asynchronous image loading
 * 
 * CHANGE LOG
 * I have added image load indicators, but they are working good on devices with newer processors 
 */
public class ImageLoader {

	//Simple in-memory image cache. We will manually clean it in the end
	private HashMap<String, Drawable> localCache=new HashMap<String, Drawable>();

	//This is the id of the resource that will be the default picture, (picture while image is loading) 
	int default_picture;
	
	//This is the background loader
	int deafult_loader;
	
	public Boolean loader;
	
	//This indicates to store image as SRC or as Background
	boolean asBackground;
	
	@SuppressWarnings("unused")
	private LinkedList<AnimationDrawable> frameAnimations;
	
	FileCache fileCache;
	

	//Constructor - set the context-the priority, and the default picture
	public ImageLoader(Context context,int default_res_id)
	{
		//This way it will not affect the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
		this.default_picture=default_res_id;
		loader=false;
		asBackground=false;
		 fileCache=new FileCache(context);
	}
	
	//Constructor - set the context-the priority, and the default Loader
	public ImageLoader(Context context,int default_res_id,boolean loaderp)
	{
		//This way it will not affect the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
		this.deafult_loader=default_res_id;
		loader=true;
		frameAnimations=new LinkedList<AnimationDrawable>();
		asBackground=false;
	}
	
	
	
	//Change the default picture
	public void changeDefaultImage(int default_res_id)
	{
		this.default_picture=default_res_id;
	}
	
	//Change the deafault src type to background
	public void setAsBackground(Boolean asBack)
	{
		asBackground=asBack;
	}
	
	
	//Change the default loader
	public void changeDefaultLoader(int default_res_id)
	{
		this.deafult_loader=default_res_id;
		this.loader=true;
	}
	
	//Save image to sd Card
		public void saveImage(String url,String saveFolder,String filename,Context c )
		{
			Bitmap bitmap=getBitmap(url);//Get the Image
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
			
			bitmap.compress(CompressFormat.JPEG, 100, bos); 
			byte[] bitmapdata = bos.toByteArray();
			createExternalStoragePublicPicture(bitmapdata,saveFolder,filename,c);
			
		}
	
	void createExternalStoragePublicPicture(byte[] data,String saveFolder,String filename, Context c) { 
			File root=android.os.Environment.getExternalStorageDirectory();
		    File path = new File(root.getAbsolutePath()+"/"+saveFolder);
		    path.mkdirs();
		    File file = new File(path, filename); 
		    Log.e("lokacija",file.toString());
		    try {
		        // Make sure the Pictures directory exists.
		        path.mkdirs();
		        OutputStream os = new FileOutputStream(file);
		        os.write(data);
		        os.close();
		        
		     // Tell the media scanner about the new file so that it is
		        // immediately available to the user.
		        //MediaScannerConnection.scanFile(file.toString(),null);
		        
		        /*MediaScannerConnection mc = new MediaScannerConnection(this,MediaScannerConnection.MediaScannerConnectionClient);
		        MediaScannerConnection.scanFile(this,
		                new String[] { file.toString() }, null,
		               null);*/
		        
		        c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
		                + Environment.getExternalStorageDirectory())));

		    } catch (IOException e) {
		        // Unable to create file, likely because external storage is
		        // not currently mounted.
		        Log.w("ExternalStorage", "Error writing " + file, e);
		    }
		}


	/** This is the the method that will be called
	 * @param url         -- URL of the image
	 * @param activity    -- Context
	 * @param imageView   -- ImageView for the image
	 * @param imageId     -- image identifier
	 */
	public void DisplayImage(String url, Activity activity, ImageView imageView, String imageId)
	{ 
		
		//First check do we have that image in cache
		if(localCache.containsKey(url))
		{
			
			
			DrawableDisplayer dd = new DrawableDisplayer(localCache.get(url), imageView);
			Activity a=(Activity)imageView.getContext();
			a.runOnUiThread(dd);

		}
		else
		{
			
			queueImage(url, activity, imageView,imageId); //Add this image to loading queue 
			
			if(loader)//Display loader
			{
				//imageView.setImageResource(default_picture);
				imageView.setImageResource(deafult_loader);
				
				// Get the background, which has been compiled to an AnimationDrawable object.
				AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getDrawable();
		        frameAnimation.setCallback(imageView);
		        frameAnimation.setVisible(true, true);
		        imageView.post(new Starter(frameAnimation));

		        
		        Log.e("where","We are inside the loader");
			}
			else//Display loading image
			{
				if(asBackground)
				{
					imageView.setBackgroundResource(default_picture);
				}
				else
				{
					imageView.setImageResource(default_picture);
				}
			}

			Log.e("Image come from","loader");
		}
		
	}

	/**  Queue the image
	 * @param url         -- URL of the image
	 * @param activity    -- Context
	 * @param imageView   -- ImageView for the image
	 * @param imageId     -- image identifier
	 */
	private void queueImage(String url, Activity activity, ImageView imageView,String imageId)
	{
		//This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
		imagesQueue.Clean(imageView);

		//push image to load
		ImageToLoad p=new ImageToLoad(url, imageView,imageId);
		synchronized(imagesQueue.imagesToLoad){
			imagesQueue.imagesToLoad.push(p);
			imagesQueue.imagesToLoad.notifyAll();
		}

		//Start thread
		if(photoLoaderThread.getState()==Thread.State.NEW)
			photoLoaderThread.start();
	}





	//Queue tasks
	private class ImageToLoad
	{
		public String url;
		public ImageView imageView;
		public String imageID;
		public ImageToLoad(String u, ImageView i,String imageId_p){
			imageID=imageId_p;
			url=u; 
			imageView=i;
		}
	}

	//ImagesQueue Class
	class ImagesQueue
	{
		private Stack<ImageToLoad> imagesToLoad=new Stack<ImageToLoad>();

		//clean the ImageView
		public void Clean(ImageView image)
		{
			for(int j=0 ;j<imagesToLoad.size();){
				if(imagesToLoad.get(j).imageView==image)
					imagesToLoad.remove(j);
				else
					++j;
			}
		}
	}

	//This is the Image Queue
	ImagesQueue imagesQueue=new ImagesQueue();

	//For some reason you can stop the loading thread (ex. You want to stop or exit the current activity)
	public void stopThread()
	{
		photoLoaderThread.interrupt();
	}



	//Method to fetch the Drawable
	public Drawable fetchDrawable(String urlString) 
	{
		try {
			InputStream is = fetch(urlString);
			Drawable drawable = Drawable.createFromStream(is, "src");
			
			return drawable;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}


	//Method to construct the input Stream
	private InputStream fetch(String urlString) throws MalformedURLException, IOException 
	{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}



    //This is the image loader Thread - Most important part
	class PhotosLoader extends Thread {
		public void run() {
			try {
				while(true)
				{
					//Is there something to be loaded
					if(imagesQueue.imagesToLoad.size()==0)
						synchronized(imagesQueue.imagesToLoad){
							imagesQueue.imagesToLoad.wait();
						}
					if(imagesQueue.imagesToLoad.size()!=0)
					{
						ImageToLoad photoToLoad;
						synchronized(imagesQueue.imagesToLoad)
						{
							photoToLoad=imagesQueue.imagesToLoad.pop();
						}

						//This is the image
						Drawable image = fetchDrawable(photoToLoad.url);
						localCache.put(photoToLoad.url, image);
						Log.e("Image in cache",localCache.size()+"");
						
						//Maybe you want to store the picture
						//You can do it here 
						
						//Compare the tag's to match the image with view
						if((photoToLoad.imageView.getTag()).equals(photoToLoad.imageID))
						{
							DrawableDisplayer dd = new DrawableDisplayer(image, photoToLoad.imageView);
							Activity a=(Activity)photoToLoad.imageView.getContext();
							a.runOnUiThread(dd);
						}
					}
					if(Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				//allow thread to exit
			}
		}
	}

	//Instance of the loader
	PhotosLoader photoLoaderThread=new PhotosLoader();

	//Used to display bitmap in the UI thread
	class DrawableDisplayer implements Runnable
	{
		Drawable drw;
		ImageView imageView;
		public DrawableDisplayer(Drawable b, ImageView i){drw=b;imageView=i;}
		public void run()
		{
			if(drw!=null)
			{
				//imageView.setImageResource(default_picture);
				if(asBackground)
				{
					imageView.setBackgroundDrawable(drw);
				}
				else
				{
					imageView.setImageDrawable(drw);
				}
			}
				
			else
			{
				if(loader)//Display loader
				{
					
					imageView.setImageResource(deafult_loader);
					// Get the background, which has been compiled to an AnimationDrawable object.
					AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getDrawable();

			        frameAnimation.setCallback(imageView);
			        frameAnimation.setVisible(true, true);

			        // Start the animation (looped playback by default).
			        //frameAnimation.start();
			        imageView.post(new Starter(frameAnimation));
			        
			        Log.e("where","We are inside the loader");
				}
				else//Display loading image
				{
					if(asBackground)
					{
						imageView.setBackgroundResource(default_picture);
					}
					else
					{
						imageView.setImageResource(default_picture);
					}
					
				}
				
			}
				
		}
	}
	
	public void setWallaper(String url, Context c) 
	{
		// TODO Auto-generated method stub
		Bitmap bitmap=getBitmap(url);//Get the Image
		try {
			c.setWallpaper(bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Util.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=320;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            Log.e("scale",scale+"");
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=1;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

	//Clear the cache
	public void clearCache() 
	{
		//clear memory cache
		localCache.clear();
	}

	public HashMap<String, Drawable> getLocalCache() {
		return localCache;
	}
	
	class Starter implements Runnable 
	{
		AnimationDrawable frameAnimation;
        public Starter(AnimationDrawable frameAnimationp) {
			// TODO Auto-generated constructor stub
        	frameAnimation=frameAnimationp;
		}

		public void run() {
        	 frameAnimation.start();
        }
	}

}
