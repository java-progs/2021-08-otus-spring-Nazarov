package ru.otus.homework.shell.utils;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;

public class ShellHelper {

    public final String infoColor;

    public final String successColor;

    public final String warningColor;

    public final String errorColor;

    private final Terminal terminal;

    public ShellHelper(Terminal terminal, String infoColor, String successColor, String warningColor, String errorColor) {
        this.terminal = terminal;
        this.infoColor = infoColor;
        this.successColor = successColor;
        this.warningColor = warningColor;
        this.errorColor = errorColor;
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
