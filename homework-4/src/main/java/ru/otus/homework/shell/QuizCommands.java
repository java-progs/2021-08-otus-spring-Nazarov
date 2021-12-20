package ru.otus.homework.shell;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.homework.service.QuizService;

@ShellComponent
public class QuizCommands {

    private final QuizService quizService;

    private String userName;
    private boolean login;

    public QuizCommands(QuizService quizService) {
        this.quizService = quizService;
    }

    @ShellMethod(value = "Login command", key = {"l", "login"})
    @ShellMethodAvailability(value = "isLoginAvailability")
    public String login(@ShellOption String userName) {
        this.userName = userName;
        this.login = true;

        return String.format("You are login. Username: %s", userName);
    }

    @ShellMethod(value = "Logout command", key = {"logout"})
    @ShellMethodAvailability(value = "isAuthenticationCompleted")
    public String logout() {
        this.userName = null;
        this.login = false;

        return String.format("You are logout");
    }

    @ShellMethod(value = "Start quiz", key = {"s", "start"})
    @ShellMethodAvailability(value = "isAuthenticationCompleted")
    public void startQuiz() {
        quizService.startQuiz(userName);
    }

    private Availability isLoginAvailability() {
        return login == true ? Availability.unavailable("You are already login") : Availability.available();
    }

    private Availability isAuthenticationCompleted() {
        return login == false ? Availability.unavailable("You are not logged in") : Availability.available();
    }
}
