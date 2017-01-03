package com.mtit.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import com.mtit.process.SyncException;

public class SSLConnection {

	public static InputStream getInputStream(String urlString) throws SyncException {
		
		HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		HttpGet httpGet = new HttpGet(urlString);
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			if ( response.getStatusLine().getStatusCode()  != 200) {
				throw new SyncException("Unable to access the URL " + urlString + ". The error is "
					+ response.getStatusLine().getReasonPhrase());
			} else {
				return response.getEntity().getContent();			
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
