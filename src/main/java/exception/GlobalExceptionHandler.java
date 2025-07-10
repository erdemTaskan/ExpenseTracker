package exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFoundException(NotFoundException ex){
        return buildResponse(HttpStatus.NOT_FOUND,ex);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,Object>> handleBadRequestException(BadRequestException ex){
        return buildResponse(HttpStatus.BAD_REQUEST,ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException ex){
        return buildResponse(HttpStatus.BAD_REQUEST,ex);
    }

    public ResponseEntity<Map<String,Object>> buildResponse(HttpStatus status, Exception ex){
        Map<String,Object> errorDetails= new HashMap<>();

        errorDetails.put("Time ", LocalDateTime.now());
        errorDetails.put("Error ",ex.getClass().getSimpleName());
        errorDetails.put("Message ",ex.getMessage());
        errorDetails.put("Status ", status.value());

        return new ResponseEntity<>(errorDetails,status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidationExcepiton(MethodArgumentNotValidException ex){
        Map<String,Object> errorDetails=new HashMap<>();

        errorDetails.put("Time ",LocalDateTime.now());
        errorDetails.put("Error ","ValidationException");
        errorDetails.put("Status ",HttpStatus.BAD_REQUEST.value());

        Map<String,String> validationErrors=new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> validationErrors.put(error.getField(),error.getDefaultMessage()));

        errorDetails.put("ValidationErrors ",validationErrors);

        return new ResponseEntity<>(errorDetails,HttpStatus.BAD_REQUEST);
    }
}
