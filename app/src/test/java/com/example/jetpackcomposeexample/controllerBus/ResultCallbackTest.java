package com.example.jetpackcomposeexample.controllerBus;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertArrayEquals;

import com.example.jetpackcomposeexample.controller.bus.ResultCallback;

import org.junit.Test;

public class ResultCallbackTest {
    //TODO:Please comment out LOG class when implement Unit Test in any function
    byte[] ret = {0x02,//stx 1byte
            0x00, 0x10,//data size 1byte
            0x10,// data size sum 1 byte
            0x00,// command 1byte
            0x01,// seq 1 byte
            0x00, 0x00, 0x03,//staff number 3byte
            0x00, 0x00, 0x00, 0x01,//route id 4byte
            0x00,  0x04, 0x00,// first date 3byte
            0x00, 0x01,//stop seq //2byte
            0x00, 0x02,// signal strength 2byte
            0xC,//data sum
            0x03
    };
    byte[] retInCorrect = {0x02,//stx 1byte
            0x00, 0x10,//data size 1byte
            0x11,// data size sum 1 byte
            0x00,// command 1byte
            0x01,// seq 1 byte
            0x00, 0x00, 0x03,//staff number 3byte
            0x00, 0x00, 0x00, 0x01,//route id 4byte
            0x00,  0x04, 0x00,// first date 3byte
            0x00, 0x01,//stop seq //2byte
            0x00, 0x02,// signal strength 2byte
            0xC,//data sum
            0x03
    };
    @Test
    public void parseTestCorrect() {
        ResultCallback resultCallback = new ResultCallback();
        byte[] actual = resultCallback.parse(ret);
        byte[] excepted = new byte[] {
                0x02,//stx 1byte
                0x00, 0x03,//data size 1byte
                0x03,// data size sum 1 byte
                0x01,// command 1byte
                0x01,// seq 1 byte
                0x02,//data sum
                0x03
        };
        assertArrayEquals(excepted, actual);
    }

    @Test
    public void parseTestInCorrect() {
        ResultCallback resultCallback = new ResultCallback();
        byte[] actual = resultCallback.parse(retInCorrect);
        byte[] excepted =  new byte[]{0};
        assertArrayEquals(excepted, actual);
    }
}
