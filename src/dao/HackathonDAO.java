package dao;

import model.Hackathon;

import java.util.List;

public interface HackathonDAO {
    void creaHackathon(Hackathon h);
    List<Hackathon> findAll();
    List<Hackathon> findByOrganizzatore(String email);

}
