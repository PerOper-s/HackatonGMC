package model;



/**
 * Rappresenta una riga di classifica: un team + il suo punteggio totale.
 * <p>
 * Nel progetto il punteggio è pensato come somma dei voti ricevuti dai giudici.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Team
 */


public class Classifica {
    private Team team;
    private int punteggio;


    /**
     * Crea una riga di classifica.
     *
     * @param team team a cui si riferisce la riga
     * @param punteggio punteggio totale del team
     */

    public Classifica(Team team, int punteggio) {
        this.team = team;
        this.punteggio = punteggio;
    }

    /**
     * @return il team della riga di classifica
     */

    public Team getTeam() {
        return team;
    }

    /**
     * @return punteggio totale associato al team
     */


    public int getPunteggio() {
        return punteggio;
    }

    /**
     * Aggiorna il punteggio della riga.
     *
     * @param punteggio nuovo punteggio totale
     */


    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}
