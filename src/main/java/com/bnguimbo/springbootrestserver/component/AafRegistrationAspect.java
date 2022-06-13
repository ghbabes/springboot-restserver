/* Created on 31 mai 2022
 *
 * $HeadURL$
 * $Id$
 *
 * ----------------------------------------------------------------------------
 *
 * Copyright 2022 by ID Informatique et Développement SA.
 * Rue Numa-Droz 109, CH-2300 La Chaux-de-Fonds
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of ID Informatique et Développement SA.
 */
package com.bnguimbo.springbootrestserver.component;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Object for logging uses of application of user registration
 *
 * @author stephaneb
 */
@Aspect
@Component
public class AafRegistrationAspect {

	private static final Logger LOG = LoggerFactory.getLogger(AafRegistrationAspect.class);

	@Before("execution(public * com.bnguimbo.springbootrestserver.service.*Service*.*(..))")
	public void logService(final JoinPoint joinPoint) {
		LOG.info("Service : " + joinPoint.toShortString() + " with " + joinPoint.getArgs().length + " parameters");
	}

	@Before("execution(public * com.bnguimbo.springbootrestserver.controller.AafRegistrationUserController.*(..))")
	public void logController(final JoinPoint joinPoint) {
		LOG.info("Controller : " + joinPoint.toShortString() + " with " + joinPoint.getArgs().length + " parameters");
	}

	@Pointcut("execution(* com.bnguimbo.springbootrestserver.service.*Service*.*(..))")
	public void traceExec() {
		LOG.info("Logged service execution ...");
	}

	@Around("traceExec()")
	public Object myadvice(final ProceedingJoinPoint pjp) throws Throwable {
		final long start = System.currentTimeMillis();
		LOG.info("Start of : " + pjp.getSignature().toShortString() + " at : " + new Date(start));
		final Object obj = pjp.proceed();
		final long finish = System.currentTimeMillis();
		LOG.info("End of : " + pjp.getSignature().toShortString() + " at : " + new Date(finish) + " in " + (finish - start)  + " ms");
		return obj;
	}

}

