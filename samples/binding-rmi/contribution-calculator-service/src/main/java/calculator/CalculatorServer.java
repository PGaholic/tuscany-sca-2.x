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

import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;

/**
 * A claculator service server. Starts up the SCA runtime which 
 * will start listening for RMI service requests.
 */
public class CalculatorServer {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting of the SCA Calculator Application exposed as RMI Services...");
        String uri = ContributionLocationHelper.getContributionLocation("CalculatorRMIServer.composite");
        Contribution contribution = new Contribution("c1", uri);
        Node node = NodeFactory.newInstance().createNode("CalculatorRMIServer.composite", contribution);
        node.start();
        System.out.println("... Press Enter to Exit...");
        System.in.read();
        node.stop();
        System.out.println("Exited...");
        System.exit(0);
    }

}
