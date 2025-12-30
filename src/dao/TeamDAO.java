package dao;


import model.TeamInfo;
import java.util.List;

public interface TeamDAO {
    boolean utenteHaTeam(long hackathonId, String emailUtente);
    boolean esisteTeamConNome(long hackathonId, String nomeTeam);
    long creaTeam(String nomeTeam, long hackathonId, String creatoreEmail);


    List<TeamInfo> findTeamsByUtente(String emailUtente);
    List<String> findMembriTeam(long teamId);
}
