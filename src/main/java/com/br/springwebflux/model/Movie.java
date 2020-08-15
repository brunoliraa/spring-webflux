package com.br.springwebflux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With //retorna setters que geram objetos imutáveis
@Table("movie")
public class Movie {

    @Id
    private Long id;
    @NotNull
    @NotEmpty(message = "title of the movie cannot be null")
    private String title;
}
