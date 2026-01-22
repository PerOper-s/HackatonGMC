package dao;

import model.CommentoInfo;
import java.util.List;

/**
 * DAO per gestire i commenti dei giudici sui progressi (documenti) dei team.
 * <p>
 * Il commento è legato ad un team dentro un hackathon e porta anche l'email del giudice che lo scrive.
 * La UI li mostra in forma tipo: "Giudice email: commento".
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.CommentoInfo
 */

public interface CommentoDAO {

    /**
     * Recupera tutti i commenti associati ad un team in uno specifico hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param teamId id del team
     * @return lista dei commenti (vuota se non ce ne sono)
     * @see model.CommentoInfo
     */


    List<CommentoInfo> findCommentiPerTeam(long hackathonId, long teamId);

    /**
     * Salva un commento di un giudice per un team in un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param teamId id del team
     * @param giudiceEmail email del giudice che scrive il commento
     * @param contenuto testo del commento
     */

    void salvaCommento(long hackathonId, long teamId, String giudiceEmail, String contenuto);

}
