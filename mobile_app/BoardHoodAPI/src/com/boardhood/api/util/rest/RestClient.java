package com.boardhood.api.util.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.boardhood.api.util.rest.errors.HttpException;
import com.boardhood.api.util.rest.errors.UnauthorizedException;

@SuppressWarnings("deprecation")
public class RestClient {
	private static final int CONNECTION_TIMEOUT = 6000;
	private static final int SOCKET_TIMEOUT = 6000;

    private String baseUrl;
    private AbstractHttpClient httpClient;
    private Map<String, String> extraParams;
    
    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        extraParams = new HashMap<String, String>();
        setupHttpClient();
    }
    
	private void setupHttpClient() {
    	HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, "BoardHood/1.0");
        
        // Create and initialize scheme registry 
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
    	httpClient = new DefaultHttpClient(cm, httpParams);
    	httpClient.addRequestInterceptor(new GzipHttpRequestInterceptor());
        httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());
    }
    
    public void setCredentials(UsernamePasswordCredentials credentials) {
        httpClient.getCredentialsProvider().setCredentials(
        		new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), credentials);
    }
    
    public void clearCredentials() {
        httpClient.getCredentialsProvider().clear();
    }
    
    public Response get(Request req) throws IOException, URISyntaxException, HttpException {
        return request(req, new HttpGet());
    }
    
    public Response head(Request req) throws IOException, URISyntaxException, HttpException {
        return request(req, new HttpHead());
    }
    
    public Response delete(Request req) throws IOException, URISyntaxException, HttpException {
        return request(req, new HttpDelete());
    }
    
    public Response post(Request req) throws IOException, URISyntaxException, HttpException {
    	return entityEnclosingRequest(req, new HttpPost());
    }
    
    public Response put(Request req) throws IOException, URISyntaxException, HttpException {
        return entityEnclosingRequest(req, new HttpPut());
    }
    
    public Response putMultipart(Request req) throws IOException, URISyntaxException, HttpException {
        return multipartRequest(req, new HttpPut());
    }
    
    public Response postMultipart(Request req) throws IOException, URISyntaxException, HttpException {
        return multipartRequest(req, new HttpPost());
    }
    
    @SuppressWarnings("unchecked")
	private Response request(Request r, HttpRequestBase request) throws IOException, URISyntaxException, HttpException {
        String url = baseUrl + r.getResource() + serializeUrlParams(r.getParams(), extraParams);
        request.setURI(new URI(url));

        for(Map.Entry<String, String> header : r.getHeaders().entrySet())
        	request.addHeader(header.getKey(), header.getValue());

        return executeRequest(request, url);
    }
    
    @SuppressWarnings("unchecked")
    private Response entityEnclosingRequest(Request r, HttpEntityEnclosingRequestBase request) throws IOException, URISyntaxException, HttpException {
    	String url = baseUrl + r.getResource() + serializeUrlParams(extraParams);
    	request.setURI(new URI(url));

    	for(Map.Entry<String, String> header : r.getHeaders().entrySet())
        	request.addHeader(header.getKey(), header.getValue());

        if(!r.getParams().isEmpty())
            request.setEntity(new UrlEncodedFormEntity(r.getParamsList(), HTTP.UTF_8));

        return executeRequest(request, url);
    }
    
    @SuppressWarnings("unchecked")
    private Response multipartRequest(Request r, HttpEntityEnclosingRequestBase request) throws IOException, URISyntaxException, HttpException {
    	String url = baseUrl + r.getResource() + serializeUrlParams(extraParams);
    	request.setURI(new URI(url));
    	
    	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

    	for(Map.Entry<String, String> header : r.getHeaders().entrySet())
        	request.addHeader(header.getKey(), header.getValue());

        if(!r.getParams().isEmpty()) {
        	for (Map.Entry<String, String> entry : r.getParams().entrySet()) {
        		reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
        	}
        }
        
        if(!r.getByteArrays().isEmpty()) {
        	for (Entry<String, ByteArrayBody> entry : r.getByteArrays().entrySet()) {
        		reqEntity.addPart(entry.getKey(), entry.getValue());
        	}
        }
        
        request.setEntity(reqEntity);

        return executeRequest(request, url);
    }

    private Response executeRequest(HttpUriRequest request, String url) throws IOException, HttpException {
        HttpResponse httpResponse;
        Response response = new Response();

        httpResponse = httpClient.execute(request);

        response.setUrl(url);
        response.setMessage(httpResponse.getStatusLine().getReasonPhrase());
        response.setCode(httpResponse.getStatusLine().getStatusCode());

        HttpEntity entity = httpResponse.getEntity();
        
        if (entity != null) {
            InputStream instream = entity.getContent();
            response.setContent(convertStreamToString(instream));
            
            // Closing the input stream will trigger connection release
            instream.close();
        }
        
        if (response.getCode() == 401) {
        	throw new UnauthorizedException();
        }
        
        return response;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            	throw e;
            }
        }
        return sb.toString();
    }
    
    private String serializeUrlParams(Map<String, String>... allParams) throws UnsupportedEncodingException {
    	Map<String, String> params = new HashMap<String, String>();
    	for (int i=0; i < allParams.length; i++)
    		params.putAll(allParams[i]);
    	
    	String sParams = "";
    	int i = 0;
        for (Map.Entry<String, String> param : params.entrySet()) {
        	sParams += (i == 0) ? "?" : "&";
        	sParams += param.getKey() + "=" + URLEncoder.encode(param.getValue(), "UTF-8");
        	i++;
        }
        return sParams;
    }
    
    public void clearExtraParams() {
    	extraParams = new HashMap<String, String>();
    }
    
    public void addExtraParam(String name, String value) {
    	extraParams.put(name, value);
    }
    
    public void deleteExtraParam(String name) {
    	extraParams.remove(name);
    }
    
    private class GzipHttpRequestInterceptor implements HttpRequestInterceptor {
        public void process(final HttpRequest request, final HttpContext context) {
            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }
        }
    }

    private class GzipHttpResponseInterceptor implements HttpResponseInterceptor {
        public void process(final HttpResponse response, final HttpContext context) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
	            final Header encoding = entity.getContentEncoding();
	            if (encoding != null) {
	                inflateGzip(response, encoding);
	            }
            }
        }

        private void inflateGzip(final HttpResponse response, final Header encoding) {
            for (HeaderElement element : encoding.getElements()) {
                if (element.getName().equalsIgnoreCase("gzip")) {
                    response.setEntity(new GzipInflatingEntity(response.getEntity()));
                    break;
                }
            }
        }
    }

    private class GzipInflatingEntity extends HttpEntityWrapper {
        public GzipInflatingEntity(final HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
