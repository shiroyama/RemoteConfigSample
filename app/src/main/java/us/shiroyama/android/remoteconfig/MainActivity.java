package us.shiroyama.android.remoteconfig;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_COLOR = "color";

    private static final String KEY_BUTTON = "button";

    private FirebaseRemoteConfig remoteConfig;

    @BindView(R.id.container)
    View container;

    @BindView(R.id.button)
    Button button;

    @OnClick(R.id.button)
    void onClickButton(Button button) {
        fetch();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettings(remoteConfigSettings);
        remoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetch();
    }

    private void fetch() {
        long cacheExpiration = 3600;
        if (remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        remoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Fetch succeeded.");
                    remoteConfig.activateFetched();

                    String colorName = remoteConfig.getString(KEY_COLOR);
                    Log.d(TAG, "colorName: " + colorName);
                    container.setBackgroundColor(Color.parseColor(colorName));

                    String buttonLabel = remoteConfig.getString(KEY_BUTTON);
                    Log.d(TAG, "buttonLabel: " + buttonLabel);
                    button.setText(buttonLabel);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Fetch failed.", e));
    }
}
