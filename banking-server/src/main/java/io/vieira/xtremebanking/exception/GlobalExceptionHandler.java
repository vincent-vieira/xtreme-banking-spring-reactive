package io.vieira.xtremebanking.exception;

import io.vieira.xtremebanking.funds.FundsManager;
import io.vieira.xtremebanking.funds.NotEnoughFundsException;
import io.vieira.xtremebanking.loan.LoanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity handleNotEnoughFundsException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler({
            FundsManager.BuyerNotFoundException.class,
            LoanNotFoundException.class
    })
    public ResponseEntity handleLoanOrBuyerNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
