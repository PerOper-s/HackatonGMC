package dao;

import java.time.LocalDate;

public interface IscrizioneDAO {
    boolean isIscritto(long hackathonId, String emailUtente);
    int countIscritti(long hackathonId);
    void iscrivi(long hackathonId, String emailUtente, LocalDate dataIscrizione);
}
