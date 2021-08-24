package com.qooco.boost.core.security;

import com.qooco.boost.core.enumeration.ResponseStatus;
import com.qooco.boost.core.exception.BoostTokenMissingException;
import com.qooco.boost.core.exception.UnauthorizedException;
import com.qooco.boost.core.model.authentication.BoostAuthenticationToken;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.qooco.boost.core.enumeration.ApplicationConfig.*;

public class AuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {
    public AuthenticationTokenFilter(AntPathRequestMatcher... pathMatchers) {
        super(new OrRequestMatcher(pathMatchers));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
        String token = request.getHeader(BOOST_CORE_SECURITY_TOKEN_NAME.value());
        token = Objects.isNull(token) ? request.getParameter(BOOST_CORE_SECURITY_TOKEN_NAME.value()) : token;
        String timezone = request.getHeader(BOOST_CORE_SECURITY_TIMEZONE.value());
        String appVersionCode = firstNonNull(request.getHeader(BOOST_CORE_SECURITY_APP_VERSION_CODE.value()), "");
        timezone = StringUtils.isBlank(timezone) ? "UTC" : timezone;
        if (StringUtils.isBlank(token)) {
            throw new BoostTokenMissingException(ResponseStatus.TOKEN_MISSING.getDescription(), ResponseStatus.TOKEN_MISSING.getCode());
        }
        BoostAuthenticationToken authRequest = new BoostAuthenticationToken()
                .token(token)
                .timezone(timezone)
                .appVersion(StringUtils.isNotBlank(appVersionCode) ? Integer.valueOf(appVersionCode.trim()): 0)
                .locale(request.getHeader(BOOST_CORE_SECURITY_APP_LOCALE.value()));
        return getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
