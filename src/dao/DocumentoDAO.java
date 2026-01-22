package dao;

import java.time.LocalDateTime;


/**
 * DAO per gestire i documenti di "progress" caricati dai team durante un hackathon.
 * <p>
 * Ogni caricamento salva contenuto + data/ora (LocalDateTime).
 * La dashboard può mostrare l'ultimo documento caricato da un team.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see java.time.LocalDateTime
 */

public interface DocumentoDAO {

    /**
     * Salva un nuovo documento caricato da un team per un hackathon.
     *
     * @param teamId id del team che carica
     * @param hackathonId id dell'hackathon
     * @param contenuto testo/contenuto del documento
     * @param dataCaricamento data e ora del caricamento
     */

    void salvaDocumento(long teamId,
                        long hackathonId,
                        String contenuto,
                        LocalDateTime dataCaricamento);


    /**
     * Recupera il contenuto dell'ultimo documento caricato da un team per un hackathon.
     *
     * @param teamId id del team
     * @param hackathonId id dell'hackathon
     * @return contenuto dell'ultimo documento, oppure {@code null} se non esiste alcun documento
     */

    String trovaUltimoDocumento(long teamId, long hackathonId);
}
