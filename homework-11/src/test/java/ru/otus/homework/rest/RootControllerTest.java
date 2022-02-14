package ru.otus.homework.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(RootController.class)
@DisplayName("Root контроллер должен ")
class RootControllerTest {

    @Autowired
    private WebTestClient client;

    @DisplayName("Возвращать пустую html страницу с <div id=\"root\">")
    @Test
    public void shouldReturnRootPage() throws Exception {
        client.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().toString().contains("id=\"root\"");

    }

}