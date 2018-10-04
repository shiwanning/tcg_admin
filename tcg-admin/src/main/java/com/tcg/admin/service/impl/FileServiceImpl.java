package com.tcg.admin.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcg.admin.service.FileService;
import com.tcg.admin.to.FileInfoTo;

@Service
public class FileServiceImpl implements FileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

	private static final String FILE_BASE_PATH = (new File("")).getAbsolutePath();
	
	@Value("${file.server.url}")
	private String fileServerUrl;
	
	@Value("${file.server.base.path}")
	private String fileServerBasePath;
	
	@Override
	public FileInfoTo uploadFile(MultipartFile multiPartFile, String moduleName) throws IOException {
		
		FileInfoTo info = new FileInfoTo();
		info.setFileName(multiPartFile.getOriginalFilename());
		
		String fileName = generateFileName(multiPartFile);   
		String subFolder = UUID.randomUUID().toString().replaceAll("-", "");
        String path = generateFilePath(fileName, moduleName, subFolder);       
        String fileUrl = generateFileUrl(fileName, moduleName, subFolder);
        
        LOGGER.info("save file {} to {}.", fileName, path);
        LOGGER.info("{} url is {}.", fileName, fileUrl);
        
        File file = new File(path);
        
        file.mkdirs();
        multiPartFile.transferTo(file);
        
        info.setFileUrl(fileUrl);
        
        return info;
	}

	private String generateFileUrl(String fileName, String moduleName, String subFolder) {
		return "/tac/files/" + moduleName + "/" + subFolder + "/" + fileName;
	}

	private String generateFilePath(String fileName, String moduleName, String subFolder) {
		return FILE_BASE_PATH + "/attach_file/" + moduleName + "/" + subFolder + "/" + fileName;
	}

	private String generateFileName(MultipartFile multiPartFile) {
		return multiPartFile.getOriginalFilename();
	}

	@Override
	public void deleteFromUrl(String fileUrl) {
		if(fileUrl == null) {
			return;
		}
		String path = fileUrl.replace("/tac/files", "");
		path = "/attach_file" + path;
		String fullPath = FILE_BASE_PATH + path;
		
		try { 
            Files.deleteIfExists(Paths.get(fullPath)); 
        } catch(Exception e) { 
            LOGGER.error("can't delete file: " + fullPath, e);
        } 
	}
	
}
