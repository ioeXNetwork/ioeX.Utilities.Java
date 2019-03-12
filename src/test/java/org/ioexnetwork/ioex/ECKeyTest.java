package org.ioexnetwork.ioex;

import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

/**
 * 
 */
public class ECKeyTest {
    private ECKey ec;
    @Before
    public void setUp(){
//        if(null == ec) {
//            System.out.println("init ec");
//            ec = new ECKey();
//        }
    }

    @Test
    public void genKeyPair(){
        ec = new ECKey();
        System.out.println("privateKey:"+ DatatypeConverter.printHexBinary(ec.getPrivateKeyBytes()));
        System.out.println("publicKey:"+ DatatypeConverter.printHexBinary(ec.getPubBytes()));

        System.out.println("address:"+ec.toAddress());

        ECKey ec2 = ECKey.fromPrivate(ec.getPrivateKeyBytes());
        System.out.println("publicKey:"+ DatatypeConverter.printHexBinary(
                ECKey.publicBytesFromPrivate(ec2.getPrivateKeyBytes())));
    }


    @Test
    public void genPub(){
        ECKey ec2 = ECKey.fromPrivate(DatatypeConverter.parseHexBinary("50DE128993A8AC4DC02340CB57D3ACB93B5D08113815AD2EF5FCF718B0EE4FFF"));
        System.out.println("publicKey:"+ DatatypeConverter.printHexBinary(
                ECKey.publicBytesFromPrivate(ec2.getPrivateKeyBytes())));
        System.out.println("address:"+ ec2.toAddress());
    }
}