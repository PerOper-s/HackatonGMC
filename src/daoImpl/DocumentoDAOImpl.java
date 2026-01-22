package daoImpl;

import dao.DocumentoDAO;
import database.Database;

import java.sql.*;
import java.time.LocalDateTime;


/**
 * Implementazione PostgreSQL del {@link dao.DocumentoDAO}.
 * <p>
 * Gestisce i documenti di "progress" caricati dai team durante un hackathon.
 * Ogni salvataggio registra anche la data/ora del caricamento.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.DocumentoDAO
 * @see database.Database#getConnection()
 */

public class DocumentoDAOImpl implements DocumentoDAO {


    /**
     * Salva un documento (progress) caricato da un team per un hackathon.
     *
     * @param teamId id del team che carica il documento
     * @param hackathonId id dell'hackathon
     * @param contenuto testo del documento
     * @param dataCaricamento data e ora del caricamento
     * @throws RuntimeException se c'è un errore SQL durante l'inserimento
     */

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

    /**
     * Recupera il contenuto dell'ultimo documento caricato da un team in un hackathon.
     * <p>
     * Se non esistono documenti, ritorna {@code null}.
     *
     * @param teamId id del team
     * @param hackathonId id dell'hackathon
     * @return contenuto dell'ultimo documento oppure {@code null} se non presente
     * @throws RuntimeException se c'è un errore SQL durante la query
     */


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
