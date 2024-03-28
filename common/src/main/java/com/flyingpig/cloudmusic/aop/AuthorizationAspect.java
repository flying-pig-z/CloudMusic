package com.flyingpig.cloudmusic.aop;

import com.flyingpig.cloudmusic.constant.StatusCode;
import com.flyingpig.cloudmusic.interceptor.LocalUserInfo;
import com.flyingpig.cloudmusic.result.Result;
import com.flyingpig.cloudmusic.util.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthorizationAspect {

    @Around("@annotation(beforeAuthorize)")
    public Object checkAuthorization(ProceedingJoinPoint proceedingJoinPoint, BeforeAuthorize beforeAuthorize) throws Throwable {
        String role = UserContext.getUser().getRole();
        System.out.println(role);

        String requiredRoles = beforeAuthorize.role();

        if (!role.equals(requiredRoles)) {
            return Result.error(StatusCode.UNAUTHORIZED, "权限不足");
        } else {
            return proceedingJoinPoint.proceed();
        }
    }


}