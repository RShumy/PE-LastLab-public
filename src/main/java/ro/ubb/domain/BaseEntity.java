package ro.ubb.domain;

public abstract class BaseEntity<ID> {
    public ID idEntity;

    public BaseEntity(ID idEntity){ this.idEntity = (ID) idEntity; }

    public BaseEntity(){}

    public void setIdEntity(ID idEntity) { this.idEntity = idEntity; }

    public ID getIdEntity() { return idEntity; }

    @Override
    public String toString() {
        return "Entity{" +
                "idEntity=" + idEntity +
                '}';
    }
}
