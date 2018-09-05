package msc.project.foodmate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import msc.project.foodmate.database.DatabaseHelper;
import msc.project.foodmate.database.model.CuisineUploads;
import msc.project.foodmate.database.model.DietDB;
import msc.project.foodmate.view.SearchAdapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


@RunWith(AndroidJUnit4.class)

public class FoodMateInstrumentedTest extends AndroidTestCase {
    private final Context context = InstrumentationRegistry.getTargetContext();
    private final Class dbHelper = DatabaseHelper.class;
    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;
    private List<CuisineUploads> mCuisineUploads;
    private SearchAdapter searchAdapter;
    private ValueEventListener mDBListener;
    private List<DietDB> diets = new ArrayList<>();

    @Before
    public void setUp() {
        deleteDatabase();
    }

    @Test
    public void createDatabase() throws Exception{
        SQLiteOpenHelper sqlHelper = (SQLiteOpenHelper) dbHelper.getConstructor(Context.class).newInstance(context);
        SQLiteDatabase database = sqlHelper.getReadableDatabase();

        //verify that the database is open
        String databaseNotOpen = "The database should be open but is not";
        assertEquals(databaseNotOpen, true, database.isOpen());

        Cursor cursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        DietDB.TABLE_NAME + "'", null);


        //if false, the database wasn't created properly WHERE type='table'
        String errorInCreatingDatabase = "Database not created properly";
        assertTrue(errorInCreatingDatabase, cursor.moveToFirst());

        //database doesn't contain expected table - diets
        assertEquals("Database doesn't contain expected tables.",
                DietDB.TABLE_NAME, cursor.getString(0));

