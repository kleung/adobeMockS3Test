package com.test.S3MockTest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class S3Service {
	
	private static Logger LOG = LoggerFactory.getLogger(S3Service.class);
	
	@Autowired
	private AmazonS3 s3Client;
	
	public S3Service() {
		super();
	}
	
	public void createBucket(String bucketName) throws Exception {
		LOG.info("bucket name: {}", bucketName);
		this.s3Client.createBucket(bucketName);
	}
	
	public S3Object getObject(String bucketName, String objectKey) throws Exception {
		LOG.info("Bucket name: {}, object key: {}", bucketName, objectKey);
		
		S3Object result = this.s3Client.getObject(new GetObjectRequest(bucketName, objectKey));
		LOG.info("S3 object: {}", result);
		
		return result;
	}
	
	public ListObjectsV2Result listObjectsForBucketName(String bucketName) throws Exception {
		LOG.info("Bucket name: {}", bucketName);
		
		ListObjectsV2Result result = this.s3Client.listObjectsV2(new ListObjectsV2Request().withBucketName(bucketName));
		LOG.info("S3 objects: {}", result);
		
		return result;
	}
	
	public void putClassPathResourceToS3(String classPathResourceUrl, String mimetype, String fileName, String bucketName, String objectKey) throws Exception {
		LOG.info("classPathResourceUrl: {}, mimetype: {}, fileName: {}, bucketName: {}, objectKey: {}", classPathResourceUrl, mimetype, fileName, bucketName, objectKey);
		ClassPathResource resource = new ClassPathResource(classPathResourceUrl);
		
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(mimetype);
        metadata.setContentLength(resource.contentLength());
        metadata.addUserMetadata("x-amz-meta-title", fileName);
		
		PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, resource.getInputStream(), metadata);
		
		this.s3Client.putObject(request);
	}

}
