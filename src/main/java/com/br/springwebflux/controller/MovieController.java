package com.br.springwebflux.controller;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import com.br.springwebflux.service.MovieService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("movies")
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasRole('ADMIN')")
    public Flux<Movie> findAll(){
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Movie> findById(@PathVariable Long id){
        return movieService.findById(id);

    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/title/{title}")
    public Mono<Movie> findByTitle(@PathVariable  String title){
        return movieService.findByTitle(title);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Movie> save(@Valid @RequestBody Movie movie){
        return movieService.save(movie);
    }
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<Movie> saveBatch(@RequestBody List<Movie> movies){
        return movieService.saveAll(movies);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable Long id, @Valid @RequestBody Movie movie){
        return movieService.update(movie.withId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id){
        return movieService.delete(id);
    }
}
