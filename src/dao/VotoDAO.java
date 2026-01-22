package dao;


/**
 * DAO per la gestione dei voti dei giudici ai team.
 * <p>
 * Ogni giudice può inserire un voto (0..10) per ciascun team di un hackathon.
 * Questi dati vengono poi usati per calcolare la classifica totale.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see dao.ClassificaDAO
 */

public interface VotoDAO {

    /**
     * Salva (o aggiorna) il voto di un giudice per un team in un hackathon.
     *
     * @param hackathonId id dell'hackathon
     * @param teamId id del team
     * @param giudiceEmail email del giudice che vota
     * @param valore valore del voto (0..10)
     */

    void salvaVoto(long hackathonId, long teamId, String giudiceEmail, int valore);


    /**
     * Conta quanti voti risultano inseriti per un hackathon.
     * <p>
     * Utile per capire a che punto siamo con le votazioni.
     *
     * @param hackathonId id dell'hackathon
     * @return numero di voti inseriti
     */

    int countVotiInseriti(long hackathonId);

    /**
     * Conta quanti voti sono attesi per un hackathon.
     * <p>
     * In genere: numeroGiudiciAssegnati * numeroTeam.
     *
     * @param hackathonId id dell'hackathon
     * @return numero di voti attesi
     */

    int countVotiAttesi(long hackathonId);

    /**
     * Verifica se per un hackathon sono stati inseriti tutti i voti attesi.
     *
     * @param hackathonId id dell'hackathon
     * @return true se i voti sono completi, false altrimenti
     * @see #countVotiInseriti(long)
     * @see #countVotiAttesi(long)
     */

    boolean votiCompleti(long hackathonId);
}

