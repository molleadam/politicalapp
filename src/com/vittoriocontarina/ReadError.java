package com.vittoriocontarina;

/**
 * YOUR API request that could not be fulfilled error.
 */
public class ReadError extends Throwable {

    private static final long serialVersionUID = 1L;

    private int mErrorCode = 0;
    private String mErrorType;

    public ReadError(String message) {
        super(message);
    }

    public ReadError(String message, String type, int code) {
        super(message);
        mErrorType = type;
        mErrorCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorType() {
        return mErrorType;
    }

}

