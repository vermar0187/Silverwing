package com.rjdiscbots.tftbot.db.synergies;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Entity
@Table(name = "synergy")
public class SynergyEntity {

    @Id
    @Column(name = "key")
    private String key;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "innate")
    private String innate;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "sets")
    private List<SetModel> setModel = new ArrayList<>();

    public SynergyEntity() {

    }

    public SynergyEntity(String key, String name, String description, String type,
        List<SetModel> setModel) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.type = type;
        this.setModel = setModel;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SetModel> getSetModel() {
        return setModel;
    }

    public void setSetModel(List<SetModel> setModel) {
        this.setModel = setModel;
    }

    public String getInnate() {
        return innate;
    }

    public void setInnate(String innate) {
        this.innate = innate;
    }
}
