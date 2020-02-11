package dbconn;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class mysqlConnHelper{
    public static Connection getConnection() {
    	Connection conn = null;
		
		try {			
	            //Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
	          
	            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/englishdictionary?user=root&password=");
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return conn;
    }
    
   
}
