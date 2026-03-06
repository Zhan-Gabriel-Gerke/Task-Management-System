package ee.zhan.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
        ) {
            this.jwtService = jwtService;
            this.userDetailsService = userDetailsService;
            this.resolver = resolver;
        }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        //Try to take "Authorization" header from http
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check
        // If the header doesn't have a header or starts from Bearer
        // it means that the request doesn't try to log in with jwt
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //get jwt token
        jwt = authHeader.substring(7);
        try {
            //trying to get email
            //here may arise runtime exception if the signature is not valid
            userEmail = jwtService.extractUsername(jwt);

            // checks if the user is authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //load user details from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                //check if the token is valid
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create a user and roles
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // add to the object details about http request (ip-address or id of the session)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    //put our data into the context
                    //now all the controllers and services will count request as Auntheticated
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }


            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // expired token
            resolver.resolveException(request, response, null, new JwtAuthException("Token has expired"));
        } catch (JwtException | IllegalArgumentException e) {
            // invalid token
            resolver.resolveException(request, response, null, new JwtAuthException("Invalid token"));
        }
    }
}
