/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * Thrown when constructor parameters cannot be unambiguously resolved to a property or reference
 *
 * @version $Rev$ $Date$
 */
public class AmbiguousConstructorException extends ProcessingException {
    public AmbiguousConstructorException() {
    }

    public AmbiguousConstructorException(String message) {
        super(message);
    }

    public AmbiguousConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousConstructorException(Throwable cause) {
        super(cause);
    }
}
