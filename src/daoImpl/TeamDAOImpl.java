package daoImpl;

import dao.TeamDAO;
import database.Database;
import model.TeamInfo;
import java.util.ArrayList;
import java.util.List;
import model.InvitoTeamInfo;


import java.sql.*;

/**
 * Implementazione PostgreSQL del {@link dao.TeamDAO}.
 * <p>
 * Gestisce la parte "team" del sistema: creazione team, membri, ricerca team e inviti ai team.
 * Tutte le query passano da JDBC usando {@link database.Database#getConnection()}.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.TeamDAO
 * @see model.TeamInfo
 * @see model.InvitoTeamInfo
 */

public class TeamDAOImpl implements TeamDAO {

    @Override
    /**
     * Controlla se un utente risulta già membro di un team in un dato hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @return true se è già in un team di quell'hackathon, false altrimenti
     */

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


    /**
     * Verifica se esiste già un team con lo stesso nome nello stesso hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param nomeTeam nome del team
     * @return true se esiste già, false altrimenti
     */

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


    /**
     * Crea un team e registra il creatore come membro del team.
     *
     * @param nomeTeam nome del team
     * @param hackathonId id dell'hackathon
     * @param creatoreEmail email dell'utente che crea il team
     * @return id del team creato nel DB (generato automaticamente)
     */

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


    /**
     * Restituisce i team a cui partecipa un utente (con info utili per la UI).
     *
     * @param emailUtente email dell'utente
     * @return lista dei team dell'utente (vuota se non ne ha)
     * @see model.TeamInfo
     */


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


