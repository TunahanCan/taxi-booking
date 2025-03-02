package com.taxibooking.bookingservice.aspect;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspectForBookingService {
    @PostConstruct
    public void init() {
        System.out.println("LoggingAspectForOrchestrator");
        log.info("LoggingAspectForOrchestrator");
    }
    // Herhangi bir public metot çağrıldığında (BookingService, BookingOrchestratorService, DataGenerator)
    @Before("execution(* com.taxibooking.bookingservice.service.*.*(..))")
    public void logBeforeMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Method invoked: {} with arguments: {}", methodName, Arrays.toString(joinPoint.getArgs()));
    }

    // Metot başarılı bir şekilde döndükten sonra loglama yap
    @AfterReturning(pointcut = "execution(* com.taxibooking.bookingservice.service.*.*(..))", returning = "result")
    public void logAfterMethodCall(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Method executed: {} returned: {}", methodName, result);
    }

    public void testCOde(){
        System.out.println("testcode ");
    }
}
