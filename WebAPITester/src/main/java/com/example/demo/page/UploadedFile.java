package com.example.demo.page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadedFile {
	private byte[] file;
	private String fileName;
}