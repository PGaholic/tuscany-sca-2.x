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
package org.apache.tuscany.sca.implementation.java.introspect.impl;

import java.lang.reflect.Member;

import org.apache.tuscany.sca.implementation.java.IntrospectionException;

/**
 * Denotes an illegal signature for a method decorated with {@link org.oasisopen.sca.annotation.Context}
 *
 * @version $Rev$ $Date$
 */
public class IllegalContextException extends IntrospectionException {
    private static final long serialVersionUID = -6946383136750117008L;

    public IllegalContextException(String message) {
        super(message);
    }

    public IllegalContextException(String message, Member member) {
        super(message, member);
    }
}
