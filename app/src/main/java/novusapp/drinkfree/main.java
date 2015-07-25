/*
    main.java: Contains all of the necessary visuals to show users the amount of money saved from not drinking, a random fact about drinking, and the amount of time
    that has passed since the user has stopped drinking.
    Author : Ivan Zhang
    Company : Novusapp.com

    TODO: 1) Add images based on different sets of days they have been alcohol free

 */

package novusapp.drinkfree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
                if (dataSnapshot.child("account").child(account_id).child("fullname").getValue() != null) {
                    account_name = dataSnapshot.child("account").child(account_id).child("fullname").getValue().toString();
                }

                // Creates the initial calendar instance to determine the count of the account
                Calendar endCal = Calendar.getInstance();
                endCal.getTime();
                Calendar startCal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                try {
                    startCal.setTime(sdf.parse(dataSnapshot.child("account").child(account_id).child("startdate").getValue().toString()));
                } catch (Exception e) {
                    // Can potentially catch an IO parse exception here
                    e.printStackTrace();
                }
                int dateCount = diffCountTime(startCal, endCal);
                double moneyCount = dateCount * avgDrinkCostPerDay;

                // Create welcome text, adds the name if there is a name on the account
                nameText.setText("Welcome, " + account_name);

                // Creates date counter for the user and adds a calendar to the beginning of it
                SpannableStringBuilder countBuilder = new SpannableStringBuilder();
                countBuilder.append(" ");
                countBuilder.setSpan(new ImageSpan(getApplication(), R.drawable.calendarimg),
                        countBuilder.length() - 1, countBuilder.length(), 0);
                countBuilder.append("  Date Counter: " + Integer.toString(dateCount) + " Days");
                countText.setText(countBuilder);

                // Creates the money saved counter, plus adds a money icon at the beginning of it
                SpannableStringBuilder moneybuilder = new SpannableStringBuilder();
                moneybuilder.append(" ");
                moneybuilder.setSpan(new ImageSpan(getApplication(), R.drawable.moneyimg),
                        moneybuilder.length() - 1, moneybuilder.length(), 0);
                moneybuilder.append("  Money Saved: $" + Double.toString(moneyCount));
                moneyText.setText(moneybuilder);

                // Set images based on how long it has been!
                // Sets text under the images based on how long it has been for the user
                TextView imgDescription = (TextView) findViewById(R.id.imageDescription);
                ImageView growingImage = (ImageView) findViewById(R.id.growingImage);
                if(dateCount < 2){
                    // 1 Days Notification
                    growingImage.setImageResource(R.drawable.seed);
                    growingImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "The Start is the Hardest!", Toast.LENGTH_LONG).show();
                        }
                    });
                    imgDescription.setText("Congrats: Getting Started Badge!");
                }else if(dateCount < 7){
                    // 1 Weeks Notification
                    growingImage.setImageResource(R.drawable.seed);
                    imgDescription.setText("Congrats: 1 Week Badge!");

                }else if(dateCount < 21){
                    // 3 Weeks Notification
                    growingImage.setImageResource(R.drawable.seed);
                    imgDescription.setText("Congrats: 3 Week Badge!");

                }else if(dateCount < 30){
                    // 1 month Notification
                    growingImage.setImageResource(R.drawable.seed);
                    imgDescription.setText("Congrats: 1 Month Badge!");

                }else if(dateCount < 60){
                    // 2 month notification
                    growingImage.setImageResource(R.drawable.seed);
                    imgDescription.setText("Congrats: 1 Month Badge!");


                }else if(dateCount < 180){
                    // 1/2 Year Notification
                    growingImage.setImageResource(R.drawable.seed);
                    imgDescription.setText("Congrats: Half Year Badge!");

                }

                // Random tip to show at the time
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
            alert.setMessage("This will reset your count, cost, and anything else currently on the account. Everything will reset to a new account.");

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
        }else if(id == R.id.action_about){
            Intent mainIntent = new Intent(getApplicationContext(), about.class);
            startActivity(mainIntent);
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
