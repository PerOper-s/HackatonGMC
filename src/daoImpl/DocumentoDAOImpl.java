package daoImpl;

import dao.DocumentoDAO;
import database.Database;

import java.sql.*;
import java.time.LocalDateTime;

public class DocumentoDAOImpl implements DocumentoDAO {

    @Override
    public void salvaDocumento(long teamId,
                               long hackathonId,
                               String contenuto,
                               LocalDateTime dataCaricamento) {

        String sql = "INSERT INTO documento(team_id, hackathon_id, contenuto, data_caricamento) " +
                "VALUES (?,?,?,?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, teamId);
            ps.setLong(2, hackathonId);
            ps.setString(3, contenuto);
            ps.setTimestamp(4, Timestamp.valueOf(dataCaricamento));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio del documento", e);
        }
    }

    @Override
    public String trovaUltimoDocumento(long teamId, long hackathonId) {
        String sql = "SELECT contenuto " +
                "FROM documento " +
                "WHERE team_id = ? AND hackathon_id = ? " +
                "ORDER BY data_caricamento DESC " +
                "LIMIT 1";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, teamId);
            ps.setLong(2, hackathonId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("contenuto");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dell'ultimo documento", e);
        }

        return null; // nessun documento
    }

}
