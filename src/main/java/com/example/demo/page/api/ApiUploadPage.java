package com.example.demo.page.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.entity.Api;
import com.example.demo.entity.Parameter;
import com.example.demo.page.SingleFileUploadPageBase;
import com.example.demo.repository.ApiRepository;
import com.example.demo.repository.ParameterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Route;

/**
 * API json data upload page.
 */
@Route("/api/upload")
public class ApiUploadPage extends SingleFileUploadPageBase {
	private static final long serialVersionUID = 1L;
	@Autowired
	ApiRepository apiRepository;
	@Autowired
	ParameterRepository parameterRepository;

	@Override
	protected Button createRegisterButton() {
		Upload upload = (Upload) getComponent("file");

		Button button = componentService.createButton(getTranslation("register"), i -> {
			File is = ((FileBuffer) upload.getReceiver()).getFileData().getFile();
			try {
				String jsonString = Files.lines(is.toPath())
						.collect(Collectors.joining(System.getProperty("line.separator")));
				Api[] apiArray = new ObjectMapper().readValue(jsonString, Api[].class);

				for (Api originalApi : apiArray) {
					Api newApi = new Api();
					List<Parameter> newParameterList = new ArrayList<>();
					newApi.setName(originalApi.getName());
					Api savedApi = apiRepository.save(newApi);

					originalApi.getParameter().forEach(j -> {
						Parameter parameter = new Parameter();
						parameter.setApi(savedApi);
						parameter.setName(j.getName());
						parameterRepository.save(parameter);
					});
					newApi.setParameter(newParameterList);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			this.getUI().ifPresent(ui -> ui.navigate(ApiListPage.class));
		});
		return button;
	}
}