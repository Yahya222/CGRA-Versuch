package de.amidar.crypto;

import org.bouncycastle.crypto.engines.DESedeEngine_optimize;

import org.bouncycastle.crypto.params.KeyParameter;

public class DESedeTest_rkg_inline_long {

    private byte[] key;

    public static void main(String[] args) {

        byte[] key = {
	    (byte)61,  (byte)182, (byte)188, (byte)145, 
	    (byte)64,  (byte)118, (byte)78,  (byte)112,
	    (byte)61,  (byte)182, (byte)188, (byte)145, 
	    (byte)64,  (byte)118, (byte)78,  (byte)112,
	    (byte)61,  (byte)182, (byte)188, (byte)145, 
	    (byte)64,  (byte)118, (byte)78,  (byte)112
	};

	byte[] data = {	
	    (byte)17,  (byte)234, (byte)58, (byte)177,
	    (byte)143, (byte)222, (byte)27, (byte)162 
	};

	DESedeTest_rkg_inline_long destest = new DESedeTest_rkg_inline_long(key);
	destest.run(data);

    }

    public DESedeTest_rkg_inline_long(byte[] key) {
	this.key = key;
    }

    public void run(byte[] data) {

	byte[] encrypted = new byte[8];

	DESedeEngine_optimize engine = new DESedeEngine_optimize();

	KeyParameter param = new KeyParameter(key);
	    
	engine.init(true, param);
	engine.init(true, param);
	engine.init(true, param);

    }

}
