package com.leah.data.dongao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.leah.ipproxy.database.MySQLDB;

public class DongaoDao {
	private Connection connection = MySQLDB.getConnection();
	public void insertUrl(Queue<String> allUrls) {
		 PreparedStatement statement;
		 try {
	        for (String url : allUrls) {
	            
	                statement = connection.prepareStatement("INSERT INTO dongaourl(url) VALUES (?)");

	                statement.setString(1, url);
	                statement.execute();
	           
	        }
		 } catch (SQLException e) {
             e.printStackTrace();
         } finally {
			
        	 try {
        		 connection.close();
        	 } catch (SQLException e) {
        		 // TODO Auto-generated catch block
        		 e.printStackTrace();
        	 }
		}
	}
	public Queue<String> getAllUrls() {
	        Queue<String> allurls = new ConcurrentLinkedQueue<>();

	        try {
	            PreparedStatement statement = connection.prepareStatement("SELECT url FROM dongaourl");
	            ResultSet resultSet = statement.executeQuery();

	            while (resultSet.next()) {
	            	allurls.offer(resultSet.getString(1));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	        	try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
	        return allurls;
	}
	public void insertTest(Queue<TestDataModel> allTest) {
		 PreparedStatement statement;
		 try {
	        for (TestDataModel test : allTest) {
	            
	                statement = connection.prepareStatement("INSERT INTO pre_dongaoTest(part,contentType,content) VALUES (?,?,?)");

	                statement.setInt(1, test.getPart());
	                statement.setString(2, test.getContentType());
	                statement.setString(3, test.getContent());
	                statement.execute();
	           
	        }
		 } catch (SQLException e) {
             e.printStackTrace();
         } finally {
			
        	 try {
        		 connection.close();
        	 } catch (SQLException e) {
        		 // TODO Auto-generated catch block
        		 e.printStackTrace();
        	 }
		}
	}
}
