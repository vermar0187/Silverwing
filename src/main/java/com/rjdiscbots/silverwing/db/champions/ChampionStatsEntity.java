package com.rjdiscbots.silverwing.db.champions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "champion_stats")
public class ChampionStatsEntity {

    @Id
    @Column
    private String id;

    @Column
    private String champion;

    @Column
    private int stars;

    @Column
    private int dps;

    @Column(name = "atk_speed")
    private double attackSpeed;

    @Column
    private int damage;

    @Column
    private int range;

    @Column
    private int health;

    @Column
    private int mana;

    @Column(name = "initial_mana")
    private int initialMana;

    @Column
    private int armor;

    @Column
    private int mr;

    public ChampionStatsEntity() {

    }

    public ChampionStatsEntity(String id, String champion, int stars, int dps,
        double attackSpeed, int damage, int range, int health, int mana, int initialMana, int armor,
        int mr) {
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

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getDps() {
        return dps;
    }

    public void setDps(int dps) {
        this.dps = dps;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getInitialMana() {
        return initialMana;
    }

    public void setInitialMana(int initialMana) {
        this.initialMana = initialMana;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getMr() {
        return mr;
    }

    public void setMr(int mr) {
        this.mr = mr;
    }
}