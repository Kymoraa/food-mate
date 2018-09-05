package msc.project.foodmate.database.model;

/**
 * Created by Jackie Moraa on 8/1/2018.
 */

/*
create the allergens table in the SQLite database
 */
public class AllergenDB {
    public static final String TABLE_NAME = "allergens";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ALLERGEN = "allergen";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String allergen;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ALLERGEN + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public AllergenDB() {
    }

    public AllergenDB(int id, String allergen, String timestamp) {
        this.id = id;
        this.allergen = allergen;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getAllergenDB() {
        return allergen;
    }

    public void setAllergenDB(String allergen) {
        this.allergen = allergen;
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
