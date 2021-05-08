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
	
	private String accessKey = ConstantsUtils.ACCESS_KEY;
    private String secretKey = ConstantsUtils.SECRET_KEY;
    private String region = ConstantsUtils.REGION;
	
	Process process;
	Scanner scanner;
	
	String[] cmdffmpeg;
	
	MainDecoder mainWindow;
	Integer updateId;
	String pathToFileCloud;
	String pathToUploud;
	
	public FFmpegDecoder(MainDecoder mainWindow, String[] cmdffmpeg,Integer updateId,String pathToFileCloud,String pathToUploud) {
		this.mainWindow = mainWindow;
		this.cmdffmpeg = cmdffmpeg;
		this.updateId = updateId;
		this.pathToFileCloud = pathToFileCloud;
		this.pathToUploud = pathToUploud;
	}
	
	public void run(){
		System.out.println("Поток запущен...");
		
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
			// System.out.println(pathToCloud);
			 String pathToUploud = FFmpegDecoder.this.pathToUploud;
			// System.out.println(pathToUploud);
			 System.out.println(ConstantsUtils.BUCKET_NAME+"pathToCloud"+pathToCloud+"pathToUploud"+pathToUploud);
			 try {
			 
			    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
				
				AmazonS3 s3Client = AmazonS3ClientBuilder
						.standard()
					    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					    .withEndpointConfiguration(
					        new AmazonS3ClientBuilder.EndpointConfiguration(
					        		"s3.selcdn.ru",region
					        )
					    )
					    .build();
				File file = new File(pathToUploud);
				s3Client.putObject(new PutObjectRequest(ConstantsUtils.BUCKET_NAME, pathToCloud,file));
				 if(file.delete()){
			            System.out.println(pathToUploud+" файл удален");
			        }else {System.out.println(pathToUploud+" файл не обнаружен");}
				
			  } catch (Exception e) {
		            e.printStackTrace();
		      }
			 
			 PreparedStatement st;
		     @SuppressWarnings("unused")
			Integer result;
		     Integer id = FFmpegDecoder.this.updateId;
		     String query = "Update medias set state_id = 1  WHERE id ="+id+" ";
		     
		     try {
	             st = ConnectDB.getConnection().prepareStatement(query);
	             result = st.executeUpdate();
	             System.out.println("сделан Update в таблице medias в поле id ="+id);
	             st.close();
	            
	         } catch (Exception e) {
	            e.printStackTrace();
	         }
		    
		         
			 MainDecoder launchYet = new MainDecoder();
			 launchYet.mainActions();
		   
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 System.out.println("Поток закрыть!");
	}


}
