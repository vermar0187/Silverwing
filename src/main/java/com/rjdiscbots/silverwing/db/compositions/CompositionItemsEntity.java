package com.rjdiscbots.silverwing.db.compositions;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Table(name = "champion_comp_items")
@TypeDefs({
    @TypeDef(name = "list-array", typeClass = ListArrayType.class)
})
public class CompositionItemsEntity {

    @Id
    @Column(name = "cci_id", nullable = false)
    private int id;

    @Column(name = "comp_name", nullable = false)
    private String compName;

    @Column(name = "champion_name", nullable = false)
    private String championName;

    @Column
    private String stage;

    @Type(type = "list-array")
    @Column(
        name = "items",
        columnDefinition = "integer[]",
        nullable = false
    )
    private List<Integer> items;

    public CompositionItemsEntity() {

    }

    public CompositionItemsEntity(int id, String compName, String championName, String stage,
        List<Integer> items) {
        this.id = id;
        this.compName = compName;
        this.championName = championName;
        this.stage = stage;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getChampionName() {
        return championName;
    }

    public void setChampionName(String championName) {
        this.championName = championName;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public List<Integer> getItems() {
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }
}
