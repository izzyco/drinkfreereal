package novusapp.drinkfree;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/* Register.java
   Adds a new account into the Firebase db.

 */

public class Register extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Setup Firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        final EditText usernameBox = (EditText) this.findViewById(R.id.username);
        final EditText emailBox = (EditText) this.findViewById(R.id.emailTI);
        final EditText passwordBox = (EditText) this.findViewById(R.id.passwordTI);
        final EditText fullnameBox = (EditText) this.findViewById(R.id.nameTI);
        final Button signup = (Button) this.findViewById(R.id.registerButton);


        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final Calendar cal = Calendar.getInstance();
        cal.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Add listener to add new login for a person. Save the data.
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Checks to make sure the text is not blank when submitted
                        if(usernameBox.getText().toString().matches("")
                                || emailBox.getText().toString().matches("")
                                || passwordBox.getText().toString().matches("")){

                            Toast.makeText(getApplicationContext(), "Please fill in all of the boxes", Toast.LENGTH_LONG).show();
                        }
                        // Add the account to the login
                       int childCount  = (int) dataSnapshot.child("account").getChildrenCount();
                       childCount++;
                       String strChildCount = Integer.toString(childCount);
                        myFirebaseRef.child("account").child(strChildCount).child("email").setValue(emailBox.getText().toString());
                        myFirebaseRef.child("account").child(strChildCount).child("username").setValue(usernameBox.getText().toString());
                        myFirebaseRef.child("account").child(strChildCount).child("password").setValue(passwordBox.getText().toString());
                        myFirebaseRef.child("account").child(strChildCount).child("full name").setValue(fullnameBox.getText().toString());
                        myFirebaseRef.child("accountdata").child(strChildCount).child("moneycount").setValue(0);
                        myFirebaseRef.child("accountdata").child(strChildCount).child("startdata").setValue(sdf.format(cal.getTime()));
                        myFirebaseRef.child("didlogin").child(android_id).setValue(childCount);
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
