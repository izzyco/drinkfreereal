/*
   drinkfree.java: The login page where people who have signed up previously will automatically go to their account.
   Author: Ivan Zhang
   Company: Novusapp.com

   TODO: 1) Add new security interface where the password would be added together with a hash

 */

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EventListener;



public class drinkfree extends Activity {
    ValueEventListener loginListener;
    private static final String FIREBASE_REF = "https://drinkfreeapp.firebaseio.com/";
    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String DID_LOGIN = "didlogin";

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
        final Firebase myFirebaseRef = new Firebase(FIREBASE_REF);

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

                    final Firebase loginRef = myFirebaseRef.child(ACCOUNT);

                    //Toast.makeText(getApplicationContext(), "Email : " + emailBox.getText().toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Password :" + passwordBox.getText().toString(), Toast.LENGTH_LONG).show();

                    // Firebase event listener. Loop through registered users to determine if the login is correct or not.
                    loginRef.addValueEventListener(loginListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            //Log.v("Event", "Count: "+ count);
                            String dbEmail = "";
                            String dbPass = "";
                            for (int i = 0; i <= count; i++) {
                                String in = Integer.toString(i);

                                if (dataSnapshot.child(in).child(EMAIL).getValue() != null) {
                                    dbEmail = dataSnapshot.child(in).child(EMAIL).getValue().toString();
                                }

                                if (dataSnapshot.child(in).child(PASSWORD).getValue() != null) {
                                    dbPass = dataSnapshot.child(in).child(PASSWORD).getValue().toString();
                                }
                                //Log.v("Event", "Account ID: "+ in);
                                //Log.v("Event", "Email: "+ dbEmail);
                                //Log.v("Event", "Pass: "+ dbPass);

                                if (dbEmail.equals(emailBox.getText().toString())) {
                                    String encryptedPass = "";
                                    try {
                                        MessageDigest digester = java.security.MessageDigest.getInstance("MD5");
                                        digester.update(passwordBox.getText().toString().getBytes());
                                        byte[] hash = digester.digest();
                                        StringBuffer hexString = new StringBuffer();
                                        for (int o = 0; o < hash.length; o++) {
                                            if ((0xff & hash[o]) < 0x10) {
                                                hexString.append("0" + Integer.toHexString((0xFF & hash[o])));
                                            }
                                            else {
                                                hexString.append(Integer.toHexString(0xFF & hash[o]));
                                            }
                                        }
                                        encryptedPass = hexString.toString();
                                    } catch (NoSuchAlgorithmException e) {
                                        //Log.v("ErrorRegister", "No Algorithm Exception!");
                                    }
                                    if(dbPass.equals(encryptedPass)) {
                                        //Log.v("login", "Login and password succeeded " + i);
                                        myFirebaseRef.child(DID_LOGIN).child(android_id).setValue(i);
                                        Intent mainIntent = new Intent(getApplicationContext(), main.class);
                                        startActivity(mainIntent);
                                        myFirebaseRef.removeEventListener(loginListener);
                                        finish();
                                    }
                                } else {
                                    //Log.v("Event", "Email Box: "+emailBox.getText().toString());
                                    //Log.v("Event", "Password Box: " + passwordBox.getText().toString());
                                    //Log.v("Event", "ELSE HIT!!");
                                    Toast.makeText(getApplicationContext(), "The password and/or email is incorrect", Toast.LENGTH_LONG);
                                }
                            }
                            passwordBox.getText().clear();
                            emailBox.getText().clear();
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

        // Clicking the logo creates a toast showing Novus App
        ImageView logo = (ImageView) findViewById(R.id.login_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Created from Novus App ( Novusapp.com )", Toast.LENGTH_LONG ).show();
            }
        });


    }
}
