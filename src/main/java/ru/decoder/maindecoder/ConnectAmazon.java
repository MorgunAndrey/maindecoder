package ru.decoder.maindecoder;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


public class ConnectAmazon {
	
	private String accessKey = ConstantsUtils.ACCESS_KEY;
    private String secretKey = ConstantsUtils.SECRET_KEY;
    private String region = ConstantsUtils.REGION;
    private String point_url = ConstantsUtils.END_POINT_URL;
    
	
	public AmazonS3 s3client() {
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		
		AmazonS3 s3Client = AmazonS3ClientBuilder
				.standard()
			    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
			    .withEndpointConfiguration(
			        new AmazonS3ClientBuilder.EndpointConfiguration(
			        		point_url,region
			        )
			    )
			    .build();
		
		return s3Client;
	}



}
