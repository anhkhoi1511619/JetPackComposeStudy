package com.example.jetpackcomposeexample.controller.tcp_ip_v2;

public class TxManager {

    public TxManager() {
        // Constructor
    }

    // Add methods and fields as needed
    public byte[] createCommand(byte[] data) {
        byte command = 0x01; // Default command
        try {
            command = data[4]; // Assuming command is at index 4
        } catch (Exception e) {
            System.out.println("Error extracting command: " + e.getMessage());
        }
        switch (command) {
            case 0x01:
                return createStatusCommand();
            case 0x10:
                return createRotateMotor();
            default:
                //throw new IllegalArgumentException("Unknown command: " + command);
        }

        //byte[] commandPacket = new byte[length + 2]; // +2 for STX and ETX

        return createStatusCommand();
    }
    private byte[] createStatusCommand() {
        // Example command packet for status
        //System.out.println( "Server is read.... with length " + rawData.length + " data: " + Arrays.toString(rawData));
        CommPackageDTO commPackage = new CommPackageDTO("02", "03", 0);
        commPackage.setTxSqNo("03");
        commPackage.setTxCmd("02");

        //byte[] response = commPackage.makeTxData(); // Example response data
        return commPackage.makeTxData(); // STX, CMD, DATA, ETX
    }
    private byte[] createRotateMotor() {
        // Example command packet for rotate motor
        //System.out.println( "Server is read.... with length " + rawData.length + " data: " + Arrays.toString(rawData));
        CommPackageDTO commPackage = new CommPackageDTO("02", "03", 0);
        commPackage.setTxSqNo("03");
        commPackage.setTxCmd("11");

        //byte[] response = commPackage.makeTxData(); // Example response data
        return commPackage.makeTxData(); // STX, CMD, DATA, ETX
    }

}
