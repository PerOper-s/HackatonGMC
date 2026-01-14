package model;

import java.sql.Timestamp;

public class InvitoTeamInfo {
    private long idInvito;
    private long teamId;
    private String nomeTeam;
    private String titoloHackathon;
    private String invitanteEmail;
    private Timestamp createdAt;

    public InvitoTeamInfo(long idInvito, long teamId, String nomeTeam, String titoloHackathon,
                          String invitanteEmail, Timestamp createdAt) {
        this.idInvito = idInvito;
        this.teamId = teamId;
        this.nomeTeam = nomeTeam;
        this.titoloHackathon = titoloHackathon;
        this.invitanteEmail = invitanteEmail;
        this.createdAt = createdAt;
    }

    public long getIdInvito() { return idInvito; }
    public long getTeamId() { return teamId; }
    public String getNomeTeam() { return nomeTeam; }
    public String getTitoloHackathon() { return titoloHackathon; }
    public String getInvitanteEmail() { return invitanteEmail; }
    public Timestamp getCreatedAt() { return createdAt; }
}
