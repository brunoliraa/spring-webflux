package com.br.springwebflux.controller;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import com.br.springwebflux.service.MovieService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@AllArgsConstructor
@RestController
@RequestMapping("movies")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public Flux<Movie> findAll(){
        return movieService.findAll();
    }
}
