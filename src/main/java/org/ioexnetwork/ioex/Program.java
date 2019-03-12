package org.ioexnetwork.ioex;

import org.ioexnetwork.common.Util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 */
public class Program {
    //the contract program code,which will be run on VM or specific envrionment
    byte[] Code;

    //the program code's parameter
    byte[] Parameter;

    public Program(byte[] Code,byte[] Paramter) throws IOException {
        this.Code = Code;

        ProgramBuilder pb =  ProgramBuilder.newProgramBuilder();
        pb.pushData(Paramter);

        this.Parameter = pb.ToArray();
    }

    //serialize the Program
    void Serialize(DataOutputStream o) throws IOException {

        Util.WriteVarBytes(o,this.Parameter);
        Util.WriteVarBytes(o,this.Code);

        return;
    }

}
