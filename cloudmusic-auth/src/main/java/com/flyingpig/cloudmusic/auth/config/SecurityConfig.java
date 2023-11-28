package com.flyingpig.cloudmusic.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不会创建HttpSession并且不通过Session获取SecurityContext对象,因为前后端分离基本session就已经灭有用了
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()//对请求认证规则进行相应配置
                //对于登录,修改密码和注册接口放行(antMatchers)
                .antMatchers("/auth-service/user/login","/auth-service/user/logout").permitAll()
                .antMatchers("/auth-service/user/password").permitAll()
                .antMatchers("/auth-service/email/verificationCode").permitAll()
                .antMatchers("/auth-service/email/register").permitAll()
                .antMatchers("/v2/api-docs", "/definitions/**", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html", "/webjars/**","/swagger-resources/configuration/ui","/swagger-ui.html").permitAll()
                .anyRequest().authenticated();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
