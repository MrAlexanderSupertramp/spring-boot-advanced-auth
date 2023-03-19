package com.microserviceproject.springauth.helper;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Messenger<T> implements Serializable {

    public String status;
	public String message;
    public T data;

    // constructor without data
    public Messenger(String status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public Messenger(T data) {
        super();
        this.data = data;
    }
    
}
