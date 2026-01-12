package daoImpl;

import dao.VotoDAO;
import database.Database;

import java.sql.*;

public class VotoDAOImpl implements VotoDAO {

    private void ensureSchema() {
        final String ddlVoto = """
            CREATE TABLE IF NOT EXISTS voto (
              hackathon_id INTEGER NOT NULL REFERENCES hackathon(id) ON DELETE CASCADE,
              team_id INTEGER NOT NULL REFERENCES team(id) ON DELETE CASCADE,
              giudice_email VARCHAR NOT NULL,
              valore INTEGER NOT NULL CHECK (valore BETWEEN 0 AND 10),
              PRIMARY KEY (hackathon_id, team_id, giudice_email)
            )
            """;

        final String ddlGiudiciHack = """
            CREATE TABLE IF NOT EXISTS giudice_hackathon (
              hackathon_id   INTEGER NOT NULL REFERENCES hackathon(id) ON DELETE CASCADE,
              giudice_email  VARCHAR NOT NULL,
              PRIMARY KEY (hackathon_id, giudice_email)
            )
            """;

        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(ddlGiudiciHack);
            st.executeUpdate(ddlVoto);
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella preparazione schema voti", e);
        }
    }

    @Override
    public void salvaVoto(long hackathonId, long teamId, String giudiceEmail, int valore) {
        if (valore < 0 || valore > 10) {
            throw new IllegalArgumentException("Il voto deve essere tra 0 e 10");
        }

        ensureSchema();

        String sql = """
            INSERT INTO voto(hackathon_id, team_id, giudice_email, valore)
            VALUES (?,?,?,?)
            ON CONFLICT (hackathon_id, team_id, giudice_email)
            DO UPDATE SET valore = EXCLUDED.valore
            """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            ps.setLong(2, teamId);
            ps.setString(3, giudiceEmail);
            ps.setInt(4, valore);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore salvataggio voto", e);
        }
    }

    @Override
    public int countVotiInseriti(long hackathonId) {
        ensureSchema();

        String sql = "SELECT COUNT(*) FROM voto WHERE hackathon_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Errore conteggio voti inseriti", e);
        }
    }

    @Override
    public int countVotiAttesi(long hackathonId) {
        ensureSchema();

        int giudici = 0;
        int teams = 0;

        try (Connection c = Database.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM giudice_hackathon WHERE hackathon_id = ?")) {
                ps.setLong(1, hackathonId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) giudici = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM team WHERE hackathon_id = ?")) {
                ps.setLong(1, hackathonId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) teams = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore conteggio voti attesi", e);
        }

        long attesi = (long) giudici * (long) teams;
        if (attesi > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) attesi;
    }

    @Override
    public boolean votiCompleti(long hackathonId) {
        int attesi = countVotiAttesi(hackathonId);
        if (attesi <= 0) return false;
        int inseriti = countVotiInseriti(hackathonId);
        return inseriti >= attesi;
    }
}
