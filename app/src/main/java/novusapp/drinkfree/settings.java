/*
    settings.java: Will be useful later on with the additions of friends and family who will be able to track the user
    Author: Ivan Zhang
    Company: Novusapp.com

*/

package novusapp.drinkfree;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;


public class settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }
    private String getPhoneId(){
        // Get android_id
        final String phone_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return phone_id;
    }

}
