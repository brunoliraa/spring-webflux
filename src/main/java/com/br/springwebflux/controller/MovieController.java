package com.br.springwebflux.controller;

import com.br.springwebflux.model.Movie;
import com.br.springwebflux.repository.MovieRepository;
import com.br.springwebflux.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
@SecurityScheme(
        name = "Basic Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all movies",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movies"})
    public Flux<Movie> findAll(){
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movie"})
    public Mono<Movie> findById(@PathVariable Long id){
        return movieService.findById(id);

    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/title/{title}")
    @Operation(
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movie"})
    public Mono<Movie> findByTitle(@PathVariable  String title){
        return movieService.findByTitle(title);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movie"})
    public Mono<Movie> save(@Valid @RequestBody Movie movie){
        return movieService.save(movie);
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movie"})
    public Flux<Movie> saveBatch(@RequestBody List<Movie> movies){
        return movieService.saveAll(movies);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movie"})
    public Mono<Void> update(@PathVariable Long id, @Valid @RequestBody Movie movie){
        return movieService.update(movie.withId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"movie"})
    public Mono<Void> delete(@PathVariable Long id){
        return movieService.delete(id);
    }
}
