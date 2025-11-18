package com.sib.ibanklosucl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MssfApiException extends RuntimeException {

   public MssfApiException(String message) {
       super(message);
   }

   public MssfApiException(String message, Throwable cause) {
       super(message, cause);
   }
}

