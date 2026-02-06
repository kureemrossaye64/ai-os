package io.aios.worker;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BrowserService {

    private Playwright playwright;
    private Browser browser;

    @Value("${ai.worker.browser.headless:true}")
    private boolean headless;

    @PostConstruct
    public void init() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    public BrowserTool createContext() {
        com.microsoft.playwright.BrowserContext context = browser.newContext();
        com.microsoft.playwright.Page page = context.newPage();
        return new BrowserTool(context, page);
    }

    @PreDestroy
    public void cleanup() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
