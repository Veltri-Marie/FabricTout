package be.fabrictout.connection;

import java.io.*;
import java.sql.*;
import java.util.*;

public class FabricToutConnection {

    private static Connection instance = null;

    private FabricToutConnection() {
        try {
            Properties properties = new Properties();
            System.out.println("Tentative de connexion à la base de données");
            System.out.println("Répertoire de travail actuel: " + System.getProperty("user.dir"));

            InputStream inputStream = getClass().getResourceAsStream("/config.properties");
            if (inputStream == null) {
                System.out.println("Fichier config.properties non trouvé");
                return;
            }
            properties.load(inputStream);

            Class.forName("oracle.jdbc.OracleDriver");

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            System.setProperty("oracle.jdbc.Trace", "true");
            instance = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
        } catch (ClassNotFoundException ex) {
            System.out.println("Classe de driver introuvable : " + ex.getMessage());
            System.exit(0);
        } catch (SQLException ex) {
            System.out.println("Erreur JDBC : " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Erreur lors du chargement du fichier de configuration : " + ex.getMessage());
        }
        
        if (instance == null) {
            System.out.println("La base de données est inaccessible, fermeture du programme.");
            System.exit(0);
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
