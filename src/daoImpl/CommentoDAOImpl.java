package daoImpl;

import dao.CommentoDAO;
import database.Database;
import model.CommentoInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentoDAOImpl implements CommentoDAO {

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
