package com.tcg.admin.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.tcg.admin.to.FileInfoTo;

public interface FileService {

	FileInfoTo uploadFile(MultipartFile file, String moduleName) throws IOException;

	void deleteFromUrl(String chineseFileUrl);

}
