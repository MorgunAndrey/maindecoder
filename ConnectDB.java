package ru.decoder.maindecoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectDB {
	public static Connection getConnection()
    {
        Connection cn = null;
        
        try {
        	cn = DriverManager.getConnection("jdbc:postgresql://"+ConstantsUtils.host+":"+ConstantsUtils.port+"/"+ConstantsUtils.db_name+","+ConstantsUtils.username+","+ConstantsUtils.password+"");
            
        } catch (SQLException ex) {
            Logger.getLogger(" Get Connection -> " + ConnectDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cn;
    }

}
