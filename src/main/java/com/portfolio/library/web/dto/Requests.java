package com.portfolio.library.web.dto;

import jakarta.validation.constraints.*;

public final class Requests {
    private Requests() {}

    public record NewBook(
            @NotBlank @Pattern(regexp = "^(97[89])?\\d{9}[\\dX]$", message = "must be a valid ISBN-10 or ISBN-13") String isbn,
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 120) String author,
            @Min(1450) @Max(2100) int publishedYear,
            @Min(1) @Max(100) int copies) {}

    public record NewMember(@NotBlank @Size(max = 100) String name, @NotBlank @Email String email) {}

    public record NewLoan(@NotNull Long memberId, @NotNull Long bookId) {}
}
