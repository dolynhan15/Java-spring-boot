package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.mongo.entities.SystemLoggerDoc;
import com.qooco.boost.data.mongo.services.SystemLoggerDocService;
import com.qooco.boost.threads.models.SaveSystemLoggerRequestBodyInMongo;
import com.qooco.boost.threads.models.SaveSystemLoggerResponseInMongo;
import com.qooco.boost.threads.models.SaveSystemLoggerUserInMongo;
import com.qooco.boost.utils.StringUtil;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveSystemLoggerInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveSystemLoggerInMongoActor.class);
    public static final String ACTOR_NAME = "saveSystemLoggerInMongoActor";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String NAME_HOST = "host";
    private static final String NAME_BOOST_TOKEN = "boost-token";
    private static final String NAME_ORIGIN = "origin";
    private static final String NAME_USER_AGENT = "user-agent";


    private SystemLoggerDocService systemLoggerDocService;

    public SaveSystemLoggerInMongoActor(SystemLoggerDocService systemLoggerDocService) {
        this.systemLoggerDocService = systemLoggerDocService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof ServletRequest) {
            saveSystemLoggerDoc((ServletRequest) message);
        } else if (message instanceof SaveSystemLoggerUserInMongo) {
            saveSystemLoggerDoc((SaveSystemLoggerUserInMongo) message);
        } else if (message instanceof SaveSystemLoggerResponseInMongo) {
            saveSystemLoggerDoc((SaveSystemLoggerResponseInMongo) message);
        } else if (message instanceof SaveSystemLoggerRequestBodyInMongo) {
            saveSystemLoggerDoc((SaveSystemLoggerRequestBodyInMongo) message);
        } else if (message instanceof Date) {
            systemLoggerDocService.deleteOldLoggerByCreatedDate((Date) message);
        }
    }

    private void saveSystemLoggerDoc(SaveSystemLoggerResponseInMongo sdo) {
        String url = getURL(sdo.getRequest());
        if (StringUtils.isNotBlank(url)) {
            String boostToken = sdo.getRequest().getHeader(NAME_BOOST_TOKEN);
            if (Strings.isNotBlank(sdo.getStackTrace())) {
                systemLoggerDocService.updateResponseByUrlAndBoostToken(url, boostToken, sdo.getResponse(), sdo.getStackTrace());
            } else {
                systemLoggerDocService.updateResponseByUrlAndBoostToken(url, boostToken, sdo.getResponse());
            }
        }
    }

    private void saveSystemLoggerDoc(SaveSystemLoggerRequestBodyInMongo sdo) {
        String url = getURL(sdo.getRequest());
        if (StringUtils.isNotBlank(url)) {
            String boostToken = sdo.getRequest().getHeader(NAME_BOOST_TOKEN);
            systemLoggerDocService.updateRequestBodyByUrlAndBoostToken(url, boostToken, sdo.getRequestData());
        }
    }

    private void saveSystemLoggerDoc(SaveSystemLoggerUserInMongo sdo) {
        String url = getURL(sdo.getRequest());
        if (StringUtils.isNotBlank(url)) {
            String boostToken = sdo.getRequest().getHeader(NAME_BOOST_TOKEN);
            AuthenticatedUser details = (AuthenticatedUser) sdo.getUserDetails();
            Collection<? extends GrantedAuthority> roles = details.getAuthorities();
            systemLoggerDocService.updateUserInfoByUrlAndBoostToken(url, boostToken, details.getUsername(),
                    details.getAppId(), details.getCompanyId(), CollectionUtils.isNotEmpty(roles) ? roles.toString() : "");
        }
    }

    private void saveSystemLoggerDoc(ServletRequest servletRequest) {
        try {
            RequestFacade request = ((RequestFacade) servletRequest);
            String url = getURL((HttpServletRequest) servletRequest);
            if (StringUtils.isNotBlank(url)) {
                SystemLoggerDoc doc = new SystemLoggerDoc();
                doc.setUrl(url);
                doc.setBoostToken(request.getHeader(NAME_BOOST_TOKEN));
                doc.setClientIP(findUserIP(servletRequest));
                doc.setHost(request.getHeader(NAME_HOST));
                doc.setOrigin(request.getHeader(NAME_ORIGIN));
                doc.setUserAgent(request.getHeader(NAME_USER_AGENT));
                doc.setMethod(request.getMethod());
                if (GET.equals(doc.getMethod())) {
                    Map<String, String[]> para = request.getParameterMap();
                    if (MapUtils.isNotEmpty(para)) {
                        doc.setRequestData((DBObject) JSON.parse(new Gson().toJson(para)));
                    }
                }
                logger.info(StringUtil.convertToJson(doc));
                systemLoggerDocService.save(doc);
            }
        } catch (IllegalStateException | NullPointerException | ClassCastException e) {
            logger.info(e.getMessage());
        }
    }

    private String findUserIP(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        StringBuffer ip = new StringBuffer();

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        String ipRealAddress = request.getHeader("X-REAL-IP");

        if (null != ipRealAddress) {
            ip.append(ipRealAddress);
        } else {
            ip.append(request.getRemoteAddr());
        }
        ip.append(":");

        if (null != ipAddress) {
            ip.append(ipAddress);
        } else {
            ip.append(request.getRemoteAddr());
        }

        return ip.toString();
    }

    private String getURL(HttpServletRequest request) {
        try {
            return String.valueOf(request.getRequestURL());
        } catch (NullPointerException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

}
