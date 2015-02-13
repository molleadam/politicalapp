package com.vittoriocontarina;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.util.Log;

/**
 * Utilities for JSONURL
 */
public final class Util {

	/**
	 * Generate the multiple part post body providing the parameters and boundary string
	 * 
	 * @param parameters the parameters need to be posted
	 * @param boundary the random string as boundary
	 * @return a string of the post body
	 */
	public static String encodePostBody(Bundle parameters, String boundary) {
		if (parameters == null) return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) {
			if (parameters.getByteArray(key) != null) {
				continue;
			}

			sb.append("Content-Disposition: form-data; name=\"" + key +
					"\"\r\n\r\n" + parameters.getString(key));
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	/**
	 * Encode the URL
	 * @param parameters
	 * @return
	 */
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first) first = false; else sb.append("&");
			sb.append(URLEncoder.encode(key) + "=" +
					URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	/**
	 * Decpdes URL parameter
	 * @param s String to be decoded
	 * @return
	 */
	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]),
						URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	


	/**
	 * Connect to an HTTP URL and return the response as a string.
	 *
	 * @param url - the resource to open: must be a well formed URL
	 * @param method - the HTTP method to use ("GET", "POST")
	 * @param params - the query parameter for the URL (e.g. api_key=788454542223)
	 * @return the URL contents as a String
	 * @throws MalformedURLException - if the URL format is invalid
	 * @throws IOException - if a network problem occurs
	 */
	public static String openUrl(String url, String method, Bundle params)
	throws MalformedURLException, IOException {
		// random string as boundary for multi-part http post
		String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
		String endLine = "\r\n";

		OutputStream os;

		if (method.equals("GET")) 
		{
			url = url + "?" + encodeUrl(params);
		}
		Log.d("iClub-Util", method + " URL: " + url);
		HttpURLConnection conn =
			(HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("User-Agent", System.getProperties().
				getProperty("http.agent") + " iClubAndroidSDK");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setConnectTimeout (1500000); 
		Log.e("SOLUTION","HERE WE ARE");
		if (!method.equals("GET"))
		{
			Bundle dataparams = new Bundle();
			for (String key : params.keySet()) {
				if (params.getByteArray(key) != null) {
					dataparams.putByteArray(key, params.getByteArray(key));
				}
				
				
				
				
			}

			// use method override
			if (!params.containsKey("method")) {
				params.putString("method", method);
			}

			if (params.containsKey("access_token")) {
				String decoded_token =
					URLDecoder.decode(params.getString("access_token"));
				params.putString("access_token", decoded_token);
			}

			conn.setRequestMethod("POST");
			conn.setRequestProperty(
					"Content-Type",
					"multipart/form-data;boundary="+strBoundary);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setConnectTimeout (1500000); 
			conn.connect();
			os = new BufferedOutputStream(conn.getOutputStream());

			os.write(("--" + strBoundary +endLine).getBytes());
			os.write((encodePostBody(params, strBoundary)).getBytes());
			os.write((endLine + "--" + strBoundary + endLine).getBytes());

			if (!dataparams.isEmpty()) {

				for (String key: dataparams.keySet()){
					os.write(("Content-Disposition: form-data; filename=\"" + key + "\"" + endLine).getBytes());
					os.write(("Content-Type: content/unknown" + endLine + endLine).getBytes());
					os.write(dataparams.getByteArray(key));
					os.write((endLine + "--" + strBoundary + endLine).getBytes());

				}
			}
			os.flush();
		}

		String response = "";
		try {
			response = read(conn.getInputStream());
		} catch (FileNotFoundException e) {
			// Error Stream contains JSON that we can parse to a FB error
			response = read(conn.getErrorStream());
		}
		return response;
	}

	/**
	 * Reform the string
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	/**
	 * Parse a server response into a JSON Object. This is a basic
	 * implementation using org.json.JSONObject representation.
	 * @param response - string representation of the response
	 * @return the response as a JSON Object
	 * @throws JSONException - if the response is not valid JSON
	 * @throws ReadError - if an error condition is set
	 */
	public static JSONObject parseJson(String response)
	throws JSONException, ReadError {
        Log.e("THE RESPONSE:",response);
        Log.e("length",response.length()+"");
        Log.e("1.First character is:",response.charAt(0)+"<----- Is this");
        int lenght=response.length();
        if(response.charAt(0)!='{')
        {
        	response=response.substring(1, lenght);
        	Log.e("2.First character is :",response.charAt(0)+"<----- Is this");
        }
		if (response.equals("false")) {
			throw new ReadError("request failed");
		}
		if (response.equals("true")) {
			response = "{value : true}";
		}
		JSONObject json = new JSONObject(response);

		return json;
	}

	public static JSONArray parseJsonArray(String response)
	throws JSONException, ReadError {

		if (response.equals("false")) {
			throw new ReadError("request failed");
		}
		if (response.equals("true")) {
			response = "{value : true}";
		}
		JSONArray json = new JSONArray(response);

		return json;
	}
	
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}

