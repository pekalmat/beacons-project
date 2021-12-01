package ch.zhaw.integration.beacons.security;

import ch.zhaw.integration.beacons.entities.person.Person;
import ch.zhaw.integration.beacons.entities.person.PersonRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final PersonRepository personRepository;

    public JWTAuthorizationFilter(
            JwtTokenUtil jwtTokenUtil,
            PersonRepository personRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.personRepository = personRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        // Add necessary response headers
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader("Access-Control-Allow-Headers", "Authorization");
        // Get authorization header and validate, return if token is not valid
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ") || header.endsWith("null")) {
            chain.doFilter(request, response);
            return;
        }
        // Get user identity
        final String token = header.split(" ")[1].trim();
        Person userDetails = personRepository
                .findByEmail(jwtTokenUtil.getUserEmailFromToken(token))
                .orElse(null);
        // validate JWT token
        if (!jwtTokenUtil.validateToken(token, userDetails)) {
            chain.doFilter(request, response);
            return;
        }
        // set user identity on the spring security context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ? Collections.EMPTY_LIST : userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // add new authorization token to every authenticated response
        response.addHeader(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateToken(userDetails.getEmail()));
        // continue
        chain.doFilter(request, response);
    }

}
