/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.binding.rest.provider;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.rest.RESTCacheContext;
import org.apache.tuscany.sca.common.http.HTTPContentTypeMapper;
import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.invocation.MessageFactory;

/**
 * Servlet responsible for dispatching HTTP requests to the
 * target component implementation.
 *
 * @version $Rev$ $Date$
 */
public class RESTBindingListenerServlet extends HttpServlet {
    private static final long serialVersionUID = 2865466417329430610L;

    transient private MessageFactory messageFactory;

    transient private Binding binding;
    transient private Invoker bindingInvoker;

    private Invoker getInvoker;
    private Invoker conditionalGetInvoker;
    private Invoker putInvoker;
    private Invoker conditionalPutInvoker;
    private Invoker postInvoker;
    private Invoker conditionalPostInvoker;
    private Invoker deleteInvoker;
    private Invoker conditionalDeleteInvoker;
    
    /**
     * Constructs a new RESTServiceListenerServlet.
     */
    public RESTBindingListenerServlet(Binding binding, Invoker bindingInvoker, MessageFactory messageFactory) {
        this.binding = binding;
        this.bindingInvoker = bindingInvoker;
        this.messageFactory = messageFactory;
    }

    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if( binding.getOperationSelector() != null && binding.getRequestWireFormat() != null) {
            // Decode using the charset in the request if it exists otherwise
            // use UTF-8 as this is what all browser implementations use.
            String charset = request.getCharacterEncoding();
            if (charset == null) {
                charset = "UTF-8";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));

            // Read the request
            CharArrayWriter data = new CharArrayWriter();
            char[] buf = new char[4096];
            int ret;
            while ((ret = in.read(buf, 0, 4096)) != -1) {
                data.write(buf, 0, ret);
            }
            
            HTTPContext bindingContext = new HTTPContext();
            bindingContext.setHttpRequest(request);
            bindingContext.setHttpResponse(response);

            // Dispatch the service interaction to the service invoker
            Message requestMessage = messageFactory.createMessage();
            requestMessage.setBindingContext(bindingContext);
            if(data.size() > 0) {
                requestMessage.setBody(new Object[]{data});
            }
            
            Message responseMessage = bindingInvoker.invoke(requestMessage);
            
