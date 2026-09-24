package com.ia.agentsia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {

    private int status;
    private String message;
    private T data;

}
