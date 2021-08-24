package com.qooco.boost.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static org.thymeleaf.templatemode.TemplateMode.HTML;

@Configuration
public class ThymeleafConfiguration {

    @Bean
    public SpringTemplateEngine templateEngine() {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(cvTemplateResolver());
        templateEngine.setEnableSpringELCompiler(true);

        return templateEngine;
    }

    @Bean
    public ClassLoaderTemplateResolver cvTemplateResolver() {

        // We set-up a Thymeleaf rendering engine. All Thymeleaf templates
        // are HTML-based files located under "src/test/resources". Beside
        // of the main HTML file, we also have partials like a footer or
        // a header. We can re-use those partials in different documents.
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("static/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

//    @Override
//    public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {
//
//    }
//
//    @Override
//    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {
//
//    }
//
//    @Override
//    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {
//
//    }
//
//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {
//
//    }
//
//    @Override
//    public void addFormatters(FormatterRegistry formatterRegistry) {
//
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
//
//    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/templates/**", "/images/**","/css/**","/fonts/**")
//                .addResourceLocations("classpath:/templates/", "classpath:/images/", "classpath:/css/","classpath:/fonts/")
//                .setCachePeriod(31556926);
//    }
//
//    @Override
//    public void addCorsMappings(CorsRegistry corsRegistry) {
//
//    }
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
//
//    }
//
//    @Override
//    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
//
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
//
//    }
//
//    @Override
//    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {
//
//    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> list) {
//
//    }
//
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> list) {
//
//    }
//
//    @Override
//    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {
//
//    }
//
//    @Override
//    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {
//
//    }
//
//    @Override
//    public Validator getValidator() {
//        return null;
//    }
//
//    @Override
//    public MessageCodesResolver getMessageCodesResolver() {
//        return null;
//    }
//
//    @Bean
//    public ViewResolver viewResolver() {
//
//        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
//        ViewResolverRegistry registry = new ViewResolverRegistry(null, applicationContext);
//
//        resolver.setTemplateEngine(templateEngine());
//        registry.viewResolver(resolver);
//
//        return resolver;
//    }
}