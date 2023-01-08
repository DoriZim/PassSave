package bepo.passsave.model;

import java.io.Serializable;
import java.time.LocalDate;

public class PassSave implements Serializable {
    String name, username, mail, password, category, addInfo;
    LocalDate date;

    public PassSave(String name, String username, String mail, String password, String category , LocalDate date) {
        this.name = name;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.category = category;
        this.date = date;
        this.addInfo = "";
    }

    public PassSave(String name, String username, String mail, String password, String category, LocalDate date, String addInfo) {
        this(name, username, mail, password, category, date);
        this.addInfo = addInfo;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", category='" + category + '\'' +
                ", addInfo='" + addInfo + '\'' +
                ", date=" + date +
                "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
