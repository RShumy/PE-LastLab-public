package ro.ubb.domain;

public class User extends BaseEntity<Integer>{
    private String name;
    private String userName;
    private String password;
    private String email;

    public User(int idEntity) {
        super(idEntity);
    }

    public User(){}

    public User(Integer idEntity, String name, String userName, String password, String email) {
        super(idEntity);
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                " idEntity=`" + idEntity + "`" +
                ", name=`" + name + '`' +
                ", userName=`" + userName + '`' +
                ", password=`" + password + '`' +
                ", email=`" + email + '`' +
                "}\n";
    }
}
