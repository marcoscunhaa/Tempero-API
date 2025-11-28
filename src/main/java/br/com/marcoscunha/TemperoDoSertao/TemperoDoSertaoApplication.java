package br.com.marcoscunha.TemperoDoSertao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class TemperoDoSertaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemperoDoSertaoApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080";

                // Tenta abrir no Windows
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    new ProcessBuilder("cmd", "/c", "start", "", url).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", url).start();
                } else if (os.contains("nix") || os.contains("nux")) {
                    new ProcessBuilder("xdg-open", url).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}


