package at.stefanirndorfer.bakingapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "recipe")
public class Recipe {

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "recipe_id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @Ignore /* we build up a dedicated table and set the foreign key programmatically ... not sure if this is the best way */
    @SerializedName("ingredients")
    private List<Ingredient> ingredients;

    @Ignore /* we build up a dedicated table and set the foreign key programmatically ... not sure if this is the best way */
    @SerializedName("steps")
    private List<Step> steps;

    @SerializedName("servings")
    private Integer servings;

    public Recipe(Integer id, String name, List<Ingredient> ingredients, List<Step> steps, Integer servings) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
    }

    public Recipe(Integer id, String name, Integer servings) {
        this.id = id;
        this.name = name;
        this.ingredients = null;
        this.steps = null;
        this.servings = servings;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }


}
