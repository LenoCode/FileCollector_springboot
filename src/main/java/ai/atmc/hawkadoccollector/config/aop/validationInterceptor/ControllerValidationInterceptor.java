package ai.atmc.hawkadoccollector.config.aop.validationInterceptor;


import ai.atmc.hawkadoccollector.domain.dto.DtoModel;
import ai.atmc.hawkadoccollector.exceptions.validationExceptions.ObjectNotValidException;
import ai.atmc.hawkadoccollector.validators.ObjectValidator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Aspect
@Component
public class ControllerValidationInterceptor {

    @Autowired
    private ObjectValidator objectsValidator;


    /**
     * execution parameter is the one who will search for all controllers folders and attach this interceptor to any class with
     * Rest controller annotations
     * @param body
     */
    @Before("within(ai.atmc.hawkadoccollector.config.baseClasses.controllerBase.ControllerBase+)")
    public void beforeAnyControllerCall( final JoinPoint body) throws ObjectNotValidException {
        MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint) body;

        if(body.getArgs().length > 0){
            Object instance = body.getArgs()[0];
            MethodSignature methodSignature = (MethodSignature) methodInvocationProceedingJoinPoint.getSignature();

            if(instance instanceof DtoModel){
                objectsValidator.validate(body.getArgs()[0],getGroups(methodSignature.getMethod().getAnnotations()));
            }
        }
    }


    /**
     * This method searches for annotation @ValidationGroup and then returns its
     * parameter 'groups'
     *
     * @return
     */
    private static Class<?>[] getGroups(Annotation[] annotations){
        for(Annotation annotation: annotations){
            if(annotation instanceof ValidationGroup){
                return ((ValidationGroup) annotation).groups();
            }
        }
        return new Class[0];
    }



}
