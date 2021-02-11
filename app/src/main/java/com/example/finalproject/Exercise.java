package com.example.finalproject;

public class Exercise {
    public long id;
    public String name;
    public Type type;
    public Integer reps;
    public Integer weight;

    public Exercise(long id, String name, Type type, Integer reps, Integer weight) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.reps = reps;
        this.weight = weight;
    }
}
