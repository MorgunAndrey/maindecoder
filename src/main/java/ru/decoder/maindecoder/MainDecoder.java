package ru.decoder.maindecoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

public class MainDecoder {
	
	static BufferedReader bufferedReader;
	public boolean debug;
	FFmpegDecoder ffmpegDecoder;
	static int i = 0;
	
	public static void main(String[] args) {
		    String text = "Старт!--------------------------";
		    MainDecoder mainDecoder = new MainDecoder();
		    mainDecoder.log(text.toString());
		    mainDecoder.mainActions();
    	}
	
	public void log(String textlog) {
		String filePath = ConstantsUtils.PATH_APP_UPLOADS+"/logdecoder.txt";
		Date date = new Date();
		 
		i++;
		String text = date+" "+i+". "+textlog+"\n";
        try {
            FileWriter writer = new FileWriter(filePath, true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write(text);
            bufferWriter.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    	}
	
	
	
	public void mainActions(){
		boolean notFile = checkMedias();
		//	System.out.println(notFile);
	    	if(!notFile) {
	    		System.out.println("Нет файла для обжима!");
	    		String text = "Нет файла для обжима! Программа остановлена--------------------";
	    		MainDecoder mainDecoder = new MainDecoder();
			    mainDecoder.log(text.toString());
			    System.exit(0) ;
	    	}else if (notFile){
	    		softCheck();
				MainDecoder md = new MainDecoder();
				md.convert();
	    	}
		  }
	
    public static  boolean checkMedias(){
        
        PreparedStatement statement;
        ResultSet result;
        boolean id_exist = false;
        
        String query = "SELECT id FROM medias WHERE type_id = 2 AND state_id < 5 AND scale > 0 ORDER BY id DESC LIMIT 1";       
        
        try {
             statement = ConnectDB.getConnection().prepareStatement(query);
             result = statement.executeQuery();
        
             if(result.next()) {
            	id_exist = true;
                System.out.println("Есть файл для общима!");
                String text = "Есть файл для общима!";
	    		MainDecoder mainDecoder = new MainDecoder();
			    mainDecoder.log(text.toString());
             }
             
             statement.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return id_exist;
    }
    	
  	private static boolean softCheck() {  
  		boolean goodToGo = false;
  		
  		ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-h");
  		Process process;
  		
  		try {
  			process = processBuilder.start();
  			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
  			
  			String line = "";
  			while((line = bufferedReader.readLine()) != null) {
  				if (line.contains("Hyper fast Audio and Video encoder")) {
  					System.out.println("обнаружен ffmpeg :)");
  					goodToGo = true;
  					break;
  				} else {
  					System.out.println("ffmpeg не найден");
  					goodToGo = false;
  				}
  			}
  			bufferedReader.close();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  		
  		if (goodToGo) {
  			System.out.println("Готовы конвертировать файлы!");
  			String text = "Готовы конвертировать файлы!";
    		MainDecoder mainDecoder = new MainDecoder();
		    mainDecoder.log(text.toString());
  		}
  		
  		return goodToGo;
  	}
  	
   private void convert() {
  		System.out.println("Сжатие...");
  		String text = "Сжатие...!";
		MainDecoder mainDecoder = new MainDecoder();
	    mainDecoder.log(text.toString());
  		
  		PreparedStatement st;
        ResultSet rs;
        
        String pathFromFileCloud = null;
        String pathToUploud = null;
        String pathToFileCloud = null;
        Object updateId = null;
        Integer state_id = null;
        Integer split = null;
        
        String query = "SELECT id, lot_id, user_id, type_id, state_id,scale,split, mediavideo "
        		+ "FROM medias WHERE type_id = 2 AND state_id < 5  AND scale > 0 ORDER BY id DESC LIMIT 1";
       
        try {
             st = ConnectDB.getConnection().prepareStatement(query);
             rs = st.executeQuery();
             
            	 while(rs.next()) {
            		    state_id = rs.getInt("state_id");
            		    split = rs.getInt("split");
            		    updateId = rs.getInt("id"); 
            		    Integer userId = rs.getInt("user_id"); 
            		    Integer lotId = rs.getInt("lot_id");
            		    String mediaVideo = rs.getString("mediavideo");
            		    
            		    pathFromFileCloud = ConstantsUtils.END_POINT_URL+ConstantsUtils.BUCKET_NAME+userId.toString()+""
            		    		+ "/"+lotId.toString()+"/"+mediaVideo;
            		    pathToUploud = ConstantsUtils.PATH_APP_UPLOADS+"/"+mediaVideo;
            		    
            		    if(state_id == 4 && split == 1) {
            		    	pathFromFileCloud = ConstantsUtils.END_POINT_URL+ConstantsUtils.BUCKET_NAME+userId.toString()+""
                		    		+ "/"+lotId.toString()+"/720_"+mediaVideo;
            		        pathToFileCloud = userId.toString()+""
            		    		+ "/"+lotId.toString()+"/360_"+mediaVideo;
            		        String text2 = pathToFileCloud;
                		    mainDecoder.log(text2.toString());
            		        
            		    }
            		    if(state_id == 4 && split != 1) {
            		        pathToFileCloud = userId.toString()+""
            		    		+ "/"+lotId.toString()+"/360_"+mediaVideo;
            		        String text2 = pathToFileCloud;
                		    mainDecoder.log(text2.toString());
            		    }
            		    if(state_id == 3) {
                		    pathToFileCloud = userId.toString()+""
                		    		+ "/"+lotId.toString()+"/720_"+mediaVideo;
                		    String text2 = pathToFileCloud;
                		    mainDecoder.log(text2.toString());
                		    }
            		    
            		    System.out.println("Успешно запрос к таблице medias!");
            		    String text2 = "Успешно запрос к таблице medias!";
            		    mainDecoder.log(text2.toString());
            	 }
             
             st.close();
            
       
  	ArrayList<String> cmdffmpeg = new ArrayList<String>();
  	
  		cmdffmpeg.add("ffmpeg");	
  		cmdffmpeg.add("-i");
  		cmdffmpeg.add(pathFromFileCloud);
  		cmdffmpeg.add("-vf");
  		
		if(state_id == 4) {
			cmdffmpeg.add("scale=360:-2");	
		}
		if(state_id == 3) {
			cmdffmpeg.add("scale=720:-2");	
		}
		System.out.print("state_id-"+state_id);
  		cmdffmpeg.add("-y");
  		
  		String out = pathToUploud;
  		cmdffmpeg.add(out);
  		
  		 
  		
        execFFMPEG(cmdffmpeg,(Integer) updateId,pathToFileCloud,pathToUploud,state_id);
  		
        } catch (Exception e) {
            e.printStackTrace();
        }
  	}
  	
  	private void execFFMPEG(ArrayList<String> command,int updateId,String pathToFileCloud,String pathToUploud,Integer state_id) {
          String[] cmdffmpeg = new String[command.size()];
          for (int i = 0; i < command.size(); i++) {
          	cmdffmpeg[i] = command.get(i);
          }
          
          System.out.println("Создана команда для ffmpeg:");
          for (int i = 0; i < cmdffmpeg.length; i++){
              System.out.print(cmdffmpeg[i] + " ");
          }
          System.out.println(cmdffmpeg);
          ffmpegDecoder = new FFmpegDecoder(this,cmdffmpeg,updateId,pathToFileCloud,pathToUploud,state_id);
          ffmpegDecoder.start();
         
  	}
  	

}
