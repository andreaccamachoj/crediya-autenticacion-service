package co.com.pragma.crediya.autenticacion.security;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.login.LoginResponse;
import co.com.pragma.crediya.model.tokensesion.TokenSesion;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionGateway;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenSesionGateway {

    private final TokenSesionRepository tokenSesionRepository;

    private final Key key = Keys.hmacShaKeyFor(
            "SJKFHGREDUIFG8GFER897ETUHE89G8TGRY7RJNBDKJGV8D76GSE89TH45T8967".getBytes(StandardCharsets.UTF_8)
    );

    private static final long TTL_SECONDS = 36000;

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private static @NonNull String maskToken(String token) {
        if (token == null) return "null";
        int n = Math.min(10, token.length());
        return token.substring(0, n) + "...";
    }

    @Override
    public Mono<TokenSesion> generarToken(Usuario u, String rolNombre) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp exp = new Timestamp(now.getTime() + TTL_SECONDS * 1000L);

        String jwt = Jwts.builder()
                .setSubject(u.getCorreoElectronico())
                .claim("uid", u.getIdUsuario())
                .claim("rol", u.getIdRol())
                .claim("rolName", rolNombre)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        TokenSesion ts = TokenSesion.builder()
                .idUsuario(u.getIdUsuario())
                .token(jwt)
                .fechaCreacion(now)
                .fechaExpiracion(exp)
                .indActivo(true)
                .build();

        return tokenSesionRepository.saveTokenSesion(ts);
    }

    @Override
    public Mono<LoginResponse> validarToken(String token) {
        final String tokenMask = maskToken(token);
        final AtomicLong startNs = new AtomicLong();

        return Mono.fromCallable(() -> {
                    return Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
                })
                .doOnSubscribe(s -> {
                    startNs.set(System.nanoTime());
                    log.info("[JWT] Validando token {}", tokenMask);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(claims -> {
                    if (claims != null) {
                        Object uid = claims.get("uid");
                        String sub = claims.getSubject();
                        log.debug("[JWT] Parse OK. subject={} uid={}", sub, uid);
                    }
                })
                .onErrorMap(ex -> {
                    log.warn("[JWT] Token inválido {}. Causa: {}", tokenMask, ex.toString());
                    return new BusinessException(BusinessExceptionMessage.INVALID_TOKEN);
                })
                .flatMap(claims -> tokenSesionRepository.findActiveByToken(token)
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("[JWT] Token no activo/registrado en BD {}", tokenMask);
                            return Mono.error(new BusinessException(BusinessExceptionMessage.INVALID_TOKEN));
                        }))
                        .flatMap(tsDb -> {
                            Timestamp now = new Timestamp(System.currentTimeMillis());
                            Timestamp exp = tsDb.getFechaExpiracion();

                            log.debug("[JWT] Sesión encontrada. idUsuario={} exp={}", tsDb.getIdUsuario(), exp);

                            if (exp != null && exp.before(now)) {
                                log.warn("[JWT] Token expirado {} (exp={})", tokenMask, exp);
                                return tokenSesionRepository.deactivateByToken(token)
                                        .doOnSuccess(v -> log.info("[JWT] Token marcado inactivo {}", tokenMask))
                                        .then(Mono.error(new BusinessException(BusinessExceptionMessage.SESSION_EXPIRED)));
                            }

                            LoginResponse u = new LoginResponse();
                            u.setIdUsuario(((Number) claims.get("uid")).longValue());
                            u.setCorreoElectronico(claims.getSubject());
                            u.setIdRol(((Number) claims.get("rol")).longValue());
                            u.setNombreRol((String) claims.get("rolName"));

                            log.info("[JWT] Token válido. usuario={} rol={}", u.getIdUsuario(), u.getNombreRol());
                            return Mono.just(u);
                        })
                )
                .doOnError(e ->
                        log.error("[JWT] Error validando token {} -> {}", tokenMask, e.toString())
                )
                .doFinally(sig -> {
                    long ms = (System.nanoTime() - startNs.get()) / 1_000_000L;
                    log.debug("[JWT] validarToken finalizó. señal={} dur={}ms", sig, ms);
                });
    }
}
