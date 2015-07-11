package novusapp.drinkfree;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*  main.java
    Contains all of the necessary visuals to show users the amount of money saved from not drinking, a random fact about drinking, and the amount of time
    that has passed since the user has stopped drinking.

    TODO: 1) Add images based on different sets of days they have been alcohol free
    TODO: 2) Add changes to the text and make things look better.
    TODO: 3) Add functionality to the reset button, add a dialogBox to make sure user wants to reset

 */

public class main extends ActionBarActivity {
    static double avgDrinkCostPerDay = 3.48;
    ValueEventListener listener;
    String account_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        final String phone_id = getPhoneId();

        // initialize text fields
        final TextView tipText = (TextView) this.findViewById(R.id.tipText);
        final TextView moneyText = (TextView) this.findViewById(R.id.moneyText);
        final TextView countText = (TextView) this.findViewById(R.id.dateText);
        final TextView nameText = (TextView) this.findViewById(R.id.name);

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                account_id = dataSnapshot.child("didlogin").child(phone_id).getValue().toString();
                Log.v("Account ID", account_id);
                String account_name = "DrinkFree User";
                if(dataSnapshot.child("account").child(account_id).child("fullname").getValue() != null){
                    account_name = dataSnapshot.child("account").child(account_id).child("fullname").getValue().toString();
                }

                Calendar endCal = Calendar.getInstance();
                endCal.getTime();
                Calendar startCal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                try {
                    startCal.setTime(sdf.parse(dataSnapshot.child("account").child(account_id).child("startdate").getValue().toString()));
                }catch(Exception e){
                  // Can potentially catch an IO parse exception here
                  e.printStackTrace();
                }
                int dateCount = diffCountTime(startCal, endCal);
                double moneyCount = dateCount * avgDrinkCostPerDay;

                nameText.setText("Welcome, " + account_name);
                countText.setText("Date Counter: " +Integer.toString(dateCount) + " Days");
                moneyText.setText("Money Saved: " + Double.toString(moneyCount));
                Toast.makeText(getApplicationContext(), "Date Count: " + dateCount, Toast.LENGTH_LONG).show();

                int childrenCount = (int) dataSnapshot.child("fact").getChildrenCount();
                Random rand = new Random();
                int randCount = rand.nextInt(--childrenCount);
                Log.v("RandCount", Integer.toString(randCount));
                if (dataSnapshot.child("fact").child(Integer.toString(randCount)).exists()) {
                    tipText.setText(dataSnapshot.child("fact").child(Integer.toString(randCount)).getValue().toString());
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

            public int diffCountTime(Calendar startDate, Calendar endDate) {
                long end = endDate.getTimeInMillis();
                end = TimeUnit.MILLISECONDS.toDays(end);
                long start = startDate.getTimeInMillis();
                start = TimeUnit.MILLISECONDS.toDays(start);
                //Log.v("DiffCount", "End: " + end + "Start: " + start + "    Computed: " + TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)));
                return (int) (end - start);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_reset) {
            //Toast.makeText(getApplicationContext(), "Resetting User", Toast.LENGTH_LONG).show();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Do you want to reset?");
            // alert.setMessage("Message");

            alert.setPositiveButton("Yes, Reset", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Your action here
                    resetUser();
                }
            });

            alert.setNegativeButton("No, Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetUser(){
        //Setup firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        //Get calender time
        final Calendar cal = Calendar.getInstance();
        cal.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        myFirebaseRef.child("account").child(account_id).child("moneycount").setValue(0);
        myFirebaseRef.child("account").child(account_id).child("startdate").setValue(cal.getTime().toString());
    }

    private String getPhoneId(){
        // Get android_id
        final String phone_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return phone_id;
    }

}
