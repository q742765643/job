package com.htht.job.executor.hander.webservicehandler.utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * * @author dingjiancheng
 */
public class NeoSecureSocketFactory implements ProtocolSocketFactory {
    private static SSLContext ssl = null;

    private static TrustManager[] getTrustManagers() {
        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String t) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String t) {
            }
        }};
        return certs;
    }

    private static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustManagers = getTrustManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);
        return sslContext;
    }

    private static SSLContext getSSLContext() {
        if (ssl == null) {
            try {
				ssl = createSSLContext();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
        }
        return ssl;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort, HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, clientHost, clientPort);
        }
        try(
        	Socket socket = socketfactory.createSocket();
        	){
	        SocketAddress localaddr = new InetSocketAddress(clientHost, clientPort);
	        SocketAddress remoteaddr = new InetSocketAddress(host, port);
	        socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        } catch (Exception e) {
            throw new ConnectTimeoutException(e.getMessage(), e);
        }
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }
}

