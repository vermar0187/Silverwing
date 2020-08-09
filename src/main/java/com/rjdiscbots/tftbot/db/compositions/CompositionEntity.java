package com.rjdiscbots.tftbot.db.compositions;

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
@Table(name = "compositions")
@TypeDefs({
    @TypeDef(name = "list-array", typeClass = ListArrayType.class)
})
public class CompositionEntity {

    @Id
    @Column(name = "comp_name")
    private String name;

    @Column(name = "tft_set", nullable = false)
    private double set;

    @Type(type = "list-array")
    @Column(
        name = "end_comp",
        columnDefinition = "text[]"
    )
    private List<String> endComp;

    @Type(type = "list-array")
    @Column(
        name = "mid_comp",
        columnDefinition = "text[]"
    )
    private List<String> midComp;

    @Type(type = "list-array")
    @Column(
        name = "early_comp",
        columnDefinition = "text[]"
    )
    private List<String> begComp;

    @Column(name = "comp_strategy")
    private String compStrategy;

    public CompositionEntity() {
    }

    public CompositionEntity(String name, double set, List<String> endComp,
        List<String> midComp, List<String> begComp, String compStrategy) {
        this.name = name;
        this.set = set;
        this.endComp = endComp;
        this.midComp = midComp;
        this.begComp = begComp;
        this.compStrategy = compStrategy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSet() {
        return set;
    }

    public void setSet(double set) {
        this.set = set;
    }

    public List<String> getEndComp() {
        return endComp;
    }

    public void setEndComp(List<String> endComp) {
        this.endComp = endComp;
    }

    public List<String> getMidComp() {
        return midComp;
    }

    public void setMidComp(List<String> midComp) {
        this.midComp = midComp;
    }

    public List<String> getBegComp() {
        return begComp;
    }

    public void setBegComp(List<String> begComp) {
        this.begComp = begComp;
    }

    public String getCompStrategy() {
        return compStrategy;
    }

    public void setCompStrategy(String compStrategy) {
        this.compStrategy = compStrategy;
    }
}
