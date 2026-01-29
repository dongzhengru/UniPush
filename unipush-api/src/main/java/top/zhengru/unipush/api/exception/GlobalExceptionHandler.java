package top.zhengru.unipush.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.zhengru.unipush.common.enums.ResponseCode;
import top.zhengru.unipush.common.exception.BaseException;
import top.zhengru.unipush.common.exception.BusinessException;
import top.zhengru.unipush.common.model.vo.ResponseVO;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author zhengru
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.error("业务异常：URI={}, Message={}", request.getRequestURI(), e.getMessage());
        return ResponseVO.fail(e.getCode(), e.getMessage());
    }

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseVO<Void> handleBaseException(BaseException e, HttpServletRequest request) {
        logger.error("基础异常：URI={}, Message={}", request.getRequestURI(), e.getMessage());
        return ResponseVO.fail(e.getCode() != null ? e.getCode() : ResponseCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 请求参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.error("参数校验异常：URI={}", request.getRequestURI());
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseVO.fail(ResponseCode.VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseVO<Void> handleBindException(BindException e, HttpServletRequest request) {
        logger.error("绑定异常：URI={}", request.getRequestURI());
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseVO.fail(ResponseCode.VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseVO<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        logger.error("约束违反异常：URI={}", request.getRequestURI());
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseVO.fail(ResponseCode.VALIDATION_ERROR.getCode(), errorMessage);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseVO<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("运行时异常：URI={}, Message={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseVO.fail(ResponseCode.SYSTEM_ERROR.getCode(), "系统异常，请稍后再试");
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<Void> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常：URI={}, Message={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseVO.fail(ResponseCode.SYSTEM_ERROR.getCode(), "系统异常，请稍后再试");
    }
}
