package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Api;
import com.example.demo.entity.Parameter;
import com.example.demo.entity.TestSet;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.ParameterRepository;
import com.example.demo.repository.TestSetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class TestController {
	@Autowired
	ApiRepository apiRepository;
	@Autowired
	ParameterRepository parameterRepository;
	@Autowired
	TestSetRepository testSetRepository;

	/**
	 * Download all api list json file.
	 * 
	 * @return Json file
	 */
	@GetMapping("/rest/api/download")
	public ResponseEntity<?> downloadAPIListJson() {
		List<Api> apiList = new ArrayList<>();
		apiRepository.findAll().forEach(i -> apiList.add(i));
		try {
			String jsonString = new ObjectMapper().writeValueAsString(apiList);
			String headerValue = "attachment; filename=\"" + "api.json" + "\"";
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(jsonString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Download specified test set json file.
	 * 
	 * @param params Url request parameters(testsetid=id)
	 * @return Json file
	 */
	@GetMapping("/rest/testset/download")
	public ResponseEntity<?> downloadTestSetJson(@RequestParam Map<String, String> params) {
		String testsetid = params.get("testsetid");
		TestSet testSet = testSetRepository.findById(Integer.parseInt(testsetid)).get();
		if (testSet == null) {
			throw new RuntimeException();
		}
		try {
			String jsonString = new ObjectMapper().writeValueAsString(testSet);
			String headerValue = "attachment; filename=\"" + "testset.json" + "\"";
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(jsonString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Register api.
	 * 
	 * @param params(name=apiName&param=param1,param2...)
	 * @return
	 */
	@GetMapping("/rest/api/register")
	public ResponseEntity<?> registerAPI(@RequestParam Map<String, String> params) {
		Api api = new Api();
		api.setName(params.get("name"));
		api = apiRepository.save(api);
		
		for (String paramName : params.get("param").split(",")) {
			Parameter parameter = new Parameter();
			parameter.setName(paramName);
			parameter.setApi(api);
			parameterRepository.save(parameter);
		}
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Registred.");
	}
}