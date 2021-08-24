package com.qooco.boost.exceptionhandler;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/25/2018 - 8:17 AM
 */

import com.qooco.boost.business.SystemLoggerService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.*;
import com.qooco.boost.models.BaseResp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;


@ControllerAdvice
@RestController
public class BoostExceptionHandler extends ResponseEntityExceptionHandler {
    protected Logger log = LogManager.getLogger(BoostExceptionHandler.class);
    @Autowired
    private SystemLoggerService systemLoggerService;

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn(ex.getMessage());
        BaseResp<?> rsp = new BaseResp<>(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        String mess = ex.getCause().getCause().getMessage();
        BaseResp<?> rsp = new BaseResp<>(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), mess);
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        log.warn(ex.getMessage());
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidParamException.class)
    public final ResponseEntity<Object> handleInvalidParamException(InvalidParamException ex, WebRequest request) {
        BaseResp<?> rsp = new BaseResp<>(ex.getStatus().getCode(), ex.getStatus().getDescription());
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        log.warn(ex.getMessage());
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        BaseResp<?> rsp;
        if (Objects.nonNull(ex)) {
            rsp = new BaseResp<>(ex.getStatus().getCode(), ex.getStatus().getDescription());
        } else {
            rsp = new BaseResp<>(ResponseStatus.LOGIN_UNAUTHORIZED.getCode(), ex.getMessage());
        }
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        log.warn(ex.getMessage());
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        BaseResp<?> rsp = new BaseResp<>(ex.getStatus().getCode(), ex.getStatus().getDescription());
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        log.warn(ex.getMessage());
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(BoostTokenMissingException.class)
    public final ResponseEntity<Object> handleBoostTokenMissingException(BoostTokenMissingException ex, WebRequest request) {
        BaseResp<?> rsp = new BaseResp<>(0, ex.getMessage());
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        log.warn(ex.getMessage());
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(NoPermissionException.class)
    public final ResponseEntity<Object> handleNoPermissionException(NoPermissionException ex, WebRequest request) {
        BaseResp<?> rsp = new BaseResp<>(ex.getStatus().getCode(), ex.getStatus().getDescription());
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        log.warn(ex.getMessage());
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException ex) {
        log.warn(ex.getMessage());
        BaseResp<?> rsp = new BaseResp<>(ResponseStatus.FILE_IS_BIG_SIZE);
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        return new ResponseEntity<>(rsp, HttpStatus.OK);

    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<Object> handleMalformedURLException(MalformedURLException ex) {
        log.warn(ex.getMessage());
        BaseResp<?> rsp = new BaseResp<>(ResponseStatus.MALFORMED_URL_NOT_FOUND);
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> handleHttpServerErrorException(HttpServerErrorException ex) {
        String mess = ex.getCause().getCause().getMessage();
        BaseResp<?> rsp = new BaseResp<>(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), mess);
        systemLoggerService.saveSystemLogger(rsp, getStackTrace(ex));
        return new ResponseEntity<>(rsp, HttpStatus.OK);
    }

    public static String getStackTrace(Throwable throwable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

}
