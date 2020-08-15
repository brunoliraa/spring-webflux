package com.br.springwebflux.util;

import com.br.springwebflux.model.Movie;

public class MovieCreator {

    public static Movie createMovieToSave(){
        return Movie.builder()
                .title("the lord of the rings").build();
    }

    public static Movie createValidMovie(){
        return Movie.builder()
                .id(1L)
                .title("the lord of the rings").build();
    }

    public static Movie createValidUpdateMovie(){
        return Movie.builder()
                .id(1L)
                .title("the lord of the rings the return of the king").build();
    }
}
