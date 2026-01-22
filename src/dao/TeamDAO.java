package dao;

import model.TeamInfo;
import java.util.List;
import model.InvitoTeamInfo;

/**
 * DAO per gestire i Team e gli inviti ai team.
 * <p>
 * Qui metto tutte le operazioni DB legate a:
 * creazione team, membri, ricerca team e gestione inviti (inviati/ricevuti/accettazione).
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.TeamInfo
 * @see model.InvitoTeamInfo
 */


public interface TeamDAO {

    /**
     * Controlla se un utente fa già parte di un team in un dato hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @return true se l'utente risulta già in un team di quell'hackathon, false altrimenti
     */

    boolean utenteHaTeam(long hackathonId, String emailUtente);

    /**
     * Verifica se esiste già un team con un certo nome nello stesso hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param nomeTeam nome del team da verificare
     * @return true se esiste già, false altrimenti
     */

    boolean esisteTeamConNome(long hackathonId, String nomeTeam);

    /**
     * Crea un nuovo team e registra il creatore come primo membro.
     *
     * @param nomeTeam nome del nuovo team
     * @param hackathonId id dell'hackathon
     * @param creatoreEmail email dell'utente che crea il team
     * @return id del team appena creato (generato dal DB)
     */

    long creaTeam(String nomeTeam, long hackathonId, String creatoreEmail);

    /**
     * Restituisce i team a cui partecipa un utente (con info utili per la UI).
     *
     * @param emailUtente email dell'utente
     * @return lista dei team dell'utente (vuota se non ha team)
     * @see model.TeamInfo
     */


    List<TeamInfo> findTeamsByUtente(String emailUtente);

    /**
     * Restituisce le email dei membri di un team.
     *
     * @param teamId id del team
     * @return lista email membri (vuota se nessun membro trovato)
     */

    List<String> findMembriTeam(long teamId);

    /**
     * Restituisce tutti i team di un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @return lista team (vuota se non ci sono team)
     */

    List<TeamInfo> findTeamsByHackathon(long hackathonId);

    /**
     * Trova l'id di un team dato hackathon + nome team.
     *
     * @param hackathonId id dell'hackathon
     * @param nomeTeam nome del team
     * @return id del team, oppure {@code null} se non esiste
     */

    Long findTeamIdByNome(long hackathonId, String nomeTeam);

    /**
     * Invia un invito ad un utente per entrare in un team.
     *
     * @param teamId id del team che invita
     * @param invitatoEmail email dell'utente invitato
     * @param invitanteEmail email di chi invia l'invito (di solito un membro/creatore)
     * @return true se l'invito viene creato, false se non è possibile (duplicato, vincoli, ecc.)
     */

    boolean inviaInvitoTeam(long teamId, String invitatoEmail, String invitanteEmail);

    /**
     * Trova l'id del team dell'utente in uno specifico hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param emailUtente email dell'utente
     * @return id del suo team, oppure {@code null} se non ne ha uno in quell'hackathon
     */


    Long findMyTeamId(long hackathonId, String emailUtente);
    /**
     * Restituisce le email degli utenti invitati da un team (inviti inviati).
     *
     * @param teamId id del team
     * @return lista email invitate (vuota se non ci sono inviti)
     */

    List<String> findInvitiInviati(long teamId);

    /**
     * Restituisce gli inviti ricevuti da un utente.
     *
     * @param invitatoEmail email dell'utente invitato
     * @return lista di inviti ricevuti (vuota se non ce ne sono)
     * @see model.InvitoTeamInfo
     */


    List<InvitoTeamInfo> findInvitiRicevuti(String invitatoEmail);

    /**
     * Accetta un invito team e rende effettiva l'iscrizione dell'utente al team.
     *
     * @param invitoId id dell'invito
     * @param invitatoEmail email dell'utente che accetta
     * @return true se l'operazione va a buon fine, false se l'invito non è valido/già gestito
     */

    boolean accettaInvitoTeam(long invitoId, String invitatoEmail);
}
