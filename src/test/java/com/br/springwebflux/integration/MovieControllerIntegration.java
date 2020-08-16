package com.br.springwebflux.integration;

import com.br.springwebflux.exception.CustomAttributes;
import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import com.br.springwebflux.service.MovieService;
import com.br.springwebflux.util.MovieCreator;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@ExtendWith(SpringExtension.class)
@WebFluxTest //vai inicializar o conteúdo relacionado ao webflux
@Import({MovieService.class, CustomAttributes.class})
public class MovieControllerIntegration {

    @MockBean
    private MovieRepository movieRepositoryMock;

    //biblioteca pra testes baseado no WebClient
    @Autowired
    private WebTestClient testClient;

    private final Movie movie = MovieCreator.createValidMovie();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0); //NOSONAR
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @BeforeEach
    public void setUp() {
        BDDMockito.when(movieRepositoryMock.findAll())
                .thenReturn(Flux.just(movie));

        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(movie));

        BDDMockito.when(movieRepositoryMock.save(MovieCreator.createMovieToSave()))
                .thenReturn(Mono.just(movie));

        BDDMockito.when(movieRepositoryMock.delete(ArgumentMatchers.any(Movie.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(movieRepositoryMock.save(MovieCreator.createValidMovie()))
                .thenReturn(Mono.empty());

    }

    //ResponseSpec é tipo o ResponseEntity
    @Test
    @DisplayName("listAll returns a flux of movie")
    public void listAll_Flavor2_ReturnFluxOfMovie_WhenSuccessful() {
        testClient
                .get()
                .uri("/movies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Movie.class)
                .hasSize(1)
                .contains(movie);
    }
    //outra forma de fazer o ListAll
    @Test
    @DisplayName("listAll returns a flux of movie")
    public void listAll_ReturnFluxOfMovie_WhenSuccessful() {
        testClient
                .get()
                .uri("/movies")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(movie.getId())
                .jsonPath("$.[0].title").isEqualTo(movie.getTitle());
    }

    @Test
    @DisplayName("findById returns a Mono with movie when it exists")
    public void findById_ReturnMonoMovie_WhenSuccessful() {
        testClient
                .get()
                .uri("/movies/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .isEqualTo(movie);
    }

    @Test
    @DisplayName("findById returns Mono error when movie does not exist")
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        testClient
                .get()
                .uri("/movies/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("save creates an movie when successful")
    public void save_CreatesMovie_WhenSuccessful() {
        Movie movieToSave = MovieCreator.createMovieToSave();

        testClient
                .post()
                .uri("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(movieToSave))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Movie.class)
                .isEqualTo(movie);
    }

    @Test
    @DisplayName("save returns mono error with bad request when title is empty")
    public void save_ReturnsError_WhenTitleIsEmpty() {
        Movie movieToSave = MovieCreator.createMovieToSave().withTitle("");

        testClient
                .post()
                .uri("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(movieToSave))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);

    }

    @Test
    @DisplayName("delete removes the movie when successful")
    public void delete_RemovesMovie_WhenSuccessful() {
        testClient
                .delete()
                .uri("/movies/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("delete returns Mono error when movie does not exist")
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        testClient
                .delete()
                .uri("/movies/{id}", 1)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("update save updated movie and returns empty mono when successful")
    public void update_SaveUpdatedMovie_WhenSuccessful() {
        testClient
                .put()
                .uri("/movies/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(movie))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("update returns Mono error when movie does not exist")
    public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        testClient.put()
                .uri("/movies/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(movie))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }
}
