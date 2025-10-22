package daoImpl;

import dao.HackathonDAO;
import database.Database;
import model.Hackathon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import model.Organizzatore;

public class HackathonDAOImpl implements HackathonDAO {

    @Override
    public void creaHackathon(Hackathon h) {
        String sql = "INSERT INTO hackathon (titolo, sede, max_partecipanti, max_team_size, data_inizio, data_inizio_iscr, data_fine_iscr, organizzatore_email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            ps.setString(1, h.getTitolo());
            ps.setString(2, h.getSede());
            ps.setInt(3, h.getMaxPartecipanti());
            ps.setInt(4, h.getMaxGrandezzaTeam());
            ps.setDate(5, Date.valueOf(LocalDate.parse(h.getInizio(), formatter)));
            ps.setDate(6, Date.valueOf(LocalDate.parse(h.getInizioIscrizioni(), formatter)));
            ps.setDate(7, Date.valueOf(LocalDate.parse(h.getFineIscrizioni(), formatter)));
            ps.setString(8, h.getOrganizzatore());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento dell'hackathon: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
    }

    @Override
    public List<Hackathon> findAll() {
        List<Hackathon> hackathonList = new ArrayList<>();
        String sql = "SELECT * FROM Hackathon";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Hackathon h = new Hackathon(
                        rs.getString("titolo"),
                        rs.getString("sede"),
                        new Organizzatore(rs.getString("organizzatore_email")),
                        rs.getInt("max_partecipanti"),
                        rs.getInt("max_team_size"),
                        rs.getDate("data_inizio").toLocalDate().toString(),
                        rs.getDate("data_inizio_iscr").toLocalDate().toString(),
                        rs.getDate("data_fine_iscr").toLocalDate().toString()
                );
                hackathonList.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hackathonList;
    }

    @Override
    public List<Hackathon> findByOrganizzatore(String email) {
        List<Hackathon> hackathonList = new ArrayList<>();
        String sql = "SELECT * FROM Hackathon WHERE organizzatore_email = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Hackathon h = new Hackathon(
                        rs.getString("titolo"),
                        rs.getString("sede"),
                        new Organizzatore(rs.getString("organizzatore_email")),
                        rs.getInt("max_partecipanti"),
                        rs.getInt("max_team_size"),
                        rs.getDate("data_inizio").toLocalDate().toString(),
                        rs.getDate("data_inizio_iscr").toLocalDate().toString(),
                        rs.getDate("data_fine_iscr").toLocalDate().toString()
                );
                hackathonList.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hackathonList;
    }


}

