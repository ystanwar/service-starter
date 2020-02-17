//package com.thoughtworks.filter;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.slf4j.MDC;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import java.io.*;
//
//@Component
//public class PaymentFilter implements Filter {
//    private static Logger logger = LogManager.getLogger(PaymentFilter.class);
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpRequest;
//        httpRequest = (HttpServletRequest) servletRequest;
//        if (httpRequest.getRequestURL().toString().endsWith("/payments")) {
//            ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(
//                    httpRequest);
//
//            String body = IOUtils.toString(wrappedRequest.getReader());
//            wrappedRequest.resetInputStream();
//            MDC.put("req.id", String.valueOf(1));
//
//            filterChain.doFilter(wrappedRequest, servletResponse);
//
//            logger.info("payment called {}", body);
//
//            logger.info("payment request end");
//
//        }
//    }
//
//
//    private static class ResettableStreamHttpServletRequest extends
//            HttpServletRequestWrapper {
//
//        private byte[] rawData;
//        private HttpServletRequest request;
//        private ResettableServletInputStream servletStream;
//
//        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
//            super(request);
//            this.request = request;
//            this.servletStream = new ResettableServletInputStream();
//        }
//
//
//        public void resetInputStream() {
//            servletStream.stream = new ByteArrayInputStream(rawData);
//        }
//
//        @Override
//        public ServletInputStream getInputStream() throws IOException {
//            if (rawData == null) {
//                rawData = IOUtils.toByteArray(this.request.getReader());
//                servletStream.stream = new ByteArrayInputStream(rawData);
//            }
//            return servletStream;
//        }
//
//        @Override
//        public BufferedReader getReader() throws IOException {
//
//            if (rawData == null) {
//                rawData = IOUtils.toByteArray(this.request.getReader());
//                servletStream.stream = new ByteArrayInputStream(rawData);
//            }
//            return new BufferedReader(new InputStreamReader(servletStream));
//        }
//
//
//        private class ResettableServletInputStream extends ServletInputStream {
//
//            private InputStream stream;
//
//            @Override
//            public int read() throws IOException {
//                return stream.read();
//            }
//
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public boolean isReady() {
//                return false;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//
//            }
//        }
//    }
//
//}

