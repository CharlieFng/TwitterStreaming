package spark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedList;

public class ConnectionPool {

    private static LinkedList<Connection> connectionQueue;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public synchronized static Connection getConnection() {
        try{
            if(connectionQueue == null){
                connectionQueue = new LinkedList<Connection>();
                for(int i=0; i<5; i++){
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/tweets", "postgres", "postgres");
                    connectionQueue.push(conn);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return connectionQueue.poll();
    }

    public static void returnConnection(Connection conn){
        connectionQueue.push(conn);
    }
}
