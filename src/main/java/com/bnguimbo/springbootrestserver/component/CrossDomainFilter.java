package com.bnguimbo.springbootrestserver.component;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// ce filtre permet d'autoriser les requêtes Cross Domain (requêtes entre deux réseaux distincts)
//plus d'explications ici: http://sqli.developpez.com/tutoriels/spring/construire-backend-springboot/
@Component
public class CrossDomainFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final FilterChain filterChain)
			throws ServletException, IOException {
		httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");//toutes les URI sont autorisées
   		httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
   		httpServletResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-req");

   		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}