package novusapp.drinkfree;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

    TODO: 1) Add content into the database on firebase. Data on how much each drink cost on average, random facts about alcahol
          2) Get the content and display them onto this app
          3) Randomize displayment of data
          4) Add time stamp for when the current text was first shown, compare that with current day. Change text to a random one if they are not the same.
 */


public class main extends Activity {
    static double avgDrinkCostPerDay = 3.48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        // Get android_id
        final String phone_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // initialize text fields
        final TextView tipText = (TextView) this.findViewById(R.id.tipText);
        final TextView moneyText = (TextView) this.findViewById(R.id.moneyText);
        final TextView countText = (TextView) this.findViewById(R.id.dateText);


        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String account_id = dataSnapshot.child("didlogin").child(phone_id).getValue().toString();
                String account_name = dataSnapshot.child("account").child(account_id).child("fullname").getValue().toString();

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

                countText.setText(Integer.toString(dateCount) + " Days");
                moneyText.setText("Money Saved: " + Double.toString(moneyCount));
                Toast.makeText(getApplicationContext(), "Date Count: "+dateCount, Toast.LENGTH_LONG).show();

                int childrenCount = (int)dataSnapshot.child("fact").getChildrenCount();
                Random rand = new Random();
                int randCount = rand.nextInt(childrenCount);
                tipText.setText(dataSnapshot.child("fact").child(Integer.toString(randCount)).getValue().toString());


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("FIREBASE ERROR!", firebaseError.getDetails());
            }

            public int diffCountTime(Calendar startDate, Calendar endDate){
                long end = endDate.getTimeInMillis();
                long start = startDate.getTimeInMillis();
                return (int)TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
            }
        });

    }

}
