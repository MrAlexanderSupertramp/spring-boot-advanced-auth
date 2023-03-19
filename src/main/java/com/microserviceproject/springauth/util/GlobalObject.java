package com.microserviceproject.springauth.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Scope("singleton")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalObject {

    public String name;

    
    
}
