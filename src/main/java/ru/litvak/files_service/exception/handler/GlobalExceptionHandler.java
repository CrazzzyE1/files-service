package ru.litvak.files_service.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.litvak.files_service.exception.NotFoundException;
import ru.litvak.files_service.exception.RequestParameterException;
import ru.litvak.files_service.exception.dto.ServerErrorDto;
import ru.litvak.files_service.exception.dto.ValidationErrorDto;
import ru.litvak.files_service.exception.dto.ValidationErrorFieldDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ValidationErrorDto handle(MethodArgumentNotValidException exception) {
		log.debug("Validation exception", exception);
		final List<ValidationErrorFieldDto> errors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(e -> new ValidationErrorFieldDto(e.getField(), e.getDefaultMessage()))
				.toList();
		return new ValidationErrorDto()
				.setMessage("The request contains incorrect data")
				.setFields(errors);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ValidationErrorDto handle(MissingServletRequestParameterException exception) {
		log.debug("Validation exception", exception);
		return new ValidationErrorDto()
				.setMessage("The request contains incorrect data")
				.setFields(List.of(new ValidationErrorFieldDto(exception.getParameterName(), "Required parameter is not specified")));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ValidationErrorDto handle(HttpMessageNotReadableException exception) {
		log.debug("Validation exception", exception);
		Throwable cause = exception.getCause();
		List<ValidationErrorFieldDto> errors = new ArrayList<>();
		if (cause instanceof JsonMappingException mappingException) {
			mappingException.getPath()
					.stream()
					.map(JsonMappingException.Reference::getFieldName)
					.filter(Objects::nonNull)
					.forEach(field -> errors.add(new ValidationErrorFieldDto(field, "Error JSON parsing")));
		}
		return new ValidationErrorDto()
				.setMessage("The request contains incorrect data")
				.setFields(errors);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(RequestParameterException.class)
	public ValidationErrorDto handle(RequestParameterException exception) {
		log.debug("Validation exception", exception);
		return new ValidationErrorDto()
				.setMessage("The request contains incorrect data")
				.setFields(List.of(new ValidationErrorFieldDto(exception.getField(), exception.getMessage())));
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ServerErrorDto handle(NotFoundException exception) {
		log.debug("Not found exception", exception);
		return new ServerErrorDto()
				.setMessage(exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RuntimeException.class)
	public ServerErrorDto handle(Exception exception) {
		log.error("Server error", exception);
		return new ServerErrorDto()
				.setMessage(exception.getMessage());
	}
}
