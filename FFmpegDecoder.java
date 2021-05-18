package ru.decoder.maindecoder;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.Scanner;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class FFmpegDecoder extends Thread {
	
//private AmazonS3 s3Client;
    private String accessKey = ConstantsUtils.ACCESS_KEY;
    private String secretKey = ConstantsUtils.SECRET_KEY;
    private String region = ConstantsUtils.REGION;
    private String cloud = ConstantsUtils.CLOUD;
	
	Process process;
	Scanner scanner;
	
	String[] cmdffmpeg;
	
	MainDecoder mainWindow;
	Integer updateId;
	String pathToFileCloud;
	String pathToUploud;
	Integer state_id;
	
	public FFmpegDecoder(MainDecoder mainWindow, String[] cmdffmpeg,Integer updateId,String pathToFileCloud,String pathToUploud,Integer state_id) {
		this.mainWindow = mainWindow;
		this.cmdffmpeg = cmdffmpeg;
		this.updateId = updateId;
		this.pathToFileCloud = pathToFileCloud;
		this.pathToUploud = pathToUploud;
		this.state_id = state_id;
	}
	
	public void run(){
		System.out.println("Поток запущен...");
		MainDecoder launchYet = new MainDecoder();
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(cmdffmpeg);
		try {
			process = processBuilder.start();
			scanner = new Scanner(process.getErrorStream());
			
			String line;
			String duration = "";
			String bitrate = "";
			String progress = "";
			String speed = "";
			String title = "";
			long durationMilli = 0;
			boolean changeTitle = false;
			
			while(scanner.hasNext()) {
				line = scanner.nextLine();
				changeTitle = false;
				if (line.contains("Duration:")) {
					
					int durationStart = line.indexOf("Duration:")+10;
					duration = "" + line.substring(durationStart, durationStart + 11);
					System.out.println("Продолжительность: " + duration); 
					int bitrateStart = line.indexOf("bitrate:")+9;
					bitrate = "" + line.substring(bitrateStart) + "\n";
					System.out.println("Битрейд: " + bitrate); 
					
					
					int hours = Integer.parseInt(duration.substring(0, 2));
					int minutes = Integer.parseInt(duration.substring(3, 5));
					int seconds = Integer.parseInt(duration.substring(6, 8));
					int milli = Integer.parseInt(duration.substring(9));
					durationMilli = (hours*3600000) + (minutes*60000) + (seconds*1000) + (milli * 10);
				} else if (line.startsWith("frame=")) {
					int progressStart = line.indexOf("time=") + 5;
					progress = "" + line.substring(progressStart, progressStart+11);
					System.out.print("Процесс: "+progress); 
					int speedStart = line.indexOf("speed=") + 6;
					speed = "" + line.substring(speedStart).trim();
					System.out.println(" - Cкорость: " + speed);
					changeTitle = true;
					
					long newProgress;
					int hours = Integer.parseInt(progress.substring(0, 2));
					int minutes = Integer.parseInt(progress.substring(3, 5));
					int seconds = Integer.parseInt(progress.substring(6, 8));
					int milli = Integer.parseInt(progress.substring(9));
					
					newProgress = (hours*3600000) + (minutes*60000) + (seconds*1000) + (milli * 10);
					
					double percent = ((double)newProgress/durationMilli)*100;
					
					title = (int)percent + "%";
				}
				final String finalTitle = title;
				final boolean finalChangeTitle = changeTitle;
				if (finalChangeTitle) {
					System.out.println(finalTitle);
			     }
				if (mainWindow.debug) System.out.println(line); 
			} 
			
			 System.out.println("поток ffmpeg закрыт");
			 
			 
			 String pathToCloud = FFmpegDecoder.this.pathToFileCloud;
			 System.out.println(pathToCloud);
			 String pathToUploud = FFmpegDecoder.this.pathToUploud;
			// System.out.println(pathToUploud);
			 try {
			 
			     BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
				
			     AmazonS3 s3Client = AmazonS3ClientBuilder
					.standard()
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					.withEndpointConfiguration(
					        new AmazonS3ClientBuilder.EndpointConfiguration(
					        		cloud,region
					        )
					    )
					    .build();
			        File file = new File(pathToUploud);
				s3Client.putObject(new PutObjectRequest(ConstantsUtils.BUCKET_NAME, pathToCloud,file));
				 if (file.exists()) {
				        file.delete();
			            System.out.println(pathToUploud+" файл удален");
			            String text = pathToUploud+" файл удален";
			   		    launchYet.log(text.toString());
		//Here you can add code to remove high-resolution videos downloaded from smartphones, for example, to save space on the cloud.			 
					 
			        }else {System.out.println(pathToUploud+" файл не обнаружен");
			            String text = pathToUploud+" файл не обнаружен";
					    launchYet.log(text.toString());
			        }
				
			  } catch (Exception e) {
		            e.printStackTrace();
		      }
			 
			 PreparedStatement st;
		     @SuppressWarnings("unused")
			 Integer result;
		     Integer id = FFmpegDecoder.this.updateId;
		     Integer state_id = FFmpegDecoder.this.state_id;
		     String query2 = null;
		     if(state_id == 4) {
		    	         query2 = "Update medias set state_id = 5  WHERE id ="+id+" ";	
				}
			 if(state_id == 3) {
				 query2 = "Update medias set state_id = 4,split = 1  WHERE id ="+id+" ";	
			 }
			 
			String query = query2;
		     try {
	             st = ConnectDB.getConnection().prepareStatement(query);
	             result = st.executeUpdate();
	             System.out.println("сделан Update в таблице medias в поле id ="+id);
	             st.close();
	            
	         } catch (Exception e) {
	            e.printStackTrace();
	         }
		    
			 launchYet.mainActions();
		   
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 System.out.println("Поток закрыть!");
		 String text = "Поток закрыть!";
		 launchYet.log(text.toString());
	}


}
