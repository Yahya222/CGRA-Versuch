// $Id: BLAKESmallCore.java 252 2011-06-07 17:55:14Z tp $

package fr.cryptohash;

/**
 * This class implements BLAKE-224 and BLAKE-256, which differ only by
 * the IV, output length, and one bit in the padding.
 *
 * <pre>
 * ==========================(LICENSE BEGIN)============================
 *
 * Copyright (c) 2007-2010  Projet RNRT SAPHIR
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * ===========================(LICENSE END)=============================
 * </pre>
 *
 * @version   $Revision: 252 $
 * @author    Thomas Pornin &lt;thomas.pornin@cryptolog.com&gt;
 */

public class BLAKE256Digest_inline {

	private int[] SIGMA, CS, initVal;

	public int h0, h1, h2, h3, h4, h5, h6, h7;
	private int s0, s1, s2, s3;
	private int t0, t1;
	private int[] tmpM;
	private byte[] tmpBuf;

        private int digestLen, blockLen, inputLen;
        private byte[] inputBuf, outputBuf;
        private long blockCount;

	public BLAKE256Digest_inline() {

	    int[] initVal = {
                0x6A09E667, 0xBB67AE85, 0x3C6EF372, 0xA54FF53A,
                0x510E527F, 0x9B05688C, 0x1F83D9AB, 0x5BE0CD19
	    };
	    this.initVal = initVal;

            digestLen = getDigestLength();
            blockLen = getInternalBlockLength();
            inputBuf = new byte[blockLen];
            outputBuf = new byte[digestLen];
            inputLen = 0;
            blockCount = 0;
	    
	    int[] SIGMA = {
		 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,
		14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3,
		11,  8, 12,  0,  5,  2, 15, 13, 10, 14,  3,  6,  7,  1,  9,  4,
		 7,  9,  3,  1, 13, 12, 11, 14,  2,  6,  5, 10,  4,  0, 15,  8,
		 9,  0,  5,  7,  2,  4, 10, 15, 14,  1, 11, 12,  6,  8,  3, 13,
		 2, 12,  6, 10,  0, 11,  8,  3,  4, 13,  7,  5, 15, 14,  1,  9,
		12,  5,  1, 15, 14, 13,  4, 10,  0,  7,  6,  3,  9,  2,  8, 11,
		13, 11,  7, 14, 12,  1,  3,  9,  5,  0, 15,  4,  8,  6,  2, 10,
		 6, 15, 14,  9, 11,  3,  0,  8, 12,  2, 13,  7,  1,  4, 10,  5,
		10,  2,  8,  4,  7,  6,  1,  5, 15, 11,  9, 14,  3, 12, 13,  0,
		 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,
		14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3,
		11,  8, 12,  0,  5,  2, 15, 13, 10, 14,  3,  6,  7,  1,  9,  4,
		 7,  9,  3,  1, 13, 12, 11, 14,  2,  6,  5, 10,  4,  0, 15,  8
	    };
	    this.SIGMA = SIGMA;

	    int[] CS = {
		0x243F6A88, 0x85A308D3, 0x13198A2E, 0x03707344,
		0xA4093822, 0x299F31D0, 0x082EFA98, 0xEC4E6C89,
		0x452821E6, 0x38D01377, 0xBE5466CF, 0x34E90C6C,
		0xC0AC29B7, 0xC97C50DD, 0x3F84D5B5, 0xB5470917
	   };
	    this.CS = CS;

            doInit();

	}

        public void update(byte[] input, int offset, int len)
        {
                while (len > 0) {
                        int copyLen = blockLen - inputLen;
                        if (copyLen > len)
                                copyLen = len;
                        System.arraycopy(input, offset, inputBuf, inputLen,
                                copyLen);
                        offset += copyLen;
                        inputLen += copyLen;
                        len -= copyLen;
                        if (inputLen == blockLen) {
                                processBlock(inputBuf);
                                blockCount ++;
                                inputLen = 0;
                        }
                }
        }

        protected int getInternalBlockLength()
        {
                return getBlockLength();
        }

        protected final int flush()
        {
                return inputLen;
        }

        protected final byte[] getBlockBuffer()
        {
                return inputBuf;
        }


/*
        int[] getInitVal()
        {
                return initVal;
        }
*/
        /** @see Digest */
        public int getDigestLength()
        {
                return 32;
        }


