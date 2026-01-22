package model;

import java.sql.Timestamp;


/**
 * Oggetto "info" usato per mostrare gli inviti team in dashboard.
 * <p>
 * Contiene i dati già pronti per la UI: id invito, team, nome team, hackathon, email invitante e data creazione.
 *
 * @author Gruppo ...
 * @version 1.0
 * @see java.sql.Timestamp
 */

public class InvitoTeamInfo {
    private long idInvito;
    private long teamId;
    private String nomeTeam;
    private String titoloHackathon;
    private String invitanteEmail;
    private Timestamp createdAt;


    /**
     * Crea un oggetto informativo per un invito team.
     *
     * @param idInvito id dell'invito
     * @param teamId id del team che invita
     * @param nomeTeam nome del team che invita
     * @param titoloHackathon titolo dell'hackathon del team
     * @param invitanteEmail email di chi ha inviato l'invito
     * @param createdAt timestamp di creazione invito
     */

    public InvitoTeamInfo(long idInvito, long teamId, String nomeTeam, String titoloHackathon,
                          String invitanteEmail, Timestamp createdAt) {
        this.idInvito = idInvito;
        this.teamId = teamId;
        this.nomeTeam = nomeTeam;
        this.titoloHackathon = titoloHackathon;
        this.invitanteEmail = invitanteEmail;
        this.createdAt = createdAt;
    }

    /** @return id dell'invito */

    public long getIdInvito() { return idInvito; }
    /** @return id del team che invita */

    public long getTeamId() { return teamId; }
    /** @return nome del team che invita */

    public String getNomeTeam() { return nomeTeam; }
    /** @return titolo dell'hackathon associato al team */

    public String getTitoloHackathon() { return titoloHackathon; }
    /** @return email dell'utente che ha inviato l'invito */

    public String getInvitanteEmail() { return invitanteEmail; }
}
