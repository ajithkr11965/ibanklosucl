package com.sib.ibanklosucl.exception;

   public class VehicleLoanBREException extends RuntimeException {
       public VehicleLoanBREException(String message) {
           super(message);
       }

       public VehicleLoanBREException(String message, Throwable cause) {
           super(message, cause);
       }
   }
