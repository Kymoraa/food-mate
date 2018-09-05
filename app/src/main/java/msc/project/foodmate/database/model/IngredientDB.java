package msc.project.foodmate.database.model;

/**
 * Created by Jackie Moraa on 8/1/2018.
 */

/*
create the ingredients table in the SQLite database
 */

public class IngredientDB {
    public static final String TABLE_NAME = "ingredients";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INGREDIENT = "ingredient";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String ingredient;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_INGREDIENT + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public IngredientDB() {
    }

    public IngredientDB(int id, String ingredient, String timestamp) {
        this.id = id;
        this.ingredient = ingredient;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getIngredientDB() {
        return ingredient;
    }

    public void setIngredientDB(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

