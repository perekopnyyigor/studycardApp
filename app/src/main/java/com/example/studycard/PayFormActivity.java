package com.example.studycard;

import android.os.Bundle;
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
        webSettings.setJavaScriptEnabled(true); // Включение JavaScript (может потребоваться для ЮKassa)
        webSettings.setDomStorageEnabled(true); // Включение DOM Storage

        // Настройка WebViewClient для обработки переходов внутри WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Обработка переходов по ссылкам (оставляем в WebView)
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Можно добавить обработку завершения загрузки
                super.onPageFinished(view, url);

                // Пример: проверка успешной оплаты по URL
                if (url.contains("success")) {
                    // Оплата успешна
                }
            }
        });

        // Загружаем URL платежа
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