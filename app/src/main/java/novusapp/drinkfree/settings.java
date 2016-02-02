/*
    settings.java: Will be useful later on with the additions of friends and family who will be able to track the user
    Author: Ivan Zhang
    Company: Novusapp.com

*/

package novusapp.drinkfree;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;


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
