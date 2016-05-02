package ru.atott.combiq.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import ru.atott.combiq.web.aop.CommonViewAttributesInjector;
import ru.atott.combiq.web.filter.RequestHolderFilter;
import ru.atott.combiq.web.security.CombiqLogoutHandler;
import ru.atott.combiq.web.security.CombiqUserDetailsService;
import ru.atott.combiq.web.security.ElasticSearchTokenRepositoryImpl;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration;
import javax.xml.transform.Source;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("ALL")
public class SpringInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {
                MvcConfiguration.class,
                AppConfiguration.class,
                SecurityConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/*" };
    }

    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        RequestHolderFilter requestHolderFilter = new RequestHolderFilter();

        return new Filter[] {
                characterEncodingFilter,
                requestHolderFilter,
                new DelegatingFilterProxy("springSecurityFilterChain")
        };
    }

    @Override
    public void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }

    @Configuration
    @ComponentScan(basePackages = "ru.atott.combiq.web")
    @ImportResource({
            "classpath:ru/atott/combiq/dao/dao-context.xml",
            "classpath:ru/atott/combiq/service/service-context.xml",
            "classpath:ru/atott/combiq/rest/rest-context.xml",
            "classpath:conf-context.xml"
    })
    @EnableScheduling
    public static class AppConfiguration {
        @Bean
        public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
            DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
            creator.setProxyTargetClass(true);
            return creator;
        }
    }

    @Configuration
    @EnableWebMvc
    public static class MvcConfiguration extends WebMvcConfigurerAdapter {
        @Autowired
        private CommonViewAttributesInjector commonViewAttributesInjector;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry
                    .addInterceptor(commonViewAttributesInjector);
        }

        @Bean
        public ViewResolver getViewResolver() {
            FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
            resolver.setCache(true);
            resolver.setSuffix(".ftl");
            resolver.setContentType("text/html;charset=UTF-8");
            return resolver;
        }

        @Bean
        public CommonsMultipartResolver multipartResolver(){
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
            multipartResolver.setDefaultEncoding("utf-8");
            multipartResolver.setMaxUploadSize(5_000_000);
            return multipartResolver;
        }

        @Bean
        public FreeMarkerConfigurer getFreeMarkerConfigurer() {
            FreeMarkerConfigurer result = new FreeMarkerConfigurer();
            result.setDefaultEncoding("UTF-8");
            result.setTemplateLoaderPath("/WEB-INF/view/");
            Properties properties = new Properties();
            properties.put("url_escaping_charset", "UTF-8");
            result.setFreemarkerSettings(properties);
            return result;
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/static/**").addResourceLocations("/static/");
            registry.addResourceHandler("/b74b66d51df1.html").addResourceLocations("/b74b66d51df1.html");
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new ByteArrayHttpMessageConverter());

            StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
            stringConverter.setWriteAcceptCharset(false);
            converters.add(stringConverter);

            converters.add(new ResourceHttpMessageConverter());
            converters.add(new SourceHttpMessageConverter<Source>());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ"));
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setObjectMapper(objectMapper);
            converters.add(converter);

            super.configureMessageConverters(converters);
        }
    }

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Value("${auth.rememberme.key}")
        private String rememberMeKey;

        @Bean
        public CombiqUserDetailsService getCombiqUserDetailsService() {
            return new CombiqUserDetailsService();
        }

        @Bean
        public ShaPasswordEncoder getShaPasswordEncoder() {
            return new ShaPasswordEncoder();
        }

        @Bean
        public ElasticSearchTokenRepositoryImpl getElasticSearchTokenRepository() {
            return new ElasticSearchTokenRepositoryImpl();
        }

        @Bean
        public PersistentTokenBasedRememberMeServices getRememberMeServices() {
            PersistentTokenBasedRememberMeServices rememberMeServices =
                    new PersistentTokenBasedRememberMeServices(
                            rememberMeKey, getCombiqUserDetailsService(), getElasticSearchTokenRepository());
            rememberMeServices.setAlwaysRemember(true);
            return rememberMeServices;
        }

        @Autowired
        private CombiqUserDetailsService combiqUserDetailsService;

        @Autowired
        private ShaPasswordEncoder shaPasswordEncoder;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                        .and()
                    .formLogin()
                        .loginPage("/login.do")
                        .loginProcessingUrl("/login.do")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login.do?error")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                        .and()
                    .rememberMe()
                        .key(rememberMeKey)
                        .rememberMeServices(getRememberMeServices())
                        .tokenValiditySeconds((int) Duration.ofDays(30).getSeconds())
                        .and()
                    .logout()
                        .logoutSuccessHandler(new CombiqLogoutHandler())
                        .logoutUrl("/logout.do");
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(combiqUserDetailsService)
                    .passwordEncoder(shaPasswordEncoder);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/static/**");
        }
    }
}
