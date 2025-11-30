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
}
