package app.project_fin_d_etude;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// @Theme(value = "project-fin-d-etude") // Supprimé, déplacé dans AppShellConfig
@SpringBootApplication
@EnableAsync
public class ProjectFinDEtudeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectFinDEtudeApplication.class, args);
    }

}
