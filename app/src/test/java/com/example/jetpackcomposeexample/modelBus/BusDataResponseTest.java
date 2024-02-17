package com.example.jetpackcomposeexample.modelBus;

import static org.junit.Assert.assertArrayEquals;

import com.example.jetpackcomposeexample.model.bus.dto.BusDataResponse;

import org.junit.Test;

import java.io.IOException;

public class BusDataResponseTest {

    @Test
    public void testSerialize() {
        BusDataResponse busDataResponse = new BusDataResponse();
        busDataResponse.setCommand((byte) 0x03);
        busDataResponse.setSequenceNum(0x01);
        busDataResponse.run();

        try {
            byte[] actual = busDataResponse.serialize();
            byte[] expected = new byte[] {
                    0x02,//stx 1byte
                    0x00, 0x03,//data size 1byte
                    0x03,// data size sum 1 byte
                    0x03,// command 1byte
                    0x01,// seq 1 byte
                    0x04,//data sum
                    0x03
            };
            assertArrayEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
