package io.relevantbox.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

import java.util.HashMap;

import io.relevantbox.android.RB;
import io.relevantbox.android.utils.RBLogger;
import io.relevantbox.hmskit.HmsKitPlugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("Xennio", "Source:" + intent.getStringExtra("source"));
        Log.d("Xennio", "Realty List:" + intent.getStringExtra("realty_list"));
        RB.plugins().get(HmsKitPlugin.class).pushMessageOpened(intent);
        RB.plugins().get(HmsKitPlugin.class).resetBadgeCounts(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RB.logout();
        RB.login("300");

        RB.eventing().pageView("homePage");
        RB.eventing().actionResult("test Action");
        RB.eventing().impression("productdetail");
        RB.eventing().custom("customEvent", new HashMap<String, Object>());

        RB.ecommerce().productView("1003", "small", 200d, 180d, "USD", null, "https://commercedemo.xenn.io/proteus-fitness-jackshirt.html");

        Intent intent = getIntent();
        Log.d("Xennio", "Source:" + intent.getStringExtra("source"));
        Log.d("Xennio", "Realty List:" + intent.getStringExtra("realty_list"));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // XennioAPI.pageView("300", "homePage", new HashMap<String, Object>());

        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this)
                            .getString("client/app_id");
                    String token = HmsInstanceId.getInstance(MainActivity.this)
                            .getToken(appId, "HCM");
                    RB.plugins().get(HmsKitPlugin.class).savePushToken(token);
                } catch (ApiException e) {
                    RBLogger.log("Receiving Token Failed: " + e.getMessage());
                }
            }
        }.start();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
