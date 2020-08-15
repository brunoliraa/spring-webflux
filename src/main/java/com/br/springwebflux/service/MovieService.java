package com.br.springwebflux.service;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    public Flux<Movie> findAll(){
        return movieRepository.findAll();
    }

    public Mono<Movie> findById(Long id){
       return movieRepository.findById(id)
               .switchIfEmpty(monoResponseStatusNotFoundException());

    }

    public Mono<Movie> findByTitle(String title){
        return movieRepository.findMovieByTitle(title);
    }

    public Mono<Movie> save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Mono<Void> update(Movie movie) {
        return findById(movie.getId())
                .flatMap(movieRepository::save).then();

    }

    public Mono<Void> delete(Long id) {
        return findById(id)
                .switchIfEmpty(monoResponseStatusNotFoundException())
                .flatMap(movieRepository::delete)
                ;
    }


    public <T> Mono<T> monoResponseStatusNotFoundException(){
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"movie not found"));
    }




}
