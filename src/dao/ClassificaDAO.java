package dao;

import model.Classifica;
import java.util.List;


/**
 * DAO per la gestione della classifica dei team in un hackathon.
 * <p>
 * La classifica viene calcolata in base ai voti inseriti dai giudici e restituita
 * ordinata per punteggio (di solito in ordine decrescente).
 * Questo DAO espone solo le operazioni di lettura necessarie alla UI.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Classifica
 * @see dao.VotoDAO
 */

public interface ClassificaDAO {

    /**
     * Restituisce la classifica dei team per un dato hackathon.
     * <p>
     * La lista è pensata per essere mostrata in UI: ogni riga contiene team + punteggio totale.
     *
     * @param hackathonId id dell'hackathon
     * @return lista di righe classifica (vuota se non ci sono voti / non ci sono team)
     * @see model.Team
     */

    List<Classifica> findClassificaByHackathon(long hackathonId);
}
