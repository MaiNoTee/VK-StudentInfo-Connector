public class Person {

    private final String name;
    private final String surname;
    private String city;
    private String bdate;
    private String photo;
    private String gender;
    private int vkID;

    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.city = "None";
        this.bdate = "None";
        this.photo = "None";
        this.gender = "None";
        this.vkID = -1;
    }

    public String getBdate() {
        return bdate;
    }

    public String getCity() {
        return city;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public int getVkID() {
        return vkID;
    }

    public void setVkID(int vkID) {
        this.vkID = vkID;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
