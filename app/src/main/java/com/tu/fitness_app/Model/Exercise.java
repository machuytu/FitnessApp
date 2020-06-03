package com.tu.fitness_app.Model;

public class Exercise {
    private int image_id;
    private String name,intro;

    public Exercise(int image_id, String name, String intro) {
        this.image_id = image_id;
        this.name = name;
       this.intro = intro;

    }



    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName1() {
        return intro;
    }

    public void setName1(String intro) {
        this.intro = intro;
    }
}
