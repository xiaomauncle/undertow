/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.undertow.testutils;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * @author Stuart Douglas
 */
public class TestHttpClient extends DefaultHttpClient {

    private static final X509HostnameVerifier NO_OP_VERIFIER = new X509HostnameVerifier() {
        @Override
        public void verify(String host, SSLSocket ssl) throws IOException {
        }

        @Override
        public void verify(String host, X509Certificate cert) throws SSLException {
        }

        @Override
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        }

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    @Override
    protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
        return new DefaultHttpRequestRetryHandler(0, false);
    }

    @Override
    protected HttpParams createHttpParams() {
        HttpParams params = super.createHttpParams();
        HttpConnectionParams.setSoTimeout(params, 30000);
        return params;
    }

    public void setSSLContext(final SSLContext sslContext) {
        SchemeRegistry registry = getConnectionManager().getSchemeRegistry();
        registry.unregister("https");
        if (DefaultServer.getHostAddress(DefaultServer.DEFAULT).equals("localhost")) {
            registry.register(new Scheme("https", 443, new SSLSocketFactory(sslContext)));
            registry.register(new Scheme("https", DefaultServer.getHostSSLPort("default"), new SSLSocketFactory(sslContext)));
        } else {
            registry.register(new Scheme("https", 443, new SSLSocketFactory(sslContext, NO_OP_VERIFIER)));
            registry.register(new Scheme("https", DefaultServer.getHostSSLPort("default"), new SSLSocketFactory(sslContext, NO_OP_VERIFIER)));
        }

    }


}
