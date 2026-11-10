package com.gabrielf.revendedora_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);

    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErroResponse> handleEstoqueInsuficiente(
            EstoqueInsuficienteException ex, HttpServletRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Estoque insuficiente",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);

    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(
            RegraDeNegocioException ex, HttpServletRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Regra de negócio violada",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        ErroResponse erro = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                "Um ou mais campos estão inválidos",
                request.getRequestURI(),
                detalhes
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> handleIntegridade(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Violação de integridade em {} {}", request.getMethod(), request.getRequestURI(), ex);
        ErroResponse erro = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                "Operação não permitida",
                "Não foi possível concluir: o registro possui vendas ou reservas vinculadas.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGenerico(
            Exception ex, HttpServletRequest request) {
        log.error("Erro não tratado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        ErroResponse erro = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        ErroResponse erro = new ErroResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciais inválidas",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);

    }

}
