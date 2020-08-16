package com.rjdiscbots.silverwing.db.galaxies;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "galaxies")
public class GalaxyEntity implements Serializable {

    @Id
    private String key;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String descripiton;

    public GalaxyEntity() {

    }

    public GalaxyEntity(String key, String name, String descripiton) {
        this.key = key;
        this.name = name;
        this.descripiton = descripiton;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripiton() {
        return descripiton;
    }

    public void setDescripiton(String descripiton) {
        this.descripiton = descripiton;
    }
}
