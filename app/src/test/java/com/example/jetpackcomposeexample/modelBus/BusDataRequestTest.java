package com.example.jetpackcomposeexample.modelBus;

import static junit.framework.TestCase.assertEquals;

import com.example.jetpackcomposeexample.model.bus.dto.BusDataRequest;

import org.junit.Test;

import java.util.Calendar;

public class BusDataRequestTest {
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

    @Test
    public void testBusDataObject() {
        BusDataRequest request = new BusDataRequest();
        request.deserialize(ret);
        request.run();

        BusDataRequest.BusData busDataActual = request.busData;

        BusDataRequest requestActual = new BusDataRequest();
        BusDataRequest.BusData busDataExcepted = requestActual.busData;
        busDataExcepted.setStaffId(3);
        busDataExcepted.setRouteId(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, Calendar.HOUR_OF_DAY);
        calendar.set(4, Calendar.MINUTE);
        calendar.set(0, Calendar.SECOND);
        busDataExcepted.setSendTime(calendar);
        busDataExcepted.setStopSequence(1);
        busDataExcepted.setSignalStrength(2);


        // do the actual test
        assertEquals(busDataExcepted.getStaffId(), busDataActual.getStaffId());
        assertEquals(busDataExcepted.getRouteId(), busDataActual.getRouteId());
//        int hourExcepted = busDataExcepted.getSendTime().get(Calendar.HOUR_OF_DAY);
//        int hourActual = busDataActual.getSendTime().get(Calendar.HOUR_OF_DAY);
//        assertEquals(hourExcepted,hourActual);
//        assertEquals(busDataExcepted.getSendTime().get(Calendar.MINUTE),
//                busDataActual.getSendTime().get(Calendar.MINUTE));
//        assertEquals(busDataExcepted.getSendTime().get(Calendar.SECOND),
//                busDataActual.getSendTime().get(Calendar.SECOND));
        assertEquals(busDataExcepted.getStopSequence(), busDataActual.getStopSequence());
        assertEquals(busDataExcepted.getSignalStrength(), busDataActual.getSignalStrength());
    }
}
