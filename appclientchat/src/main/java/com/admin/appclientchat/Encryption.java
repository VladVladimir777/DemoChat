package com.admin.appclientchat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Encryption {

    // Size block 8 byte (64 bit)
    private static final int SIZE_BLOCK = 64;

    // Array blocks
    private ArrayList<ArrayList<Byte>> arrayListsBlocks;
    private ArrayList<ArrayList<Byte>> arrayListsBlocksCrypt;
    private ArrayList<ArrayList<Byte>> arrayListsBlocksDecrypt;

    private int key;

    public Encryption(int key) {
        this.key = key;
    }


    public byte[] encrypt(String message){
        createBlocks(message);

        // Encrypt
        arrayListsBlocksCrypt = new ArrayList<>();
        for (ArrayList<Byte> block: arrayListsBlocks) {
            ArrayList<Byte> temp = new ArrayList<>();
            for (Byte element: block) {
                byte b = (byte) (element + key);
                temp.add(b);
            }
            arrayListsBlocksCrypt.add(temp);
        }
        // Create byteArray
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        for (int i = 0; i < arrayListsBlocksCrypt.size(); i++) {
            byte[] block = new byte[arrayListsBlocksCrypt.get(i).size()];
            for (int j = 0; j < arrayListsBlocksCrypt.get(i).size(); j++) {
                block[j] = arrayListsBlocksCrypt.get(i).get(j);
            }
            try {
                outStream.write(block);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outStream.toByteArray();
    }

    public byte[] decrypt(String message) {
        createBlocks(message);

        // Decrypt
        arrayListsBlocksDecrypt = new ArrayList<>();
        for (ArrayList<Byte> block: arrayListsBlocks) {
            ArrayList<Byte> temp = new ArrayList<>();
            for (Byte element: block) {
                byte b = (byte) (element - key);
                temp.add(b);
            }
            arrayListsBlocksDecrypt.add(temp);
        }

        // Create byteArray
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        for (int i = 0; i < arrayListsBlocksDecrypt.size(); i++) {
            byte[] block = new byte[arrayListsBlocksDecrypt.get(i).size()];
            for (int j = 0; j < arrayListsBlocksDecrypt.get(i).size(); j++) {
                block[j] = arrayListsBlocksDecrypt.get(i).get(j);
            }
            try {
                outStream.write(block);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outStream.toByteArray();
    }

    // Create blocks
    private void createBlocks(String message) {

        // Create array blocks
        arrayListsBlocks = new ArrayList<>();

        // Convert string to byteArray
        byte[] bytesArray = new byte[0];
        try {
            bytesArray = message.getBytes("UTF-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Count block size
        int count = 0;
        ArrayList<Byte> temp = new ArrayList<>();
        for (int i = 0; i < bytesArray.length; i++) {
            temp.add(bytesArray[i]);
            count++;
            // Add block
            if (count == SIZE_BLOCK) {
                arrayListsBlocks.add(temp);
                temp = new ArrayList<>();
                count = 0;
            }
            // Add last block
            if (i == (bytesArray.length - 1)) {
                arrayListsBlocks.add(temp);
            }
        }
    }
}
