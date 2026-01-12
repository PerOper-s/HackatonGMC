package dao;


import model.TeamInfo;
import java.util.List;

public interface TeamDAO {
    boolean utenteHaTeam(long hackathonId, String emailUtente);
    boolean esisteTeamConNome(long hackathonId, String nomeTeam);
    long creaTeam(String nomeTeam, long hackathonId, String creatoreEmail);


    List<TeamInfo> findTeamsByUtente(String emailUtente);
    List<String> findMembriTeam(long teamId);
    List<TeamInfo> findTeamsByHackathon(long hackathonId);
    Long findTeamIdByNome(long hackathonId, String nomeTeam);
    boolean inviaInvitoTeam(long teamId, String invitatoEmail, String invitanteEmail);
    Long findMyTeamId(long hackathonId, String emailUtente);
    List<String> findInvitiInviati(long teamId);


}
