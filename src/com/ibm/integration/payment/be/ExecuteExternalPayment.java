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
import java.util.Properties;
import org.w3c.dom.Document;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfs.japi.YFSEnvironment;
public class ExecuteExternalPayment  implements YIFCustomApi{
	public Document executePayment(YFSEnvironment env, Document inXML){
        if(!YFCObject.isVoid(inXML)){
        	YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
        	if(!YFCObject.isVoid(inDoc)){
        		YFCElement inElem = inDoc.getDocumentElement();
        		String errorCode = inElem.getAttribute("errorCode");
        		if(YFCObject.isVoid(errorCode)){
        			String txnType = inElem.getAttribute("txnType");
        			if(YFCObject.equals(txnType, "REMOTE_PAY")){
        				executeRemotePay(env, inDoc);
        			}else if(YFCObject.equals(txnType, "CHARGE")){
        				//Don't do anhthing, this is the usecase for store order payment. It is handled in UI so no need to do anything from backend. If requried, this type of transaction can be split between UI and backend
        			}else if(YFCObject.equals(txnType, "REFUND")){
        				executeRefund(env, inDoc);
        			}
        		}else{
        			//no handling required for now, payment transaction failed event
        		}
        	}
        }else{
	        	//no handling required
	    }
    	return YFCDocument.getDocumentFor("<ExecuteExternalPayment Succss=\"true\" />").getDocument();
    }
	private void executeRemotePay(YFSEnvironment env, YFCDocument inDoc) {
		YFCElement inElem = inDoc.getDocumentElement();
		String orderHeaderKey = inElem.getAttribute("externalRefNumber");
		String status = inElem.getAttribute("status");
		if(!YFCObject.isVoid(orderHeaderKey) && YFCObject.equals(status, "AUTHORIZED")){
			if(isPaymentPendingforOrder(env, orderHeaderKey)){
				invokeCapturePayment(env, inElem);
				invokeRequestCollection(env, inElem);
			}
		}else{
			//no processing requried
		}
	}
	private void invokeCapturePayment(YFSEnvironment env, YFCElement inElem) {
		String orderHeaderKey = inElem.getAttribute("externalRefNumber");
		String requestedAmount = inElem.getAttribute("amount");
		String paymentMode = inElem.getAttribute("paymentMode");
		String paymentReference1 = paymentMode;
		String paymentReference2 = inElem.getAttribute("authCode");
		String paymentReference3 = inElem.getAttribute("txnId");
		String paymentReference4 = "";
		if(YFCObject.equals(paymentMode, "CARD")){
			paymentReference4 = inElem.getAttribute("paymentCardType");
		}
		YFCDocument paymentCaptureDoc = YFCDocument.createDocument("CapturePayment");
		YFCElement paymentCaptureInElem = paymentCaptureDoc.getDocumentElement();
		paymentCaptureInElem.setAttribute("OrderHeaderKey", orderHeaderKey);
		YFCElement paymentMethod = paymentCaptureInElem.createChild("PaymentMethods").createChild("PaymentMethod");
		paymentMethod.setAttribute("PaymentReference1", paymentReference1);
		paymentMethod.setAttribute("PaymentReference2", paymentReference2);
		paymentMethod.setAttribute("PaymentReference3", paymentReference3);
		paymentMethod.setAttribute("PaymentReference4", paymentReference4);
		paymentMethod.setAttribute("PaymentType", "EZETAP");
		paymentMethod.setAttribute("RequestedAmount", requestedAmount);
		paymentMethod.setAttribute("Operation", "Collect");
		YCDFoundationBridge.invokeAPI(env, "capturePayment", paymentCaptureDoc);
	}
	private void invokeProcessOrderPayment(YFSEnvironment env, YFCElement inElem) {
		String orderHeaderKey = inElem.getAttribute("externalRefNumber");
		YFCDocument paymentProcessDoc = YFCDocument.createDocument("Order");
		YFCElement paymentProcessInElem = paymentProcessDoc.getDocumentElement();
		paymentProcessInElem.setAttribute("OrderHeaderKey", orderHeaderKey);
		YCDFoundationBridge.invokeAPI(env, "processOrderPayments", paymentProcessDoc);
	}
	private void invokeRequestCollection(YFSEnvironment env, YFCElement inElem) {
		String orderHeaderKey = inElem.getAttribute("externalRefNumber");
		YFCDocument paymentProcessDoc = YFCDocument.createDocument("Order");
		YFCElement paymentProcessInElem = paymentProcessDoc.getDocumentElement();
		paymentProcessInElem.setAttribute("OrderHeaderKey", orderHeaderKey);
		YCDFoundationBridge.invokeAPI(env, "requestCollection", paymentProcessDoc);
	}
    private boolean isPaymentPendingforOrder(YFSEnvironment env, String orderHeaderKey) {
		YFCDocument orderDetails = getOrderDetails(env, orderHeaderKey);
		if(!YFCObject.isVoid(orderDetails)){
			YFCElement orderDetailsElem = orderDetails.getDocumentElement(); 
			String paymentStatus = orderDetailsElem.getAttribute("PaymentStatus");
			double remainingAmtToAuth = orderDetailsElem.getChildElement("ChargeTransactionDetails").getDoubleAttribute("RemainingAmountToAuth");
			YFCNodeList<YFCElement> paymentMethods = orderDetailsElem.getElementsByTagName("PaymentMethod");
			if(remainingAmtToAuth > 0){
				return true;
			}
		}
		return false;
	}
    
	private YFCDocument getOrderDetails(YFSEnvironment env, String orderHeaderKey) {
		if(!YFCObject.isVoid(orderHeaderKey)){
			YFCDocument inDoc = YFCDocument.getDocumentFor("<Order OrderHeaderKey=\""+orderHeaderKey+"\"/>");
			YFCDocument template = YFCDocument.getDocumentFor("<Order OrderHeaderKey=\"\" PaymentStatus=\"\"><PaymentMethods><PaymentMethod/></PaymentMethods><ChargeTransactionDetails RemainingAmountToAuth=\"\"/></Order>");
			return YCDFoundationBridge.invokeAPI(env, "getOrderDetails", inDoc, template);
		}
		return null;
	}

	private void executeRefund(YFSEnvironment env, YFCDocument inDoc) {
		
		
	}

	@Override
	public void setProperties(Properties paramProperties) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
