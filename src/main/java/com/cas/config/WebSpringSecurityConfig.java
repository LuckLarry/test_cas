package com.cas.config;

import com.cas.filter.HttpParamsFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CasAuthenticationEntryPoint casAuthenticationEntryPoint;
    @Autowired
    private CasAuthenticationProvider casAuthenticationProvider;
    @Autowired
    private CasAuthenticationFilter casAuthenticationFilter;
    @Autowired
    private LogoutFilter logoutFilter;
    @Autowired
    private CasServerConfig casServerConfig;

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.authorizeRequests().requestMatchers(CorsUtils::isCorsRequest).permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
                .and().logout().permitAll();
        http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint);
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerConfig.getHost());
        http.addFilter(casAuthenticationFilter).addFilterBefore(logoutFilter,LogoutFilter.class)
                .addFilterBefore(singleSignOutFilter,CasAuthenticationFilter.class);
        http.antMatcher("/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(casAuthenticationProvider);
    }
    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener(){
       ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean<>();
       servletListenerRegistrationBean.setListener(new SingleSignOutHttpSessionListener());
       return servletListenerRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean httpParamsFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HttpParamsFilter());
        filterRegistrationBean.setOrder(-999);
        filterRegistrationBean.addUrlPatterns("/");
        return filterRegistrationBean;
    }
}

