package com.qooco.boost.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;


public class HttpHelper {
	private static final int TIME_OUT = 5000; //5s for waiting
	private static final int TIME_OUT_SYNC = 30000;

	public static <T> T doPost(String url, Object request, Class<T> clazz, boolean isSync) {
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory(isSync);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpEntity<Object> obj = new HttpEntity<>(request);
		return restTemplate.postForObject(url, obj, clazz);
	}

	public static <T> T doPost(String url, Object request, HttpHeaders header, Class<T> clazz, boolean isSync) {
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory(isSync);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpEntity<Object> obj = new HttpEntity<>(request, header);

		String responseData = restTemplate.postForObject(url, obj, String.class);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, UnixEpochDateTypeAdapter.getUnixEpochDateTypeAdapter()).create();
		return gson.fromJson(responseData, clazz);
	}

	private static ClientHttpRequestFactory getClientHttpRequestFactory(boolean isSync) {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		if (isSync) {
			clientHttpRequestFactory.setConnectTimeout(TIME_OUT_SYNC);
		} else {
			clientHttpRequestFactory.setConnectTimeout(TIME_OUT);
		}

		return clientHttpRequestFactory;
	}

	public static void doPut(String url, Object request, HttpHeaders header, boolean isSync) {

		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory(isSync);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpEntity<Object> obj = new HttpEntity<>(request, header);
		restTemplate.put(url, obj);
	}

	public static <T> T doGet(String url, Class<T> clazz, boolean isSync) {
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory(isSync);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate.getForObject(url, clazz);
	}

	public static <T> T doGetParseString(String url, Class<T> clazz, boolean isSync) {
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory(isSync);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String responseData;
		if (StringUtil.isContainBrace(url)) {
			URI uri = UriComponentsBuilder.fromUriString(url).build().encode().toUri();
			responseData = restTemplate.getForObject(uri, String.class);
		} else {
			responseData = restTemplate.getForObject(url, String.class);

		}
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, UnixEpochDateTypeAdapter.getUnixEpochDateTypeAdapter()).create();
		return gson.fromJson(responseData, clazz);
	}
}
