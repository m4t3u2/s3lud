package com.s3lud.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.s3lud.services.BucketService;

@Service
public class S3Service implements BucketService {

	private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

	private AmazonS3 amazonS3;
	private String s3BucketName;

	public S3Service(
			@Autowired AmazonS3 amazonS3, 
			@Value("${s3.bucket.name}") String s3BucketName) {
		this.amazonS3 = amazonS3;
		this.s3BucketName = s3BucketName;
	}

	@Override
	public InputStream findByName(String fileName) {
		LOG.info("Downloading file with name {}", fileName);
		return amazonS3.getObject(s3BucketName, fileName).getObjectContent();
	}

	@Override
	public void save(MultipartFile multipartFile) {
		try {
			final File file = convertMultiPartFileToFile(multipartFile);
			final String fileName = file.getName();
			LOG.info("Uploading file with name {}", fileName);
			final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, fileName, file);
			amazonS3.putObject(putObjectRequest);
			Files.delete(file.toPath());
		} catch (AmazonServiceException e) {
			LOG.error("Error {} occurred while uploading file", e.getLocalizedMessage());
		} catch (IOException ex) {
			LOG.error("Error {} occurred while deleting temporary file", ex.getLocalizedMessage());
		}
	}

	private File convertMultiPartFileToFile(MultipartFile multipartFile) {
		File file = new File(multipartFile.getOriginalFilename());
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (IOException e) {
			LOG.error("Error {} occurred while converting the multipart file", e.getLocalizedMessage());
		}
		return file;
	}

	@Override
	public List<String> findAll() {
		ObjectListing listObjects = amazonS3.listObjects(s3BucketName);
		List<S3ObjectSummary> sumaries = listObjects.getObjectSummaries();
		while(listObjects.isTruncated()) {
			listObjects = amazonS3.listNextBatchOfObjects(listObjects);
			sumaries.addAll(listObjects.getObjectSummaries());
		}
		return sumaries.stream().map(s -> s.getKey()).collect(Collectors.toList());
	}

}
