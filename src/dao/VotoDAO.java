package dao;

public interface VotoDAO {

    void salvaVoto(long hackathonId, long teamId, String giudiceEmail, int valore);

    int countVotiInseriti(long hackathonId);

    int countVotiAttesi(long hackathonId);

    boolean votiCompleti(long hackathonId);
}

