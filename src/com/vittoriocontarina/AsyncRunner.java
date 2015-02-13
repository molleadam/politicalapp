package com.vittoriocontarina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import android.os.Bundle;

/**
 * Asynchronous Runner 
 * Used to make Asynchronous request to your API
 */
public final class AsyncRunner 
{
	/**
	 * Your server URL
	 * CHANGE THIS WITH YOUR SERVER URL, with this class constructor
	 */
	protected  String YOUR_SERVER_URL ="";
	
	/**
	 * Constructor
	 * @param serverURL Your server URL
	 */
	public AsyncRunner (String serverURL) 
	{
		YOUR_SERVER_URL = serverURL;
	}
	
	/**
	 * @param Path Path to the resource
	 *             Example:
	 *                      My server Wordpress url is "http://vordol.com/store/components/andgallery/"
	 *                      And now I want to query the NextGenGallery. 
	 *                      So the path variable will be "wp-content/gallery/album/"
	 * @param listener
	 */
	public void requestYourApi(final String Path,final RequestListener listener) 
	{	
		//Call requestYourApi override with no additional parameters (query parameters) 
		requestYourApi(Path,null,listener);
	}
	
	/**
	 * @param Path Path to the resource, read example above
	 * @param parameters Query parameters
	 *                   Example: I want to query the server with my api key and a search term
	 *                            So my address string should have this key:value
	 *                            "http://example.com/api/?key=25&term=water
	 *                            The parameters are passed in Bundle
	 * @param listener
	 */
	public void requestYourApi(
			final String path,
			final Bundle parameters,
			final RequestListener listener) 
	{
		new Thread() {
			@Override public void run() {
				try {
					String resp = request(path, parameters);
					listener.onComplete(resp, null);
				} catch (FileNotFoundException e) {
					listener.onFileNotFoundException(e, null);
				} catch (MalformedURLException e) {
					listener.onMalformedURLException(e, null);
				} catch (IOException e) {
					System.out.println(e.getMessage());
					listener.onIOException(e, null);
				}
			}
		}.start();
	}
	
	/**
	 * 
	 * @param path Path to the resource, read example above
	 * @param params Query parameters, read example above
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String request(String path,Bundle params) throws MalformedURLException, IOException
	 {
		//Form the url that we are going to query
		String url = YOUR_SERVER_URL+path;
		
		//Default post method we use is get, You can replace this with "POST"
		String results=Util.openUrl(url, "GET", params);
		return results;
	 }
	
	/**
	 * Interface listener
	 */
	public static interface RequestListener 
	{
		public void onComplete(String response, Object state);
		public void onIOException(IOException e, Object state);
		public void onFileNotFoundException(FileNotFoundException e,Object state);
		public void onMalformedURLException(MalformedURLException e,Object state);
		public void oniClubError(ReadError e, Object state);
	}

}
