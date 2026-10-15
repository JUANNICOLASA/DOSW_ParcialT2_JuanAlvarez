package edu.dosw.parcial.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filtro;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deberiaAutenticarConTokenValido() throws Exception {
        // Arrange
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("adoptante@dosw.local");
        when(claims.get("rol", String.class)).thenReturn("ADOPTANTE");
        when(jwtService.validarToken("token-valido")).thenReturn(claims);
        request.addHeader("Authorization", "Bearer token-valido");

        // Act
        filtro.doFilterInternal(request, response, chain);

        // Assert
        Authentication autenticacion =
                SecurityContextHolder.getContext().getAuthentication();
        assertEquals("adoptante@dosw.local", autenticacion.getName());
        assertEquals("ROLE_ADOPTANTE",
                autenticacion.getAuthorities().iterator().next().getAuthority());
        verify(chain).doFilter(request, response);
    }

    @Test
    void noDeberiaAutenticarConTokenInvalido() throws Exception {
        // Arrange
        when(jwtService.validarToken("token-malo"))
                .thenThrow(new JwtException("firma inválida"));
        request.addHeader("Authorization", "Bearer token-malo");

        // Act
        filtro.doFilterInternal(request, response, chain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void noDeberiaValidarSiNoHayEncabezado() throws Exception {
        // Act
        filtro.doFilterInternal(request, response, chain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).validarToken(any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void noDeberiaValidarSiElEncabezadoNoEsBearer() throws Exception {
        // Arrange
        request.addHeader("Authorization", "Basic dXN1YXJpbzpjbGF2ZQ==");

        // Act
        filtro.doFilterInternal(request, response, chain);

        // Assert
        verify(jwtService, never()).validarToken(any());
        verify(chain).doFilter(request, response);
    }
}
