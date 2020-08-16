package com.br.springwebflux.service;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.br.springwebflux.util.MovieCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) //pro JUnit 5
class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepositoryMock;

    private final Movie movie = MovieCreator.createValidMovie();


    /*como está trabalhando de forma reativa, precisa se certificar
     que o código não está bloqueando nenhuma thread*/
    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install();
    }

    //Para ter certeza que o Blockhound está sendo carregado durante os testes
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
        BDDMockito.when(movieRepositoryMock
                .saveAll(Arrays.asList(MovieCreator.createMovieToSave(), MovieCreator.createMovieToSave())))
                .thenReturn(Flux.just(movie, movie));

        BDDMockito.when(movieRepositoryMock.delete(ArgumentMatchers.any(Movie.class)))
                .thenReturn(Mono.empty());

        BDDMockito.when(movieRepositoryMock.save(MovieCreator.createValidMovie()))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findAll returns a flux of movie")
    public void findAll_ReturnFluxOfMovie_When_Successful(){
        //Step Verifier da um .subscribe no Flux
        StepVerifier.create(movieService.findAll())
                .expectSubscription()
                .expectNext(movie).
                verifyComplete();

    }

    @Test
    @DisplayName("findById returns a Mono with a movie when it exists")
    public void findById_ReturnMonoMovie_WhenSuccessful() {
        StepVerifier.create(movieService.findById(1L))
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById returns Mono error when movie does not exist")
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(movieService.findById(1L))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("save creates a movie when successful")
    public void save_CreatesMovie_WhenSuccessful() {
        Movie movieToSave = MovieCreator.createMovieToSave();

        StepVerifier.create(movieService.save(movieToSave))
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("saveAll creates a list of movies when successful")
    public void saveAll_CreatesListOfMovies_WhenSuccessful() {
        Movie movieToSave = MovieCreator.createMovieToSave();

        StepVerifier.create(movieService.saveAll(Arrays.asList(movieToSave, movieToSave)))
                .expectSubscription()
                .expectNext(movie, movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("saveAll returns Mono error when one of the objects in the list contains empty or null title")
    public void saveAll_ReturnsMonoError_WhenContainsInvalidTitle() {
        Movie movieToSave = MovieCreator.createMovieToSave();

        BDDMockito.when(movieRepositoryMock
                .saveAll(ArgumentMatchers.anyIterable()))
                .thenReturn(Flux.just(movie, movie.withTitle("")));

        StepVerifier.create(movieService.saveAll(Arrays.asList(movieToSave, movieToSave.withTitle(""))))
                .expectSubscription()
                .expectNext(movie)
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("delete removes the movie when successful")
    public void delete_RemovesMovie_WhenSuccessful() {
        StepVerifier.create(movieService.delete(1L))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete returns Mono error when movie does not exist")
    public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(movieService.delete(1L))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("update save updated a movie and returns empty mono when successful")
    public void update_SaveUpdatedMovie_WhenSuccessful() {
        StepVerifier.create(movieService.update(MovieCreator.createValidMovie()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update returns Mono error when movie does not exist")
    public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(movieRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(movieService.update(MovieCreator.createValidMovie()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }
}