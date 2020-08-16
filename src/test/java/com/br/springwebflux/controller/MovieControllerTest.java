package com.br.springwebflux.controller;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import com.br.springwebflux.service.MovieService;
import com.br.springwebflux.util.MovieCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) //pro JUnit 5
class MovieControllerTest {


    @InjectMocks //usado para classe target do teste
    private MovieController movieController;

    @Mock
    private MovieService movieServiceMock;

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
        BDDMockito.when(movieServiceMock.findAll())
                .thenReturn(Flux.just(movie));

        BDDMockito.when(movieServiceMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(movie));

        BDDMockito.when(movieServiceMock.save(MovieCreator.createMovieToSave()))
                .thenReturn(Mono.just(movie));
        BDDMockito.when(movieServiceMock
                .saveAll(Arrays.asList(MovieCreator.createMovieToSave(), MovieCreator.createMovieToSave())))
                .thenReturn(Flux.just(movie, movie));

        BDDMockito.when(movieServiceMock.delete(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());

        BDDMockito.when(movieServiceMock.update(MovieCreator.createValidMovie()))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findAll returns a flux of movie")
    public void findAll_ReturnFluxOfMovie_When_Successful(){
        //Step Verifier da um .subscribe no Flux
        StepVerifier.create(movieServiceMock.findAll())
                .expectSubscription()
                .expectNext(movie).
                verifyComplete();
    }

    @Test
    @DisplayName("findById returns a Mono with a movie when it exists")
    public void findById_ReturnMonoMovie_WhenSuccessful() {
        StepVerifier.create(movieController.findById(1L))
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("save creates a movie when successful")
    public void save_CreatesMovie_WhenSuccessful() {
        Movie movieToSave = MovieCreator.createMovieToSave();

        StepVerifier.create(movieController.save(movieToSave))
                .expectSubscription()
                .expectNext(movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("saveBatch creates a list of movie when successful")
    public void saveBatch_CreatesListOfMovie_WhenSuccessful() {
        Movie movieToSave = MovieCreator.createMovieToSave();

        StepVerifier.create(movieController.saveBatch(Arrays.asList(movieToSave, movieToSave)))
                .expectSubscription()
                .expectNext(movie, movie)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete removes the movie when successful")
    public void delete_RemovesMovie_WhenSuccessful() {
        StepVerifier.create(movieController.delete(1L))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update save updated a movie and returns empty mono when successful")
    public void update_SaveUpdatedMovie_WhenSuccessful() {
        StepVerifier.create(movieController.update(1L, MovieCreator.createValidMovie()))
                .expectSubscription()
                .verifyComplete();
    }



}