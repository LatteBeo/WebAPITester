package com.example.demo;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

/**
 * Service class of API caller.
 */
@Service
public class ApiCallerService {
	/**
	 * Execute GET request.
	 * 
	 * @param endpointUrl Request base url
	 * @param apiName     Request API Name
	 * @param urlParam    URL parameters(Key:Parameter Name, Value:Parameter Value)
	 * @return Request result.
	 */
	public RequestResult get(String endpointUrl, String apiName, Map<String, String> urlParam) {
		LocalDateTime execDate = null;
		final WatHttpClientInterceptor interceptor = new WatHttpClientInterceptor();
		try {
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(endpointUrl).append("/").append(apiName);
			if (!urlParam.isEmpty()) {
				urlBuilder.append("?");
				urlParam.forEach((key, value) -> {
					if (!urlBuilder.toString().endsWith("&")) {
						urlBuilder.append("&");
					}
					urlBuilder.append(key).append("=").append("{").append(key).append("}");
				});
			}
			RestClient client = RestClient.builder().requestInterceptor(interceptor).build();
			execDate = LocalDateTime.now();
			ResponseSpec response = client.get().uri(urlBuilder.toString(), urlParam).retrieve();
			return new RequestResult(interceptor.getRequestUrl(), response.body(String.class), true, execDate);
		} catch (Exception e) {
			return new RequestResult(interceptor.getRequestUrl(), e.getMessage(), false, execDate);
		}
	}

	/**
	 * Execute POST request.
	 * 
	 * @param endpointUrl Request base url
	 * @param apiName     Request API Name
	 * @param param       Request parameters()Key:Parameter Name, Value:Parameter
	 *                    Value
	 * @return Request result
	 */
	public RequestResult post(String endpointUrl, String apiName, Map<String, Object> param) {
		LocalDateTime execDate = null;
		final WatHttpClientInterceptor interceptor = new WatHttpClientInterceptor();
		try {
			MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
			param.forEach(parts::add);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(endpointUrl).append("/").append(apiName);
			RestClient client = RestClient.builder().requestInterceptor(interceptor).build();
			execDate = LocalDateTime.now();
			ResponseSpec response = client.post().uri(urlBuilder.toString()).contentType(MediaType.MULTIPART_FORM_DATA)
					.body(parts).retrieve();
			return new RequestResult(interceptor.getRequestUrl(), response.body(String.class), true, execDate);
		} catch (Exception e) {
			return new RequestResult(interceptor.getRequestUrl(), e.getMessage(), false, execDate);
		}
	}

	/**
	 * Stores request result information.
	 */
	public record RequestResult(String requestUrl, String responseString, boolean succeeded,
			LocalDateTime execDateTime) {
	}
}