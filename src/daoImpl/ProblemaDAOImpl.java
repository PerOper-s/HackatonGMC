package daoImpl;

import dao.ProblemaDAO;
import database.Database;
import model.Giudice;
import model.Problema;

import java.sql.*;


/**
 * Implementazione PostgreSQL del {@link dao.ProblemaDAO}.
 * <p>
 * Gestisce i problemi pubblicati dai giudici per un hackathon.
 * La dashboard mostra i problemi in lista (es. "Problema 1) ... giudice: email").
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.ProblemaDAO
 * @see model.Problema
 * @see database.Database#getConnection()
 */

public class ProblemaDAOImpl implements ProblemaDAO {


    /**
     * Recupera un problema associato ad un hackathon (se presente).
     *
     * @param hackathonId id dell'hackathon
     * @return problema trovato, oppure {@code null} se non ci sono problemi
     * @throws RuntimeException se c'è un errore SQL durante la query
     */

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


    /**
     * Salva un nuovo problema pubblicato da un giudice per un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param giudiceEmail email del giudice che pubblica
     * @param descrizione testo/descrizione del problema
     * @throws RuntimeException se c'è un errore SQL durante l'inserimento
     */

    @Override
    public void pubblicaProblema(long hackathonId, String giudiceEmail, String descrizione) {
        final String ddl = """
        CREATE TABLE IF NOT EXISTS problema (
          id SERIAL PRIMARY KEY,
          hackathon_id INTEGER NOT NULL REFERENCES hackathon(id) ON DELETE CASCADE,
          descrizione TEXT NOT NULL,
          giudice_email VARCHAR NOT NULL,
          data_pubblicazione TIMESTAMP NOT NULL DEFAULT NOW()
        )
        """;

        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Errore creazione tabella problema", e);
        }

        String sql = "INSERT INTO problema(hackathon_id, descrizione, giudice_email, data_pubblicazione) VALUES (?,?,?, NOW())";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            ps.setString(2, descrizione);
            ps.setString(3, giudiceEmail);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore pubblicazione problema", e);
        }
    }


    /**
     * Recupera tutti i problemi pubblicati per un hackathon.
     * <p>
     * Utile per mostrare "Problema 1, Problema 2, ..." in UI.
     *
     * @param hackathonId id dell'hackathon
     * @return lista problemi (vuota se non ce ne sono)
     * @throws RuntimeException se c'è un errore SQL durante la query
     */

    @Override
    public java.util.List<Problema> trovaTuttiPerHackathon(long hackathonId) {
        String sql = """
        SELECT descrizione, giudice_email, data_pubblicazione
        FROM problema
        WHERE hackathon_id = ?
        ORDER BY data_pubblicazione ASC
        """;

        java.util.List<Problema> result = new java.util.ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String descrizione = rs.getString("descrizione");
                    String emailGiudice = rs.getString("giudice_email");
                    String dataPubblicazione = String.valueOf(rs.getTimestamp("data_pubblicazione"));

                    Giudice g = new Giudice(emailGiudice);
                    result.add(new Problema(descrizione, dataPubblicazione, g));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei problemi", e);
        }

        return result;
    }


}
