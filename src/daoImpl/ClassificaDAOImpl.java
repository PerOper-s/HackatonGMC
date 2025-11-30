package daoImpl;

import dao.ClassificaDAO;
import database.Database;
import model.Classifica;
import model.Hackathon;
import model.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassificaDAOImpl implements ClassificaDAO {

    @Override
    public List<Classifica> findClassificaByHackathon(long hackathonId) {

        String sql = """
            SELECT t.id      AS team_id,
                   t.nome    AS team_nome,
                   COALESCE(AVG(v.valore), 0) AS punteggio
            FROM team t
            LEFT JOIN voto v
              ON v.team_id = t.id
             AND v.hackathon_id = ?
            WHERE t.hackathon_id = ?
            GROUP BY t.id, t.nome
            ORDER BY punteggio DESC, t.nome ASC
            """;

        List<Classifica> result = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            ps.setLong(2, hackathonId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nomeTeam = rs.getString("team_nome");
                    double punteggioMedio = rs.getDouble("punteggio");

                    // crea il Team e impostane il nome
                    model.Team team = new model.Team(null); // l'Hackathon qui non ti serve
                    team.setNome(nomeTeam);

                    int punteggioInt = (int) Math.round(punteggioMedio);

                    // usa il costruttore Classifica(Team, int) che hai già
                    result.add(new model.Classifica(team, punteggioInt));
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero della classifica", e);
        }

        return result;
    }
}
