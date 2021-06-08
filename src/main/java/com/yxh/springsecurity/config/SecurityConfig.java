package com.yxh.springsecurity.config;

import com.yxh.springsecurity.auth.DemoAuthenticationFailureHandler;
import com.yxh.springsecurity.auth.DemoAuthenticationSuccessHandler;
import com.yxh.springsecurity.filter.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.naming.AuthenticationException;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("userDetailService")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DemoAuthenticationFailureHandler demoAuthenticationFailureHandler;
    @Autowired
    private DemoAuthenticationSuccessHandler demoAuthenticationSuccessHandler;
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
//        String password =passwordEncoder.encode("123");
//        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder).withUser("user").password(password).roles("admin");
//    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
    @Bean
    PasswordEncoder password(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setDemoAuthenticationFailureHandler(demoAuthenticationFailureHandler);

        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class).formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/authentication/form") //登录访问路径
                .successHandler(demoAuthenticationSuccessHandler)//登录成功的操作
                .failureHandler(demoAuthenticationFailureHandler)//登录失败的操作
                //.defaultSuccessUrl("/demo.html").permitAll() //默认跳转路径
                .and().authorizeRequests()
                .antMatchers("/login.html","/code/image","/demo.html","/js/*","/font/*","/users/*","/css/*","/file/**").permitAll() //可以直接访问
                .anyRequest().authenticated()
                .and().csrf().disable(); //关闭csrf防护
//        http.formLogin()
//                .loginPage("/login.html")
//                .loginProcessingUrl("/authentication/form") //登录访问路径
//                .successHandler(demoAuthenticationSuccessHandler)//登录成功的操作
//                .failureHandler(demoAuthenticationFailureHandler)//登录失败的操作
//                .defaultSuccessUrl("/test/hello").permitAll() //默认跳转路径
//                .and().authorizeRequests()
//                .antMatchers("/login.html","/code/image").permitAll() //可以直接访问
//                .anyRequest().authenticated()
//                .and().csrf().disable(); //关闭csrf防护
    }


}
