package com.rafaelalves.course.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Object id){
        super("Resource not fount. Id: "+ id);
    }
}
