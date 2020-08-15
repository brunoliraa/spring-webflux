package com.br.springwebflux.repository;

import com.br.springwebflux.model.Movie;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MovieRepository extends ReactiveCrudRepository<Movie, Long> {

    Mono<Movie> findMovieByTitle(String title);
}
