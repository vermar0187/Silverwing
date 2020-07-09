package com.rjdiscbots.tftbot.db.champions;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "champions")
@TypeDef(
    name = "list-array",
    typeClass = ListArrayType.class
)
public class ChampionsEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cost", nullable = false)
    private int cost;

    @Type(type = "list-array")
    @Column(
        name = "traits",
        columnDefinition = "text[]"
    )
    private List<String> traits;

    public ChampionsEntity() {
    }

    public ChampionsEntity(String id, String name, int cost,
        List<String> traits) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.traits = traits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<String> getTraits() {
        return traits;
    }

    public void setTraits(List<String> traits) {
        this.traits = traits;
    }
}
