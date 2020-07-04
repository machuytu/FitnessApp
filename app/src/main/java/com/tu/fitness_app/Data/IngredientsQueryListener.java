package com.tu.fitness_app.Data;

import com.tu.fitness_app.Model.Ingredient;

import java.util.List;

public interface IngredientsQueryListener {
    void allIngredientsLoaded(List<Ingredient> ingredients);
}
