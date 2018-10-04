package com.tcg.admin.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcg.admin.service.FileService;
import com.tcg.admin.to.FileInfoTo;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping("/resources/file")
public class FileController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileService fileService;

	@PostMapping(value = "upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public JsonResponseT<FileInfoTo> createFile(@RequestParam("file") MultipartFile file) throws IOException {
		LOGGER.info("prepare to upload {}", file.getName());
		FileInfoTo info = fileService.uploadFile(file, "announcement");
		
		JsonResponseT<FileInfoTo> result = new JsonResponseT<>(true);
		result.setValue(info);
		
		return result;
	}

}
