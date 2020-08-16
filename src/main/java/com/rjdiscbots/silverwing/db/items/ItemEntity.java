package com.rjdiscbots.silverwing.db.items;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "component1")
    private Integer componentOne;

    @Column(name = "component1_name")
    private String componentOneName;

    @Column(name = "component2")
    private Integer componentTwo;

    @Column(name = "component2_name")
    private String componentTwoName;

    public ItemEntity() {

    }

    public ItemEntity(int id, String name, String description, Integer componentOne,
        Integer componentTwo, String componentOneName, String componentTwoName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.componentOne = componentOne;
        this.componentOneName = componentOneName;
        this.componentTwo = componentTwo;
        this.componentTwoName = componentTwoName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Integer getComponentOne() {
        return componentOne;
    }

    public void setComponentOne(Integer componentOne) {
        this.componentOne = componentOne;
    }

    public Integer getComponentTwo() {
        return componentTwo;
    }

    public void setComponentTwo(Integer componentTwo) {
        this.componentTwo = componentTwo;
    }

    public String getComponentOneName() {
        return componentOneName;
    }

    public void setComponentOneName(String componentOneName) {
        this.componentOneName = componentOneName;
    }

    public String getComponentTwoName() {
        return componentTwoName;
    }

    public void setComponentTwoName(String componentTwoName) {
        this.componentTwoName = componentTwoName;
    }
}
