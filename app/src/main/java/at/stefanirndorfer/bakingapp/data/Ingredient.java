package at.stefanirndorfer.bakingapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "ingredient", foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "recipe_id",
        childColumns = "ingredient_id",
        onDelete = CASCADE))

public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    private final Integer ingredientId;

    @ColumnInfo(name = "recipe_id")
    private final Integer recipeId;

    @SerializedName("quantity")
    private Integer quantity;

    //todo: find out how to make it an ENUM in first place
    @SerializedName("measure")
    private String measure;

    @SerializedName("ingredient")
    @ColumnInfo(name = "ingredient_name")
    private String ingredientName;

    @Ignore
    public Ingredient(Integer quantity, String measure, String ingredientName) {
        this.recipeId = null;
        this.ingredientId = null;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredientName = ingredientName;
    }

    public Ingredient(Integer recipeId, Integer ingredientId, Integer quantity, String measure, String ingredientName) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredientName = ingredientName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }
}
