package com.sib.ibanklosucl.config.secure;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
            Pattern.compile("exec\\s", Pattern.CASE_INSENSITIVE), // EXEC keyword
            Pattern.compile("union\\s+select\\s", Pattern.CASE_INSENSITIVE), // UNION SELECT keyword
            Pattern.compile("insert\\s+into\\s", Pattern.CASE_INSENSITIVE), // INSERT INTO keyword
            Pattern.compile("select\\s+.*from\\s", Pattern.CASE_INSENSITIVE),// SELECT * FROM keyword
            Pattern.compile("drop\\s+table", Pattern.CASE_INSENSITIVE), // DROP TABLE keyword
            Pattern.compile("declare\\s+.*=", Pattern.CASE_INSENSITIVE), // DECLARE variable assignment
            Pattern.compile("1\\s*=\\s*1", Pattern.CASE_INSENSITIVE), // Tautological condition
            Pattern.compile("<"), // Less-than symbol
            Pattern.compile(">") // Greater-than symbol
    };

    private boolean containsSqlInjectionPattern(String value) {
        return Arrays.stream(SQL_INJECTION_PATTERNS)
                .anyMatch(pattern -> pattern.matcher(value).find());
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // Read the input stream and cache it after sanitization
        cachedBody = sanitizeStream(request.getInputStream());
    }

    // Sanitize input stream and block if invalid characters are found
    private byte[] sanitizeStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        while ((len = inputStream.read(data)) > -1) {
            buffer.write(data, 0, len);
        }
        String bodyContent = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        // Example check for HTML special characters or other invalid input
        if (containsSqlInjectionPattern(bodyContent)) {

            throw new IOException("Invalid characters found in request body");
        }
        return buffer.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream buffer;

        public CachedBodyServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public int read() {
            return buffer.read();
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
