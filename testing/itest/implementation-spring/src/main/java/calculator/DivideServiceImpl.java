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
package calculator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the Divide service.
 */
public class DivideServiceImpl implements DivideService {

    public double divide(double n1, double n2) {
        Logger logger = Logger.getLogger("calculator");
        logger.log(Level.FINEST, "Dividing " + n1 + " with " + n2);
        return n1 / n2;
    }

}
