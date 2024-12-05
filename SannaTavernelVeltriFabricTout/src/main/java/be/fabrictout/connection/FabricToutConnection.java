package be.fabrictout.connection;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FabricToutConnection {

    private static Connection instance = null;

    private FabricToutConnection() {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/FabricToutDB");

            instance = ds.getConnection();
            System.out.println("Connexion réussie avec JNDI !");
        } catch (NamingException ex) {
            throw new RuntimeException("Erreur JNDI : " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur JDBC : " + ex.getMessage(), ex);
        }

        if (instance == null) {
            throw new RuntimeException("La base de données est inaccessible, fermeture du programme.");
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new FabricToutConnection();
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                instance = null; 
                System.out.println("Connexion fermée !");
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + ex.getMessage());
            }
        }
    }
}
