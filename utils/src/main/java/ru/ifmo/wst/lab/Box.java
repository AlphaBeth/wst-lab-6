package ru.ifmo.wst.lab;

import lombok.Data;

@Data
public class Box<T> {
    private T value;
}
