package daoImpl;

import dao.HackathonDAO;
import database.Database;
import model.Hackathon;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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


    @Override
    public void invitaGiudice(String titoloHackathon, String organizzatoreEmail, String giudiceEmail) {
        // 1) Assicura tabella
        final String ddl = """
            CREATE TABLE IF NOT EXISTS invito_giudice (
              hackathon_titolo     VARCHAR NOT NULL,
              organizzatore_email  VARCHAR NOT NULL,
              giudice_email        VARCHAR NOT NULL
            )
            """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(ddl);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2) Evita duplicati
        final String check = "SELECT 1 FROM invito_giudice WHERE hackathon_titolo=? AND organizzatore_email=? AND giudice_email=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(check)) {
            ps.setString(1, titoloHackathon);
            ps.setString(2, organizzatoreEmail);
            ps.setString(3, giudiceEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return; // già invitato, esci silenziosamente
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3) Inserisce invito
        final String insert = "INSERT INTO invito_giudice (hackathon_titolo, organizzatore_email, giudice_email) VALUES (?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(insert)) {
            ps.setString(1, titoloHackathon);
            ps.setString(2, organizzatoreEmail);
            ps.setString(3, giudiceEmail);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === NEW: lista giudici invitati ===
    @Override
    public List<String> listaGiudiciInvitati(String titoloHackathon, String organizzatoreEmail) {
        final String sql = "SELECT giudice_email FROM invito_giudice WHERE hackathon_titolo=? AND organizzatore_email=?";
        List<String> emails = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titoloHackathon);
            ps.setString(2, organizzatoreEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emails.add(rs.getString("giudice_email"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    public Long findIdByTitolo(String titolo) {
        String sql = "SELECT id FROM hackathon WHERE titolo = ? LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titolo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // non trovato
    }

    @Override
    public List<Hackathon> findByUtenteIscritto(String emailUtente) {
        List<Hackathon> result = new ArrayList<>();

        String sql = """
        SELECT h.*
        FROM hackathon h
        JOIN iscrizione i ON i.hackathon_id = h.id
        WHERE i.utente_email = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailUtente);

            try (ResultSet rs = ps.executeQuery()) {
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
                    result.add(h);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}



