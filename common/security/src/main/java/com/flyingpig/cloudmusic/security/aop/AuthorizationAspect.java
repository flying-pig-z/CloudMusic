package com.flyingpig.cloudmusic.security.aop;


import com.flyingpig.cloudmusic.security.util.UserContext;
import com.flyingpig.cloudmusic.web.Result;
import com.flyingpig.cloudmusic.web.StatusCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAspect {

    @Around("@annotation(beforeAuthorize)")
    public Object checkAuthorization(ProceedingJoinPoint proceedingJoinPoint, BeforeAuthorize beforeAuthorize) throws Throwable {
        String role = UserContext.getUser().getRole();

        String requiredRoles = beforeAuthorize.role();

        if (!role.equals(requiredRoles)) {
            return Result.error(StatusCode.UNAUTHORIZED, "权限不足");
        } else {
            return proceedingJoinPoint.proceed();
        }
    }


}