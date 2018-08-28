package msc.project.foodmate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.*;


/**
 * Created by Jackie Moraa on 8/26/2018.
 */
public class TestFirebase extends AndroidTestCase {
   // private static Logger logger = LoggerFactory.getLogger(TestFirebase.class);
    private static final Logger LOGGER = Logger.getLogger("TestFirebase.class");

    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;
    private Context mContext;

    @Override
    public void setUp() throws InterruptedException {
        authSignal = new CountDownLatch(1);
//        mContext = InstrumentationRegistry.getTargetContext();
//        Firebase.setAndroidContext(mContext); //initializeFireBase(context);


        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("2282034N@student.gla.ac.uk", "123456").addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            final AuthResult result = task.getResult();
                            final FirebaseUser user = result.getUser();
                            authSignal.countDown();
                        }
                    });
        } else {
            authSignal.countDown();
        }
        authSignal.await(10, TimeUnit.SECONDS);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if(auth != null) {
            auth.signOut();
            auth = null;
        }
    }

    @Test
    public void testWrite() throws InterruptedException {
        final CountDownLatch writeSignal = new CountDownLatch(1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Do you have data? You'll love Firebase. - 3")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {
                        writeSignal.countDown();
                    }
                });

        writeSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        assertEquals("msc.project.foodmate", mContext.getPackageName());
    }

}