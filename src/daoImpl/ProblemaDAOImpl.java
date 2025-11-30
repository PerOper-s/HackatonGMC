package daoImpl;

import dao.ProblemaDAO;
import database.Database;
import model.Giudice;
import model.Problema;

import java.sql.*;

public class ProblemaDAOImpl implements ProblemaDAO {

    @Override
    public Problema trovaPerHackathon(long hackathonId) {
        String sql = """
            SELECT descrizione, giudice_email, data_pubblicazione
            FROM problema
            WHERE hackathon_id = ?
            ORDER BY data_pubblicazione DESC
            LIMIT 1
            """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String descrizione = rs.getString("descrizione");
                    String emailGiudice = rs.getString("giudice_email");
                    String dataPubblicazione = String.valueOf(rs.getTimestamp("data_pubblicazione"));

                    Giudice g = new Giudice(emailGiudice);
                    return new Problema(descrizione, dataPubblicazione, g);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero del problema", e);
        }

        return null; // nessun problema pubblicato
    }
}
