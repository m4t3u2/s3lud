package com.s3lud.resource;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3lud.services.BucketService;

@RestController
@RequestMapping("/file")
public class FileResource {

	private BucketService bucketService;

	public FileResource(@Autowired BucketService bucketService) {
		this.bucketService = bucketService;
	}

	@GetMapping("/{name}")
	public ResponseEntity<Resource> findByName(@PathVariable String name) {
		InputStream file = bucketService.findByName(name);
		Resource resource = new InputStreamResource(file);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@PostMapping
	public ResponseEntity<Void> save(@RequestParam MultipartFile file) {
		bucketService.save(file);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping
	public ResponseEntity<List<String>> findAll() {
		return ResponseEntity.ok(bucketService.findAll());
	}

}
