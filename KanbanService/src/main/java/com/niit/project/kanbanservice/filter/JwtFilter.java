package com.niit.project.kanbanservice.filter;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;


public class JwtFilter extends GenericFilterBean {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String authenticationHeader = httpServletRequest.getHeader("Authorization");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer")) {
            servletOutputStream.print("Token is missing");
            servletOutputStream.close();
        } else {
            String jwtToken = authenticationHeader.substring("Bearer ".length());
            String email = Jwts.parser().setSigningKey("kanbankey").parseClaimsJws(jwtToken)
                    .getBody().getSubject();
            httpServletRequest.setAttribute("email", email);
        }

        chain.doFilter(request, response);
    }
}
