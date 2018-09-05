package msc.project.foodmate.database.model;

/**
 * Created by Jackie Moraa on 8/1/2018.
 */

/*
create the diets table in the SQLite database
 */

public class DietDB {
    public static final String TABLE_NAME = "diets";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DIET = "diet";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String diet;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DIET + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public DietDB() {
    }

    public DietDB(int id, String diet, String timestamp) {
        this.id = id;
        this.diet = diet;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getDietDB() {
        return diet;
    }

    public void setDietDB(String diet) {
        this.diet = diet;
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
