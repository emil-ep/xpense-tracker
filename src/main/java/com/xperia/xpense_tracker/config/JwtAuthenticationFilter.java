package com.xperia.xpense_tracker.config;

import com.xperia.xpense_tracker.models.response.ErrorResponse;
import com.xperia.xpense_tracker.services.JwtService;
import com.xperia.xpense_tracker.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    private static final Pattern AUTH_PATTERN = Pattern.compile("/v1/auth/.*");

    private static final Pattern ACTUATOR_EXTRAS_PATTERN = Pattern.compile("/actuator/.*");
    private static final Pattern ACTUATOR_PATTERN = Pattern.compile("/actuator");
    //TODO this should be removed once the ai is integrated within product
    private static final Pattern MCP_SERVER_PATTERN = Pattern.compile("/v1/mcp");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (AUTH_PATTERN.matcher(request.getRequestURI()).matches()
                || ACTUATOR_PATTERN.matcher(request.getRequestURI()).matches()
                || ACTUATOR_EXTRAS_PATTERN.matcher(request.getRequestURI()).matches()
                || MCP_SERVER_PATTERN.matcher(request.getRequestURI()).matches()

        ) {
            filterChain.doFilter(request, response); // Proceed with the filter chain
            return;
        }

        if(StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ErrorResponse("No Authentication provided").toString());
            return;
        }
        jwt = authHeader.substring(7);
        try{
            userEmail = jwtService.extractUserName(jwt);
        }catch (Exception ex){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ErrorResponse("Invalid or expired JWT token").toString());
            return;
        }

        if (StringUtils.isNotEmpty(userEmail)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService()
                    .loadUserByUsername(userEmail);
            if(!jwtService.isValidToken(jwt, userDetails)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(new ErrorResponse("Invalid or expired JWT token").toString());
                return;
            }
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
