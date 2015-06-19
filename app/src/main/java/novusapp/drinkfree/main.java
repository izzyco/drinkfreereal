package novusapp.drinkfree;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/*  main.java
    Contains all of the necessary visuals to show users the amount of money saved from not drinking, a random fact about drinking, and the amount of time
    that has passed since the user has stopped drinking.

    TODO: 1) Add content into the database on firebase. Data on how much each drink cost on average, random facts about alcahol
          2) Get the content and display them onto this app
 */


public class main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup firebase
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://drinkfreeapp.firebaseio.com/");

        // initialize text fields
        TextView tipText = (TextView) this.findViewById(R.id.tipText);
        TextView dateText = (TextView) this.findViewById(R.id.dateText);
        TextView countText = (TextView) this.findViewById(R.id.dateText);

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.child("accountdata").child("startdata").getValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

}
