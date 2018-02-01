/*******************************************************************************
 * Copyright IBM Corp. 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.integration.payment.be;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.w3c.dom.Document;
import com.ibm.integration.payment.be.rest.HttpRestClient;
import com.ibm.integration.payment.be.rest.HttpRestClient.HttpRestException;
import com.ibm.integration.payment.be.rest.HttpRestClient.Response;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCConfigurator;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;

public class InvokeExternalPayment  implements YIFCustomApi{
	private Properties properties = null;

	private static YFCLogCategory cat = YFCLogCategory.instance(InvokeExternalPayment.class.getName());

	@Override
	public void setProperties(Properties paramProperties) throws Exception {
		properties = paramProperties;
        Enumeration<?> keys = properties.propertyNames();
        cat.verbose("InvokeExternalPayment properties...");
        while (keys.hasMoreElements()) {
          String propertyName = (String) keys.nextElement();
          String value = properties.getProperty(propertyName);
          cat.verbose("Property [" + propertyName + ":" + value + "]");
        }
		
	}
	
	public Document invokePayment(YFSEnvironment env, Document inXML){
		System.out.println("#######inside invokePayment########");
		if(!YFCObject.isVoid(inXML)){
			System.out.println(YFCDocument.getDocumentFor(inXML).toString());
			YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
			if(!YFCObject.isVoid(inDoc)){
				YFCElement inElem = inDoc.getDocumentElement();
				String txnType = inElem.getAttribute("TransactionType");
				if(!YFCObject.isVoid(txnType)){
					return executeExternalCall(env, inDoc);
				}else{
					YFCException ex = new YFCException("NO_TRANSACTION_TYPE", "Type of transaction is undefined, it has to be one of RemotePay or Refund or TransactionDetials");
					ex.setErrorDescription("Type of transactino is undefined");
					throw ex;
				}        		
			}
			else{
				YFCException ex = new YFCException("EMPTY_INPUT", "Input document is empty, please pass valid input document");
				ex.setErrorDescription("Input document is empty");
				throw ex;
			} 
		}
		return null;
	}
	
	private Document executeExternalCall(YFSEnvironment env, YFCDocument inDoc){
		YFCElement inElem = inDoc.getDocumentElement();
		String txnType = inElem.getAttribute("TransactionType");
		String url = YFCConfigurator.getInstance().getProperty("ezeTapUrl");
		String endPoint = getEndPoint(txnType);
		
		if(!YFCObject.isVoid(url) && !YFCObject.isVoid(endPoint)) {
			JSONObject json = getInputJSON(env, txnType, inElem);
			if(!YFCObject.isVoid(json)) {
				System.out.println(json.toString());
				HttpRestClient rc = new HttpRestClient(url, endPoint);
				rc.setPostParams(json);
				rc.setHeader("Content-Type", "application/json");
				try {
					Response res = rc.post();
					System.out.println(res.getCode());
					String message = res.getMessage();
					System.out.println(message);
					if(message != null && res.getCode()==200) {
						JSONObject result = new JSONObject(message);
						System.out.println(result.toString());
						if(result.getBoolean("success")) {
							YFCDocument outDoc = YFCDocument.createDocument("Order");
							YFCElement outElem = outDoc.getDocumentElement();
							outElem.setAttribute("Success", true);
							outElem.setAttribute("OrderHeaderKey", inElem.getAttribute("OrderHeaderKey"));
							outElem.setAttribute("TransactionId", result.getString("txnId"));
							System.out.println(outElem);
							return outDoc.getDocument();
						}else {
							YFCException ex = new YFCException(result.getString("errorCode"), result.getString("errorMessage"));
							ex.setErrorDescription(result.getString("errorMessage"));
							throw ex;
						}
					}else {
						YFCException ex =  new YFCException("HTTP Error code "+res.getCode(), res.getMessage());
						ex.setErrorDescription(res.getMessage());
						throw ex;
					}
				} catch (JSONException jsonEx) {
					jsonEx.printStackTrace();
					throw new YFCException(jsonEx);
				} catch (HttpRestException restEx) {
					restEx.printStackTrace();
					throw new YFCException(restEx);
				}
			}
		}else {
			YFCException ex = new YFCException("URL_NOT_CONFIGURED", "Ezetap url and end points are not configured");
			ex.setErrorDescription("Ezetap url and end points are not configured");
			throw ex;
		}
		
		return null;
	}

	private JSONObject getInputJSON(YFSEnvironment env, String requestType, YFCElement inElem) {
		JSONObject json = new JSONObject();
		try {
			json.put("appKey", YFCConfigurator.getInstance().getProperty("ezeTapAppKey"));
			json.put("username", YFCConfigurator.getInstance().getProperty("ezeTapUserName"));
			
			if(YFCObject.equals(requestType, "RemotePay")){
				getInputJSONForRemotePay(json, inElem, env);
			}else if(YFCObject.equals(requestType, "Refund")){
				getInputJSONForRefund(json, inElem, env);
			}else if(YFCObject.equals(requestType, "TransactionDetials")){
				getInputJSONForTxnDetails(json, inElem);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new YFCException(e);
		}
		return json;
	}
	
	private void getInputJSONForRemotePay(JSONObject json, YFCElement inElem, YFSEnvironment env) throws JSONException {
		json.put("externalRefNumber", inElem.getAttribute("OrderHeaderKey"));
		YFCElement billTo = inElem.getChildElement("PersonInfoBillTo");
		if(!YFCObject.isVoid(billTo))
			json.put("customerMobileNumber", getMobileNoFromContact(billTo));
		YFCElement orderTotals = inElem.getChildElement("OverallTotals");
		if(!YFCObject.isVoid(orderTotals))
			json.put("amount", orderTotals.getAttribute("GrandTotal"));
		json.put("payMode", "WALLET");
		json.put("agentMobileNumber", getAgentMobileNo(env));
		json.put("expiryTime", "10");
		
	}
	
	private String getAgentMobileNo(YFSEnvironment env) {
		String csrLoginId = env.getUserId();
		YFCDocument apiInDoc = YFCDocument.getDocumentFor("<User Loginid=\""+csrLoginId+"\"/>");
		YFCDocument templateDoc = YFCDocument.getDocumentFor("<User Loginid=\"\"><ContactPersonInfo DayPhone=\"\" EveningPhone=\"\" MobilePhone=\"\"/></User>");
		YFCDocument csrDetails = YCDFoundationBridge.invokeAPI(env, "getUserHierarchy", apiInDoc, templateDoc);
		if(!YFCObject.isVoid(csrDetails)) {
			YFCElement contactElem = csrDetails.getDocumentElement().getChildElement("ContactPersonInfo");
			if(!YFCObject.isVoid(contactElem))
				return getMobileNoFromContact(contactElem);
		}
		return null;
	}

	private String getMobileNoFromContact(YFCElement contactElem) {
		if(!YFCObject.isVoid(contactElem.getAttribute("MobilePhone")))
			return contactElem.getAttribute("MobilePhone");
		if(!YFCObject.isVoid(contactElem.getAttribute("DayPhone")))
			return contactElem.getAttribute("DayPhone");
		if(!YFCObject.isVoid(contactElem.getAttribute("EveningPhone")))
			return contactElem.getAttribute("EveningPhone");
		return null;
	}

	private void getInputJSONForRefund(JSONObject json, YFCElement inElem, YFSEnvironment env) throws JSONException {
		String orderHdrKey = inElem.getAttribute("OrderHeaderKey");
		if(!YFCObject.isVoid(orderHdrKey)) {
			YFCDocument apiInDoc = YFCDocument.getDocumentFor("<Order OrderHeaderKey=\""+orderHdrKey+"\"/>");
			YFCDocument templateDoc = YFCDocument.getDocumentFor("<Order OrderHeaderKey=\"\" OrderNo=\"\" DocumentType=\"\"><PaymentMethods><PaymentMethod/></PaymentMethods><OrderLines><OrderLine OrderLineKey=\"\"><DerivedFromOrder/></OrderLine></OrderLines></Order>");
			YFCDocument returnDetails = YCDFoundationBridge.invokeAPI(env, "getOrderDetails", apiInDoc, templateDoc);
			YFCElement orderLine = (YFCElement) ((YFCNodeList<YFCElement>)returnDetails.getElementsByTagName("OrderLine")).item(0);
			if(!YFCObject.isVoid(orderLine)) {
				YFCElement originalOrderElem = orderLine.getChildElement("DerivedFromOrder");
				if(!YFCObject.isVoid(originalOrderElem)) {//it means the return is for an order. Also it assumes only one original order in return
					String origOrderHeaderKey = originalOrderElem.getAttribute("OrderHeaderKey");
					YFCElement paymentMethod = (YFCElement) ((YFCNodeList<YFCElement>)returnDetails.getElementsByTagName("PaymentMethod")).item(0);//again assumption is only one payment method and that has to be EZETAP
					if(!YFCObject.isVoid(paymentMethod) && YFCObject.equals("EZETAP", paymentMethod.getAttribute("PaymentType"))) {
						double amount = paymentMethod.getDoubleAttribute("PlannedRefundAmount");
						String originalTransactionId = paymentMethod.getAttribute("PaymentReference3");//original tran id is stored in PaymentReference3
						YFCElement refundInput = YFCDocument.createDocument("Order").getDocumentElement();
						json.put("originalTransactionId", originalTransactionId);
						//json.put("externalRefNumber1", originalTransactionId);
						json.put("externalRefNumber2", orderHdrKey);
						json.put("amount", amount);
					}
				}
			}
		}
		
	}
	
	private void getInputJSONForTxnDetails(JSONObject json, YFCElement inElem) throws JSONException {
		json.put("txnId", "170823125547772E010046059");
	}

	private String getEndPoint(String requestType) {
		if(YFCObject.equals(requestType, "RemotePay")){
			return YFCConfigurator.getInstance().getProperty("ezeTapRemotePayEndPoint");
		}else if(YFCObject.equals(requestType, "Refund")){
			return YFCConfigurator.getInstance().getProperty("ezeTapRefundEndPoint");
		}else if(YFCObject.equals(requestType, "TransactionDetials")){
			return YFCConfigurator.getInstance().getProperty("ezeTapTrasactionEndPoint");
		}
		return null;
	}
}
