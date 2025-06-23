package com.example.studycard;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class PayFormActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_form);

        // Получаем URL из Intent (переданный из предыдущей активности)
        String paymentUrl = getIntent().getStringExtra("payment_url");
        webView = findViewById(R.id.webView);
        setupWebView(paymentUrl);
    }

    private void setupWebView(String url) {
        WebSettings webSettings = webView.getSettings();

        // Основные настройки WebView
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        // Разрешаем куки (важно для платежных систем)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().setAcceptCookie(true);

        // Настройка обработки URL
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            private boolean handleUrl(String url) {
                // Если это стандартный HTTP/HTTPS URL - загружаем в WebView
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false; // Продолжаем загрузку в WebView
                }

                // Если это кастомная схема (например, банковское приложение)
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true; // Прерываем загрузку в WebView
                } catch (ActivityNotFoundException e) {
                    // Если приложения для обработки схемы нет
                    Log.e("WebView", "Не удалось открыть URL: " + url);
                    return false;
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Проверяем URL на успешную оплату
                if (url.contains("your-site.ru/success")) {
                    // Оплата прошла успешно

                } else if (url.contains("your-site.ru/fail")) {
                    // Ошибка оплаты

                }
            }

        });

        // Загружаем начальный URL
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        // Обработка кнопки "Назад"
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}