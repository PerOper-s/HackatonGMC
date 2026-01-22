package daoImpl;

import dao.CommentoDAO;
import database.Database;
import model.CommentoInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione PostgreSQL del {@link dao.CommentoDAO}.
 * <p>
 * Gestisce i commenti dei giudici sui team durante un hackathon.
 * I commenti vengono salvati e poi recuperati ordinati per data.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.CommentoDAO
 * @see model.CommentoInfo
 * @see database.Database#getConnection()
 */


public class CommentoDAOImpl implements CommentoDAO {


    /**
     * Recupera tutti i commenti inseriti per un team in un hackathon.
     * <p>
     * I risultati sono ordinati per data di commento (dal più vecchio al più recente).
     *
     * @param hackathonId id dell'hackathon
     * @param teamId id del team
     * @return lista di {@link model.CommentoInfo} (vuota se non ci sono commenti)
     * @throws RuntimeException se c'è un errore SQL durante la query
     */

    @Override
    public List<CommentoInfo> findCommentiPerTeam(long hackathonId, long teamId) {
        List<CommentoInfo> result = new ArrayList<>();

        String sql = """
            SELECT giudice_email, contenuto
            FROM commento
            WHERE hackathon_id = ? AND team_id = ?
            ORDER BY data_commento
            """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            ps.setLong(2, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("giudice_email");
                    String contenuto = rs.getString("contenuto");
                    result.add(new CommentoInfo(email, contenuto));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei commenti", e);
        }

        return result;
    }

    /**
     * Salva un commento di un giudice per un team in un hackathon.
     * <p>
     * In questa implementazione, prima assicuro l'esistenza della tabella {@code commento}
     * con un CREATE TABLE IF NOT EXISTS, poi inserisco il record con data corrente (NOW()).
     *
     * @param hackathonId id dell'hackathon
     * @param teamId id del team
     * @param giudiceEmail email del giudice che commenta
     * @param contenuto testo del commento
     * @throws RuntimeException se fallisce la creazione tabella o l'inserimento
     */

    @Override
    public void salvaCommento(long hackathonId, long teamId, String giudiceEmail, String contenuto) {

        final String ddl = """
        CREATE TABLE IF NOT EXISTS commento (
          id SERIAL PRIMARY KEY,
          hackathon_id INTEGER NOT NULL REFERENCES hackathon(id) ON DELETE CASCADE,
          team_id INTEGER NOT NULL REFERENCES team(id) ON DELETE CASCADE,
          giudice_email VARCHAR NOT NULL,
          contenuto TEXT NOT NULL,
          data_commento TIMESTAMP NOT NULL DEFAULT NOW()
        )
        """;

        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Errore creazione tabella commento", e);
        }

        String sql = "INSERT INTO commento(hackathon_id, team_id, giudice_email, contenuto, data_commento) VALUES (?,?,?,?, NOW())";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            ps.setLong(2, teamId);
            ps.setString(3, giudiceEmail);
            ps.setString(4, contenuto);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore salvataggio commento", e);
        }
    }

}
