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
 package org.apache.tuscany.sca.itest;

/**
 * A business exception
 *
 */
public class BusinessFault2 extends Exception {

	// Serialization UID
	private static final long serialVersionUID = 44240525335368929L;

	public BusinessFault2() {
		super();
	}

	public BusinessFault2(String arg0) {
		super(arg0);
	}

	public BusinessFault2(Throwable arg0) {
		super(arg0);
	}

	public BusinessFault2(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

} // end class BusinessFault1
