package daoImpl;

import dao.VotoDAO;
import database.Database;

import java.sql.*;

/**
 * Implementazione PostgreSQL del {@link dao.VotoDAO}.
 * <p>
 * Gestisce salvataggio voti (0..10) e conteggi utili (inseriti/attesi) per la classifica.
 * In questo file c'è anche un metodo che assicura l'esistenza delle tabelle necessarie.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.VotoDAO
 * @see dao.ClassificaDAO
 */

public class VotoDAOImpl implements VotoDAO {


    /**
     * Crea le tabelle necessarie ai voti se non esistono già.
     * <p>
     * Serve per rendere il progetto più “robusto” in locale, ma in consegna può essere anche sostituito
     * da uno script SQL esterno (dipende da come preferite gestire il DB).
     *
     * @throws RuntimeException se non riesce a creare/validare lo schema
     */

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


    /**
     * Salva (o aggiorna) il voto di un giudice per un team in un hackathon.
     * <p>
     * Il valore deve essere tra 0 e 10. Se per la stessa tripletta (hackathon, team, giudice)
     * esiste già un voto, viene aggiornato.
     *
     * @param hackathonId id dell'hackathon
     * @param teamId id del team
     * @param giudiceEmail email del giudice
     * @param valore voto (0..10)
     * @throws IllegalArgumentException se il valore non è nel range 0..10
     */

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


    /**
     * Conta quanti voti risultano attualmente inseriti per un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @return numero voti inseriti
     */

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

    /**
     * Calcola quanti voti sono attesi per un hackathon.
     * <p>
     * In pratica: numero giudici assegnati * numero team.
     *
     * @param hackathonId id dell'hackathon
     * @return numero voti attesi
     */


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

    /**
     * Dice se per un hackathon sono stati inseriti tutti i voti attesi.
     *
     * @param hackathonId id dell'hackathon
     * @return true se inseriti == attesi, false altrimenti
     * @see #countVotiInseriti(long)
     * @see #countVotiAttesi(long)
     */

    @Override
    public boolean votiCompleti(long hackathonId) {
        int attesi = countVotiAttesi(hackathonId);
        if (attesi <= 0) return false;
        int inseriti = countVotiInseriti(hackathonId);
        return inseriti >= attesi;
    }
}
