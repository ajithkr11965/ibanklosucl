package com.sib.ibanklosucl.aop.logging;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StopWatch;

/**
 * Aspect for logging execution of service and repository Spring components.
 *
 * By default, it only runs with the "dev" profile.
 */
@Aspect
public class LoggingAspect {


	private final Environment env;
	private final List<String> sensitiveParamNames = Arrays.asList("password", "userName", "credentials");


	public LoggingAspect(Environment env) {
		this.env = env;
	}

	/**
	 * Pointcut that matches all repositories, services and Web REST endpoints.
	 */
	@Pointcut("within(@org.springframework.stereotype.Repository *)"
			+ " || within(@org.springframework.stereotype.Service *)"
			+ " || within(@org.springframework.web.bind.annotation.RestController *)")
	public void springBeanPointcut() {
		// Method is empty as this is just a Pointcut, the implementations are in the
		// advices.
	}
	@Pointcut("within(com.sib.ibanklosucl..*)" + " || within(com.sib.ibanklosucl.service..*)"
			+ " || within(com.sib.ibanklosucl.controller..*)")
	public void applicationPackagePointcut() {
		// Method is empty as this is just a Pointcut, the implementations are in the
		// advices.
	}

	/**
	 * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
	 *
	 * @param joinPoint join point we want the logger for.
	 * @return {@link Logger} associated to the given {@link JoinPoint}.
	 */
	private Logger logger(JoinPoint joinPoint) {
		return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
	}

	/**
	 * Advice that logs methods throwing exceptions.
	 *
	 * @param joinPoint join point for advice.
	 * @param e         exception.
	 */
	@AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		logger(joinPoint).error("Exception in {}() with cause = '{}' and exception = '{}'",
				joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
	}

	@Around("applicationPackagePointcut() && springBeanPointcut()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger log = logger(joinPoint);
		if (log.isDebugEnabled()) {
			log.info("Enter: {}() with argument[s] = {}", joinPoint.getSignature().getName(),
                    sanitizeArgs(joinPoint));
		}
		try {
			Object result = joinPoint.proceed();
			if (log.isDebugEnabled()) {
				log.info("Exit: {}() with result = {}", joinPoint.getSignature().getName(), result);
			}
			return result;
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument: {} in {}()", Arrays.toString(joinPoint.getArgs()),
					joinPoint.getSignature().getName());
			throw e;
		}
	}

	@Around("execution(* com.sib.ibanklosucl.service..*(..)))")
    public Object logMethodExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Logger log = logger(proceedingJoinPoint);
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        final StopWatch stopWatch = new StopWatch();

        //calculate method execution time
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();

        //Log method execution time
        log.info("Execution time of "
                + methodSignature.getDeclaringType().getSimpleName() // Class Name
                + "." + methodSignature.getName() + " " // Method Name
                + ":: " + stopWatch.getTotalTimeMillis() + " ms");

        return result;
    }
	private String sanitizeArgs(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        return IntStream.range(0, args.length)
            .mapToObj(i -> {
                if (sensitiveParamNames.contains(parameterNames[i].toLowerCase())) {
                    return parameterNames[i] + "=*****";
                } else {
                    return parameterNames[i] + "=" + args[i];
                }
            })
            .collect(Collectors.joining(", "));
    }

    private String sanitizeResult(Object result) {
        return (result != null) ? "[PROTECTED]" : "null";
    }

}