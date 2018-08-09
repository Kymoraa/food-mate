package msc.project.foodmate.database.model;

/**
 * Created by Jackie Moraa on 8/1/2018.
 */

public class FavouritesDB {
    public static final String TABLE_NAME = "favourites";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE = "imageUrl";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_DIET = "diet";

    private int id;
    private String imageUrl;
    private String name;
    private String price;
    private String description;
    private String ingredients;
    private String diet;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PRICE + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_INGREDIENTS + " TEXT,"
                    + COLUMN_DIET + " TEXT"
                    + ")";

    public FavouritesDB() {
    }


    public FavouritesDB(int id, String imageUrl, String name, String price, String description, String ingredients, String diet) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
        this.diet = diet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String diet) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

}
