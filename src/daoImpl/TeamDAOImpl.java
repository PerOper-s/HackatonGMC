package daoImpl;

import dao.TeamDAO;
import database.Database;
import model.TeamInfo;
import java.util.ArrayList;
import java.util.List;


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


    @Override
    public List<TeamInfo> findTeamsByUtente(String emailUtente) {
        String sql = """
            SELECT t.id,
                   t.nome             AS team_nome,
                   h.titolo           AS hackathon_titolo,
                   t.creatore_email   AS creatore_email,
                   COUNT(tm2.utente_email) AS num_membri
            FROM team t
            JOIN team_membro tm
              ON tm.team_id = t.id
             AND tm.utente_email = ?
            JOIN hackathon h
              ON h.id = t.hackathon_id
            LEFT JOIN team_membro tm2
              ON tm2.team_id = t.id
            GROUP BY t.id, t.nome, h.titolo, t.creatore_email
            ORDER BY h.titolo, t.nome
            """;

        List<TeamInfo> result = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, emailUtente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String nomeTeam = rs.getString("team_nome");
                    String titoloH = rs.getString("hackathon_titolo");
                    String creatore = rs.getString("creatore_email");
                    int numMembri = rs.getInt("num_membri");

                    result.add(new TeamInfo(id, nomeTeam, titoloH, creatore, numMembri));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei team dell'utente", e);
        }

        return result;
    }

    @Override
    public List<String> findMembriTeam(long teamId) {
        String sql = "SELECT utente_email FROM team_membro WHERE team_id = ? ORDER BY utente_email";
        List<String> result = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("utente_email"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero dei membri del team", e);
        }

        return result;
    }

}
