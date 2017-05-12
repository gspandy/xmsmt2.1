/**
 *====================================================
 * 文件名称: VstBase64.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月8日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package org.apache.commons.codec.binary;

import java.math.BigInteger;

/**
 * @ClassName: VstBase64
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年8月8日 上午9:55:25
 */
public class ExBase64 extends BaseNCodec {

    private static final int BITS_PER_ENCODED_BYTE = 6;
    private static final int BYTES_PER_UNENCODED_BLOCK = 3;
    private static final int BYTES_PER_ENCODED_BLOCK = 4;

    static final byte[] CHUNK_SEPARATOR = {'\r', '\n'};

    private static final byte[] STANDARD_ENCODE_TABLE = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private static final byte[] URL_SAFE_ENCODE_TABLE = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };

    private static final byte[] DECODE_TABLE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54,
            55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 
            29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 
            46, 47, 48, 49, 50, 51, -1, -1, -1, -1, 63, -1, 0, 1, 2, 3, 4, 5, 6, 
            7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
    };

    private static final int MASK_6BITS = 0x3f;

    private final byte[] encodeTable;

    private final byte[] decodeTable = DECODE_TABLE;

    private final byte[] lineSeparator;

    private final int decodeSize;

    private final int encodeSize;

    private int bitWorkArea;

    public ExBase64() {
        this(0);
    }

    public ExBase64(boolean urlSafe) {
        this(MIME_CHUNK_SIZE, CHUNK_SEPARATOR, urlSafe);
    }

    public ExBase64(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public ExBase64(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }

	public ExBase64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
		super(BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK, lineLength, lineSeparator == null ? 0 : lineSeparator.length);
		if (lineSeparator != null) {
			if (containsAlphabetOrPad(lineSeparator)) {
				String sep = StringUtils.newStringUtf8(lineSeparator);
				throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
			}
			if (lineLength > 0) {
				this.encodeSize = BYTES_PER_ENCODED_BLOCK + lineSeparator.length;
				this.lineSeparator = new byte[lineSeparator.length];
				System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
			} else {
				this.encodeSize = BYTES_PER_ENCODED_BLOCK;
				this.lineSeparator = null;
			}
		} else {
			this.encodeSize = BYTES_PER_ENCODED_BLOCK;
			this.lineSeparator = null;
		}
		this.decodeSize = this.encodeSize - 1;
		this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
	}

    public boolean isUrlSafe() {
        return this.encodeTable == URL_SAFE_ENCODE_TABLE;
    }

    @Override
    void encode(byte[] in, int inPos, int inAvail) {
        if (eof) {
            return;
        }
        if (inAvail < 0) {
            eof = true;
            if (0 == modulus && lineLength == 0) {
                return; // no leftovers to process and not using chunking
            }
            ensureBufferSize(encodeSize);
            int savedPos = pos;
            switch (modulus) { // 0-2
                case 1 : // 8 bits = 6 + 2
                    buffer[pos++] = encodeTable[(bitWorkArea >> 2) & MASK_6BITS]; // top 6 bits
                    buffer[pos++] = encodeTable[(bitWorkArea << 4) & MASK_6BITS]; // remaining 2 
                    // URL-SAFE skips the padding to further reduce size.
                    if (encodeTable == STANDARD_ENCODE_TABLE) {
                        buffer[pos++] = PAD;
                        buffer[pos++] = PAD;
                    }
                    break;

                case 2 : // 16 bits = 6 + 6 + 4
                    buffer[pos++] = encodeTable[(bitWorkArea >> 10) & MASK_6BITS];
                    buffer[pos++] = encodeTable[(bitWorkArea >> 4) & MASK_6BITS];
                    buffer[pos++] = encodeTable[(bitWorkArea << 2) & MASK_6BITS];
                    // URL-SAFE skips the padding to further reduce size.
                    if (encodeTable == STANDARD_ENCODE_TABLE) {
                        buffer[pos++] = PAD;
                    }
                    break;
            }
            currentLinePos += pos - savedPos; // keep track of current line position
            // if currentPos == 0 we are at the start of a line, so don't add CRLF
            if (lineLength > 0 && currentLinePos > 0) { 
                System.arraycopy(lineSeparator, 0, buffer, pos, lineSeparator.length);
                pos += lineSeparator.length;
            }
        } else {
            for (int i = 0; i < inAvail; i++) {
                ensureBufferSize(encodeSize);
                modulus = (modulus+1) % BYTES_PER_UNENCODED_BLOCK;
                int b = in[inPos++];
                if (b < 0) {
                    b += 256;
                }
                bitWorkArea = (bitWorkArea << 8) + b; //  BITS_PER_BYTE
                if (0 == modulus) { // 3 bytes = 24 bits = 4 * 6 bits to extract
                    buffer[pos++] = encodeTable[(bitWorkArea >> 18) & MASK_6BITS];
                    buffer[pos++] = encodeTable[(bitWorkArea >> 12) & MASK_6BITS];
                    buffer[pos++] = encodeTable[(bitWorkArea >> 6) & MASK_6BITS];
                    buffer[pos++] = encodeTable[bitWorkArea & MASK_6BITS];
                    currentLinePos += BYTES_PER_ENCODED_BLOCK;
                    if (lineLength > 0 && lineLength <= currentLinePos) {
                        System.arraycopy(lineSeparator, 0, buffer, pos, lineSeparator.length);
                        pos += lineSeparator.length;
                        currentLinePos = 0;
                    }
                }
            }
        }
    }

    @Override
    void decode(byte[] in, int inPos, int inAvail) {
        if (eof) {
            return;
        }
        if (inAvail < 0) {
            eof = true;
        }
        for (int i = 0; i < inAvail; i++) {
            ensureBufferSize(decodeSize);
            byte b = in[inPos++];
            if (b == PAD) {
                // We're done.
                eof = true;
                break;
            } else {
                if (b >= 0 && b < DECODE_TABLE.length) {
                    int result = DECODE_TABLE[b];
                    if (result >= 0) {
                        modulus = (modulus+1) % BYTES_PER_ENCODED_BLOCK;
                        bitWorkArea = (bitWorkArea << BITS_PER_ENCODED_BYTE) + result;
                        if (modulus == 0) {
                            buffer[pos++] = (byte) ((bitWorkArea >> 16) & MASK_8BITS);
                            buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                            buffer[pos++] = (byte) (bitWorkArea & MASK_8BITS);
                        }
                    }
                }
            }
        }

        if (eof && modulus != 0) {
            ensureBufferSize(decodeSize);
            
            switch (modulus) {
                case 2 : // 12 bits = 8 + 4
                    bitWorkArea = bitWorkArea >> 4; // dump the extra 4 bits
                    buffer[pos++] = (byte) ((bitWorkArea) & MASK_8BITS);
                    break;
                case 3 : // 18 bits = 8 + 8 + 2
                    bitWorkArea = bitWorkArea >> 2; // dump 2 bits
                    buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                    buffer[pos++] = (byte) ((bitWorkArea) & MASK_8BITS);
                    break;
            }
        }
    }


    public static boolean isBase64(byte octet) {
        return octet == PAD_DEFAULT || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1);
    }

    public static boolean isBase64(String base64) {
        return isBase64(StringUtils.getBytesUtf8(base64));
    }
    
    public static boolean isBase64(byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; i++) {
            if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static byte[] encodeBase64(byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }

    public static String encodeBase64String(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase64(binaryData, false));
    }
    
    public static byte[] encodeBase64URLSafe(byte[] binaryData) {
        return encodeBase64(binaryData, false, true);
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
    }    

    public static byte[] encodeBase64Chunked(byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }


    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }

        ExBase64 b64 = isChunked ? new ExBase64(urlSafe) : new ExBase64(0, CHUNK_SEPARATOR, urlSafe);
        long len = b64.getEncodedLength(binaryData);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
                len +
                ") than the specified maximum size of " +
                maxResultSize);
        }
                
        return b64.encode(binaryData);
    }

    public static byte[] decodeBase64(String base64String) {
        return new ExBase64().decode(base64String);
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        return new ExBase64().decode(base64Data);
    }

    public static BigInteger decodeInteger(byte[] pArray) {
        return new BigInteger(1, decodeBase64(pArray));
    }

    public static byte[] encodeInteger(BigInteger bigInt) {
        if (bigInt == null) {
            throw new NullPointerException("encodeInteger called with null parameter");
        }
        return encodeBase64(toIntegerBytes(bigInt), false);
    }

    static byte[] toIntegerBytes(BigInteger bigInt) {
        int bitlen = bigInt.bitLength();
        bitlen = ((bitlen + 7) >> 3) << 3;
        byte[] bigBytes = bigInt.toByteArray();

        if (((bigInt.bitLength() % 8) != 0) && (((bigInt.bitLength() / 8) + 1) == (bitlen / 8))) {
            return bigBytes;
        }
        // set up params for copying everything but sign bit
        int startSrc = 0;
        int len = bigBytes.length;

        // if bigInt is exactly byte-aligned, just skip signbit in copy
        if ((bigInt.bitLength() % 8) == 0) {
            startSrc = 1;
            len--;
        }
        int startDst = bitlen / 8 - len; // to pad w/ nulls as per spec
        byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
        return resizedBytes;
    }

    @Override
    protected boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < decodeTable.length && decodeTable[octet] != -1;
    }
    
    public static void main(String[] args) {
    	String message = "abcdedf123456ABCDEFG";
    	String encodeBase64String = ExBase64.encodeBase64String(message.getBytes());
    	System.out.println(encodeBase64String);
    	System.out.println(new String(ExBase64.decodeBase64(encodeBase64String)));
    }

}
