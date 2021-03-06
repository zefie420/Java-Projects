/*
 * Copyright 2014 Lurf Jurv
 * All rights reserved
 */
package cat;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author leijurv
 */
public class CatInputStream extends InputStream {
    private final CAT c;
    private final InputStream input;
    public CatInputStream(InputStream input, CAT c) {
        this.c = c;
        this.input = input;
    }
    @Override
    public int read() throws IOException {
        int a = input.read();
        if (a == -1) {
            return a;
        } else {
            byte b = (byte) (a & 0xff);
            b = c.decode(b);
            if (b < 0) {
                return 256 + b;
            }
            return b;
        }
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int a = input.read(b, off, len);
        for (int i = off; i < off + a; i++) {
            b[i] = c.decode(b[i]);
        }
        return a;
    }
    @Override
    public int available() throws IOException {
        return input.available();
    }
    @Override
    public void close() throws IOException {
        input.close();
    }
    @Override
    public void reset() throws IOException {
        input.reset();
    }
}
