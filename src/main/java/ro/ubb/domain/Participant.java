package ro.ubb.domain;

public class Participant extends BaseEntity<Integer> {
    private Integer idUser;
    private Integer idEvent;

    public Participant(Integer idEntity, Integer idUser, Integer idEvent) {
        super(idEntity);
        this.idUser = idUser;
        this.idEvent = idEvent;
    }

    public Participant(Integer idEntity){ super(idEntity); }

    public Participant(){}

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    @Override
    public String toString() {
        return "Parcitipant{" +
                "idEntity=" + idEntity +
                ", idUser=" + idUser +
                ", idEvent=" + idEvent +
                '}';
    }

}
