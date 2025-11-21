package daoImpl;

import dao.TeamDAO;
import database.Database;

import java.sql.*;

public class TeamDAOImpl implements TeamDAO {

    @Override
    public boolean utenteHaTeam(long hackathonId, String emailUtente) {
        String sql = """
            SELECT 1
            FROM team t
            JOIN team_membro tm ON tm.team_id = t.id
            WHERE t.hackathon_id = ? AND tm.utente_email = ?
            LIMIT 1
            """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, hackathonId);
            ps.setString(2, emailUtente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean esisteTeamConNome(long hackathonId, String nomeTeam) {
        String sql = "SELECT 1 FROM team WHERE hackathon_id = ? AND nome = ? LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, hackathonId);
            ps.setString(2, nomeTeam);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long creaTeam(String nomeTeam, long hackathonId, String creatoreEmail) {
        String insertTeam = "INSERT INTO team(nome, hackathon_id, creatore_email) VALUES (?,?,?) RETURNING id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(insertTeam)) {

            ps.setString(1, nomeTeam);
            ps.setLong(2, hackathonId);
            ps.setString(3, creatoreEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long teamId = rs.getLong(1);

                    // aggiungo il creatore come primo membro
                    try (PreparedStatement ps2 = c.prepareStatement(
                            "INSERT INTO team_membro(team_id, utente_email) VALUES (?,?)")) {
                        ps2.setLong(1, teamId);
                        ps2.setString(2, creatoreEmail);
                        ps2.executeUpdate();
                    }

                    return teamId;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante la creazione del team", e);
        }
        throw new RuntimeException("Impossibile creare il team");
    }
}
