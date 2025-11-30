package dao;

import model.Classifica;
import java.util.List;

public interface ClassificaDAO {

    /**
     * Restituisce la classifica di un hackathon:
     * lista di (Team, punteggio) ordinati dal migliore al peggiore.
     */
    List<Classifica> findClassificaByHackathon(long hackathonId);
}
