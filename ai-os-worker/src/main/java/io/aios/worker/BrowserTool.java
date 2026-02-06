package io.aios.worker;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BrowserTool implements AutoCloseable {

    private final BrowserContext context;
    private final Page page;

    public void navigate(String url) {
        page.navigate(url);
    }

    public String extractText(String selector) {
        return page.locator(selector).innerText();
    }

    public void click(String selector) {
        page.click(selector);
    }

    public void type(String selector, String text) {
        page.fill(selector, text);
    }

    public byte[] takeScreenshot() {
        return page.screenshot();
    }

    public String getSource() {
        return page.content();
    }

    @Override
    public void close() {
        context.close();
    }
}
