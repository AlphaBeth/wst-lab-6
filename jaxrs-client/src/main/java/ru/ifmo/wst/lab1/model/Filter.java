package ru.ifmo.wst.lab1.model;

import lombok.Data;

import java.util.Date;

@Data
public class Filter {
    private Long id;
    private String initiator;
    private String reason;
    private String method;
    private String planet;
    private Date date;

}
