package com.trackstudio.soap.bean;

public class SimilarTaskBean {

    private float[] ratings;
    private TaskBean[] tasks;

    public SimilarTaskBean() {
    }

    public float[] getRatings() {
        return ratings;
    }

    public void setRatings(float[] ratings) {
        this.ratings = ratings;
    }

    public TaskBean[] getTasks() {
        return tasks;
    }

    public void setTasks(TaskBean[] tasks) {
        this.tasks = tasks;
    }

}
