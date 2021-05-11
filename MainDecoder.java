package ru.decoder.maindecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MainDecoder {
	
	static BufferedReader bufferedReader;
	public boolean debug;
	FFmpegDecoder ffmpegDecoder;
	
	public static void main(String[] args) {
		    MainDecoder mainDecoder = new MainDecoder();
		    mainDecoder.mainActions();
    	}
	
	public void mainActions(){
		boolean notFile = checkMedias();
		//	System.out.println(notFile);
	    	if(!notFile) {
	    		System.out.println("Нет файла для обжима!");
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
        
        String query = "SELECT id FROM medias WHERE type_id = 2 AND state_id = 0 AND split > 0 ORDER BY id DESC LIMIT 1";       
        
        try {
             statement = ConnectDB.getConnection().prepareStatement(query);
             result = statement.executeQuery();
        
             if(result.next()) {
            	id_exist = true;
                System.out.println("Есть файл для общима!");
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
  		}
  		
  		return goodToGo;
  	}
  	
   private void convert() {
  		System.out.println("Сжатие...");
  		
  		PreparedStatement st;
        ResultSet rs;
        
        String pathFromFileCloud = null;
        String pathToUploud = null;
        String pathToFileCloud = null;
        Object updateId = null;
        Integer split = null;
        
        String query = "SELECT id, lot_id, user_id, type_id, state_id, scale, split, mediavideo "
        		+ "FROM medias WHERE type_id = 2 AND state_id = 0 AND split > 0 ORDER BY id DESC LIMIT 1";
       
        try {
             st = ConnectDB.getConnection().prepareStatement(query);
             rs = st.executeQuery();
             
            	 while(rs.next()) {
            		    split= rs.getInt("split");
            		    updateId = rs.getInt("id"); 
            		    Integer userId = rs.getInt("user_id"); 
            		    Integer lotId = rs.getInt("lot_id");
            		    String mediaVideo = rs.getString("mediavideo");
            		    
            		    pathFromFileCloud = ConstantsUtils.END_POINT_URL+ConstantsUtils.BUCKET_NAME+userId.toString()+""
            		    		+ "/"+lotId.toString()+"/"+mediaVideo;
            		    pathToUploud = ConstantsUtils.PATH_APP_UPLOADS+"/"+mediaVideo;
            		    pathToFileCloud = userId.toString()+""
            		    		+ "/"+lotId.toString()+"/240_"+mediaVideo;
            		    
            		    System.out.println("Успешно запрос к таблице medias!");
            	 }
             
             st.close();
            
       
  	ArrayList<String> cmdffmpeg = new ArrayList<String>();
  	
  		cmdffmpeg.add("ffmpeg");	
  		cmdffmpeg.add("-i");
  		cmdffmpeg.add(pathFromFileCloud);
  		cmdffmpeg.add("-vf");
  		
		if(split>4) {
			cmdffmpeg.add("scale=iw/4:ih/4");	
		} else {
  		        cmdffmpeg.add("scale=iw/2:ih/2");
  		}
		
  		cmdffmpeg.add("-y");
  		
  		String out = pathToUploud;
  		cmdffmpeg.add(out);
  		
  		
        execFFMPEG(cmdffmpeg,(Integer) updateId,pathToFileCloud,pathToUploud);
  		
          } catch (Exception e) {
            e.printStackTrace();
          }
  	}
  	
  	private void execFFMPEG(ArrayList<String> command,int updateId,String pathToFileCloud,String pathToUploud) {
          String[] cmdffmpeg = new String[command.size()];
          for (int i = 0; i < command.size(); i++) {
          	cmdffmpeg[i] = command.get(i);
          }
          
          System.out.println("Создана команда для ffmpeg:");
          for (int i = 0; i < cmdffmpeg.length; i++){
              System.out.print(cmdffmpeg[i] + " ");
          }
          System.out.println(cmdffmpeg);
          ffmpegDecoder = new FFmpegDecoder(this,cmdffmpeg,updateId,pathToFileCloud,pathToUploud);
          ffmpegDecoder.start();
         
  	}
  	

}
