package com.example.studycard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studycard.databinding.ActivityInterstitialAdBinding;
import com.example.studycard.objects.Card;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;

import java.util.ArrayList;

public class InterstitialAdActivity extends AppCompatActivity {
    @Nullable
    private InterstitialAd mInterstitialAd = null;
    @Nullable
    private InterstitialAdLoader mInterstitialAdLoader = null;
    private ActivityInterstitialAdBinding mBinding;

    public static String message;
    public  static String topic_id ;
    public  static String degree;
    ArrayList<Card> cards = new ArrayList<Card>();

    public InterstitialAdActivity() {
        super(R.layout.activity_interstitial_ad);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityInterstitialAdBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        Bundle arguments = getIntent().getExtras();
        topic_id = arguments.getString("topic_id");
        degree = arguments.getString("degree");
        // Interstitial ads loading should occur after initialization of the SDK.
        // Initialize SDK as early as possible, for example in Application.onCreate or Activity.onCreate
        mInterstitialAdLoader = new InterstitialAdLoader(this);
        mInterstitialAdLoader.setAdLoadListener(new InterstitialAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                showAd();
                // The ad was loaded successfully. Now you can show loaded ad.
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
            }
        });
        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        if (mInterstitialAdLoader != null ) {
            final AdRequestConfiguration adRequestConfiguration =
                    new AdRequestConfiguration.Builder("R-M-14087555-1").build();
            mInterstitialAdLoader.loadAd(adRequestConfiguration);
        }
    }
    public void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdEventListener(new InterstitialAdEventListener() {
                @Override
                public void onAdShown() {
                    // Called when ad is shown.
                }

                @Override
                public void onAdFailedToShow(@NonNull final AdError adError) {
                    // Called when an InterstitialAd failed to show.
                    destroyInterstitialAd();
                }

                @Override
                public void onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    if (mInterstitialAd != null) {
                        mInterstitialAd.setAdEventListener(null);
                        mInterstitialAd = null;
                    }
                    // Now you can preload the next interstitial ad.
                    //loadInterstitialAd();

                    Intent intent = new Intent(InterstitialAdActivity.this, EndTestActivity.class);
                    intent.putExtra("degree",degree);
                    intent.putExtra("topic_id",topic_id);
                    startActivity(intent);


                }

                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                }

                @Override
                public void onAdImpression(@Nullable final ImpressionData impressionData) {
                    // Called when an impression is recorded for an ad.
                }
            });
            mInterstitialAd.show(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInterstitialAdLoader != null) {
            mInterstitialAdLoader.setAdLoadListener(null);
            mInterstitialAdLoader = null;
        }
        destroyInterstitialAd();
    }

    private void destroyInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdEventListener(null);
            mInterstitialAd = null;
        }
    }



}
