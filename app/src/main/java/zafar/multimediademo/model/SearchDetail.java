package zafar.multimediademo.model;

import java.io.Serializable;

/**
 * Created by Zafar on 10-01-2017.
 */
public class SearchDetail implements Serializable{

    private String email;

    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String gender;

    public String getGender() { return this.gender; }

    public void setGender(String gender) { this.gender = gender; }
}
