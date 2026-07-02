package com.mycompany.bancoadn.cliente.httpapi.endpoint;

import com.mycompany.bancoadn.cliente.httpapi.dto.AuthDto;
import com.mycompany.bancoadn.cliente.httpapi.service.AuthService;
import com.mycompany.bancoadn.cliente.httpapi.service.CreateAccountResult;
import io.javalin.Javalin;

public class AuthEndpoints {

    public static void register(Javalin app) {
        app.post("/api/auth/login", ctx -> {
            AuthDto.LoginRequest req = ctx.bodyAsClass(AuthDto.LoginRequest.class);
            var user = AuthService.login(req.getEmail(), req.getPassword());
            if (user == null) {
                ctx.status(401).result("Invalid email or password");
                return;
            }
            // Build response DTO
            AuthDto.LoginResponse res = new AuthDto.LoginResponse(
                    user.getEmail(),
                    user.getNombreCuenta(),
                    user.getIdRol()
            );
            // Note: idCuenta is not available from login, so we leave it as -1 (default in constructor)
            ctx.json(res);
        });

        app.post("/api/auth/crear-cuenta", ctx -> {
            AuthDto.CreateAccountRequest req = ctx.bodyAsClass(AuthDto.CreateAccountRequest.class);
            var result = AuthService.crearCuenta(
                    req.getEmail(),
                    req.getNombreCuenta(),
                    req.getPassword()
            );
            if (result.success) {
                var res = new AuthDto.CreateAccountResponse(true, result.message);
                ctx.status(201).json(res);
            } else {
                var res = new AuthDto.CreateAccountResponse(false, result.message);
                ctx.status(400).json(res);
            }
        });
    }
}
