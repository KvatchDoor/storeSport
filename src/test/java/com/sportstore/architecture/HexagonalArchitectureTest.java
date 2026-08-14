package com.sportstore.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifie le sens des dependances impose par l'architecture hexagonale, par analyse des imports
 * du code source. Aucun framework requis : ce test tourne sans contexte Spring.
 */
class HexagonalArchitectureTest {

    private static final Path SOURCES = Path.of("src", "main", "java", "com", "sportstore");

    private static final List<String> TECHNICAL_PACKAGES = List.of(
            "org.springframework",
            "jakarta.persistence",
            "jakarta.validation",
            "org.hibernate",
            "com.fasterxml.jackson",
            "tools.jackson"
    );

    @Test
    @DisplayName("le domaine ne depend d'aucun framework technique")
    void domainHasNoTechnicalDependency() {
        List<String> violations = violations(SOURCES.resolve("domain"), TECHNICAL_PACKAGES);

        assertThat(violations)
                .as("domain/ doit compiler sans Spring, JPA, Jackson ni Hibernate")
                .isEmpty();
    }

    @Test
    @DisplayName("le domaine ne connait ni l'application ni l'infrastructure")
    void domainDependsOnNothingElse() {
        List<String> violations = violations(SOURCES.resolve("domain"),
                List.of("com.sportstore.application", "com.sportstore.infrastructure"));

        assertThat(violations).as("le sens de dependance est infrastructure -> application -> domain").isEmpty();
    }

    @Test
    @DisplayName("l'application ne connait pas l'infrastructure")
    void applicationDoesNotDependOnInfrastructure() {
        List<String> violations = violations(SOURCES.resolve("application"),
                List.of("com.sportstore.infrastructure"));

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("l'application n'expose aucun type technique de persistance ou de web")
    void applicationHasNoTechnicalLeak() {
        List<String> violations = violations(SOURCES.resolve("application"),
                List.of("jakarta.persistence", "org.springframework.web", "org.springframework.data", "java.sql"));

        assertThat(violations)
                .as("les services applicatifs passent par les ports, jamais par Spring Data ni par l'API web")
                .isEmpty();
    }

    @Test
    @DisplayName("les ports sont des interfaces sans annotation de framework")
    void portsAreFrameworkFree() {
        List<String> violations = violations(SOURCES.resolve("application").resolve("port"), TECHNICAL_PACKAGES);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("aucune classe du domaine n'est annotee par un stereotype technique")
    void domainHasNoStereotypeAnnotation() {
        List<String> forbidden = List.of("@Entity", "@Table", "@Service", "@Component", "@Repository", "@Autowired");
        List<String> violations = new ArrayList<>();

        for (Path source : javaFiles(SOURCES.resolve("domain"))) {
            String content = read(source);
            forbidden.stream()
                    .filter(content::contains)
                    .forEach(annotation -> violations.add(source + " utilise " + annotation));
        }

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("les adaptateurs vivent dans infrastructure/, jamais dans application/")
    void adaptersLiveInInfrastructure() {
        List<String> violations = javaFiles(SOURCES.resolve("application")).stream()
                .map(Path::toString)
                .filter(path -> path.contains("Controller") || path.contains("Jpa") || path.contains("adapter"))
                .toList();

        assertThat(violations).isEmpty();
    }

    private List<String> violations(Path root, List<String> forbiddenPrefixes) {
        List<String> violations = new ArrayList<>();

        for (Path source : javaFiles(root)) {
            read(source).lines()
                    .map(String::strip)
                    .filter(line -> line.startsWith("import "))
                    .map(line -> line.substring("import ".length()).replace("static ", ""))
                    .forEach(imported -> forbiddenPrefixes.stream()
                            .filter(imported::startsWith)
                            .forEach(prefix -> violations.add(source + " importe " + imported)));
        }

        return violations;
    }

    private List<Path> javaFiles(Path root) {
        assertThat(root).as("repertoire source analyse").exists();

        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(path -> path.toString().endsWith(".java")).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String read(Path source) {
        try {
            return Files.readString(source);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
