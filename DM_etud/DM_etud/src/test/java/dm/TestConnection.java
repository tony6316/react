package dm;

import util.DB;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection connection = DB.createDefaultDB().getConnection()) {
            if (connection != null) {
                System.out.println("Connexion à la base de données Faizandier réussie.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }
}
