package com.example.demo.testset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Test;
import com.example.demo.entity.TestAssertion;
import com.example.demo.entity.TestParameter;
import com.example.demo.entity.TestSet;
import com.example.demo.entity.TestSetDetail;
import com.example.demo.page.SingleFileUploadPageBase;
import com.example.demo.repository.TestAssertionRepository;
import com.example.demo.repository.TestParameterRepository;
import com.example.demo.repository.TestRepository;
import com.example.demo.repository.TestSetDetailRepository;
import com.example.demo.repository.TestSetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Route;

@Route("testset/upload")
public class TestSetUpload extends SingleFileUploadPageBase {

	private static final long serialVersionUID = 1L;
	@Autowired
	transient TestSetRepository testSetRepository;
	@Autowired
	transient TestSetDetailRepository testSetDetailRepository;
	@Autowired
	transient TestRepository testRepository;
	@Autowired
	transient TestParameterRepository testParameterRepository;
	@Autowired
	transient TestAssertionRepository testAssertionRepository;

	@Override
	protected Button createRegisterButton() {
		Upload upload = (Upload) getComponent("file");

		return componentService.createButton(getTranslation("register"), i -> {
			File is = ((FileBuffer) upload.getReceiver()).getFileData().getFile();
			try {
				String jsonString = Files.lines(is.toPath())
						.collect(Collectors.joining(System.getProperty("line.separator")));
				TestSet set = new ObjectMapper().readValue(jsonString, TestSet.class);
				saveTestSet(set);
			} catch (IOException e) {
				return;
			}
			this.getUI().ifPresent(ui -> ui.navigate(TestSetListPage.class));
		});

	}

	private void saveTestSet(TestSet testSet) {
		// Register test set
		TestSet newTestSet = new TestSet();
		newTestSet.setName(testSet.getName());
		newTestSet = testSetRepository.save(newTestSet);

		List<TestSetDetail> detail = testSet.getTestList();

		for (TestSetDetail detailData : detail) {
			Test test = detailData.getTest();

			Test newTest = new Test();
			newTest.setMemo(test.getMemo());
			newTest.setEndpointurl(test.getEndpointurl());
			newTest.setApiname(test.getApiname());
			newTest.setMethod(test.getMethod());
			newTest = testRepository.save(newTest);

			List<TestParameter> parameterList = test.getTestParameters();
			for (TestParameter pa : parameterList) {
				TestParameter parameter = new TestParameter();
				parameter.setName(pa.getName());
				parameter.setValue(pa.getValue());
				parameter.setTest(newTest);
				testParameterRepository.save(parameter);
			}
			List<TestAssertion> assertionList = test.getTestAssertions();
			for (TestAssertion assetio : assertionList) {
				TestAssertion assertion = new TestAssertion();
				assertion.setXpath(assetio.getXpath());
				assertion.setExpectedValue(assetio.getExpectedValue());
				assertion.setTest(newTest);
				testAssertionRepository.save(assertion);
			}
			TestSetDetail newDetail = new TestSetDetail();
			newDetail.setTest(newTest);
			newDetail.setTestSet(newTestSet);
			testSetDetailRepository.save(newDetail);
		}
	}
}