	/** @see Digest */
	public int getBlockLength()
	{
		return 64;
	}

	/** @see DigestEngine */
	protected void engineReset()
	{
		int[] iv = getInitVal();
		h0 = iv[0];
		h1 = iv[1];
		h2 = iv[2];
		h3 = iv[3];
		h4 = iv[4];
		h5 = iv[5];
		h6 = iv[6];
		h7 = iv[7];
		s0 = s1 = s2 = s3 = 0;
		t0 = t1 = 0;
	}

        int[] getInitVal()
        {
                return initVal;
        }

	/** @see DigestEngine */
	protected void doPadding(byte[] output, int outputOffset)
	{
		int ptr = flush();
		int bitLen = ptr << 3;
		int th = t1;
		int tl = t0 + bitLen;
		tmpBuf[ptr] = (byte)0x80;
		if (ptr == 0) {
			t0 = (int)0xFFFFFE00;
			t1 = (int)0xFFFFFFFF;
		} else if (t0 == 0) {
			t0 = (int)0xFFFFFE00 + bitLen;
			t1 --;
		} else {
			t0 -= 512 - bitLen;
		}
		if (ptr < 56) {
			for (int i = ptr + 1; i < 56; i ++)
				tmpBuf[i] = 0x00;
			if (getDigestLength() == 32)
				tmpBuf[55] |= 0x01;
			encodeBEInt(th, tmpBuf, 56);
			encodeBEInt(tl, tmpBuf, 60);
			update(tmpBuf, ptr, 64 - ptr);
		} else {
			for (int i = ptr + 1; i < 64; i ++)
				tmpBuf[i] = 0;
			update(tmpBuf, ptr, 64 - ptr);
			t0 = (int)0xFFFFFE00;
			t1 = (int)0xFFFFFFFF;
			for (int i = 0; i < 56; i ++)
				tmpBuf[i] = 0x00;
			if (getDigestLength() == 32)
				tmpBuf[55] = 0x01;
			encodeBEInt(th, tmpBuf, 56);
			encodeBEInt(tl, tmpBuf, 60);
			update(tmpBuf, 0, 64);
		}
		encodeBEInt(h0, output, outputOffset +  0);
		encodeBEInt(h1, output, outputOffset +  4);
		encodeBEInt(h2, output, outputOffset +  8);
		encodeBEInt(h3, output, outputOffset + 12);
		encodeBEInt(h4, output, outputOffset + 16);
		encodeBEInt(h5, output, outputOffset + 20);
		encodeBEInt(h6, output, outputOffset + 24);
		if (getDigestLength() == 32)
			encodeBEInt(h7, output, outputOffset + 28);
	}

	/** @see DigestEngine */
	protected void doInit()
	{
		tmpM = new int[16];
		tmpBuf = new byte[64];
		engineReset();
	}

	/**
	 * Encode the 32-bit word {@code val} into the array
	 * {@code buf} at offset {@code off}, in big-endian
	 * convention (most significant byte first).
	 *
	 * @param val   the value to encode
	 * @param buf   the destination buffer
	 * @param off   the destination offset
	 */
	private static final void encodeBEInt(int val, byte[] buf, int off)
	{
		buf[off + 0] = (byte)(val >>> 24);
		buf[off + 1] = (byte)(val >>> 16);
		buf[off + 2] = (byte)(val >>> 8);
		buf[off + 3] = (byte)val;
	}

	/**
	 * Decode a 32-bit big-endian word from the array {@code buf}
	 * at offset {@code off}.
	 *
	 * @param buf   the source buffer
	 * @param off   the source offset
	 * @return  the decoded value
	 */
	private static final int decodeBEInt(byte[] buf, int off)
	{
		return ((buf[off] & 0xFF) << 24)
			| ((buf[off + 1] & 0xFF) << 16)
			| ((buf[off + 2] & 0xFF) << 8)
			| (buf[off + 3] & 0xFF);
	}

	/**
	 * Perform a circular rotation by {@code n} to the right
	 * of the 32-bit word {@code x}. The {@code n} parameter
	 * must lie between 1 and 31 (inclusive).
	 *
	 * @param x   the value to rotate
	 * @param n   the rotation count (between 1 and 31)
	 * @return  the rotated value
	*/
	static private int circularRight(int x, int n)
	{
		return ((x) >>> n) | ((x) << (32 - n));
	}

