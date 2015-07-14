package novusapp.drinkfree;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.EventListener;

/* drinkfree.java
   The login page where people who have signed up previously will automatically go to their account.

   TODO: 1) When clicking on the novusapp imageview, show a toast linking back to http://novusapp.com

 */


public class drinkfree extends Activity {

    ValueEventListener loginListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getApplicationContext());
        setContentView(R.layout.activity_drinkfree);

        final Button login = (Button) this.findViewById((R.id.loginButton));
        Button regButton = (Button) this.findViewById((R.id.regButton));
        final EditText emailBox = (EditText)this.findViewById(R.id.emailBox);
        final EditText passwordBox = (EditText)this.findViewById(R.id.passwordBox);

        // Setup Firebase
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        // Do a check on the android_id, proceed onto the next page if the user has already logged in with this android_id
        final String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);

        /* Login button to check if this user has already created an account
        *
        * */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initial check to see if there are values in the text boxes
                if (emailBox.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
                } else if (passwordBox.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
                }else {

                    final Firebase loginRef = myFirebaseRef.child("account");

                    //Toast.makeText(getApplicationContext(), "Email : " + emailBox.getText().toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Password :" + passwordBox.getText().toString(), Toast.LENGTH_LONG).show();

                    // Firebase event listener. Loop through registered users to determine if the login is correct or not.
                    loginRef.addValueEventListener(loginListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            Log.v("Event", "Count: "+ count);
                            String dbEmail = "";
                            String dbPass = "";
                            for (int i = 0; i <= count; i++) {
                                String in = Integer.toString(i);

                                if (dataSnapshot.child(in).child("email").getValue() != null) {
                                    dbEmail = dataSnapshot.child(in).child("email").getValue().toString();
                                }

                                if (dataSnapshot.child(in).child("password").getValue() != null) {
                                    dbPass = dataSnapshot.child(in).child("password").getValue().toString();
                                }
                                //Log.v("Event", "Account ID: "+ in);
                                //Log.v("Event", "Email: "+ dbEmail);
                                //Log.v("Event", "Pass: "+ dbPass);
                                if (dbEmail.equals(emailBox.getText().toString()) && dbPass.equals(passwordBox.getText().toString())) {
                                    Log.v("login", "Login and password succeeded " + i);
                                    myFirebaseRef.child("didlogin").child(android_id).setValue(i);
                                    Intent mainIntent = new Intent(getApplicationContext(), main.class);
                                    startActivity(mainIntent);
                                    myFirebaseRef.removeEventListener(loginListener);
                                    finish();
                                } else {
                                    Log.v("Event", "Email Box: "+emailBox.getText().toString());
                                    Log.v("Event", "Password Box: " + passwordBox.getText().toString());
                                    Log.v("Event", "ELSE HIT!!");
                                    Toast.makeText(getApplicationContext(), "The password and/or email is incorrect", Toast.LENGTH_LONG);
                                }
                            }
                            passwordBox.getText().clear();
                            emailBox.getText().clear();

                            Toast.makeText(getApplicationContext(), "Ello Bud", Toast.LENGTH_LONG);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });



    }
}
