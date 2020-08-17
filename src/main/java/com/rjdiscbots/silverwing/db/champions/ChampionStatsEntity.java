package com.rjdiscbots.silverwing.db.champions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "champion_stats")
public class ChampionStatsEntity {

    @Id
    @Column(columnDefinition = "text")
    private String id;

    @Column
    private String champion;

    @Column
    private Integer stars;

    @Column
    private Integer dps;

    @Column(name = "atk_speed")
    private Double attackSpeed;

    @Column
    private Integer damage;

    @Column
    private Integer range;

    @Column
    private Integer health;

    @Column
    private Integer mana;

    @Column(name = "initial_mana")
    private Integer initialMana;

    @Column
    private Integer armor;

    @Column
    private Integer mr;

    public ChampionStatsEntity() {

    }

    public ChampionStatsEntity(String id, String champion, Integer stars, Integer dps,
        Double attackSpeed, Integer damage, Integer range, Integer health, Integer mana,
        Integer initialMana, Integer armor,
        Integer mr) {
        this.id = id;
        this.champion = champion;
        this.stars = stars;
        this.dps = dps;
        this.attackSpeed = attackSpeed;
        this.damage = damage;
        this.range = range;
        this.health = health;
        this.mana = mana;
        this.initialMana = initialMana;
        this.armor = armor;
        this.mr = mr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChampion() {
        return champion;
    }

    public void setChampion(String champion) {
        this.champion = champion;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Integer getDps() {
        return dps;
    }

    public void setDps(Integer dps) {
        this.dps = dps;
    }

    public Double getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(Double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getMana() {
        return mana;
    }

    public void setMana(Integer mana) {
        this.mana = mana;
    }

    public Integer getInitialMana() {
        return initialMana;
    }

    public void setInitialMana(Integer initialMana) {
        this.initialMana = initialMana;
    }

    public Integer getArmor() {
        return armor;
    }

    public void setArmor(Integer armor) {
        this.armor = armor;
    }

    public Integer getMr() {
        return mr;
    }

    public void setMr(Integer mr) {
        this.mr = mr;
    }
}
