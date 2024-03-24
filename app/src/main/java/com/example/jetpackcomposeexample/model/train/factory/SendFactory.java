package com.example.jetpackcomposeexample.model.train.factory;

public interface SendFactory {
    Send fill(int command, int sequence, Object obj);
}
