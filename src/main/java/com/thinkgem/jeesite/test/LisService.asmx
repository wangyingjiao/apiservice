<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://tempuri.org/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" targetNamespace="http://tempuri.org/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://tempuri.org/">
      <s:element name="HelloWorld">
        <s:complexType />
      </s:element>
      <s:element name="HelloWorldResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="HelloWorldResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="T1">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="UserName" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="UserPwd" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PatName" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PatID" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="T1Response">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="T1Result">
              <s:complexType>
                <s:sequence>
                  <s:any minOccurs="0" maxOccurs="unbounded" namespace="http://www.w3.org/2001/XMLSchema" processContents="lax" />
                  <s:any minOccurs="1" namespace="urn:schemas-microsoft-com:xml-diffgram-v1" processContents="lax" />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisGetRepList">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="UserName" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="UserPwd" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PatName" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PatID" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisGetRepListResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="LisGetRepListResult">
              <s:complexType>
                <s:sequence>
                  <s:any minOccurs="0" maxOccurs="unbounded" namespace="http://www.w3.org/2001/XMLSchema" processContents="lax" />
                  <s:any minOccurs="1" namespace="urn:schemas-microsoft-com:xml-diffgram-v1" processContents="lax" />
                </s:sequence>
              </s:complexType>
            </s:element>
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisGetRepXML">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="UserName" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="UserPwd" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PatName" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PatID" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisGetRepXMLResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="LisGetRepXMLResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisSetRepString">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="H_CODE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="H_IP" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="H_MAC" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="H_USER" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="H_PWD" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="HOSPITAL_CODE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_CARD_ID" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_NAME" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_SEX" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_BIRTHDAY" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_AGE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_AGE_UNIT" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="AT_AGE_MARRY" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_AGE_OCC" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_NATION" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_POO" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_ADDRESS" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_TEL" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_PHONE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_GWS" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="PAT_GMS" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_ID" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="_Date" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_Instr" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_Ssample_Code" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_MEDICAL_RECORD" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_NO_ZY" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_NO_MZ" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_NO_BED" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_DIAG" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_SAMPLE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_APP_DEPT" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_APP_USER" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_APP_TIME" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_SEND_TIME" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_TEST_TIME" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_PRINT_TIME" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_TEST_USER" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_EXAMINE_USER" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="J_REMARK" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_ITEM_NUM" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_ITEM_CODE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_ITEM_NAME" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_VALUE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_REF" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_BADGE" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_UNIT" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_OD" type="s:string" />
            <s:element minOccurs="0" maxOccurs="1" name="R_CUTOFF" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisSetRepStringResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="LisSetRepStringResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisSetRepXML">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="strXML" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="LisSetRepXMLResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="LisSetRepXMLResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="HelloWorldSoapIn">
    <wsdl:part name="parameters" element="tns:HelloWorld" />
  </wsdl:message>
  <wsdl:message name="HelloWorldSoapOut">
    <wsdl:part name="parameters" element="tns:HelloWorldResponse" />
  </wsdl:message>
  <wsdl:message name="T1SoapIn">
    <wsdl:part name="parameters" element="tns:T1" />
  </wsdl:message>
  <wsdl:message name="T1SoapOut">
    <wsdl:part name="parameters" element="tns:T1Response" />
  </wsdl:message>
  <wsdl:message name="LisGetRepListSoapIn">
    <wsdl:part name="parameters" element="tns:LisGetRepList" />
  </wsdl:message>
  <wsdl:message name="LisGetRepListSoapOut">
    <wsdl:part name="parameters" element="tns:LisGetRepListResponse" />
  </wsdl:message>
  <wsdl:message name="LisGetRepXMLSoapIn">
    <wsdl:part name="parameters" element="tns:LisGetRepXML" />
  </wsdl:message>
  <wsdl:message name="LisGetRepXMLSoapOut">
    <wsdl:part name="parameters" element="tns:LisGetRepXMLResponse" />
  </wsdl:message>
  <wsdl:message name="LisSetRepStringSoapIn">
    <wsdl:part name="parameters" element="tns:LisSetRepString" />
  </wsdl:message>
  <wsdl:message name="LisSetRepStringSoapOut">
    <wsdl:part name="parameters" element="tns:LisSetRepStringResponse" />
  </wsdl:message>
  <wsdl:message name="LisSetRepXMLSoapIn">
    <wsdl:part name="parameters" element="tns:LisSetRepXML" />
  </wsdl:message>
  <wsdl:message name="LisSetRepXMLSoapOut">
    <wsdl:part name="parameters" element="tns:LisSetRepXMLResponse" />
  </wsdl:message>
  <wsdl:portType name="LisServiceSoap">
    <wsdl:operation name="HelloWorld">
      <wsdl:input message="tns:HelloWorldSoapIn" />
      <wsdl:output message="tns:HelloWorldSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="T1">
      <wsdl:input message="tns:T1SoapIn" />
      <wsdl:output message="tns:T1SoapOut" />
    </wsdl:operation>
    <wsdl:operation name="LisGetRepList">
      <wsdl:input message="tns:LisGetRepListSoapIn" />
      <wsdl:output message="tns:LisGetRepListSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="LisGetRepXML">
      <wsdl:input message="tns:LisGetRepXMLSoapIn" />
      <wsdl:output message="tns:LisGetRepXMLSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="LisSetRepString">
      <wsdl:input message="tns:LisSetRepStringSoapIn" />
      <wsdl:output message="tns:LisSetRepStringSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="LisSetRepXML">
      <wsdl:input message="tns:LisSetRepXMLSoapIn" />
      <wsdl:output message="tns:LisSetRepXMLSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="LisServiceSoap" type="tns:LisServiceSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="HelloWorld">
      <soap:operation soapAction="http://tempuri.org/HelloWorld" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="T1">
      <soap:operation soapAction="http://tempuri.org/T1" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisGetRepList">
      <soap:operation soapAction="http://tempuri.org/LisGetRepList" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisGetRepXML">
      <soap:operation soapAction="http://tempuri.org/LisGetRepXML" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisSetRepString">
      <soap:operation soapAction="http://tempuri.org/LisSetRepString" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisSetRepXML">
      <soap:operation soapAction="http://tempuri.org/LisSetRepXML" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="LisServiceSoap12" type="tns:LisServiceSoap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="HelloWorld">
      <soap12:operation soapAction="http://tempuri.org/HelloWorld" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="T1">
      <soap12:operation soapAction="http://tempuri.org/T1" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisGetRepList">
      <soap12:operation soapAction="http://tempuri.org/LisGetRepList" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisGetRepXML">
      <soap12:operation soapAction="http://tempuri.org/LisGetRepXML" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisSetRepString">
      <soap12:operation soapAction="http://tempuri.org/LisSetRepString" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="LisSetRepXML">
      <soap12:operation soapAction="http://tempuri.org/LisSetRepXML" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="LisService">
    <wsdl:port name="LisServiceSoap" binding="tns:LisServiceSoap">
      <soap:address location="http://182.106.189.84:7179/LisService/LisService.asmx" />
    </wsdl:port>
    <wsdl:port name="LisServiceSoap12" binding="tns:LisServiceSoap12">
      <soap12:address location="http://182.106.189.84:7179/LisService/LisService.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>