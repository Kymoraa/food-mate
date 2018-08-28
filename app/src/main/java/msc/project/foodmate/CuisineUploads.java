package msc.project.foodmate;

import com.google.firebase.database.Exclude;

/**
 * Created by Jackie Moraa on 7/10/2018.
 */

public class CuisineUploads {

    private String etCuisineName, etPrice, etDescription, etIngredients, ivImageUri, etDiet;
    private String mKey;

    public CuisineUploads(){
        //this is an empty constructor
        //needed for FireBase
    }

    public CuisineUploads(String cuisineName, String price, String description, String ingredients, String imageUri, String diet){

        etCuisineName = cuisineName;
        etPrice = price;
        etDescription = description;
        etIngredients = ingredients;
        etDiet = diet;
        ivImageUri = imageUri;
    }

    public String getName() {
        return etCuisineName;
    }

    public void setName(String etCuisineName) {
        this.etCuisineName = etCuisineName;
    }

    public String getPrice() {
        return etPrice;
    }

    public void setPrice(String etPrice) {
        this.etPrice = etPrice;
    }

    public String getDescription() {
        return etDescription;
    }

    public void setDescription(String etDescription) {
        this.etDescription = etDescription;
    }

    public String getIngredients() {
        return etIngredients;
    }

    public void setIngredients(String etIngredients) {
        this.etIngredients = etIngredients;
    }

    public String getImageUri() {
        return ivImageUri;
    }

    public void setImageUri(String ivImageUri) {
        this.ivImageUri = ivImageUri;
    }

    public String getDiet() {
        return etDiet;
    }

    public void setDiet(String etDiet) {
        this.etDiet = etDiet;
    }

    @Exclude
    public  String getKey(){return mKey;}

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}