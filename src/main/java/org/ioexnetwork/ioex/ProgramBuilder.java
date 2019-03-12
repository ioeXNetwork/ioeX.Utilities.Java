package org.ioexnetwork.ioex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 */
public class ProgramBuilder {
    private static final byte PUSHBYTES75 = 0x4B;
    private static final byte PUSHDATA1   = 0x4C; // The next byte contains the number of bytes to be pushed onto the stack.
    private static final byte PUSHDATA2   = 0x4D; // The next two bytes contain the number of bytes to be pushed onto the stack.
    private static final byte PUSHDATA4   = 0x4E; // The next four bytes contain the number of bytes to be pushed onto the stack.

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);


    public static ProgramBuilder newProgramBuilder()  {
        return new ProgramBuilder();
    }
    public void addOp(byte op) throws IOException {
        dos.writeByte(op);

    }


    public void pushData(byte[] data) throws IOException {
        if (data == null) {
            return; //TODO: add error
        }

        if (data.length <= PUSHBYTES75) {
            dos.writeByte(data.length);
            dos.write(data);
        } else if (data.length < 0x100) {
            this.addOp(PUSHDATA1);
            dos.writeByte(data.length);
            dos.write(data);
        } else if (data.length < 0x10000) {
            this.addOp(PUSHDATA2);
            short len = (short)data.length;
            len = Short.reverseBytes(len);
            dos.writeShort(len);
            dos.write(data);

        } else {
            this.addOp(PUSHDATA4);
            int len = (short)data.length;
            len = Integer.reverseBytes(len);
            dos.writeInt(len);
            dos.write(data);
        }
    }

    public byte[] ToArray(){
        return baos.toByteArray();
    }
}
