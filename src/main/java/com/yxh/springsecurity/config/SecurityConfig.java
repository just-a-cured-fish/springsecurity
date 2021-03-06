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
                .loginProcessingUrl("/authentication/form") //??????????????????
                .successHandler(demoAuthenticationSuccessHandler)//?????????????????????
                .failureHandler(demoAuthenticationFailureHandler)//?????????????????????
                //.defaultSuccessUrl("/demo.html").permitAll() //??????????????????
                .and().authorizeRequests()
                .antMatchers("/login.html","/code/image","/demo.html","/js/*","/font/*","/users/*","/css/*","/file/**").permitAll() //??????????????????
                .anyRequest().authenticated()
                .and().csrf().disable(); //??????csrf??????
//        http.formLogin()
//                .loginPage("/login.html")
//                .loginProcessingUrl("/authentication/form") //??????????????????
//                .successHandler(demoAuthenticationSuccessHandler)//?????????????????????
//                .failureHandler(demoAuthenticationFailureHandler)//?????????????????????
//                .defaultSuccessUrl("/test/hello").permitAll() //??????????????????
//                .and().authorizeRequests()
//                .antMatchers("/login.html","/code/image").permitAll() //??????????????????
//                .anyRequest().authenticated()
//                .and().csrf().disable(); //??????csrf??????
    }


}
