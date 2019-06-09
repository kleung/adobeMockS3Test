package com.test.S3MockTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.adobe.testing.s3mock.S3MockApplication;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.test.S3MockTest.config.BasicLocalHostS3Config;
import com.test.S3MockTest.service.S3Service;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(value= {
		BasicLocalHostS3Config.class,
		S3Service.class
})
public class S3MockTestApplicationTests {
	
	private S3MockApplication s3Mock;
	
	@Autowired
	private S3Service s3Service;
	
	@Before
	public void setup() throws Exception {
		this.s3Mock = S3MockApplication.start("--http.port=9090");
		String bucketName = "testbucket";
		this.s3Service.createBucket(bucketName);
	}
	
	@After
	public void shutdown() {
		if(this.s3Mock != null) {
			this.s3Mock.stop();
		}
	}
	
	//completely ignore exception here
	@Test
	public void shouldBeAbleToPutObject() throws Exception {
		String bucketName = "testbucket";
		String fileName = "test.txt";
		
		ListObjectsV2Result listResult = this.s3Service.listObjectsForBucketName(bucketName);
		int beforePutObjectCount = listResult.getKeyCount();
		
		this.s3Service.putClassPathResourceToS3(fileName, "text/plain", "test.txt", bucketName, "test.txt");
		
		listResult = this.s3Service.listObjectsForBucketName(bucketName);
		int afterPutObjectCount = listResult.getKeyCount();
		
		assertEquals(1, afterPutObjectCount - beforePutObjectCount);
		
		S3Object objectFromS3 = null;
		InputStream sourceFileInputStream = null;
		try {
			objectFromS3 = this.s3Service.getObject(bucketName, "test.txt");
			assertNotNull(objectFromS3);
			
			ClassPathResource sourceFile = new ClassPathResource(fileName);
			sourceFileInputStream = sourceFile.getInputStream();
			
			assertTrue(IOUtils.contentEquals(sourceFileInputStream, objectFromS3.getObjectContent()));	
		} finally {
			if(objectFromS3 != null) {
				objectFromS3.close();
			}
			
			if(sourceFileInputStream != null) {
				sourceFileInputStream.close();
			}
		}
	}

}
