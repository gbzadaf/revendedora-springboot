package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.LoginRequest;
import com.gabrielf.revendedora_api.dto.LoginResponse;
import com.gabrielf.revendedora_api.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e geração de token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Operation(
            summary = "Autenticar e gerar token JWT",
            description = "Único endpoint público da API. O token retornado deve ser enviado no header " +
                    "'Authorization: Bearer {token}' em todas as demais requisições."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.login(), request.senha()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Login ou senha inválidos");
        }

        String token = jwtService.gerarToken(request.login());
        return ResponseEntity.ok(new LoginResponse(token));

    }
}
