/*******************************************************************************
 * Copyright IBM Corp. 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.integration.payment.be.rest;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

//this client is built using apache HttpClient
public class HttpRestClient {
	private static final String UTF_8 = "UTF-8";
	private static final String USER_AGENT = "httprestclient/1.0;java";
	private String url;
	private String endpoint;
	private HttpClient client;
	private String params;
	private HashMap<String, String> getParamMap = new HashMap<String, String>();
	private HashMap<String, String> headerMap = new HashMap<String, String>();
	
	public HttpRestClient(){
		this.client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();
	}
	
	public HttpRestClient(String url){
		this.url = url;
		this.client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();
	}
	
	public HttpRestClient(String url, String endPoint){
		this.url = url;
		this.endpoint = endPoint;
		this.client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();
	}
	
	public HttpRestClient(String requestType, String host, String endPoint){
		this.url = requestType + "://" + host;
		this.endpoint = endPoint;
		this.client = HttpClientBuilder.create().setUserAgent(USER_AGENT).build();
	}
	
	public HttpRestClient setUrl(String url) {
		this.url = url;
		return this;
	}

	public HttpRestClient setEndpoint(String endpoint) {
		this.endpoint = endpoint;
		return this;
	}

	public HttpRestClient setGetParams(String name, String value) {
		this.headerMap.put(name, value);
		if(this.params != null){
			this.params = name + "=" + value;
		}else{
			this.params = name + "&=" + value;
		}
		return this;
	}
	
	public HttpRestClient setPostParams(JSONObject params) {
		this.params = params.toString();
		return this;
	}
	
	public void setHeader(String name, String value) {
		this.headerMap.put(name, value);
	}
	
	public Response post() throws HttpRestException {
		HttpResponse response = null;
		HttpPost httppost = new HttpPost(this.url + this.endpoint);
		try {
			httppost.setEntity(new StringEntity(this.params));
			if(!this.headerMap.isEmpty()){
				for (String key : this.headerMap.keySet()) {
					httppost.setHeader(key, this.headerMap.get(key));
				}
			}
			response = this.client.execute(httppost);
			return new Response(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			throw new HttpRestException(e);
		}

	}
	
	public Response get() throws HttpRestException{
		HttpResponse response = null;
		HttpGet httpget = new HttpGet(this.url + this.endpoint + "/" + this.params);
		
		try {
			if(!this.headerMap.isEmpty()){
				for (String key : this.headerMap.keySet()) {
					httpget.setHeader(key, this.headerMap.get(key));
				}
			}
			response = this.client.execute(httpget);
			return new Response(response.getStatusLine().getStatusCode(),
					EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			throw new HttpRestException(e);
		}
		
	}
	
	public static class Response {
		private int code;
		private boolean success;
		private String message;

		public Response(int code, String msg) {
			this.code = code;
			this.success = (code == 200);
			this.message = msg;
		}

		public int getCode() {
			return this.code;
		}

		public boolean getStatus() {
			return this.success;
		}

		public String getMessage() {
			return this.message;
		}
	}
	
	public static class HttpRestException extends Exception{
		public HttpRestException(Exception e) {
			super(e);
		}
	}
	
}
