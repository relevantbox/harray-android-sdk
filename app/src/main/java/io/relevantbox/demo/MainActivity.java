package io.relevantbox.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.relevantbox.android.RB;
import io.relevantbox.android.common.ResultConsumer;
import io.relevantbox.android.model.RBEvent;
import io.relevantbox.android.utils.RBLogger;
import io.relevantbox.fcmkit.FcmKitPlugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("Relevantbox", "Source:" + intent.getStringExtra("source"));
        Log.d("Relevantbox", "Realty List:" + intent.getStringExtra("realty_list"));
        RB.plugins().get(FcmKitPlugin.class).pushMessageOpened(intent);
        RB.plugins().get(FcmKitPlugin.class).resetBadgeCounts(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RB.login("asdasdassdfds");
        /**RB.pushMessagesHistory().getPushMessagesHistory(10, new ResultConsumer<List<Map<String, String>>>() {
            @Override
            public void consume(List<Map<String, String>> data) {

            }
        });**/
        RB.eventing().pageView("homePage");

        //RB.eventing().actionResult("test Action");
        //RB.eventing().impression("productdetail");
        //RB.eventing().custom("customEvent", new HashMap<String, Object>());

        //RB.ecommerce().productView("1003", "small", 200d, 180d, "USD", null, "https://commercedemo.relevantbox.io/proteus-fitness-jackshirt.html");
        /**RB.recommendations().getRecommendations("boxId", null, 4, new ResultConsumer<List<Map<String, String>>>() {
            @Override
            public void consume(List<Map<String, String>> data) {
                RBLogger.log("Reco data is here! : " + data);
            }
        });
        RB.browsingHistory().getBrowsingHistory("listings", 10, new ResultConsumer<List<Map<String, String>>>() {
            @Override
            public void consume(List<Map<String, String>> data) {
                RBLogger.log("BrowsingHistory data is here! : " + data);
            }
        });
        RB.pushMessagesHistory().getPushMessagesHistory(10, new ResultConsumer<List<Map<String, String>>>() {
            @Override
            public void consume(List<Map<String, String>> data) {
                RBLogger.log("PushMessagesHistory data is here! : " + data);
            }
        });**/

        Intent intent = getIntent();
        Log.d("Relevantbox", "Source:" + intent.getStringExtra("source"));
        Log.d("Relevantbox", "Realty List:" + intent.getStringExtra("realty_list"));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        RB.plugins().get(FcmKitPlugin.class).savePushToken(task.getResult().getToken());
                    }
                });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RB.logout();
                RB.eventing().pageView("homePage");
                RB.eventing().actionResult("click");
                Snackbar.make(view, "Replace with your own action 2", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
