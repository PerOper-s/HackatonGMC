package daoImpl;

import dao.IscrizioneDAO;
import database.Database;

import java.sql.*;
import java.time.LocalDate;


/**
 * Implementazione PostgreSQL del {@link dao.IscrizioneDAO}.
 * <p>
 * Gestisce le iscrizioni degli utenti agli hackathon: verifica iscrizione, conteggio iscritti,
 * inserimento nuova iscrizione con data.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.IscrizioneDAO
 * @see java.time.LocalDate
 * @see database.Database#getConnection()
 */


public class IscrizioneDAOImpl implements IscrizioneDAO {


    /**
     * Controlla se un utente risulta iscritto ad un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @return true se l'iscrizione esiste, false altrimenti
     * @throws RuntimeException se c'è un errore SQL durante la query
     */

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


    /**
     * Conta quanti utenti sono iscritti ad un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @return numero iscritti (0 se nessuno)
     * @throws RuntimeException se c'è un errore SQL durante la query
     */

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


    /**
     * Inserisce una nuova iscrizione di un utente ad un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @param dataIscrizione data in cui avviene l'iscrizione
     * @throws RuntimeException se c'è un errore SQL durante l'inserimento
     */

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