	/** @see DigestEngine */
	protected void processBlock(byte[] data)
	{
		t0 += 512;
		if ((t0 & ~0x1FF) == 0)
			t1 ++;
		int v0 = h0;
		int v1 = h1;
		int v2 = h2;
		int v3 = h3;
		int v4 = h4;
		int v5 = h5;
		int v6 = h6;
		int v7 = h7;
		int v8 = s0 ^ (int)0x243F6A88;
		int v9 = s1 ^ (int)0x85A308D3;
		int vA = s2 ^ (int)0x13198A2E;
		int vB = s3 ^ (int)0x03707344;
		int vC = t0 ^ (int)0xA4093822;
		int vD = t0 ^ (int)0x299F31D0;
		int vE = t1 ^ (int)0x082EFA98;
		int vF = t1 ^ (int)0xEC4E6C89;
		int[] m = tmpM;
		for (int i = 0; i < 16; i ++)
			m[i] = decodeBEInt(data, 4 * i);
		for (int r = 0; r < 14; r ++) {
			int o0 = SIGMA[(r << 4) + 0x0];
			int o1 = SIGMA[(r << 4) + 0x1];
			v0 += v4 + (m[o0] ^ CS[o1]);
			//vC = circularRight(vC ^ v0, 16);
			vC = ((vC ^ v0) >>> 16) | ((vC ^ v0) << (32 - 16));
			v8 += vC;
			//v4 = circularRight(v4 ^ v8, 12);
			v4 = ((v4 ^ v8) >>> 12) | ((v4 ^ v8) << (32 - 12));
			v0 += v4 + (m[o1] ^ CS[o0]);
			//vC = circularRight(vC ^ v0, 8);
			vC = ((vC ^ v0) >>> 8) | ((vC ^ v0) << (32 - 8));
			v8 += vC;
			//v4 = circularRight(v4 ^ v8, 7);
			v4 = ((v4 ^ v8) >>> 7) | ((v4 ^ v8) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0x2];
			o1 = SIGMA[(r << 4) + 0x3];
			v1 += v5 + (m[o0] ^ CS[o1]);
			//vD = circularRight(vD ^ v1, 16);
			vD = ((vD ^ v1) >>> 16) | ((vD ^ v1) << (32 - 16));
			v9 += vD;
			//v5 = circularRight(v5 ^ v9, 12);
			v5 = ((v5 ^ v9) >>> 12) | ((v5 ^ v9) << (32 - 12));
			v1 += v5 + (m[o1] ^ CS[o0]);
			//vD = circularRight(vD ^ v1, 8);
			vD = ((vD ^ v1) >>> 8) | ((vD ^ v1) << (32 - 8));
			v9 += vD;
			//v5 = circularRight(v5 ^ v9, 7);
			v5 = ((v5 ^ v9) >>> 7) | ((v5 ^ v9) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0x4];
			o1 = SIGMA[(r << 4) + 0x5];
			v2 += v6 + (m[o0] ^ CS[o1]);
			//vE = circularRight(vE ^ v2, 16);
			vE = ((vE ^ v2) >>> 16) | ((vE ^ v2) << (32 - 16));
			vA += vE;
			//v6 = circularRight(v6 ^ vA, 12);
			v6 = ((v6 ^ vA) >>> 12) | ((v6 ^ vA) << (32 - 12));
			v2 += v6 + (m[o1] ^ CS[o0]);
			//vE = circularRight(vE ^ v2, 8);
			vE = ((vE ^ v2) >>> 8) | ((vE ^ v2) << (32 - 8));
			vA += vE;
			//v6 = circularRight(v6 ^ vA, 7);
			v6 = ((v6 ^ vA) >>> 7) | ((v6 ^ vA) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0x6];
			o1 = SIGMA[(r << 4) + 0x7];
			v3 += v7 + (m[o0] ^ CS[o1]);
			//vF = circularRight(vF ^ v3, 16);
			vF = ((vF ^ v3) >>> 16) | ((vF ^ v3) << (32 - 16));
			vB += vF;
			//v7 = circularRight(v7 ^ vB, 12);
			v7 = ((v7 ^ vB) >>> 12) | ((v7 ^ vB) << (32 - 12));
			v3 += v7 + (m[o1] ^ CS[o0]);
			//vF = circularRight(vF ^ v3, 8);
			vF = ((vF ^ v3) >>> 8) | ((vF ^ v3) << (32 - 8));
			vB += vF;
			//v7 = circularRight(v7 ^ vB, 7);
			v7 = ((v7 ^ vB) >>> 7) | ((v7 ^ vB) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0x8];
			o1 = SIGMA[(r << 4) + 0x9];
			v0 += v5 + (m[o0] ^ CS[o1]);
			//vF = circularRight(vF ^ v0, 16);
			vF = ((vF ^ v0) >>> 16) | ((vF ^ v0) << (32 - 16));
			vA += vF;
			//v5 = circularRight(v5 ^ vA, 12);
			v5 = ((v5 ^ vA) >>> 12) | ((v5 ^ vA) << (32 - 12));
			v0 += v5 + (m[o1] ^ CS[o0]);
			//vF = circularRight(vF ^ v0, 8);
			vF = ((vF ^ v0) >>> 8) | ((vF ^ v0) << (32 - 8));
			vA += vF;
			//v5 = circularRight(v5 ^ vA, 7);
			v5 = ((v5 ^ vA) >>> 7) | ((v5 ^ vA) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0xA];
			o1 = SIGMA[(r << 4) + 0xB];
			v1 += v6 + (m[o0] ^ CS[o1]);
			//vC = circularRight(vC ^ v1, 16);
			vC = ((vC ^ v1) >>> 16) | ((vC ^ v1) << (32 - 16));
			vB += vC;
			//v6 = circularRight(v6 ^ vB, 12);
			v6 = ((v6 ^ vB) >>> 12) | ((v6 ^ vB) << (32 - 12));
			v1 += v6 + (m[o1] ^ CS[o0]);
			//vC = circularRight(vC ^ v1, 8);
			vC = ((vC ^ v1) >>> 8) | ((vC ^ v1) << (32 - 8));
			vB += vC;
			//v6 = circularRight(v6 ^ vB, 7);
			v6 = ((v6 ^ vB) >>> 7) | ((v6 ^ vB) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0xC];
			o1 = SIGMA[(r << 4) + 0xD];
			v2 += v7 + (m[o0] ^ CS[o1]);
			//vD = circularRight(vD ^ v2, 16);
			vD = ((vD ^ v2) >>> 16) | ((vD ^ v2) << (32 - 16));
			v8 += vD;
			//v7 = circularRight(v7 ^ v8, 12);
			v7 = ((v7 ^ v8) >>> 12) | ((v7 ^ v8) << (32 - 12));
			v2 += v7 + (m[o1] ^ CS[o0]);
			//vD = circularRight(vD ^ v2, 8);
			vD = ((vD ^ v2) >>> 8) | ((vD ^ v2) << (32 - 8));
			v8 += vD;
			//v7 = circularRight(v7 ^ v8, 7);
			v7 = ((v7 ^ v8) >>> 7) | ((v7 ^ v8) << (32 - 7));
			o0 = SIGMA[(r << 4) + 0xE];
			o1 = SIGMA[(r << 4) + 0xF];
			v3 += v4 + (m[o0] ^ CS[o1]);
			//vE = circularRight(vE ^ v3, 16);
			vE = ((vE ^ v3) >>> 16) | ((vE ^ v3) << (32 - 16));
			v9 += vE;
			//v4 = circularRight(v4 ^ v9, 12);
			v4 = ((v4 ^ v9) >>> 12) | ((v4 ^ v9) << (32 - 12));
			v3 += v4 + (m[o1] ^ CS[o0]);
			//vE = circularRight(vE ^ v3, 8);
			vE = ((vE ^ v3) >>> 8) | ((vE ^ v3) << (32 - 8));
			v9 += vE;
			//v4 = circularRight(v4 ^ v9, 7);
			v4 = ((v4 ^ v9) >>> 7) | ((v4 ^ v9) << (32 - 7));
		}
		h0 ^= s0 ^ v0 ^ v8;
		h1 ^= s1 ^ v1 ^ v9;
		h2 ^= s2 ^ v2 ^ vA;
		h3 ^= s3 ^ v3 ^ vB;
		h4 ^= s0 ^ v4 ^ vC;
		h5 ^= s1 ^ v5 ^ vD;
		h6 ^= s2 ^ v6 ^ vE;
		h7 ^= s3 ^ v7 ^ vF;
	}
	
	public int[] getH(){
		int [] erg = {h0,h1,h2,h3,h4,h5,h6,h7};
		return erg;
	}

}
