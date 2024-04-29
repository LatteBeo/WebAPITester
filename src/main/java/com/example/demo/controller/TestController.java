package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DataNotFoundException;
import com.example.demo.entity.Api;
import com.example.demo.entity.Parameter;
import com.example.demo.entity.TestSet;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.ParameterRepository;
import com.example.demo.repository.TestSetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Rest api controller.
 */
@RestController
public class TestController {
	@Autowired
	private ApiRepository apiRepository;
	@Autowired
	private ParameterRepository parameterRepository;
	@Autowired
	private TestSetRepository testSetRepository;
	private static final String API_DOWNLOAD_HEADER = "attachment; filename=\"" + "api.json" + "\"";
	private static final String TESTSET_DOWNLOAD_HEADER = "attachment; filename=\"" + "testset.json" + "\"";

	/**
	 * Download all api list json file.
	 * 
	 * @return Json file
	 * @throws JsonProcessingException
	 */
	@GetMapping("/rest/api/download")
	public ResponseEntity<String> downloadAPIListJson() throws JsonProcessingException {
		List<Api> apiList = new ArrayList<>();
		apiRepository.findAll().forEach(apiList::add);

		final String jsonString = new ObjectMapper().writeValueAsString(apiList);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, API_DOWNLOAD_HEADER).body(jsonString);
	}

	/**
	 * Download specified test set json file.
	 * 
	 * @param params Url request parameters(testsetid=id)
	 * @return Json file
	 * @throws DataNotFoundException
	 * @throws JsonProcessingException
	 */
	@GetMapping("/rest/testset/download")
	public ResponseEntity<String> downloadTestSetJson(@RequestParam Map<String, String> params)
			throws DataNotFoundException, JsonProcessingException {
		Optional<TestSet> optTestSet = testSetRepository.findById(Integer.parseInt(params.get("testsetid")));
		if (optTestSet.isEmpty()) {
			throw new DataNotFoundException();
		}
		final String jsonString = new ObjectMapper().writeValueAsString(optTestSet.get());
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, TESTSET_DOWNLOAD_HEADER).body(jsonString);
	}

	/**
	 * Register api.
	 * 
	 * @param params(name=apiName&param=param1,param2...)
	 * @return
	 */
	@GetMapping("/rest/api/register")
	public ResponseEntity<String> registerAPI(@RequestParam Map<String, String> params) {
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