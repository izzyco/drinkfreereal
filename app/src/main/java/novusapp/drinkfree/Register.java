/*
   Register.java : This allows users to sign up if they have not already done so. Once they have registered,
   they will immediately be directed to the home page(main.java).

   Author : Ivan Zhang
   Company : Novusapp.com

   TODO: 1) Add new security interface where the password would be added together with a hash, before saved

 */

package novusapp.drinkfree;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLDisplay;

public class Register extends Activity {

    static final String FIREBASE_REF = "https://drinkfreeapp.firebaseio.com/";
    static final String ACCOUNT = "account";
    static final String EMAIL = "email";
    static final String PASSWORD = "password";
    static final String DID_LOGIN = "didlogin";
    static final String USER_NAME = "username";
    static final String FULL_NAME = "fullname";
    static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    static final String MONEY_COUNT = "moneycount";
    static final String START_DATE = "startdate";
    static final long BUTTON_PRESSED_STATE = 8;

    private SimpleDateFormat sdf;

    private DatePickerDialog sobrietyDatePicker;
    private EditText sobrietyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setup Firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase(FIREBASE_REF);

        final EditText usernameBox = (EditText) this.findViewById(R.id.username);
        final EditText emailBox = (EditText) this.findViewById(R.id.emailTI);
        final EditText passwordBox = (EditText) this.findViewById(R.id.passwordTI);
        final EditText fullnameBox = (EditText) this.findViewById(R.id.nameTI);
        final Button signup = (Button) this.findViewById(R.id.registerButton);
        final Button login = (Button) this.findViewById(R.id.loginButtonRegister);

        // Setup sorbiety date edittext, and initiate DatePickerDialog on click
        sobrietyDate = (EditText) this.findViewById(R.id.dateTI);
        sobrietyDate.setInputType(InputType.TYPE_NULL);
        sobrietyDate.requestFocus();
        sobrietyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateTimeField();
                sobrietyDatePicker.show();
            }
        });

        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final Calendar cal = Calendar.getInstance();
        cal.getTime();
        sdf = new SimpleDateFormat(DATE_FORMAT);
        // Add listener to add new login for a person. Save the data.
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Checks to make sure the text is not blank when submitted
                        if (usernameBox.getText().toString().matches("")
                                || emailBox.getText().toString().matches("")
                                || passwordBox.getText().toString().matches("")) {

                            Toast.makeText(getApplicationContext(), "Please fill in all of the boxes", Toast.LENGTH_LONG).show();
                        } else {

                            int childCount = (int) dataSnapshot.child(ACCOUNT).getChildrenCount();
                            childCount++;
                            String strChildCount = Integer.toString(childCount);

                            boolean hasEmail = false;
                            for (int i = 0; i <= childCount; i++) {
                                if (dataSnapshot.child(ACCOUNT).child(Integer.toString(i)).child(EMAIL).exists()) {
                                    if (dataSnapshot.child(ACCOUNT).child(Integer.toString(i)).child(EMAIL).getValue().toString().contentEquals(emailBox.getText().toString())) {
                                        hasEmail = true;
                                        break;
                                    }
                                }
                            }

                            if (!hasEmail) {
                                // Add the account to the login
                                myFirebaseRef.child(ACCOUNT).child(strChildCount).child(EMAIL).setValue(emailBox.getText().toString());
                                myFirebaseRef.child(ACCOUNT).child(strChildCount).child(USER_NAME).setValue(usernameBox.getText().toString());

                                Toast.makeText(getApplicationContext(), "Got to end of line!", Toast.LENGTH_LONG).show();

                                String encryptedPass = "";
                                try {
                                    MessageDigest digester = java.security.MessageDigest.getInstance("MD5");
                                    digester.update(passwordBox.getText().toString().getBytes());
                                    byte[] hash = digester.digest();
                                    StringBuffer hexString = new StringBuffer();
                                    for (int i = 0; i < hash.length; i++) {
                                        if ((0xff & hash[i]) < 0x10) {
                                            hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                                        } else {
                                            hexString.append(Integer.toHexString(0xFF & hash[i]));
                                        }
                                    }
                                    encryptedPass = hexString.toString();
                                } catch (NoSuchAlgorithmException e) {
                                    Log.v("ErrorRegister", "No Algorithm Exception!");
                                }

                                myFirebaseRef.child(ACCOUNT).child(strChildCount).child(PASSWORD).setValue(encryptedPass);
                                myFirebaseRef.child(ACCOUNT).child(strChildCount).child(FULL_NAME).setValue(fullnameBox.getText().toString());
                                myFirebaseRef.child(ACCOUNT).child(strChildCount).child(MONEY_COUNT).setValue(0);
                                myFirebaseRef.child(ACCOUNT).child(strChildCount).child(START_DATE).setValue(sobrietyDate.getText().toString());
                                myFirebaseRef.child(DID_LOGIN).child(android_id).setValue(childCount);
                                Intent mainIntent = new Intent(getApplicationContext(), main.class);
                                startActivity(mainIntent);
                                finish();

                            } else {
                                emailBox.getText().clear();
                                Toast.makeText(getApplicationContext(), "This email is already used", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Login button, brings the user to the login activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(getApplicationContext(), drinkfree.class);
                startActivity(loginActivity);
            }
        });

        // Clicking the logo creates a toast showing Novus App
        ImageView logo = (ImageView) findViewById(R.id.register_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Created from Novus App ( Novusapp.com )", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        sobrietyDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                sobrietyDate.setText(sdf.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
