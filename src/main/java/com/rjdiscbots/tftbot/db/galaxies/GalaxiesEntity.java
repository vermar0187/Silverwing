package com.rjdiscbots.tftbot.db.galaxies;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.graalvm.compiler.lir.CompositeValue.Component;

@Entity
@Table(name = "galaxies")
public class GalaxiesEntity implements Serializable {
    @Id
    private String key;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String descripiton;

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
