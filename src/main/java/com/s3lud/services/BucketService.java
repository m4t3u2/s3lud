package com.s3lud.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface BucketService {

	public InputStream findByName(String fileName);

	public void save(MultipartFile multipartFile);

	public List<String> findAll();

}
