package ru.otus.homework.shell.utils;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;

public class ShellHelper {

    @Value("${shell.out.info}")
    public String infoColor;

    @Value("${shell.out.success}")
    public String successColor;

    @Value("${shell.out.warning}")
    public String warningColor;

    @Value("${shell.out.error}")
    public String errorColor;

    private Terminal terminal;

    public ShellHelper(Terminal terminal) {
        this.terminal = terminal;
    }

    public String getColored(String message, PromptColor color) {
        AttributedStringBuilder builder = new AttributedStringBuilder();
        String coloredString = builder.append(message, AttributedStyle.DEFAULT
                .foreground(color.toJlineAttributedStyle()))
                .toAnsi();

        return coloredString;
    }

    public String getInfoMessage(String message) {
        return getColored(message, PromptColor.valueOf(infoColor));
    }

    public String getSuccessMessage(String message) {
        return getColored(message, PromptColor.valueOf(successColor));
    }

    public String getWarningMessage(String message) {
        return getColored(message, PromptColor.valueOf(warningColor));
    }

    public String getErrorMessage(String message) {
        return getColored(message, PromptColor.valueOf(errorColor));
    }

    public void printInfo(String message) {
        print(getInfoMessage(message));
    }

    public void printSuccess(String message) {
        print(getSuccessMessage(message));
    }

    public void printWarning(String message) {
        print(getWarningMessage(message));
    }

    public void printError(String message) {
        print(getErrorMessage(message));
    }

    public void print(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }
}
