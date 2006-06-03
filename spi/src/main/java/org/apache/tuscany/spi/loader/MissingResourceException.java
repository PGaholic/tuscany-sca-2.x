/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.spi.loader;

/**
 * Exception that indicates an expected resource could not be found. The message should be set to the name of
 * the resource.
 *
 * @version $Rev$ $Date$
 */
public class MissingResourceException extends LoaderException {
    private static final long serialVersionUID = 3775013318397916445L;

    /**
     * Constructor that indicates which resource could not be found. The supplied parameter is also returned
     * as the message.
     *
     * @param resource the resource that could not be found
     */
    public MissingResourceException(String resource) {
        super(resource);
    }

    /**
     * Constructor that indicates which resource could not be found. The supplied parameter is also returned
     * as the message.
     *
     * @param resource the resource that could not be found
     */
    public MissingResourceException(String resource, Throwable cause) {
        super(resource, cause);
    }
}
