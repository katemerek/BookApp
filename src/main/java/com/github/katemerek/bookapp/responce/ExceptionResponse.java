package com.github.katemerek.bookapp.responce;

import java.time.LocalDateTime;

public record ExceptionResponse(LocalDateTime timestamp, int status, String error, String message) {}
