package model;


/**
 * Documento di progress caricato da un team durante un hackathon.
 * <p>
 * Tiene: contenuto del documento, team che lo ha caricato e data (come stringa).
 * Quando creo un Documento con un team non nullo, lo aggiungo anche alla lista documenti del team.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see model.Team
 */

public class Documento {
    private String contenuto;
    private Team team;
    private String data;


    /**
     * Crea un documento e (se il team è valorizzato) lo collega automaticamente al team.
     *
     * @param contenuto testo del documento
     * @param team team che ha caricato il documento (può essere null)
     * @param data data del caricamento (stringa)
     * @see model.Team#aggiungiDocumento(Documento)
     */

    public Documento(String contenuto, Team team, String data) {
        this.contenuto = contenuto;
        this.team = team;
        this.data = data;
        if (team != null) {
            team.aggiungiDocumento(this);
        }
    }

    /** @return contenuto del documento */

    public String getContenuto() {
        return contenuto;
    }

    /** @return team associato al documento */


    public Team getTeam() {
        return team;
    }

    /** @return data del documento (come stringa) */


    public String getData() {
        return data;
    }
}
