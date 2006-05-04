/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.injection;

import org.apache.tuscany.common.ObjectCreationException;

/**
 * Implementations inject a pre-configured value on an instance
 *
 * @version $Rev: 399488 $ $Date: 2006-05-03 16:20:27 -0700 (Wed, 03 May 2006) $
 * @see MethodInjector
 * @see FieldInjector
 */
public interface Injector<T> {

    /**
     * Inject a value on the given instance
     */
    void inject(T instance) throws ObjectCreationException;

}
