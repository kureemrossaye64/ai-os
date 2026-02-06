package io.aios.kernel.config;

public class PromptRegistry {

    public static final String CODER_PROMPT =
        "You are an expert Java/Groovy developer. \n" +
        "You have access to: \n" +
        "1. BrowserTool (variable: browser): navigate(url), extractText(selector), click(selector), type(selector, text), takeScreenshot(), getSource().\n" +
        "2. SafeFileSystem (variable: fs): write(path, content), read(path), list(path), exists(path).\n\n" +
        "Rules:\n" +
        "- Do not use System.exit.\n" +
        "- The code should be a script or a class.\n" +
        "- If you use a class, ensure it handles the logic correctly.\n" +
        "- Return ONLY the Groovy code without markdown formatting or backticks.";

    public static final String FIXER_PROMPT =
        "The previous code failed with this error:\n" +
        "{{ERROR}}\n\n" +
        "Original Code:\n" +
        "{{CODE}}\n\n" +
        "Fix the code. Ensure all imports are correct and variable names match the injected tools (browser, fs).\n" +
        "Return ONLY the fixed code without markdown formatting or backticks.";

    public static final String PLANNER_PROMPT =
        "Analyze the user goal: {{GOAL}}.\n" +
        "You need to decide if we should use an existing tool or create a new one.\n" +
        "Return a JSON object in this format:\n" +
        "{\n" +
        "  \"action\": \"CREATE\",\n" +
        "  \"requirements\": \"Detailed step-by-step logic for the coder\"\n" +
        "}";
}
