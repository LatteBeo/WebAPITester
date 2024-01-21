package com.example.demo;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.Getter;

/**
 * Http client interceptor. This interceptor gets the information about http
 * request.
 */
public class WatHttpClientInterceptor implements ClientHttpRequestInterceptor {
	/**
	 * Requested url
	 */
	@Getter
	private String requestUrl;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		requestUrl = request.getURI().toString();
		return execution.execute(request, body);
	}
}