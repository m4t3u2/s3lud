package com.s3lud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Configuration {

	private String accessKeyId;
	private String accessKeySecret;
	private String s3RegionName;

	public S3Configuration(
			@Value("${access.key.id}") String accessKeyId,
			@Value("${access.key.secret}") String accessKeySecret, 
			@Value("${s3.region.name}") String s3RegionName) {
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
		this.s3RegionName = s3RegionName;
	}

	@Bean
	public AmazonS3 getAmazonS3Client() {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
		return AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.withRegion(s3RegionName)
				.build();
	}

}
