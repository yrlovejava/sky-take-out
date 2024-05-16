package com.sky.exception;

/**
 * 业务异常
 * 这样可以在基类中自定义异常处理逻辑，然后所有的子类都能共享这套处理逻辑，并且可以通过捕获BaseException捕获所有自定义异常，有助于更好的组织和管理异常类
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
