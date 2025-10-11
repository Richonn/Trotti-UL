package ca.ulaval.glo4003.trotti;

import ca.ulaval.glo4003.Main;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {
    @Test
    void startServer_shouldNotReturnNull() {
        HttpServer server = Main.startServer();
        assertThat(server).isNotNull();
        server.shutdownNow();
    }

    @Test
    void startServer_shouldStartWithoutException() {
        assertDoesNotThrow(() -> {
            HttpServer server = Main.startServer();
            server.shutdownNow();
        });
    }

    @Test
    void baseUri_shouldBeCorrect() {
        assertThat(Main.BASE_URI).isEqualTo("http://0.0.0.0:8080/api/");
    }
}
