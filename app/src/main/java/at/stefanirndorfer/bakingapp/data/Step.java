package at.stefanirndorfer.bakingapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "step", foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "recipe_id",
        childColumns = "step_id",
        onDelete = CASCADE))

public class Step {

    @ColumnInfo(name = "recipe_id")
    private Integer recipeId;


    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "step_id")
    private Integer id;


    @SerializedName("shortDescription")
    @ColumnInfo(name = "short_description")
    private String shortDescription;

    @SerializedName("description")
    private String description;

    @SerializedName("videoURL")
    @ColumnInfo(name = "video_url")
    private String videoURL;

    @SerializedName("thumbnailURL")
    @ColumnInfo(name = "thumbnail_url")
    private String thumbnailURL;

    @Ignore
    public Step(Integer id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.recipeId = null;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public Step(Integer recipeId, Integer id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.recipeId = recipeId;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }
}
