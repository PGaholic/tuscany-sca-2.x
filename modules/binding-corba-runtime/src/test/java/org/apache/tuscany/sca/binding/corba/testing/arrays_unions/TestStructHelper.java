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
package org.apache.tuscany.sca.binding.corba.testing.arrays_unions;


/**
* org/apache/tuscany/sca/binding/corba/testing/arrays_unions/TestStructHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from arrays_unions.idl
* niedziela, 17 sierpie� 2008 15:45:39 CEST
*/

abstract public class TestStructHelper
{
  private static String  _id = "IDL:org/apache/tuscany/sca/binding/corba/testing/arrays_unions/TestStruct/TestStruct:1.0";

  public static void insert (org.omg.CORBA.Any a, org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [3];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (2, _tcOf_members0 );
          _members0[0] = new org.omg.CORBA.StructMember (
            "oneDimArray",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_long);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (2, _tcOf_members0 );
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (4, _tcOf_members0 );
          _members0[1] = new org.omg.CORBA.StructMember (
            "twoDimArray",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_float);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (2, _tcOf_members0 );
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (4, _tcOf_members0 );
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (2, _tcOf_members0 );
          _members0[2] = new org.omg.CORBA.StructMember (
            "threeDimArray",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStructHelper.id (), "TestStruct", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct read (org.omg.CORBA.portable.InputStream istream)
  {
    org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct value = new org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct ();
    value.oneDimArray = new String[2];
    for (int _o0 = 0;_o0 < (2); ++_o0)
    {
      value.oneDimArray[_o0] = istream.read_string ();
    }
    value.twoDimArray = new int[2][];
    for (int _o1 = 0;_o1 < (2); ++_o1)
    {
      value.twoDimArray[_o1] = new int[4];
      for (int _o2 = 0;_o2 < (4); ++_o2)
      {
        value.twoDimArray[_o1][_o2] = istream.read_long ();
      }
    }
    value.threeDimArray = new float[2][][];
    for (int _o3 = 0;_o3 < (2); ++_o3)
    {
      value.threeDimArray[_o3] = new float[4][];
      for (int _o4 = 0;_o4 < (4); ++_o4)
      {
        value.threeDimArray[_o3][_o4] = new float[2];
        for (int _o5 = 0;_o5 < (2); ++_o5)
        {
          value.threeDimArray[_o3][_o4][_o5] = istream.read_float ();
        }
      }
    }
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.apache.tuscany.sca.binding.corba.testing.arrays_unions.TestStruct value)
  {
    if (value.oneDimArray.length != (2))
      throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    for (int _i0 = 0;_i0 < (2); ++_i0)
    {
      ostream.write_string (value.oneDimArray[_i0]);
    }
    if (value.twoDimArray.length != (2))
      throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    for (int _i1 = 0;_i1 < (2); ++_i1)
    {
      if (value.twoDimArray[_i1].length != (4))
        throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
      for (int _i2 = 0;_i2 < (4); ++_i2)
      {
        ostream.write_long (value.twoDimArray[_i1][_i2]);
      }
    }
    if (value.threeDimArray.length != (2))
      throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    for (int _i3 = 0;_i3 < (2); ++_i3)
    {
      if (value.threeDimArray[_i3].length != (4))
        throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
      for (int _i4 = 0;_i4 < (4); ++_i4)
      {
        if (value.threeDimArray[_i3][_i4].length != (2))
          throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        for (int _i5 = 0;_i5 < (2); ++_i5)
        {
          ostream.write_float (value.threeDimArray[_i3][_i4][_i5]);
        }
      }
    }
  }

}
