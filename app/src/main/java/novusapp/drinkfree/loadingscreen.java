package novusapp.drinkfree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/*
    Loadingscreen.java
    Acts like a buffer screen while the application is determining whether the user is already logged in or not.
 */


public class loadingscreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingscreen);
        Firebase.setAndroidContext(getApplicationContext());

        // Checks to see if the network is available, shows toast if it is not
        if(isNetworkAvailable() == false) {
            Toast.makeText(getApplicationContext(), "Please enable internet", Toast.LENGTH_LONG).show();
            Log.d("NetworkAvailable", "Hello");
        }else {

            // Setup Firebase
            final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

            // Get android_id
            final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            myFirebaseRef.child("didlogin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(android_id).exists()) {
                        Toast.makeText(getApplicationContext(), "You are all set to go.", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(getApplicationContext(), main.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Intent loginIntent = new Intent(getApplicationContext(), drinkfree.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null) return true;
        else return false;
    }

}