            // return response to client
            if (responseMessage.isFault()) {            
                // Turn a fault into an exception
                //throw new ServletException((Throwable)responseMessage.getBody());
                Throwable e = (Throwable)responseMessage.getBody();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            } else {
                byte[] bout;
                bout = responseMessage.<Object>getBody().toString().getBytes("UTF-8");
                response.getOutputStream().write(bout);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } 
        } else {
            super.service(request, response);
        }

        
    }    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();

        String id = path.substring(1);
        
        Message responseMessage = null;
        RESTCacheContext cacheContext = null;
        try { 
           cacheContext = RESTCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) { 
            
        }
        
        if (path == null || path.length() == 0 || path.equals("/")) {
            
        }

        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalGetInvoker != null )) {
            if(id != null && id.length() > 0) {
                requestMessage.setBody(new Object[] {id, cacheContext});
            } else {
                requestMessage.setBody(new Object[] {cacheContext});
            }

            responseMessage = conditionalGetInvoker.invoke(requestMessage);
        } else {
            if(id != null && id.length() > 0) {
                requestMessage.setBody(new Object[] {id});
            } else {
                //requestMessage.setBody(new Object[] {id});
            }

            responseMessage = getInvoker.invoke(requestMessage);
        }
        
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }
        		        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        if(response.getContentType() == null || response.getContentType().length() == 0){
            // Calculate content-type based on extension
            String contentType = HTTPContentTypeMapper.getContentType(id);
            if(contentType != null && contentType.length() >0) {
                response.setContentType(contentType);
            }
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();        
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        String id = path.substring(1);
        
        Message responseMessage = null;
        RESTCacheContext cacheContext = null;
        try { 
           cacheContext = RESTCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }
        
        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalDeleteInvoker != null )) {        
        	requestMessage.setBody(new Object[] {id, cacheContext});
        	responseMessage = conditionalDeleteInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {id});
        	responseMessage = deleteInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }        		
        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();        
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        String id = path.substring(1);
        
        Message responseMessage = null;
        RESTCacheContext cacheContext = null;
        try { 
           cacheContext = RESTCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }
        
        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalPutInvoker != null )) {        
        	requestMessage.setBody(new Object[] {id, cacheContext});
        	responseMessage = conditionalPutInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {id});
        	responseMessage = putInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }        		
        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }
        
        // Write the response from the service implementation to the response
        // output stream
        InputStream is = (InputStream)responseMessage.getBody();
        OutputStream os = response.getOutputStream(); 
        byte[] buffer = new byte[2048];
        for (;;) {
            int n = is.read(buffer);
            if (n <= 0)
                break;
            os.write(buffer, 0, n);
        }
        os.flush();
        os.close();        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the request path
        String path = URLDecoder.decode(request.getRequestURI().substring(request.getServletPath().length()), "UTF-8");
        if (path.length() ==0) {            
            // Redirect to a URL ending with / to make relative hrefs work
            // relative to the served resource.
            response.sendRedirect(request.getRequestURL().append('/').toString());
            return;
        }

        // Invoke the get operation on the service implementation
        Message requestMessage = messageFactory.createMessage();
        // String id = path.substring(1);
        
        Message responseMessage = null;
        RESTCacheContext cacheContext = null;
        try { 
           cacheContext = RESTCacheContext.getCacheContextFromRequest(request);
        } catch (ParseException e) {        	
        }
        
        // Route message based on availability of cache info and cache methods
        if (( cacheContext != null ) && (cacheContext.isEnabled()) && (conditionalPostInvoker != null )) {        
        	requestMessage.setBody(new Object[] {cacheContext});
        	responseMessage = conditionalPostInvoker.invoke(requestMessage);
        } else {
        	requestMessage.setBody(new Object[] {});
        	responseMessage = postInvoker.invoke(requestMessage);
        }
        if (responseMessage.isFault()) {
        	Object body = responseMessage.getBody();
        	
        	int index = -1;
        	if ( -1 < (index = body.getClass().getName().indexOf( "NotModifiedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
        		return;
        	} else if ( -1 < (index = body.getClass().getName().indexOf( "PreconditionFailedException")) ) {
        		if ( index > -1 ) 
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED, body.toString().substring( index ));
        		else
            		response.sendError( HttpServletResponse.SC_PRECONDITION_FAILED );
        		return;            		
            }        		
        	
            throw new ServletException((Throwable)responseMessage.getBody());
        }


        // Test if the ETag and LastModified are returned as a cache context.
    	Object body = responseMessage.getBody();
    	if ( body.getClass() == RESTCacheContext.class ) {
    		// Transfer to header if so.
    		RESTCacheContext cc = (RESTCacheContext)responseMessage.getBody();
    		if (( cc != null ) && ( cc.isEnabled() )) {
    			String eTag = cc.getETag();
            	if ( eTag != null )
            		response.setHeader( "ETag", cc.getETag() );
            	String lastModified = cc.getLastModified();
            	if ( lastModified != null)
            		response.setHeader( "LastModified", cc.getLastModified() );
    		}
    	}
    }

    /**
     * @return the getInvoker
     */
    public Invoker getGetInvoker() {
        return getInvoker;
    }

    /**
     * @param getInvoker the getInvoker to set
     */
    public void setGetInvoker(Invoker getInvoker) {
        this.getInvoker = getInvoker;
    }

    /**
     * @return the conditionalGetInvoker
     */
    public Invoker getConditionalGetInvoker() {
        return conditionalGetInvoker;
    }

    /**
     * @param conditionalGetInvoker the conditionalGetInvoker to set
     */
    public void setConditionalGetInvoker(Invoker conditionalGetInvoker) {
        this.conditionalGetInvoker = conditionalGetInvoker;
    }

    /**
     * @return the putInvoker
     */
    public Invoker getPutInvoker() {
        return putInvoker;
    }

    /**
     * @param putInvoker the putInvoker to set
     */
    public void setPutInvoker(Invoker putInvoker) {
        this.putInvoker = putInvoker;
    }

    /**
     * @return the conditionalPutInvoker
     */
    public Invoker getConditionalPutInvoker() {
        return conditionalPutInvoker;
    }

    /**
     * @param conditionalPutInvoker the conditionalPutInvoker to set
     */
    public void setConditionalPutInvoker(Invoker conditionalPutInvoker) {
        this.conditionalPutInvoker = conditionalPutInvoker;
    }

    /**
     * @return the postInvoker
     */
    public Invoker getPostInvoker() {
        return postInvoker;
    }

    /**
     * @param postInvoker the postInvoker to set
     */
    public void setPostInvoker(Invoker postInvoker) {
        this.postInvoker = postInvoker;
    }

    /**
     * @return the conditionalPostInvoker
     */
    public Invoker getConditionalPostInvoker() {
        return conditionalPostInvoker;
    }

    /**
     * @param conditionalPostInvoker the conditionalPostInvoker to set
     */
    public void setConditionalPostInvoker(Invoker conditionalPostInvoker) {
        this.conditionalPostInvoker = conditionalPostInvoker;
    }

    /**
     * @return the deleteInvoker
     */
    public Invoker getDeleteInvoker() {
        return deleteInvoker;
    }

    /**
     * @param deleteInvoker the deleteInvoker to set
     */
    public void setDeleteInvoker(Invoker deleteInvoker) {
        this.deleteInvoker = deleteInvoker;
    }

    /**
     * @return the conditionalDeleteInvoker
     */
    public Invoker getConditionalDeleteInvoker() {
        return conditionalDeleteInvoker;
    }

    /**
     * @param conditionalDeleteInvoker the conditionalDeleteInvoker to set
     */
    public void setConditionalDeleteInvoker(Invoker conditionalDeleteInvoker) {
        this.conditionalDeleteInvoker = conditionalDeleteInvoker;
    }
}
