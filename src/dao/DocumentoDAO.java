package dao;

import java.time.LocalDateTime;

public interface DocumentoDAO {
    void salvaDocumento(long teamId,
                        long hackathonId,
                        String contenuto,
                        LocalDateTime dataCaricamento);


    String trovaUltimoDocumento(long teamId, long hackathonId);
}
