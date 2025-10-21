package daoImpl;

import dao.UtenteDAO;
import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAOImpl implements UtenteDAO {

    @Override
    public boolean esisteUtente(String email, String ruolo) {
        String query = "SELECT * FROM utente WHERE email = ? AND ruolo = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, ruolo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void registraUtente(String email, String ruolo) {
        String insert = "INSERT INTO utente (email, ruolo) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, email);
            ps.setString(2, ruolo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
