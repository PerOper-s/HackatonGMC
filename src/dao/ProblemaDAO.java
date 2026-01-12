package dao;

import model.Problema;

import java.util.List;

public interface ProblemaDAO {


    Problema trovaPerHackathon(long hackathonId);

    void pubblicaProblema(long hackathonId, String giudiceEmail, String descrizione);


    List<Problema> trovaTuttiPerHackathon(long hackathonId);



}
