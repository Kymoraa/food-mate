package msc.project.foodmate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import msc.project.foodmate.database.DatabaseHelper;
import msc.project.foodmate.database.model.DietDB;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends AndroidTestCase {
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private final Class dbHelper = DatabaseHelper.class;


    @Before
    public void setUp() throws Exception {
        this.appContext = InstrumentationRegistry.getTargetContext();

    }

//    @Test
//    public void useAppContext() throws Exception {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        assertEquals("msc.project.foodmate", appContext.getPackageName());
//    }

    @Test
    public void dropDB(){
        assertTrue(appContext.deleteDatabase(DatabaseHelper.DATABASE_NAME));
        System.out.println("DB drop test passed");
    }

    @Test
    public void insertDiet(){
        //assertEquals(appContext.insertDiet("abc"), -10);
      //assertTrue(appContext.deleteDatabase(DatabaseHelper.DATABASE_NAME));
        System.out.println("Numeric test");
        System.out.println(dbHelper.insertDiet("abc"));
    }


    @Test
    public void correctDataInDB() throws Exception {
        // Context of the app under test.
        DatabaseHelper dbHelper =new DatabaseHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DietDB.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());
    }



}
