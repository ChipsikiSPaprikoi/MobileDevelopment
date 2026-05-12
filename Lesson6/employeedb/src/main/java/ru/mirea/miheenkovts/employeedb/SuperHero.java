package ru.mirea.miheenkovts.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "superhero")
public class SuperHero {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String power;
    public int strength;

    public SuperHero() {}

    public SuperHero(String name, String power, int strength) {
        this.name = name;
        this.power = power;
        this.strength = strength;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (" + power + "), сила: " + strength;
    }
}
