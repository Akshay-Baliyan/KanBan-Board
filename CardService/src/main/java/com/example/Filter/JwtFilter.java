package com.example.Filter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;

public class JwtFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // Parse and validate the token and set the user id from claims in the request header as an attribute.
        String header = request.getHeader("Authorization");
        System.out.println(header);
        if (header == null || !header.startsWith("Bearer")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.println("Invalid or Missing Token");
        } else {
            String token = header.substring(7);
            System.out.println("token : " + token);
            Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody();
            System.out.println("userID"+claims.get("userId"));
            System.out.println(claims.get("userRole"));

            request.setAttribute("claims", claims);


            filterChain.doFilter(request, response);
        }
    }

}
