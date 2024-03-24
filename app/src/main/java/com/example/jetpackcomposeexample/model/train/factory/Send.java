package com.example.jetpackcomposeexample.model.train.factory;

public interface Send {
    byte[] serialize();
    void deserialize(byte[] data);
}
