package dao;

import model.CommentoInfo;
import java.util.List;

public interface CommentoDAO {


    List<CommentoInfo> findCommentiPerTeam(long hackathonId, long teamId);
    void salvaCommento(long hackathonId, long teamId, String giudiceEmail, String contenuto);

}
