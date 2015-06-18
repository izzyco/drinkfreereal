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
