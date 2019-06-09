package com.test.S3MockTest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;;

@Configuration
public class BasicLocalHostS3Config {
	
	public BasicLocalHostS3Config() {
		super();
	}
	
	@Bean
	public AmazonS3 amazonS3() {
		AWSStaticCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicAWSCredentials("", ""));
		
		AmazonS3 result = AmazonS3ClientBuilder.standard()
				  								.withCredentials(credentials)
				  								.withEndpointConfiguration(
				  										new EndpointConfiguration(
				  												"http://localhost:9090/",
				  												Region.US_Standard.getFirstRegionId()
				  												))
				  								.withPathStyleAccessEnabled(true)
				  								.build();
		
		return result;
	}

}