        cursor.close();

    }

    @Test
    public void insertDiet() throws Exception{
        //test inserting a record in a table
        SQLiteOpenHelper sqlHelper = (SQLiteOpenHelper) dbHelper.getConstructor(Context.class).newInstance(context);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String newDiet = "Atkins";

        //insert values into database
        //get id
        long id = dbHelper.insertDiet(newDiet);
        //If the insert fails, insertDiet returns
        assertNotEquals("Unable to insert into the database", -1, id);

        //Query the database and receive a Cursor
        Cursor cursor = database.query(
                DietDB.TABLE_NAME,null,null,null,null,null,null);

        //return false if there are no records
        assertTrue("Records do not exist", cursor.moveToFirst());

        cursor.close();
        sqlHelper.close();

    }

    @Test
    public void accurateData() throws Exception{
        //test that accurate data is added to the database
        SQLiteOpenHelper sqlHelper = (SQLiteOpenHelper) dbHelper.getConstructor(Context.class).newInstance(context);
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        //test that the size of the database increases when the first diet is added
        dbHelper.insertDiet("Ketogenic");
        diets = dbHelper.getAllDiets();
        assertThat(diets.size(), is(1));

        //test that the size of the database increases when more diets are added
        dbHelper.insertDiet("Vegan");
        diets = dbHelper.getAllDiets();
        assertThat(diets.size(), is(2));

        String dietQuery = "SELECT " + DietDB.COLUMN_DIET + " FROM " + DietDB.TABLE_NAME;
        Cursor cursor = database.rawQuery(dietQuery, null);
        ArrayList<String> dietsString =new ArrayList<String>() ;

        cursor.moveToFirst();
        if(cursor.getCount()>0){
            do{
                dietsString.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        String string = dietsString.get(0);
        Log.d("This is diet at index 0:", string);

        //test whether the diet added at index 0 is the accurate data
        assertTrue(dietsString.get(0).equals("Ketogenic"));

        cursor.close();
        sqlHelper.close();
    }

    @Test
    public void autoIncrement() throws Exception{
        //test if id is auto incremented in the database
        insertDiet();
        SQLiteOpenHelper sqlHelper = (SQLiteOpenHelper) dbHelper.getConstructor(Context.class).newInstance(context);
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        //add first item
        String diet_1 = "Atkins";
        long id_1 = dbHelper.insertDiet(diet_1);

        //add second item
        String diet_2 = "Vegan";
        long id_2 = dbHelper.insertDiet(diet_2);

        //the id of the second item should be +1 the id of the first item
        assertEquals("id autoincrement test failed!", id_1+1, id_2);

    }

    @Test
    public void deleteDatabase(){
        //test to delete the SQLite database
        try{
            Field field = dbHelper.getDeclaredField("DATABASE_NAME");
            field.setAccessible(true);
            context.deleteDatabase((String)field.get(null));
        }catch (NoSuchFieldException ex){
            fail("Database doesn't exists");

        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }


    @Test
    public void signIn(){
        //test to sign in a user with Firebase
        //fail if the user with that email doesn't exist in Firebase database
        authSignal = new CountDownLatch(1);
        Firebase.setAndroidContext(context); //initializeFireBase(context);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("2282034N@student.gla.ac.uk", "123456").addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            final AuthResult result = task.getResult();
                            final FirebaseUser firebaseUser = result.getUser();
                            final String userEmail = firebaseUser.getEmail();
                            authSignal.countDown();

                            assertEquals("Sign in failed!", "2282034N@student.gla.ac.uk", userEmail);

                        }
                    });
        }
        else {
            authSignal.countDown();
        }
        try {
            authSignal.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void signUp(){
        //test the Firebase sign up/ register process
        //Fail if the user exists in the database
        authSignal = new CountDownLatch(1);
        Firebase.setAndroidContext(context); //initializeFireBase(context);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.createUserWithEmailAndPassword("test@student.gla.ac.uk", "123456").addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            //final AuthResult result = task.getResult();
                            final FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                final String userEmail = firebaseUser.getEmail();
                                authSignal.countDown();

                                assertEquals("Registration failed!", "test@student.gla.ac.uk", userEmail);
                            }
                        }
                    });
        } else {
            authSignal.countDown();
        }
        try {
            authSignal.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void passwordValidation () {
        //test for password rules
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        String password = "123ABcd@";
        assertTrue(password.matches(pattern));

    }

    @Test
    public void emailValidation(){
        //test to check if email entered has the correct pattern
       String pattern =
                "[a-zA-Z0-9+._%-+]{1,256}" +
                        "@" +
                        "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                        "(" +
                        "." +
                        "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                        ")+";

        String email = "2282034N@student.gla.ac.uk";
        assertTrue(email.matches(pattern));
    }

    @Test
    public void dbWrite() throws InterruptedException {
        //test to check whether it is possible to write data to Firebase database
        final CountDownLatch writeSignal = new CountDownLatch(1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("testMessage");

        myRef.setValue("This is a test!! - 3")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {
                        writeSignal.countDown();
                    }
                });

        writeSignal.await(10, TimeUnit.SECONDS);
    }


    @Test
    public void performSearch(){
        //test to see if the ingredient entered will be contained in the results displayed
        mCuisineUploads= new ArrayList<>();
        searchAdapter = new SearchAdapter(context, mCuisineUploads);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");

        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCuisineUploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CuisineUploads cuisineUploads = postSnapshot.getValue(CuisineUploads.class);
                    mCuisineUploads.add(cuisineUploads);
                }

                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //check if string input is contained in the cuisine list
        String input = "beef";
        for(CuisineUploads newCuisine : mCuisineUploads) {
            String ingredients = newCuisine.getIngredients().toLowerCase();
            ArrayList<String> cuisineIngredients = new ArrayList<String>(Arrays.asList(ingredients.split(",")));

            assertTrue(cuisineIngredients.contains(input));

            assertThat(mCuisineUploads, contains(
                    hasProperty("ingredients", is(input))
            ));

        }

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if(auth != null) {
            auth.signOut();
            auth = null;
        }
    }


}


//    @Test
//    public void correctDataInDB() throws Exception {
//        // Context of the app under test.
//        DatabaseHelper dbHelper =new DatabaseHelper(appContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cursor = db.query(DietDB.TABLE_NAME, null, null, null, null, null, null);
//        assertTrue(cursor.moveToFirst());
//    }

//assertEquals(appContext.insertDiet("abc"), -10);
//assertTrue(appContext.deleteDatabase(DatabaseHelper.DATABASE_NAME));
//        System.out.println("Numeric test");
//        System.out.println(dbHelper.insertDiet("abc"));