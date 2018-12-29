package playground.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PlaygroundLoggerAspect {
	private Log log = LogFactory.getLog(PlaygroundLoggerAspect.class);

	@Around("@annotation(playground.aop.PlaygroundLogger)")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String methodSignature = className + "." + methodName + "()";
		log.info(methodSignature + "- start");
		try {
			Object rv = joinPoint.proceed();
			log.info(methodSignature + " - ended successfully");
			return rv;
		} catch (Throwable e) {
			log.error(methodSignature + " - end with error" + e.getClass().getName());
			throw e;
		}
	}
}
