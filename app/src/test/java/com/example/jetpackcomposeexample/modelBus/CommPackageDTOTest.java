package com.example.jetpackcomposeexample.modelBus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.example.jetpackcomposeexample.model.bus.dto.CommPackageDTO;

import org.junit.Test;

import java.io.IOException;

public class CommPackageDTOTest {
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
    public void testDeserialize() {
        CommPackageDTO commPackageDTO = new CommPackageDTO() {
            @Override
            protected void doRun() {
                //Nothing
            }
        };
        commPackageDTO.deserialize(ret);
        assertEquals(0x02, commPackageDTO.getStx());
        assertEquals(0x03, commPackageDTO.getEtx());
        assertEquals(0x10, commPackageDTO.getDataSize());
        assertEquals(0x10, commPackageDTO.getDataSizeSum());
        assertEquals(0x00, commPackageDTO.getCommand());
        assertEquals(0x01, commPackageDTO.getSequenceNum());
        assertArrayEquals(new byte[]{
                0x00, 0x00, 0x03,//staff number 3byte
                0x00, 0x00, 0x00, 0x01,//route id 4byte
                0x00,  0x04, 0x00,// first date 3byte
                0x00, 0x01,//stop seq //2byte
                0x00, 0x02,// signal strength 2byte
        }, commPackageDTO.getData());
        assertEquals(0x0C, commPackageDTO.getDataSum());
    }

    @Test
    public void testSerialize() {
        CommPackageDTO commPackageDTO = new CommPackageDTO() {
            @Override
            protected void doRun() {
                setCommand((byte) (getCommand()+1));
                setSequenceNum(getSequenceNum());
            }
        };
        commPackageDTO.run();
        try {
            byte[] actual = commPackageDTO.serialize();
            byte[] excepted = new byte[] {
                    0x02,//stx 1byte
                    0x00, 0x03,//data size 1byte
                    0x03,// data size sum 1 byte
                    0x01,// command 1byte
                    0x01,// seq 1 byte
                    0x02,//data sum
                    0x03
            };
            assertArrayEquals(excepted,actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIsCorrect() {
        CommPackageDTO commPackageDTO = new CommPackageDTO() {
            @Override
            protected void doRun() {
                //Nothing
            }
        };
        commPackageDTO.deserialize(ret);
        boolean actual = commPackageDTO.isCorrectData();
        assertEquals(true, actual);
    }
}
