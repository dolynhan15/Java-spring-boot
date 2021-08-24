package com.qooco.boost.utils;

import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
public class ServletUriUtils {
    @Value(ApplicationConstant.BOOST_PATA_CONFIG_API)
    private String urlPataServer;

    private static String apiPath;

    @PostConstruct
    public void init(){
        this.apiPath = urlPataServer;
    }
    public static String getDomain() {
        if (StringUtils.isNotBlank(apiPath)) {
            return apiPath;
        } else {
            try {
                return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            } catch (Exception ex) {
                return "";
            }
        }
    }

    public static String getDownloadMethodPath() {
        return StringUtil.append(getDomain(), URLConstants.DOWNLOAD_IMAGE_PATH);
    }

    public static String getRelativePath(String fullPath) {
        //Check http or https first,
        if (!Validation.validateHttpOrHttps(fullPath)) {
            throw new InvalidParamException(ResponseStatus.HTTP_OR_HTTPS_WRONG_FORMAT);
        }
        //Remove menthol path to get relate url
        return fullPath.replace(getDownloadMethodPath(), "");
    }

    public static String getAbsolutePath(String relativePath) {
        if (StringUtils.isNotBlank(relativePath)) {
            return StringUtil.append(getDownloadMethodPath(), relativePath);
        }
        return null;
    }

    public static String getAbsoluteResourcePath(String relativePath) {
        if (StringUtils.isNotBlank(relativePath)) {
            return StringUtil.append(getDomain(), relativePath);
        }
        return null;
    }

    public static List<String> getRelativePaths(List<String> fullPaths) {
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(fullPaths)) return result;
        for (String fullPath : fullPaths) {
            result.add(getRelativePath(fullPath));
        }
        return result;
    }

    public static List<String> getAbsolutePaths(List<String> relativePaths) {
        List<String> result = new ArrayList<>();
        ofNullable(relativePaths)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(paths -> paths.forEach(it -> ofNullable(it).ifPresent(path -> result.add(getAbsolutePath(path)))));
        return result;
    }
}
