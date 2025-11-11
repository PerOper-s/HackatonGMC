package daoImpl;

import dao.IscrizioneDAO;
import database.Database;

import java.sql.*;
import java.time.LocalDate;

public class IscrizioneDAOImpl implements IscrizioneDAO {

    @Override
    public boolean isIscritto(long hackathonId, String emailUtente) {
        String sql = "SELECT 1 FROM iscrizione WHERE hackathon_id = ? AND utente_email = ? LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, hackathonId);
            ps.setString(2, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore isIscritto", e);
        }
    }

    @Override
    public int countIscritti(long hackathonId) {
        String sql = "SELECT COUNT(*) FROM iscrizione WHERE hackathon_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, hackathonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore countIscritti", e);
        }
    }

    @Override
    public void iscrivi(long hackathonId, String emailUtente, LocalDate dataIscrizione) {
        String sql = "INSERT INTO iscrizione(hackathon_id, utente_email, data_iscrizione) VALUES (?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, hackathonId);
            ps.setString(2, emailUtente);
            ps.setDate(3, java.sql.Date.valueOf(dataIscrizione));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore iscrizione utente", e);
        }
    }
}
