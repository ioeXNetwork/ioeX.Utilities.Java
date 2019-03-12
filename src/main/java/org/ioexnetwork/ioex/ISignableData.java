package org.ioexnetwork.ioex;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 */
public interface ISignableData extends ISignableObject{
    //Get the the SignableData's program hashes
    byte[][] GetUniqAndOrdedProgramHashes();

    void SetPrograms(Program[] programs);

    Program[] GetPrograms();

        //TODO: add serializeUnsigned
    void SerializeUnsigned(DataOutputStream o) throws IOException;
}
