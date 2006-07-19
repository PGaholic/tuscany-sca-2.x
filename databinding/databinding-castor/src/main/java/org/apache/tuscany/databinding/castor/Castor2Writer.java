/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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

package org.apache.tuscany.databinding.castor;

import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.exolab.castor.xml.Marshaller;
import org.xml.sax.ContentHandler;

public class Castor2Writer<T> implements PushTransformer<T, ContentHandler> {
    private Class<T> type;

    /**
     * @param type
     */
    public Castor2Writer(Class<T> type) {
        super();
        this.type = type;
    }

    public Class<T> getSourceType() {
        return type;
    }

    public Class<ContentHandler> getTargetType() {
        return ContentHandler.class;
    }

    public int getWeight() {
        return 40;
    }

    /**
     * @see org.apache.tuscany.databinding.PullTransformer#transform(java.lang.Object, org.apache.tuscany.databinding.TransformationContext)
     */
    public void transform(T source, ContentHandler contentHandler, TransformationContext context) {
        try {
            Marshaller.marshal(source, contentHandler);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
