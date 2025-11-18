package com.sib.ibanklosucl.config.secure;

import javax.servlet.ServletInputStream;
import javax.servlet.ReadListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] rawData;
    private HttpServletRequest request;
    private ResettableServletInputStream servletStream;

    public ResettableStreamHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletStream = new ResettableServletInputStream();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawData == null) {
            rawData = toByteArray(request.getInputStream());
            servletStream.setStream(new ByteArrayInputStream(rawData));
        }
        return servletStream;
    }

    public void resetInputStream() {
        servletStream.setStream(new ByteArrayInputStream(rawData));
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }

    private class ResettableServletInputStream extends ServletInputStream {

        private InputStream stream;

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public boolean isFinished() {
            try {
                return stream.available() == 0;
            } catch (IOException e) {
                return true; // Default to finished if an IO error occurs.
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }
    }
}