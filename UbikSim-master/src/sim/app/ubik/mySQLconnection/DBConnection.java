package sim.app.ubik.mySQLconnection;

import java.sql.*;

public class DBConnection {

   private Connection conn = null;
   private Statement stmt = null;
   private STATUS connectionStatus;
   public enum STATUS {CONNECTED,DISCONNECTED,ERROR};
   
   public DBConnection(){
	connectionStatus = STATUS.DISCONNECTED;	   
	
   }
   
   public void connect(){
       
            String DriverURL = "jdbc:mysql://localhost/db_datafromsimulation";
            String Driver = "com.mysql.jdbc.Driver";
	    String USER_NAME = "root";
	    String PASSWORD = "1234";
	    
	    try{
	    	Class.forName(Driver);
	    	System.out.println("Driver Loaded");
                conn = DriverManager.getConnection(DriverURL, USER_NAME, PASSWORD);
	    	System.out.println("Connected to Local DB");	    		    	
	    } catch (ClassNotFoundException | SQLException e1) {
	    	System.err.println("Got an Exception!");
	    	e1.printStackTrace();
                try{
                    conn.close();
                }catch(SQLException e2){
                    System.err.println("Got an Exception!");
                    e2.printStackTrace();
                }	    	
	    }
   }
   
   public STATUS checkConnection(){
	   return connectionStatus;
   }
   
   public void setConnection(STATUS connectionStatus){
	   this.connectionStatus = connectionStatus;
   }
      
   public void executeUpdate(String query){
        try{
            if(conn != null){
               stmt = conn.createStatement();
                if(stmt != null){
                    stmt.executeUpdate(query);
                    stmt.close();
                }
            }
        }catch(Exception e){
           System.err.println("Got an Exception!");
            e.printStackTrace();
        }
        
   }
   
    public ResultSet executeQuery(String query){
            ResultSet resultSet = null;
            try{
                if(conn != null){
                    stmt = conn.createStatement();
                    if(stmt != null){
                        resultSet = stmt.executeQuery(query);
			stmt.close();
                    }
                }
            }catch(Exception e){
                System.err.println("Got an Exception!");
                e.printStackTrace();
            }
            return resultSet;
    }
   
   public ResultSet executeQueryOpenStatement(String query) throws SQLException{
	  
	ResultSet resultSet = null;
	if(conn != null){
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(query);
        }else{
            System.out.println("WARNING CONNECTION = NULL");
	}
	   return resultSet;
	}
   
  public void closeStatement() throws SQLException{
	   stmt.close();
   }
   
   public void disconnect()throws SQLException{
	   conn.close();   
   }

    public boolean isConnected() {
	if(connectionStatus == STATUS.CONNECTED) return true;
	else return false;
    }
   
   
}