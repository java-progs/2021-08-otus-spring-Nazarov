package ru.otus.homework.actuator;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.homework.service.BookService;

@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookService bookService;

    @Override
    public Health health() {
        val bookResult = getLibraryStatus();
        if (bookResult > 0) {
            return Health.up().withDetail("countBook", bookResult).build();
        } else {
            return Health.down().withDetail("countBook", bookResult).build();
        }
    }

    private long getLibraryStatus() {
        return bookService.getCountBooks();
    }

}