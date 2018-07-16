package msc.project.foodmate;

/**
 * Created by Jackie Moraa on 7/10/2018.
 */

public class CuisineUploads {

    private String etCuisineName, etPrice, etDescription, etIngredients, ivImageUri;

    public CuisineUploads(){
        //this is an empty constructor
        //needed for FireBase
    }

    public CuisineUploads(String cuisineName, String price, String description, String ingredients, String imageUri){

        etCuisineName = cuisineName;
        etPrice = price;
        etDescription = description;
        etIngredients = ingredients;
        ivImageUri = imageUri;
    }

    public String getName (){
        return etCuisineName;
    }

    public void setName (String cuisineName){
        etCuisineName = cuisineName;
    }

    public String getPrice (){
        return etPrice;
    }

    public void setPrice (String price){
        etPrice = price;
    }

    public String getDescription (){
        return etDescription;
    }

    public void setDescription (String description){
        etDescription = description;
    }

    public String getIngredients (){
        return etIngredients;
    }

    public void setIngredients (String ingredients){
        etIngredients = ingredients;
    }

    public String getImageUri (){
        return ivImageUri;
    }

    public void setImageUri (String imageUri){
        ivImageUri = imageUri;
    }
}