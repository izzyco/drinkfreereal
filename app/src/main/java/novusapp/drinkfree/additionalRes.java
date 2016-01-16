package novusapp.drinkfree;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class additionalRes extends Activity {
    private String TWELVE_STEPS_RESOURCE = "<a href='http://www.aa.org/pages/en_US/twelve-steps-and-twelve-traditions'> 12 Steps Resource </a>";
    private String AA_RESOURCE = "<a href='http://www.aa.org/'> Alcohol Anonymous </a>";
    private String LOCAL_AA_RESOURCE = "<a href='http://alcoholicsanonymous.com/'> Local Alcohol Anonymous</a>";
    private String RELAPSE_RESOURCE = "<a href='http://alcoholicsanonymous.com/what-should-i-do-if-i-relapse-during-aa/'>What to do if I relapse?</a>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_res);

        TextView resource1 = (TextView) findViewById(R.id.addResource1);
        TextView resource2 = (TextView) findViewById(R.id.addResource2);
        TextView resource3 = (TextView) findViewById(R.id.addResource3);
        TextView resource4 = (TextView) findViewById(R.id.addResource4);

        resource1.setClickable(true);
        resource1.setMovementMethod(LinkMovementMethod.getInstance());
        resource1.setText(Html.fromHtml(AA_RESOURCE));

        resource2.setClickable(true);
        resource2.setMovementMethod(LinkMovementMethod.getInstance());
        resource2.setText(Html.fromHtml(TWELVE_STEPS_RESOURCE));

        resource3.setClickable(true);
        resource3.setMovementMethod(LinkMovementMethod.getInstance());
        resource3.setText(Html.fromHtml(LOCAL_AA_RESOURCE));

        resource4.setClickable(true);
        resource4.setMovementMethod(LinkMovementMethod.getInstance());
        resource4.setText(Html.fromHtml(RELAPSE_RESOURCE));


    }

}
