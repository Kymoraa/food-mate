package msc.project.foodmate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import msc.project.foodmate.database.model.AllergenDB;
import msc.project.foodmate.database.model.DietDB;
import msc.project.foodmate.database.model.FavouritesDB;
import msc.project.foodmate.database.model.IngredientDB;

/**
 * Created by Jackie Moraa on 8/1/2018.
 */
/*
database helper class
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    public static final String DATABASE_NAME = "food_mate";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create diets table
        db.execSQL(DietDB.CREATE_TABLE);

        // create ingredients table
        db.execSQL(IngredientDB.CREATE_TABLE);

        // create allergens table
        db.execSQL(AllergenDB.CREATE_TABLE);

        // create favourites table
        db.execSQL(FavouritesDB.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DietDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IngredientDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AllergenDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesDB.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertDiet(String diet) {
        long id = -10;
        boolean numeric = true;

        numeric = diet.matches(".*\\d+.*");
        // get writable database to write the data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // id and timestamp will be inserted automatically.

        if(!numeric){
            values.put(DietDB.COLUMN_DIET, diet);
            // insert row
            id = db.insert(DietDB.TABLE_NAME, null, values);
        }

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insertIngredient(String ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IngredientDB.COLUMN_INGREDIENT, ingredient);
        long id = db.insert(IngredientDB.TABLE_NAME, null, values);
        db.close();
        return id;
    }


    public long insertAllergen(String allergen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AllergenDB.COLUMN_ALLERGEN, allergen);
        long id = db.insert(AllergenDB.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public DietDB getDietDB(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DietDB.TABLE_NAME,
                new String[]{DietDB.COLUMN_ID, DietDB.COLUMN_DIET, DietDB.COLUMN_TIMESTAMP},
                DietDB.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare diet object
        DietDB diet = new DietDB(
                cursor.getInt(cursor.getColumnIndex(DietDB.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DietDB.COLUMN_DIET)),
                cursor.getString(cursor.getColumnIndex(DietDB.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return diet;
    }

    public IngredientDB getIngredientDB(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(IngredientDB.TABLE_NAME,
                new String[]{IngredientDB.COLUMN_ID, IngredientDB.COLUMN_INGREDIENT, IngredientDB.COLUMN_TIMESTAMP},
                IngredientDB.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare ingredient object
        IngredientDB ingredient = new IngredientDB(
                cursor.getInt(cursor.getColumnIndex(IngredientDB.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(IngredientDB.COLUMN_INGREDIENT)),
                cursor.getString(cursor.getColumnIndex(IngredientDB.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return ingredient;
    }

    public AllergenDB getAllergenDB(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(AllergenDB.TABLE_NAME,
                new String[]{AllergenDB.COLUMN_ID, AllergenDB.COLUMN_ALLERGEN, AllergenDB.COLUMN_TIMESTAMP},
                AllergenDB.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare allergen object
        AllergenDB allergen = new AllergenDB(
                cursor.getInt(cursor.getColumnIndex(AllergenDB.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(AllergenDB.COLUMN_ALLERGEN)),
                cursor.getString(cursor.getColumnIndex(AllergenDB.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return allergen;
    }

    public List<DietDB> getAllDiets() {
        List<DietDB> diets = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DietDB.TABLE_NAME + " ORDER BY " +
                DietDB.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DietDB diet = new DietDB();
                diet.setId(cursor.getInt(cursor.getColumnIndex(DietDB.COLUMN_ID)));
                diet.setDietDB(cursor.getString(cursor.getColumnIndex(DietDB.COLUMN_DIET)));
                diet.setTimestamp(cursor.getString(cursor.getColumnIndex(DietDB.COLUMN_TIMESTAMP)));

                diets.add(diet);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return diets list
        return diets;
    }

    public List<IngredientDB> getAllIngredients() {
        List<IngredientDB> ingredients = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + IngredientDB.TABLE_NAME + " ORDER BY " +
                IngredientDB.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                IngredientDB ingredient = new IngredientDB();
                ingredient.setId(cursor.getInt(cursor.getColumnIndex(IngredientDB.COLUMN_ID)));
                ingredient.setIngredientDB(cursor.getString(cursor.getColumnIndex(IngredientDB.COLUMN_INGREDIENT)));
                ingredient.setTimestamp(cursor.getString(cursor.getColumnIndex(IngredientDB.COLUMN_TIMESTAMP)));

                ingredients.add(ingredient);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return ingredients list
        return ingredients;
    }

    public List<AllergenDB> getAllAllergens() {
        List<AllergenDB> allergens = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + AllergenDB.TABLE_NAME + " ORDER BY " +
                AllergenDB.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AllergenDB allergen = new AllergenDB();
                allergen.setId(cursor.getInt(cursor.getColumnIndex(AllergenDB.COLUMN_ID)));
                allergen.setAllergenDB(cursor.getString(cursor.getColumnIndex(AllergenDB.COLUMN_ALLERGEN)));
                allergen.setTimestamp(cursor.getString(cursor.getColumnIndex(AllergenDB.COLUMN_TIMESTAMP)));

                allergens.add(allergen);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return allergens list
        return allergens;
    }

    public List<FavouritesDB> getAllFavourites() {
        List<FavouritesDB> favourites = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + FavouritesDB.TABLE_NAME + " ORDER BY " +
                FavouritesDB.COLUMN_NAME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FavouritesDB favourite = new FavouritesDB();
                favourite.setId(cursor.getInt(cursor.getColumnIndex(FavouritesDB.COLUMN_ID)));
                favourite.setImageUrl(cursor.getString(cursor.getColumnIndex(FavouritesDB.COLUMN_IMAGE)));
                favourite.setName(cursor.getString(cursor.getColumnIndex(FavouritesDB.COLUMN_NAME)));
                favourite.setPrice(cursor.getString(cursor.getColumnIndex(FavouritesDB.COLUMN_PRICE)));
                favourite.setDescription(cursor.getString(cursor.getColumnIndex(FavouritesDB.COLUMN_DESCRIPTION)));
                favourite.setIngredients(cursor.getString(cursor.getColumnIndex(FavouritesDB.COLUMN_INGREDIENTS)));
                favourite.setDiet(cursor.getString(cursor.getColumnIndex(FavouritesDB.COLUMN_DIET)));

                favourites.add(favourite);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return favourites list
        return favourites;
    }

    public int getDietDBCount() {
        String countQuery = "SELECT  * FROM " + DietDB.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int getIngredientDBCount() {
        String countQuery = "SELECT  * FROM " + IngredientDB.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int getAllergenDBCount() {
        String countQuery = "SELECT  * FROM " + AllergenDB.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int getFavouritesDBCount() {
        String countQuery = "SELECT  * FROM " + FavouritesDB.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int updateDietDB(DietDB diet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DietDB.COLUMN_DIET, diet.getDietDB());

        // updating row
        return db.update(DietDB.TABLE_NAME, values, DietDB.COLUMN_ID + " = ?",
                new String[]{String.valueOf(diet.getId())});
    }

    public int updateIngredientDB(IngredientDB ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(IngredientDB.COLUMN_INGREDIENT, ingredient.getIngredientDB());

        // updating row
        return db.update(IngredientDB.TABLE_NAME, values, IngredientDB.COLUMN_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())});
    }

    public int updateAllergenDB(AllergenDB allergen) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AllergenDB.COLUMN_ALLERGEN, allergen.getAllergenDB());

        // updating row
        return db.update(AllergenDB.TABLE_NAME, values, AllergenDB.COLUMN_ID + " = ?",
                new String[]{String.valueOf(allergen.getId())});
    }

    public void deleteDietDB(DietDB diet) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DietDB.TABLE_NAME, DietDB.COLUMN_ID + " = ?",
                new String[]{String.valueOf(diet.getId())});
        db.close();
    }

    public void deleteIngredientDB(IngredientDB ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IngredientDB.TABLE_NAME, IngredientDB.COLUMN_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())});
        db.close();
    }

    public void deleteAllergenDB(AllergenDB allergen) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AllergenDB.TABLE_NAME, AllergenDB.COLUMN_ID + " = ?",
                new String[]{String.valueOf(allergen.getId())});
        db.close();
    }

    public void deleteFavouriteDB(FavouritesDB favourite) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavouritesDB.TABLE_NAME, FavouritesDB.COLUMN_NAME + " = ?",
                new String[]{String.valueOf(favourite.getName())});
        db.close();
    }


}