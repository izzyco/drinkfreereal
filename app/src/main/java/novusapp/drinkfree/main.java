/*
    main.java: Contains all of the necessary visuals to show users the amount of money saved from not drinking, a random fact about drinking, and the amount of time
    that has passed since the user has stopped drinking.
    Author : Ivan Zhang
    Company : Novusapp.com


 */

package novusapp.drinkfree;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class main extends ActionBarActivity {
    static double avgDrinkCostPerDay = 3.481111111111;
    static String DIDLOGIN = "didlogin";
    static String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    static String FULL_NAME = "fullname";
    static String ACCOUNT = "account";
    static String FACT = "fact";
    static String START_DATE = "startdate";
    static String MONEY_COUNT = "moneycount";
    static String SPONSORCALL_REF = "sponsorcall_ref";
    static String PREF_FILE = "pref_file";
    Context context;

    ValueEventListener listener;
    String account_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

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
                account_id = dataSnapshot.child(DIDLOGIN).child(phone_id).getValue().toString();
                String account_name = "DrinkFree User";
                if (dataSnapshot.child(ACCOUNT).child(account_id).child(FULL_NAME).getValue() != null) {
                    account_name = dataSnapshot.child(ACCOUNT).child(account_id).child(FULL_NAME).getValue().toString();
                }

                // Creates the initial calendar instance to determine the count of the account
                Calendar endCal = Calendar.getInstance();
                endCal.getTime();
                Calendar startCal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
                try {
                    String data = dataSnapshot.child(ACCOUNT).child(account_id).child(START_DATE).getValue().toString();
                    Date date = sdf.parse(data);
                    startCal.setTime(date);
                    //startCal.setTime();
                    Log.v("DiffCount", "Start Calendar Data from Firebase: " + data);

                } catch (Exception e) {
                    // Can potentially catch an IO parse exception here
                    e.printStackTrace();
                    Log.v("DiffCount", "Exception! ");

                }


                int dateCount = diffCountTime(startCal, endCal);
                double moneyCount = dateCount * avgDrinkCostPerDay;

                // Create welcome text, adds the name if there is a name on the account
                nameText.setText("Welcome, " + account_name);
                countText.setText(" Date Counter: " + Integer.toString(dateCount) + " Days");
                moneyCount = round(moneyCount, 2);
                moneyText.setText(" Money Saved: $" + Double.toString(moneyCount));

                // Set images based on how long it has been!
                // Sets text under the images based on how long it has been for the user
                ImageView growingImage1 = (ImageView) findViewById(R.id.growingImage);
                ImageView growingImage2 = (ImageView) findViewById(R.id.growingImage2);
                ImageView growingImage3 = (ImageView) findViewById(R.id.growingImage3);
                ImageView growingImage4 = (ImageView) findViewById(R.id.growingImage4);
                ImageView growingImage5 = (ImageView) findViewById(R.id.growingImage5);

                if (dateCount < 2) {
                    // 1 Days Notification
                    growingImage1.setVisibility(View.VISIBLE);
                    growingImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 1 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (dateCount >= 7 && dateCount < 21) {
                    // 1 Weeks Notification
                    growingImage1.setVisibility(View.VISIBLE);
                    growingImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 1 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (dateCount >= 21 && dateCount < 30) {
                    // 3 Weeks Notification
                    growingImage1.setVisibility(View.VISIBLE);
                    growingImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 1 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage2.setVisibility(View.VISIBLE);
                    growingImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 3 Badge", Toast.LENGTH_LONG).show();
                        }
                    });

                } else if (dateCount >= 30 && dateCount < 60) {
                    // 1 month Notification
                    growingImage1.setVisibility(View.VISIBLE);
                    growingImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 1 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage2.setVisibility(View.VISIBLE);
                    growingImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 3 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage3.setVisibility(View.VISIBLE);
                    growingImage3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "1 Month Badge", Toast.LENGTH_LONG).show();
                        }
                    });

                } else if (dateCount >= 60 && dateCount < 180) {
                    // 2 month notification
                    growingImage1.setVisibility(View.VISIBLE);
                    growingImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 1 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage2.setVisibility(View.VISIBLE);
                    growingImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 3 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage3.setVisibility(View.VISIBLE);
                    growingImage3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "1 Month Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage4.setVisibility(View.VISIBLE);
                    growingImage4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "2 Month Badge", Toast.LENGTH_LONG).show();
                        }
                    });

                } else if (dateCount >= 180) {
                    // 1/2 Year Notification
                    growingImage1.setVisibility(View.VISIBLE);
                    growingImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 1 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage2.setVisibility(View.VISIBLE);
                    growingImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Week 3 Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage3.setVisibility(View.VISIBLE);
                    growingImage3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "1 Month Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage4.setVisibility(View.VISIBLE);
                    growingImage4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "2 Months Badge", Toast.LENGTH_LONG).show();
                        }
                    });
                    growingImage5.setVisibility(View.VISIBLE);
                    growingImage5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "6 Months Badge", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                // Random tip to show at the time
                int childrenCount = (int) dataSnapshot.child(FACT).getChildrenCount();
                Random rand = new Random();
                int randCount = rand.nextInt(--childrenCount);
                Log.v("RandCount", Integer.toString(randCount));
                if (dataSnapshot.child(FACT).child(Integer.toString(randCount)).exists()) {
                    tipText.setText(dataSnapshot.child(FACT).child(Integer.toString(randCount)).getValue().toString());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

    }

    // Method that takes two calendar dats and returns the amount of time between them. Used to determine current count.
    public int diffCountTime(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        end = TimeUnit.MILLISECONDS.toDays(end);
        long start = startDate.getTimeInMillis();
        start = TimeUnit.MILLISECONDS.toDays(start);
        Log.v("DiffCount", "End: " + end + "Start: " + start + "    Computed: " + TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)));
        Log.v("DiffCount", "Start Calendar " + startDate.getTime().toString() + " End Calendar :" + endDate.getTime().toString());
        return (int) (end - start);
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
        }else if(id == R.id.action_sponsor){
            SharedPreferences prefs = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
            String sponsorPhoneNumber = prefs.getString(SPONSORCALL_REF, null);
            if(sponsorPhoneNumber == null){
                addSponsorNumberAction();
            }else{
                String phoneNumber = "tel:" + sponsorPhoneNumber;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                startActivity(intent);
            }
        }else if(id == R.id.action_changeSponsor){
            addSponsorNumberAction();
        }else if(id == R.id.action_addResources){
            Intent additionalRes = new Intent(getApplicationContext(), additionalRes.class);
            startActivity(additionalRes);
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to reset user to zero again
    private void resetUser(){
        //Setup firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        //Get calender time
        final Calendar cal = Calendar.getInstance();
        cal.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        myFirebaseRef.child(ACCOUNT).child(account_id).child(MONEY_COUNT).setValue(0);
        myFirebaseRef.child(ACCOUNT).child(account_id).child(START_DATE).setValue(cal.getTime().toString());
    }

    private String getPhoneId(){
        // Get android_id
        final String phone_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return phone_id;
    }

    //Method to round. Used for the rounding on money count
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    /**
     *  Allows the user to add a sponsor number, in which they can immediately call their sponsor from
     *
     */
    private void addSponsorNumberAction(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogalert_sponsornumber, null);
        alert.setView(dialogView);

        final EditText sponsorInput = (EditText) dialogView.findViewById(R.id.numberBox);
        alert.setTitle("Please add your sponsors number");

        alert.setPositiveButton("Yes, Add Sponsor", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                String sponsornumber = sponsorInput.getText().toString();
                SharedPreferences.Editor editor = getSharedPreferences(PREF_FILE, MODE_PRIVATE).edit();
                editor.putString(SPONSORCALL_REF, sponsornumber);
                editor.commit();
            }
        });

        alert.setNegativeButton("No, Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

}