    private Long getHackathonIdOfTeam(long teamId) {
        String sql = "SELECT hackathon_id FROM team WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Errore recupero hackathon_id del team", e);
        }
    }

    private int countMembri(long teamId) {
        String sql = "SELECT COUNT(*) FROM team_membro WHERE team_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore conteggio membri team", e);
        }
    }

    private int getMaxTeamSize(long hackathonId) {
        String sql = "SELECT max_team_size FROM hackathon WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, hackathonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Errore recupero max_team_size", e);
        }
    }

    private boolean utenteEsiste(String email, String ruolo) {
        String sql = "SELECT 1 FROM utente WHERE email = ? AND ruolo = ? LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, ruolo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore controllo utente/ruolo", e);
        }
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

    /**
     * Restituisce tutti i team di un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @return lista team dell'hackathon (vuota se non ce ne sono)
     */

    @Override
    public List<TeamInfo> findTeamsByHackathon(long hackathonId) {
        String sql = """
        SELECT t.id,
               t.nome             AS team_nome,
               h.titolo           AS hackathon_titolo,
               t.creatore_email   AS creatore_email,
               COUNT(tm2.utente_email) AS num_membri
        FROM team t
        JOIN hackathon h ON h.id = t.hackathon_id
        LEFT JOIN team_membro tm2 ON tm2.team_id = t.id
        WHERE t.hackathon_id = ?
        GROUP BY t.id, t.nome, h.titolo, t.creatore_email
        ORDER BY t.nome
        """;

        List<TeamInfo> result = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);

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
            throw new RuntimeException("Errore nel recupero dei team dell'hackathon", e);
        }

        return result;
    }


    /**
     * Trova l'id di un team dato hackathon + nome team.
     *
     * @param hackathonId id dell'hackathon
     * @param nomeTeam nome del team
     * @return id del team, oppure {@code null} se non esiste
     */

    @Override
    public Long findTeamIdByNome(long hackathonId, String nomeTeam) {
        String sql = "SELECT id FROM team WHERE hackathon_id = ? AND LOWER(nome) = LOWER(?) LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, hackathonId);
            ps.setString(2, nomeTeam);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Errore findTeamIdByNome", e);
        }
    }


    /**
     * Invia un invito ad un utente per entrare in un team.
     * <p>
     * Questa operazione controlla i vincoli principali (duplicati, esistenza utente, capienza team, ecc.)
     * e registra l'invito in stato "pending" (o equivalente).
     *
     * @param teamId id del team che invita
     * @param invitatoEmail email dell'utente invitato
     * @param invitanteEmail email di chi invia l'invito
     * @return true se l'invito viene creato, false se non è possibile
     */

    @Override
    public boolean inviaInvitoTeam(long teamId, String invitatoEmail, String invitanteEmail) {


        if (invitatoEmail == null || invitatoEmail.trim().isEmpty()) return false;
        if (invitanteEmail == null || invitanteEmail.trim().isEmpty()) return false;

        invitatoEmail = invitatoEmail.trim();
        invitanteEmail = invitanteEmail.trim();

        if (!utenteEsiste(invitatoEmail, "utente")) return false;

        // 1) recupero hackathon del team
        Long hackathonId = getHackathonIdOfTeam(teamId);
        if (hackathonId == null) return false;



        // 2) invitante deve essere nel team (almeno membro)
        String checkInvitante = "SELECT 1 FROM team_membro WHERE team_id = ? AND utente_email = ? LIMIT 1";
        // 3) invitato non deve essere già nel team
        String checkInvitatoNelTeam = "SELECT 1 FROM team_membro WHERE team_id = ? AND utente_email = ? LIMIT 1";
        // 4) invitato non deve già avere un team nello stesso hackathon
        // (riuso la tua utenteHaTeam)
        // 5) team non deve essere pieno
        int membri = countMembri(teamId);
        int maxSize = getMaxTeamSize(hackathonId);
        if (maxSize > 0 && membri >= maxSize) return false;

        try (Connection c = Database.getConnection()) {

            // invitante nel team?
            try (PreparedStatement ps = c.prepareStatement(checkInvitante)) {
                ps.setLong(1, teamId);
                ps.setString(2, invitanteEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                }
            }

            // invitato già nel team?
            try (PreparedStatement ps = c.prepareStatement(checkInvitatoNelTeam)) {
                ps.setLong(1, teamId);
                ps.setString(2, invitatoEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return false;
                }
            }

            // invitato già ha team in hackathon?
            if (utenteHaTeam(hackathonId, invitatoEmail)) return false;

            // 6) inserisco invito (se esiste già, non lo duplico)
            String insert = """
            INSERT INTO team_invito(team_id, invitato_email, invitante_email, stato)
            VALUES (?,?,?, 'PENDING')
            ON CONFLICT (team_id, invitato_email) DO UPDATE
            SET invitante_email = EXCLUDED.invitante_email,
                stato = 'PENDING',
                created_at = NOW()
            """;

            try (PreparedStatement ps = c.prepareStatement(insert)) {
                ps.setLong(1, teamId);
                ps.setString(2, invitatoEmail);
                ps.setString(3, invitanteEmail);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Errore invio invito team", e);
        }
    }
    /**
     * Trova l'id del team dell'utente in uno specifico hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @return id del team dell'utente, oppure {@code null} se non ne ha uno
     */

    @Override
    public Long findMyTeamId(long hackathonId, String emailUtente) {
        String sql = """
        SELECT t.id
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
                if (rs.next()) return rs.getLong(1);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Errore findMyTeamId", e);
        }
    }
    /**
     * Restituisce le email degli utenti invitati da un team (inviti inviati).
     *
     * @param teamId id del team
     * @return lista email invitate (vuota se non ce ne sono)
     */

    @Override
    public List<String> findInvitiInviati(long teamId) {
        String sql = """
        SELECT invitato_email
        FROM team_invito
        WHERE team_id = ?
        ORDER BY created_at DESC
        """;

        java.util.List<String> out = new java.util.ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore findInvitiInviati", e);
        }

        return out;
    }


    /**
     * Restituisce la lista degli inviti ricevuti da un utente.
     *
     * @param invitatoEmail email dell'utente invitato
     * @return lista inviti ricevuti (vuota se nessuno)
     * @see model.InvitoTeamInfo
     */

    @Override
    public List<InvitoTeamInfo> findInvitiRicevuti(String invitatoEmail) {
        String sql = """
        SELECT ti.id,
               ti.team_id,
               t.nome AS team_nome,
               h.titolo AS hackathon_titolo,
               ti.invitante_email,
               ti.created_at
        FROM team_invito ti
        JOIN team t ON t.id = ti.team_id
        JOIN hackathon h ON h.id = t.hackathon_id
        WHERE ti.invitato_email = ?
          AND ti.stato = 'PENDING'
        ORDER BY ti.created_at DESC
        """;

        List<model.InvitoTeamInfo> out = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, invitatoEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new model.InvitoTeamInfo(
                            rs.getLong("id"),
                            rs.getLong("team_id"),
                            rs.getString("team_nome"),
                            rs.getString("hackathon_titolo"),
                            rs.getString("invitante_email"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore findInvitiRicevuti", e);
        }

        return out;
    }
    /**
     * Accetta un invito ad un team e inserisce l'utente tra i membri del team.
     * <p>
     * Se l'invito non esiste, non è più valido o è già stato gestito, ritorna false.
     *
     * @param invitoId id dell'invito
     * @param invitatoEmail email dell'utente che accetta
     * @return true se accettazione riuscita, false altrimenti
     */

    @Override
    public boolean accettaInvitoTeam(long invitoId, String invitatoEmail) {

        String select = """
        SELECT ti.team_id, t.hackathon_id
        FROM team_invito ti
        JOIN team t ON t.id = ti.team_id
        WHERE ti.id = ?
          AND ti.invitato_email = ?
          AND ti.stato = 'PENDING'
        FOR UPDATE
        """;

        String insertMembro = """
        INSERT INTO team_membro(team_id, utente_email)
        VALUES (?, ?)
        ON CONFLICT DO NOTHING
        """;

        String updateAccetta = "UPDATE team_invito SET stato = 'ACCEPTED' WHERE id = ?";

        // annulla gli altri inviti PENDING dello stesso hackathon (così 1 team per hackathon)
        String cancelAltri = """
        UPDATE team_invito
        SET stato = 'CANCELLED'
        WHERE invitato_email = ?
          AND stato = 'PENDING'
          AND team_id IN (SELECT id FROM team WHERE hackathon_id = ?)
        """;

        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);

            long teamId;
            long hackathonId;

            // 1) carico invito e blocco riga
            try (PreparedStatement ps = c.prepareStatement(select)) {
                ps.setLong(1, invitoId);
                ps.setString(2, invitatoEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        return false;
                    }
                    teamId = rs.getLong("team_id");
                    hackathonId = rs.getLong("hackathon_id");
                }
            }

            // 2) non deve già avere un team in QUEL hackathon
            if (utenteHaTeam(hackathonId, invitatoEmail)) {
                c.rollback();
                return false;
            }

            // 3) team non deve essere pieno
            int membri = countMembri(teamId);
            int maxSize = getMaxTeamSize(hackathonId);
            if (maxSize > 0 && membri >= maxSize) {
                c.rollback();
                return false;
            }

            // 4) inserisco membro
            try (PreparedStatement ps = c.prepareStatement(insertMembro)) {
                ps.setLong(1, teamId);
                ps.setString(2, invitatoEmail);
                ps.executeUpdate();
            }

            // 5) accetto invito scelto
            try (PreparedStatement ps = c.prepareStatement(updateAccetta)) {
                ps.setLong(1, invitoId);
                ps.executeUpdate();
            }

            // 6) cancello gli altri inviti nello stesso hackathon
            try (PreparedStatement ps = c.prepareStatement(cancelAltri)) {
                ps.setString(1, invitatoEmail);
                ps.setLong(2, hackathonId);
                ps.executeUpdate();
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Errore accettaInvitoTeam", e);
        }
    }



}
