package com.br.springwebflux.service;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    public Flux<Movie> findAll(){
        return movieRepository.findAll();
    }


}
