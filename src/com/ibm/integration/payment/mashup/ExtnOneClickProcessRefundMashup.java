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
package com.ibm.integration.payment.mashup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.ibm.sterling.G11N;
import com.ibm.wsc.core.WSCConstants;
import com.ibm.wsc.core.utils.WSCDataProviderUtils;
import com.ibm.wsc.core.utils.WSCSessionUtils;
import com.ibm.wsc.mashups.utils.WSCMashupUtils;
import com.ibm.wsc.order.payment.WSCPaymentUtil;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.extensions.ISCUIMashup;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.sterlingcommerce.ui.web.framework.utils.SCUIUtils;
import com.sterlingcommerce.woodstock.util.frame.Manager;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.ui.backend.util.APIManager;
import com.yantra.yfc.ui.backend.util.APIManager.XMLExceptionWrapper;
import com.yantra.yfc.util.YFCLocale;

public class ExtnOneClickProcessRefundMashup implements ISCUIMashup{
	
	private final YFCLogCategory	logger	= YFCLogCategory.instance(ExtnOneClickProcessRefundMashup.class);
	private static final String CAPTURE_PAYMENT_OPERATION_COLLECT = "Collect";

	@Override
	public Object execute(Object inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.execute");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Inside WSCOneClickProcessRefundMashup");
			logger.debug("Original Input to the mashup WSCOneClickProcessRefundMashup is : " + SCXmlUtil.getString((Element)inputEl));
		}
		
		WSCMashupUtils.addAttributeToUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, false);
		
		Element originalInput = SCXmlUtil.getCopy((Element)inputEl);
		
		Element returnInput = (Element)inputEl;
		
		boolean isRefundExpected = isRefundExpectedOnReturn(returnInput, uiContext);	
		boolean isExchangeOrderPresent = isExchangeOrderPresentOnReturn(returnInput, uiContext);
		boolean isLinesPresentOnReturnOrder = isLinesPresentOnReturn(originalInput, uiContext);
		boolean isLinesPresentOnExchangeOrder = isLinesPresentOnExchange(originalInput, uiContext, isExchangeOrderPresent);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Initalized boolen values.");
			logger.debug("isRefundExpected : " + isRefundExpected);
			logger.debug("isExchangeOrderPresent : " + isExchangeOrderPresent);
			logger.debug("isLinesPresentOnReturnOrder : " + isLinesPresentOnReturnOrder);
			logger.debug("isLinesPresentOnExchangeOrder : " + isLinesPresentOnExchangeOrder);
		}
		
		Element exchangeOrderInput = null;
		Element originalExchangeOrderInput = null;
		
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing return (and exchange) order API input now.");
		}
		
		if(isExchangeOrderPresent){
			if (logger.isDebugEnabled()) {
				logger.debug("Exchange order is present. Initializing API input for exchange order.");
			}
			exchangeOrderInput = SCXmlUtil.getCopy(getExchangeOrderInput(originalInput, uiContext));
			returnInput = SCXmlUtil.getCopy(getReturnOrderInput(originalInput, uiContext));
			originalExchangeOrderInput = SCXmlUtil.getCopy(exchangeOrderInput);
			SCXmlUtil.removeNode(SCXmlUtil.getChildElement(exchangeOrderInput, "Return"));
			exchangeOrderInput.removeAttribute("BillToID");
			exchangeOrderInput.removeAttribute("BillToKey");
			exchangeOrderInput.removeAttribute("ShipToKey");
			exchangeOrderInput.removeAttribute("EnterpriseCode");
			exchangeOrderInput.removeAttribute("CustomerContactID");
			exchangeOrderInput.removeAttribute("RefundExpected");
			exchangeOrderInput.removeAttribute("DocumentType");
			if(!SCUIUtils.isVoid(exchangeOrderInput.getAttribute("ReturnLinesPresent"))){
				exchangeOrderInput.removeAttribute("ReturnLinesPresent");
			}
			if(!SCUIUtils.isVoid(exchangeOrderInput.getAttribute("ExchangeLinesPresent"))){
				exchangeOrderInput.removeAttribute("ExchangeLinesPresent");
			}
			if(!SCUtil.isVoid(SCXmlUtil.getChildElement(exchangeOrderInput, "PaymentTypeList"))){
				SCXmlUtil.removeNode(SCXmlUtil.getChildElement(exchangeOrderInput, "PaymentTypeList"));;
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("Final exchange order input is : " + SCXmlUtil.getString(exchangeOrderInput));
			}
			
		}
		Element originalReturnInput = SCXmlUtil.getCopy(returnInput);
		Element paymentTypeList = null;		
		
		if(!SCUtil.isVoid(SCXmlUtil.getChildElement(originalInput, "PaymentTypeList"))){
			paymentTypeList = SCXmlUtil.getCopy(SCXmlUtil.getChildElement(originalInput, "PaymentTypeList"));
			SCXmlUtil.removeNode(SCXmlUtil.getChildElement(originalInput, "PaymentTypeList"));
		}
		
		returnInput.removeAttribute("BillToID");
		returnInput.removeAttribute("BillToKey");
		returnInput.removeAttribute("ShipToKey");
		returnInput.removeAttribute("EnterpriseCode");
		returnInput.removeAttribute("CustomerContactID");
		returnInput.removeAttribute("RefundExpected");
		if(!SCUIUtils.isVoid(returnInput.getAttribute("ReturnLinesPresent"))){
			returnInput.removeAttribute("ReturnLinesPresent");
		}
		if(!SCUIUtils.isVoid(returnInput.getAttribute("ReturnLinesPresent"))){
			returnInput.removeAttribute("ReturnLinesPresent");
		}
		returnInput.removeAttribute("DocumentType");
		if(!SCUtil.isVoid(SCXmlUtil.getChildElement(returnInput, "PaymentTypeList"))){
			SCXmlUtil.removeNode(SCXmlUtil.getChildElement(returnInput, "PaymentTypeList"));;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Final return order input is : " + SCXmlUtil.getString(returnInput));
		}
		
		try{
			//call validateAddress on return order
			if(isRefundExpected){
				if (logger.isDebugEnabled()) {
					logger.debug("Refund expected on the order. Hence validating the payment addresses if any present for return order");
				}
				Element verifyAddressOutput = validateAddress(uiContext, returnInput);
				if(verifyAddressOutput.getAttribute("ErrorFound").equals("Y")){
					verifyAddressOutput.setAttribute("ErrorInAddress", "Y");
					WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
					if (logger.isDebugEnabled()) {
						logger.debug("verifyAddress returned error for one or more addresses present on one or more payment on return order.");
						logger.debug("Returning from mashup with address error.");
						logger.debug("Returning output: " +  SCXmlUtil.getString(verifyAddressOutput));
					}
					return verifyAddressOutput;
				}
			}
			
			if(isExchangeOrderPresent && !isRefundExpected){
				//call validateAddress on exchange order
				if (logger.isDebugEnabled()) {
					logger.debug("Exchange order is present on the order and payment capture for additional amount on exchange order."); 
					logger.debug("Hence validating the payment addresses if any present for exchange order");
				}
				Element verifyAddressOutputForExchange = validateAddress(uiContext, exchangeOrderInput);
				if(verifyAddressOutputForExchange.getAttribute("ErrorFound").equals("Y")){
					verifyAddressOutputForExchange.setAttribute("ErrorInAddress", "Y");
					WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
					if (logger.isDebugEnabled()) {
						logger.debug("verifyAddress returned error for one or more addresses present on one or more payment on exchange order.");
						logger.debug("Returning from mashup with address error.");
						logger.debug("Returning output: " +  SCXmlUtil.getString(verifyAddressOutputForExchange));
					}
					return verifyAddressOutputForExchange;
				}
			}
			Element returnOutEl = null;
			Element exchangeOutEl = null;
			Element finalReturnPOPInputElem = null;
			Element finalExchangePOPElem = null;
			if(isRefundExpected){
				/*customization for Ezetap
				//exchange lines are not supported and returns for only orders are supported
				*/
				if(!isExchangeOrderPresent) {
					invokeEzetapRefund(uiContext, SCXmlUtil.getAttribute(returnInput, "OrderHeaderKey"));
				}
				//call capturePayment on Return order	
				if (logger.isDebugEnabled()) {
					logger.debug("Refund expected on the order. Hence calling capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order."); 
					logger.debug("capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) input being passed for return order is : " + SCXmlUtil.getString(returnInput));
				}
				returnOutEl = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_capturePayment", returnInput, uiContext);
				if (logger.isDebugEnabled()) {
					logger.debug("Output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order : " + SCXmlUtil.getString(returnOutEl)); 
				}
				finalReturnPOPInputElem = prepareFinalPOPInput(uiContext,returnOutEl,originalReturnInput);
				if(returnOutEl.getAttribute("ErrorFound").equals("Y")){
					if (logger.isDebugEnabled()) {
						logger.debug("Output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order returned with errors."); 
						logger.debug("Input which was prepared for processOrderPayments API (wsc_paymentConfirmation_processOrderPayments) on return order was : " + SCXmlUtil.getString(finalReturnPOPInputElem)); 
						logger.debug("Updating capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order, before sending back with errors."); 
					}
					updateSecureAuth(returnOutEl, finalReturnPOPInputElem);
					WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
					returnOutEl = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, returnOutEl);
					if (logger.isDebugEnabled()) {
						logger.debug("Return order output to be returned is : " + SCXmlUtil.getString(returnOutEl)); 
					}
					if(isExchangeOrderPresent){
						if (logger.isDebugEnabled()) {
							logger.debug("Exchange order present. Calling getCompleteOrderDetails for exchange order to send as part of output."); 
						}
						Element exchangeOrderDetails = getCompleteOrderDetailsForOneDocument(originalExchangeOrderInput.getAttribute("OrderHeaderKey"), uiContext);
						Element returnOrder = SCXmlUtil.createChild(exchangeOrderDetails, "Return");
						SCXmlUtil.importElement(returnOrder, returnOutEl);
						exchangeOrderDetails.setAttribute("ErrorFound","Y");
						if (logger.isDebugEnabled()) {
							logger.debug("Final output returned by this mashup with errors in capture payment on return order : " + SCXmlUtil.getString(exchangeOrderDetails)); 
						}
						return exchangeOrderDetails;
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("Final output returned by this mashup with errors in capture payment on return order : " + SCXmlUtil.getString(returnOutEl)); 
						}
						return returnOutEl;
					}
				}
			}
			else {
				
				if(!isExchangeOrderPresent){
					if(hasNoProcessReqdPayments(returnInput)){
						returnOutEl = handleNoProcessingRequiredPaymentsForCollect(uiContext,exchangeOrderInput,paymentTypeList);
					}
					else {
						returnOutEl = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_capturePayment", returnInput, uiContext);
						returnOutEl.setAttribute("PaymentProcessSuccess", "Y");
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order : " + SCXmlUtil.getString(returnOutEl)); 
					}
					finalReturnPOPInputElem = prepareFinalPOPInput(uiContext,returnOutEl,originalReturnInput);
					if(returnOutEl.getAttribute("ErrorFound").equals("Y")){
						if (logger.isDebugEnabled()) {
							logger.debug("Output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order returned with errors."); 
							logger.debug("Input which was prepared for processOrderPayments API (wsc_paymentConfirmation_processOrderPayments) on return order was : " + SCXmlUtil.getString(finalReturnPOPInputElem)); 
							logger.debug("Updating capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on return order, before sending back with errors."); 
						}
						updateSecureAuth(returnOutEl, finalReturnPOPInputElem);
						WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
						returnOutEl = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, returnOutEl);
						return returnOutEl;
					}else {
						return returnOutEl;
						
					}
				}
				//call capturePayment on Exchange order
				if (logger.isDebugEnabled()) {
					logger.debug("Additional payment required on return/exchange. Hence calling capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on exchange order."); 
					logger.debug("capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) input being passed for return order is : " + SCXmlUtil.getString(exchangeOrderInput));
				}
				if(hasNoProcessReqdPayments(exchangeOrderInput)){
					exchangeOutEl = handleNoProcessingRequiredPaymentsForCollect(uiContext,exchangeOrderInput,paymentTypeList);
				}
				else {
					exchangeOutEl = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_capturePayment", exchangeOrderInput, uiContext);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on exchange order : " + SCXmlUtil.getString(exchangeOutEl)); 
				}
				finalExchangePOPElem = prepareFinalPOPInput(uiContext,exchangeOutEl,originalExchangeOrderInput);
				if(exchangeOutEl.getAttribute("ErrorFound").equals("Y")){
					if (logger.isDebugEnabled()) {
						logger.debug("Output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on exchange order returned with errors."); 
						logger.debug("Input which was prepared for processOrderPayments API (wsc_paymentConfirmation_processOrderPayments) on exchange order was : " + SCXmlUtil.getString(finalExchangePOPElem)); 
						logger.debug("Updating capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on exchange order, before sending back with errors."); 
					}
					updateSecureAuth(exchangeOutEl, finalExchangePOPElem);
					WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
					exchangeOutEl = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, exchangeOutEl);
					if (logger.isDebugEnabled()) {
						logger.debug("Calling getCompleteOrderDetails for return order to send as part of output."); 
					}
					Element returnOrderDetails = getCompleteOrderDetailsForOneDocument(originalReturnInput.getAttribute("OrderHeaderKey"), uiContext);
					Element returnOrder = SCXmlUtil.createChild(exchangeOutEl, "Return");
					SCXmlUtil.importElement(returnOrder, returnOrderDetails);
					if (logger.isDebugEnabled()) {
						logger.debug("Final output returned by this mashup with errors in capture payment on exchange order : " + SCXmlUtil.getString(exchangeOutEl)); 
					}
					return exchangeOutEl;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Checking output returned by capturePayment API (mashup : wsc_paymentConfirmation_capturePayment) on exchange order to validate RemainingAmountToAuth."); 
				}
				Element chargeTransactionDetailsElem = SCXmlUtil.getChildElement(exchangeOutEl, "ChargeTransactionDetails");
				double remainingAmountToAuth = SCXmlUtil.getDoubleAttribute(chargeTransactionDetailsElem, "RemainingAmountToAuth");
				if(remainingAmountToAuth > 0){
					if (logger.isDebugEnabled()) {
						logger.debug("RemainingAmountToAuth is : " + remainingAmountToAuth + " which is still greater than zero for exchange order. Hence payment processing will stop here."); 
					}
					updateSecureAuth(exchangeOutEl, finalExchangePOPElem);
					WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
					exchangeOutEl = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, exchangeOutEl);
					Element returnOrderDetails = getCompleteOrderDetailsForOneDocument(originalReturnInput.getAttribute("OrderHeaderKey"), uiContext);
					Element returnOrder = SCXmlUtil.createChild(exchangeOutEl, "Return");
					SCXmlUtil.importElement(returnOrder, returnOrderDetails);
					if (logger.isDebugEnabled()) {
						logger.debug("Final output returned by this mashup with errors in capture payment on exchange order due to pending amount : " + SCXmlUtil.getString(exchangeOutEl)); 
					}
					return exchangeOutEl;
				}
			}
			//call processOrderPayments on return order
			//call processOrderPayments on exchange order
			if (logger.isDebugEnabled()) {
				logger.debug("Payment capture call capture Payment API (mashup : wsc_paymentConfirmation_capturePayment) on return/exchange order was successful.");
				logger.debug("Will proceed with calling processOrderPayments API (mashup: wsc_paymentConfirmation_processOrderPayments) on return/exchange order.");
			}
			if(SCUIUtils.isVoid(finalReturnPOPInputElem)){
				finalReturnPOPInputElem = SCXmlUtil.createDocument("Order").getDocumentElement();
				finalReturnPOPInputElem.setAttribute("OrderHeaderKey", originalReturnInput.getAttribute("OrderHeaderKey"));
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Calling processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) for return order with input : " + SCXmlUtil.getString(finalReturnPOPInputElem));
			}
			Element returnPopMashupOutput = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_processOrderPayments", finalReturnPOPInputElem, uiContext);
			if (logger.isDebugEnabled()) {
				logger.debug("Output returned by processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on return order : " + SCXmlUtil.getString(returnPopMashupOutput)); 
			}
			if(returnPopMashupOutput.getAttribute("ErrorFound").equals("Y")){
				if (logger.isDebugEnabled()) {
					logger.debug("Output returned by processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on return order returned with errors."); 
					logger.debug("Updating processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on return order, before sending back with errors."); 
				}
				updateSecureAuth(returnPopMashupOutput, finalReturnPOPInputElem);
				WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
				returnPopMashupOutput = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, returnPopMashupOutput);
				if(isExchangeOrderPresent){
					if (logger.isDebugEnabled()) {
						logger.debug("Calling getCompleteOrderDetails for return order to send as part of output."); 
					}
					Element exchangeOrderDetails = getCompleteOrderDetailsForOneDocument(originalExchangeOrderInput.getAttribute("OrderHeaderKey"), uiContext);
					Element returnOrder = SCXmlUtil.createChild(exchangeOrderDetails, "Return");
					SCXmlUtil.importElement(returnOrder, returnPopMashupOutput);
					exchangeOrderDetails.setAttribute("ErrorFound", "Y");
					if (logger.isDebugEnabled()) {
						logger.debug("Final output returned by this mashup with errors in processOrderPayments on return order : " + SCXmlUtil.getString(exchangeOrderDetails)); 
					}
					return exchangeOrderDetails;
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Final output returned by this mashup with errors in processOrderPayments on return order : " + SCXmlUtil.getString(returnPopMashupOutput)); 
					}
					return returnPopMashupOutput;
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) for return order was successful with output : " + SCXmlUtil.getString(returnPopMashupOutput));
			}
			if(isExchangeOrderPresent && realTimeAuthRequired(uiContext,originalExchangeOrderInput.getAttribute("EnterpriseCode"))){
				if (logger.isDebugEnabled()) {
					logger.debug("Store Real time authorization turned on and exchange order present. Hence will call processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) for exchange order.");
				}
				//call processOrderPayments on exchange order
				if(SCUIUtils.isVoid(finalExchangePOPElem)){
					finalExchangePOPElem = SCXmlUtil.createDocument("Order").getDocumentElement();
					finalExchangePOPElem.setAttribute("OrderHeaderKey", originalExchangeOrderInput.getAttribute("OrderHeaderKey"));
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Calling processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) for exchange order with input : " + SCXmlUtil.getString(finalExchangePOPElem));
				}
				Element exchangePopMashupOutput = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_processOrderPayments", finalExchangePOPElem, uiContext);
				if (logger.isDebugEnabled()) {
					logger.debug("Output returned by processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on exchange order : " + SCXmlUtil.getString(exchangePopMashupOutput)); 
				}
				if(exchangePopMashupOutput.getAttribute("ErrorFound").equals("Y")){
					if (logger.isDebugEnabled()) {
						logger.debug("Output returned by processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on exchange order returned with errors."); 
						logger.debug("Updating processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on exchange order, before sending back with errors."); 
					}
					updateSecureAuth(exchangePopMashupOutput, finalExchangePOPElem);
					WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
					exchangePopMashupOutput = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, exchangePopMashupOutput);
					Element returnOrderDetails = getCompleteOrderDetailsForOneDocument(originalReturnInput.getAttribute("OrderHeaderKey"), uiContext);
					Element returnOrder = SCXmlUtil.createChild(exchangePopMashupOutput, "Return");
					SCXmlUtil.importElement(returnOrder, returnOrderDetails);
					if (logger.isDebugEnabled()) {
						logger.debug("Final output returned by this mashup with errors in processOrderPayments on exchange order : " + SCXmlUtil.getString(exchangePopMashupOutput)); 
					}
					return exchangePopMashupOutput;
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Checking output returned by processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) on exchange order to validate RemainingAmountToAuth."); 
					}
					Element chargeTransactionDetailsElemPOP = SCXmlUtil.getChildElement(exchangePopMashupOutput, "ChargeTransactionDetails");
					double remainingAmountToAuthPOP = SCXmlUtil.getDoubleAttribute(chargeTransactionDetailsElemPOP, "RemainingAmountToAuth");
					if(remainingAmountToAuthPOP > 0){
						if (logger.isDebugEnabled()) {
							logger.debug("RemainingAmountToAuth is : " + remainingAmountToAuthPOP + " which is still greater than zero for exchange order. Hence payment processing will stop here."); 
						}
						updateSecureAuth(exchangePopMashupOutput, finalExchangePOPElem);
						WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
						exchangePopMashupOutput = WSCPaymentUtil.updatePaymentMethodsElem(uiContext, exchangePopMashupOutput);
						Element finalReturnOutput = getCompleteOrderDetails(exchangePopMashupOutput.getAttribute("OrderHeaderKey"), uiContext);
						if (logger.isDebugEnabled()) {
							logger.debug("Final output returned by this mashup with errors in processOrderPayments on exchange order due to pending amount : " + SCXmlUtil.getString(finalReturnOutput)); 
						}
						return finalReturnOutput;
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("processOrderPayments API (mashup : wsc_paymentConfirmation_processOrderPayments) for exchange order was successful with output : " + SCXmlUtil.getString(exchangePopMashupOutput));
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Validating and updating Billing address on return/exchange if not present.");
			}
			//Update Billing addresses on order
			if(SCUtil.isVoid(originalReturnInput.getAttribute("BillToKey"))){
				if (logger.isDebugEnabled()) {
					logger.debug("BillToKey is empty on return order. Will update billing address on return order.");
				}
				updateBillingAddress(uiContext, originalReturnInput);
			}
			if(isExchangeOrderPresent){
				if(SCUtil.isVoid(originalExchangeOrderInput.getAttribute("BillToKey"))){
					if (logger.isDebugEnabled()) {
						logger.debug("BillToKey is empty on exchange order. Will update billing address on exchange order.");
					}
					updateBillingAddress(uiContext, originalExchangeOrderInput);
				}
			}
			
			//call confirmDraftOrder on return order
			if(isLinesPresentOnReturnOrder){
				Element confirmReturnDraftOrderInputElem = SCXmlUtil.createDocument("ConfirmDraftOrder").getDocumentElement();
				confirmReturnDraftOrderInputElem.setAttribute("OrderHeaderKey", originalReturnInput.getAttribute("OrderHeaderKey"));
				if (logger.isDebugEnabled()) {
					logger.debug("Return order has lines to confirm. Calling confirmDraftOrder API (mashup : wsc_orderCapture_confirmDraftOrder) on return order with input : " + SCXmlUtil.getString(confirmReturnDraftOrderInputElem));
				}
				Element confirmReturnDraftOrderOutput = (Element)SCUIMashupHelper.invokeMashup("wsc_orderCapture_confirmDraftOrder", confirmReturnDraftOrderInputElem, uiContext);
				if (logger.isDebugEnabled()) {
					logger.debug("confirmDraftOrder API (mashup : wsc_orderCapture_confirmDraftOrder) on return order was successful with output : " + SCXmlUtil.getString(confirmReturnDraftOrderOutput));
				}
			}
			if(isExchangeOrderPresent && isLinesPresentOnExchangeOrder){
				//call confirmDraftOrder on exchange order
				Element confirmExchangeDraftOrderInputElem = SCXmlUtil.createDocument("ConfirmDraftOrder").getDocumentElement();
				confirmExchangeDraftOrderInputElem.setAttribute("OrderHeaderKey", originalExchangeOrderInput.getAttribute("OrderHeaderKey"));
				if (logger.isDebugEnabled()) {
					logger.debug("Exchange order has lines to confirm. Calling confirmDraftOrder API (mashup : wsc_orderCapture_confirmDraftOrder) on exchange order with input : " + SCXmlUtil.getString(confirmExchangeDraftOrderInputElem));
				}
				Element confirmExchangeDraftOrderOutput = (Element)SCUIMashupHelper.invokeMashup("wsc_orderCapture_confirmDraftOrder", confirmExchangeDraftOrderInputElem, uiContext);
				if (logger.isDebugEnabled()) {
					logger.debug("confirmDraftOrder API (mashup : wsc_orderCapture_confirmDraftOrder) on exchange order was successful with output : " + SCXmlUtil.getString(confirmExchangeDraftOrderOutput));
				}
			}
			
			//call getOrderDetails on return;
			//Element finalOutput = getCompleteOrderDetails(returnInput.getAttribute("OrderHeaderKey"), uiContext);
			Element finalOutput = getFinalMashupOutput(originalReturnInput, originalExchangeOrderInput, uiContext);
			if (logger.isDebugEnabled()) {
				logger.debug("All payment capture, processing payment and confirming draft order actions were successul withouth any errors.");
				logger.debug("Final output returned by this mashup is : " + SCXmlUtil.getString(finalOutput));
			}
			return finalOutput;
			
		} catch (APIManager.XMLExceptionWrapper e) {
			
			if (logger.isEnabledFor(YFCLogLevel.ERROR) && !(Manager.getBooleanProperty("yfs", "uifwkimpl.donotlog.apiexception", false))) {
				logger.error(G11N.getString(G11N.AFC_DEFAULT_BUNDLE, "WSCOneClickProcessRefundMashup: APIException occurred {1}", new String [] { SCUIUtils.getExceptionAsString(e)}));
				logger.error(G11N.getString(G11N.AFC_DEFAULT_BUNDLE, "WSCOneClickProcessRefundMashup: Throwing Exception"));
			}
			WSCMashupUtils.getAttributeFromUIContext(uiContext, WSCPaymentUtil.SHOULD_ENCRYPT_PAYMENT_ATTR_FLAG, true);
			throw massageException(returnInput, uiContext, e);
		}
		
		finally{
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.execute");
			}
		}
	}
	
	private void invokeEzetapRefund(SCUIContext uiContext, String orderHeaderKey) {
		if(!SCUIUtils.isVoid(orderHeaderKey)) {
			Element returnOrderInput = SCXmlUtil.createDocument("Order").getDocumentElement();
			returnOrderInput.setAttribute("OrderHeaderKey", orderHeaderKey);
			Element output = (Element) SCUIMashupHelper.invokeMashup("extn_wsc_paymentConfirmation_invokeExternalPayment", returnOrderInput, uiContext);
		}
		
	}

	private Element getFinalMashupOutput(Element originalReturnInput,
			Element originalExchangeOrderInput, SCUIContext uiContext) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getFinalMashupOutput");
		}
		Element output = SCXmlUtil.createDocument("Order").getDocumentElement();
		if(!SCUtil.isVoid(originalExchangeOrderInput)){
			output.setAttribute("OrderHeaderKey", originalExchangeOrderInput.getAttribute("OrderHeaderKey"));
			Element returnOrder = SCXmlUtil.createChild(output, "Return");
			Element returnOrderOrderElem = SCXmlUtil.createChild(returnOrder, "Order");
			returnOrderOrderElem.setAttribute("OrderHeaderKey", originalReturnInput.getAttribute("OrderHeaderKey"));
		}
		else {
			output.setAttribute("OrderHeaderKey", originalReturnInput.getAttribute("OrderHeaderKey"));
		}
		output.setAttribute("PaymentProcessSuccess", "Y");
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getFinalMashupOutput");
		}
		return output;
	}

	private Element getExchangeOrderInput(Element input, SCUIContext uiContext) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getExchangeOrderInput");
		}
		String documentType = input.getAttribute("DocumentType");
		String returnType = WSCDataProviderUtils.getReturnOrderType(uiContext);
		String salesOrderDocType = WSCDataProviderUtils.getSalesOrderType(uiContext);
		if (documentType.equals(salesOrderDocType)) {
			//If there is an Exchange Order see if there is a return order element
			Element returnOrders = SCXmlUtil.getChildElement(input, "Return");
			if(!SCUIUtils.isVoid(returnOrders)){
				Element returnOrder = SCXmlUtil.getChildElement(returnOrders, "Order");
				if(!SCUIUtils.isVoid(returnOrder)){
					if (logger.isTimerEnabled()){
						logger.endTimer("WSCOneClickProcessRefundMashup.getExchangeOrderInput");
					}
					return input;
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getExchangeOrderInput");
		}
		return input;
	}
	
	private Element getReturnOrderInput(Element input, SCUIContext uiContext) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getReturnOrderInput");
		}
		String documentType = input.getAttribute("DocumentType");
		String returnType = WSCDataProviderUtils.getReturnOrderType(uiContext);
		String salesOrderDocType = WSCDataProviderUtils.getSalesOrderType(uiContext);
		if(documentType.equals(returnType)){
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.getReturnOrderInput");
			}
			return input;
		}
		else if (documentType.equals(salesOrderDocType)) {
			//If there is an Exchange Order see if there is a return order element
			Element returnOrders = SCXmlUtil.getChildElement(input, "Return");
			if(!SCUIUtils.isVoid(returnOrders)){
				Element returnOrder = SCXmlUtil.getChildElement(returnOrders, "Order");
				if(!SCUIUtils.isVoid(returnOrder)){
					if (logger.isTimerEnabled()){
						logger.endTimer("WSCOneClickProcessRefundMashup.getReturnOrderInput");
					}
					return returnOrder;
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getReturnOrderInput");
		}
		return input;
	}

	private boolean isExchangeOrderPresentOnReturn(Element input, SCUIContext uiContext) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.isExchangeOrderPresentOnReturn");
		}
		String documentType = input.getAttribute("DocumentType");
		String returnType = WSCDataProviderUtils.getReturnOrderType(uiContext);
		String salesOrderDocType = WSCDataProviderUtils.getSalesOrderType(uiContext);
		if (documentType.equals(salesOrderDocType)) {
			//If there is an Exchange Order see if there is a return order element
			Element returnOrders = SCXmlUtil.getChildElement(input, "Return");
			if(!SCUIUtils.isVoid(returnOrders)){
				Element returnOrder = SCXmlUtil.getChildElement(returnOrders, "Order");
				if(!SCUIUtils.isVoid(returnOrder)){
					if (logger.isTimerEnabled()){
						logger.endTimer("WSCOneClickProcessRefundMashup.isExchangeOrderPresentOnReturn");
					}
					return true;
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.isExchangeOrderPresentOnReturn");
		}
		return false;
	}

	private boolean isRefundExpectedOnReturn(Element input, SCUIContext uiContext) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.isRefundExpectedOnReturn");
		}
		String refundExpected = input.getAttribute("RefundExpected");
		if(!SCUtil.isVoid(refundExpected) && refundExpected.equalsIgnoreCase("Y")){
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.isRefundExpectedOnReturn");
			}
			return true;
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.isRefundExpectedOnReturn");
		}
		return false;
	}
	
	private boolean isLinesPresentOnReturn(Element input, SCUIContext uiContext) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.isLinesPresentOnReturn");
		}
		String returnLinesPresent = input.getAttribute("ReturnLinesPresent");
		if(!SCUtil.isVoid(returnLinesPresent) && returnLinesPresent.equalsIgnoreCase("N")){
			logger.endTimer("WSCOneClickProcessRefundMashup.isLinesPresentOnReturn");
			return false;
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.isLinesPresentOnReturn");
		}
		return true;
	}
	
	private boolean isLinesPresentOnExchange(Element input, SCUIContext uiContext, boolean isExchangeOrderPresent) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.isLinesPresentOnExchange");
		}
		String exchangeLinesPresent = input.getAttribute("ExchangeLinesPresent");
		if(isExchangeOrderPresent && (SCUtil.isVoid(exchangeLinesPresent) || !exchangeLinesPresent.equalsIgnoreCase("N"))){
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.isLinesPresentOnExchange");
			}
			return true;
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.isLinesPresentOnExchange");
		}
		return false;
	}

	private boolean hasNoProcessReqdPayments(Element input) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.hasNoProcessReqdPayments");
		}
		Element paymentMethods = SCXmlUtil.getChildElement(input, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethods)){
			for(Element paymentMethod : SCXmlUtil.getChildrenList(paymentMethods)){
				if(!SCUtil.isVoid(paymentMethod)){
					String noProcessingReqd = paymentMethod.getAttribute("NoProcessingRequired");
					if(!SCUtil.isVoid(noProcessingReqd) && noProcessingReqd.equals("Y")){
						if (logger.isTimerEnabled()){
							logger.endTimer("WSCOneClickProcessRefundMashup.hasNoProcessReqdPayments");
						}
						return true;
					}
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.hasNoProcessReqdPayments");
		}
		return false;
	}

	private Element handleNoProcessingRequiredPaymentsForCollect(
			SCUIContext uiContext, Element input, Element paymentTypeList) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.handleNoProcessingRequiredPaymentsForCollect");
		}
		Element changeOrderInput = SCXmlUtil.getCopy(input);
		changeOrderInput.getOwnerDocument().renameNode(changeOrderInput, changeOrderInput.getNamespaceURI(), "Order");
		Element changeOrderoutput = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_changeOrderForPayment", changeOrderInput, uiContext);
		Element capturePaymentInputForNoProcessReqd = getCapturePaymentInput(changeOrderoutput,true,paymentTypeList);
		Element capturePaymentInputForProcessReqd = getCapturePaymentInput(changeOrderoutput,false,paymentTypeList);
		Element capturePaymentOutEl = null;
		if(hasAnyPaymentMethod(capturePaymentInputForNoProcessReqd)){
			capturePaymentOutEl = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_capturePayment", capturePaymentInputForNoProcessReqd, uiContext);
		}
		if(hasAnyPaymentMethod(capturePaymentInputForProcessReqd)){
			capturePaymentOutEl = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_capturePayment", capturePaymentInputForProcessReqd, uiContext);
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.handleNoProcessingRequiredPaymentsForCollect");
		}
		return capturePaymentOutEl;
	}

	private boolean hasAnyPaymentMethod(
			Element capturePaymentInput) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.hasAnyPaymentMethod");
		}
		Element paymentMethods = SCXmlUtil.getChildElement(capturePaymentInput, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethods)){
			for(Element paymentMethod : SCXmlUtil.getChildrenList(paymentMethods)){
				if(!SCUtil.isVoid(paymentMethod)){
					if (logger.isTimerEnabled()){
						logger.endTimer("WSCOneClickProcessRefundMashup.hasAnyPaymentMethod");
					}
					return true;
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.hasAnyPaymentMethod");
		}
		return false;
	}

	private Element getCapturePaymentInput(Element originalEl, boolean noProcessReqd, Element paymentTypeList) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getCapturePaymentInput");
		}
		Element capturePaymentInput = SCXmlUtil.createDocument("CapturePayment").getDocumentElement();
		capturePaymentInput.setAttribute("OrderHeaderKey", originalEl.getAttribute("OrderHeaderKey"));
		Element paymentMethods = SCXmlUtil.getChildElement(originalEl, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethods)){
			for(Element paymentMethod : SCXmlUtil.getChildrenList(paymentMethods)){
				if(!SCUtil.isVoid(paymentMethod)){
					if(noProcessReqd && WSCPaymentUtil.isPaymentMethodNoProcessReqd(paymentMethod.getAttribute("PaymentType"),paymentTypeList)){
						Element capturePaymentOutputPaymentMethods = SCXmlUtil.getChildElement(capturePaymentInput, "PaymentMethods", true);
						Element capturePaymentOutputPaymentMethod = SCXmlUtil.createChild(capturePaymentOutputPaymentMethods, "PaymentMethod");
						capturePaymentOutputPaymentMethod.setAttribute("PaymentKey", paymentMethod.getAttribute("PaymentKey"));
						if(paymentMethod.hasAttribute("SecureAuthenticationCode")){
							capturePaymentOutputPaymentMethod.setAttribute("SecureAuthenticationCode", paymentMethod.getAttribute("SecureAuthenticationCode"));
						}
					}
					else if(!noProcessReqd && !(WSCPaymentUtil.isPaymentMethodNoProcessReqd(paymentMethod.getAttribute("PaymentType"),paymentTypeList))) {
						Element capturePaymentOutputPaymentMethods = SCXmlUtil.getChildElement(capturePaymentInput, "PaymentMethods", true);
						Element capturePaymentOutputPaymentMethod = SCXmlUtil.createChild(capturePaymentOutputPaymentMethods, "PaymentMethod");
						capturePaymentOutputPaymentMethod.setAttribute("PaymentKey", paymentMethod.getAttribute("PaymentKey"));
						capturePaymentOutputPaymentMethod.setAttribute("RequestedAmount", "0");
						if(paymentMethod.hasAttribute("SecureAuthenticationCode")){
							capturePaymentOutputPaymentMethod.setAttribute("SecureAuthenticationCode", paymentMethod.getAttribute("SecureAuthenticationCode"));
						}
					}
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getCapturePaymentInput");
		}
		return capturePaymentInput;
	}

	private Element validateAddress(SCUIContext uiContext, Element input) {
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.validateAddress");
		}
		
		Element tempInput = SCXmlUtil.getCopy(input);
		Element addressVerifyOutput = SCXmlUtil.createDocument("Order").getDocumentElement();
		Element paymentMethodsElem = SCXmlUtil.getChildElement(input, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethodsElem)){
			List<Element> paymentMethodList = SCXmlUtil.getElements(paymentMethodsElem, "PaymentMethod");
			for (Element paymentMethod : paymentMethodList) {
				String paymentTypeGroup = paymentMethod.getAttribute("PaymentTypeGroup");
				if(paymentTypeGroup.equalsIgnoreCase("CREDIT_CARD")){
					Element personInfoBillTo = SCXmlUtil.getChildElement(paymentMethod, "PersonInfoBillTo");
					if(!SCUtil.isVoid(personInfoBillTo)){
						String addressVerifyReqd = personInfoBillTo.getAttribute("AddressVerifyRequired");
						if(SCUtil.isVoid(addressVerifyReqd) || addressVerifyReqd.equalsIgnoreCase("Y")){
							Element verifyAddressOutput = callVerifyAddress(uiContext,personInfoBillTo);
							String status = verifyAddressOutput.getAttribute("Status");
							if(status.equals("FAILED")){
								addressVerifyOutput.setAttribute("ErrorFound", "Y");
								if (logger.isTimerEnabled()){
									logger.endTimer("WSCOneClickProcessRefundMashup.validateAddress");
								}
								return addressVerifyOutput;
							}
							else if(status.equals("AVS_DOWN") || status.equals("UE_MISSING")){
								personInfoBillTo.setAttribute("IsAddressVerified", "N");
								if(SCXmlUtil.hasAttribute(personInfoBillTo, "AddressVerifyRequired")){
									personInfoBillTo.removeAttribute("AddressVerifyRequired");
								}
							}
							else if(status.equals("VERIFIED")){
								int totalNumberOfRecords = SCXmlUtil.getIntAttribute(verifyAddressOutput,"TotalNumberOfRecords");
								boolean proceedWithSingleAVSResult = true;
								if(SCXmlUtil.hasAttribute(verifyAddressOutput, "ProceedWithSingleAVSResult")){
									proceedWithSingleAVSResult = SCXmlUtil.getBooleanAttribute(verifyAddressOutput, "ProceedWithSingleAVSResult");
								}
								if(!proceedWithSingleAVSResult || totalNumberOfRecords > 1){
									addressVerifyOutput.setAttribute("ErrorFound", "Y");
									if (logger.isTimerEnabled()){
										logger.endTimer("WSCOneClickProcessRefundMashup.validateAddress");
									}
									return addressVerifyOutput;
								}
								else {
									personInfoBillTo.setAttribute("IsAddressVerified", "Y");
									if(SCXmlUtil.hasAttribute(personInfoBillTo, "AddressVerifyRequired")){
										personInfoBillTo.removeAttribute("AddressVerifyRequired");
									}
								}
							}
							else {
								personInfoBillTo.setAttribute("IsAddressVerified", "N");
								if(SCXmlUtil.hasAttribute(personInfoBillTo, "AddressVerifyRequired")){
									personInfoBillTo.removeAttribute("AddressVerifyRequired");
								}
							}
						}
						personInfoBillTo.setAttribute("IsAddressVerified", "Y");
						if(SCXmlUtil.hasAttribute(personInfoBillTo, "AddressVerifyRequired")){
							personInfoBillTo.removeAttribute("AddressVerifyRequired");
						}
					}
				}
			}
		}
		addressVerifyOutput.setAttribute("ErrorFound", "N");
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.validateAddress");
		}
		return addressVerifyOutput;
	}

	private Element callVerifyAddress(SCUIContext uiContext,
			Element personInfoBillTo) {
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.callVerifyAddress");
		}
		
		Element verifyAddressOutput = null;
		Element addressVerifyInput = SCXmlUtil.createDocument("PersonInfo").getDocumentElement();
		Element personInfoCopy = SCXmlUtil.getCopy(personInfoBillTo);
		if(SCXmlUtil.hasAttribute(personInfoCopy, "AddressVerifyRequired")){
			personInfoCopy.removeAttribute("AddressVerifyRequired");
		}
		SCXmlUtil.mergeAttributes(personInfoCopy, addressVerifyInput, false);
		try {
			verifyAddressOutput = (Element) SCUIMashupHelper.invokeMashup("wscaddressCapture_verifyAddress", addressVerifyInput, uiContext);
		}catch (APIManager.XMLExceptionWrapper e) {
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.callVerifyAddress");
			}
			throw e;
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.callVerifyAddress");
		}
		return verifyAddressOutput;
	}

	private void updateSecureAuth(Element outEl, Element finalPOPInputElem) {
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.updateSecureAuth");
		}
		
		Map<String, String> seucreAuthPaymentKeyMap = new HashMap<String,String>();
		Element paymentMethodsElem = SCXmlUtil.getChildElement(finalPOPInputElem, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethodsElem)){
			List<Element> paymentMethodList = SCXmlUtil.getElements(paymentMethodsElem, "PaymentMethod");
			for (Element paymentMethod : paymentMethodList) {
				String paymentKey = paymentMethod.getAttribute("PaymentKey");
				String secureAuthCode = paymentMethod.getAttribute("SecureAuthenticationCode");
				if(!SCUIUtils.isVoid(paymentKey) && !SCUIUtils.isVoid(secureAuthCode)){
					seucreAuthPaymentKeyMap.put(paymentKey, secureAuthCode);
				}
			}
		}
		
		Element paymentMethodsOutElem = SCXmlUtil.getChildElement(outEl, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethodsOutElem)){
			List<Element> paymentMethodOutList = SCXmlUtil.getElements(paymentMethodsOutElem, "PaymentMethod");
			for (Element paymentMethod : paymentMethodOutList) {
				String paymentKey = paymentMethod.getAttribute("PaymentKey");
				if(!SCUIUtils.isVoid(paymentKey)){
					String secureAuthCode = seucreAuthPaymentKeyMap.get(paymentKey);
					if(!SCUIUtils.isVoid(secureAuthCode)){
						paymentMethod.setAttribute("SecureAuthenticationCode", secureAuthCode);
					}
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.updateSecureAuth");
		}
	}

	private XMLExceptionWrapper massageException(Object input,
			SCUIContext uiContext, XMLExceptionWrapper e) {		
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.massageException");
		}
		
		Element getCompleteOrderDetailsOutput = getCompleteOrderDetails(((Element)input).getAttribute("OrderHeaderKey"), uiContext);
		YFCDocument ordertailsYfcDoc = YFCDocument.getDocumentFor(getCompleteOrderDetailsOutput.getOwnerDocument());
		YFCElement exceptionXML = e.getXML();
		YFCElement orderDetailsInEx = exceptionXML.createChild("OrderDetails");
		orderDetailsInEx.importNode(ordertailsYfcDoc.getDocumentElement());
		
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.massageException");
		}
		return e;
	}
	
	private Element getCompleteOrderDetails(String orderHeaderKey, SCUIContext uiContext) {
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getCompleteOrderDetails");
		}
		
		Element output = null;
		Element input = SCXmlUtil.createDocument("Order").getDocumentElement();
		SCXmlUtil.setAttribute(input, "OrderHeaderKey", orderHeaderKey);
		SCXmlUtil.setAttribute(input, "RetrieveDefaultCustomerPaymentMethod", "N");
		if (!SCUtil.isVoid(input)){
			try {
				output = (Element) SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_getCompleteReturnOrderDetails", input, uiContext);
			}catch (APIManager.XMLExceptionWrapper e) {
				if (logger.isTimerEnabled()){
					logger.beginTimer("WSCOneClickProcessRefundMashup.getCompleteOrderDetails");
				}
				throw e;
			}
		}
		
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getCompleteOrderDetails");
		}
		
		return output;
	}
	
	private Element getCompleteOrderDetailsForOneDocument(String orderHeaderKey, SCUIContext uiContext) {
		
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getCompleteOrderDetailsForOneDocument");
		}
		
		Element output = null;
		Element input = SCXmlUtil.createDocument("Order").getDocumentElement();
		SCXmlUtil.setAttribute(input, "OrderHeaderKey", orderHeaderKey);
		SCXmlUtil.setAttribute(input, "RetrieveDefaultCustomerPaymentMethod", "N");
		if (!SCUtil.isVoid(input)){
			try {
				output = (Element) SCUIMashupHelper.invokeMashup("wsc_paymentConfirmation_getCompleteOrderDetails", input, uiContext);
			}catch (APIManager.XMLExceptionWrapper e) {
				if (logger.isTimerEnabled()){
					logger.endTimer("WSCOneClickProcessRefundMashup.getCompleteOrderDetailsForOneDocument");
				}
				throw e;
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getCompleteOrderDetailsForOneDocument");
		}
		return output;
	}

	private boolean realTimeAuthRequired(SCUIContext uiContext, String organizationCode) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.realTimeAuthRequired");
		}
		Element eRuleOutput = WSCPaymentUtil.getRuleValue(uiContext,"WSC_STORE_REAL_TIME_AUTHORIZATION", organizationCode, null);
		String ruleValue = SCXmlUtil.getAttribute(eRuleOutput, "RuleSetValue");
		if(ruleValue.equals("02")){
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.realTimeAuthRequired");
			}
			return true;
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.realTimeAuthRequired");
		}
		return false;
	}

	private Element changeOrderInput(Element input) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.changeOrderInput");
		}
		Element changeOrderInputElem = SCXmlUtil.createDocument("Order").getDocumentElement();
		changeOrderInputElem.setAttribute("OrderHeaderKey", input.getAttribute("OrderHeaderKey"));
		if(!SCUtil.isVoid(input.getAttribute("NewBillToKey"))){
			changeOrderInputElem.setAttribute("BillToKey", input.getAttribute("NewBillToKey"));
		}
		else if(!SCUtil.isVoid(SCXmlUtil.getChildElement(input, "NewPersonInfoBillTo"))){
			Element personInfoBillTo = SCXmlUtil.createChild(changeOrderInputElem, "PersonInfoBillTo");
			SCXmlUtil.mergeAttributes(SCXmlUtil.getChildElement(input, "NewPersonInfoBillTo"), personInfoBillTo, false);
		}
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.changeOrderInput");
		}
		return changeOrderInputElem;
	}
	
	private Element prepareFinalPOPInput(SCUIContext uiContext, Element outEl, Element inputEl) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.prepareFinalPOPInput");
		}
		Map<String, String> paymentKeyMap = new HashMap<String,String>();
		Element paymentMethodsElem = SCXmlUtil.getChildElement(outEl, "PaymentMethods");
		if(!SCUtil.isVoid(paymentMethodsElem)){
			List<Element> paymentMethodList = SCXmlUtil.getElements(paymentMethodsElem, "PaymentMethod");
			
			for (Element paymentMethod : paymentMethodList) {
				String paymentTypeGroup = SCXmlUtil.getAttribute(paymentMethod, "PaymentTypeGroup");				
				if (paymentTypeGroup.equals("CREDIT_CARD")) {
					String paymentType = paymentMethod.getAttribute("PaymentType");
					String creditCardNo = paymentMethod.getAttribute("CreditCardNo");
					String creditCardType = paymentMethod.getAttribute("CreditCardType");
					String displayCardNo = paymentMethod.getAttribute("DisplayCreditCardNo");
					String ccExpDate = paymentMethod.getAttribute("CreditCardExpDate");
					String paymentKey = paymentMethod.getAttribute("PaymentKey");
					paymentKeyMap.put(paymentType + "_" + creditCardNo + "_" + creditCardType + "_" + displayCardNo + "_" + ccExpDate
							, paymentKey);
				}
			}
		}
		Element popInputElem = SCXmlUtil.createDocument("Order").getDocumentElement();
		popInputElem.setAttribute("OrderHeaderKey", inputEl.getAttribute("OrderHeaderKey"));
		Element inputPaymentMethodsElem = SCXmlUtil.getChildElement(inputEl, "PaymentMethods");
		if(!SCUtil.isVoid(inputPaymentMethodsElem)){
			List<Element> paymentMethodList = SCXmlUtil.getElements(inputPaymentMethodsElem, "PaymentMethod");
			
			for (Element paymentMethod : paymentMethodList) {
				String paymentTypeGroup = SCXmlUtil.getAttribute(paymentMethod, "PaymentTypeGroup");				
				if (paymentTypeGroup.equals("CREDIT_CARD")) {
					Element popInputPaymentMethodsElem = SCXmlUtil.getChildElement(popInputElem, "PaymentMethods", true);
					Element popInputPaymentMethodElem = SCXmlUtil.createChild(popInputPaymentMethodsElem, "PaymentMethod");
					if(SCUtil.isVoid(paymentMethod.getAttribute("PaymentKey"))){
						String paymentType = paymentMethod.getAttribute("PaymentType");
						String creditCardNo = paymentMethod.getAttribute("CreditCardNo");
						String creditCardType = paymentMethod.getAttribute("CreditCardType");
						String displayCardNo = paymentMethod.getAttribute("DisplayCreditCardNo");
						String ccExpDate = paymentMethod.getAttribute("CreditCardExpDate");
						String paymentKey = paymentKeyMap.get(paymentType + "_" + creditCardNo + "_" + creditCardType + "_" + displayCardNo + "_" + ccExpDate);
						if(!SCUtil.isVoid(paymentKey)){
							popInputPaymentMethodElem.setAttribute("PaymentKey", paymentKey);
						}
					}
					else {
						popInputPaymentMethodElem.setAttribute("PaymentKey", paymentMethod.getAttribute("PaymentKey"));
					}
					if(!SCUtil.isVoid(paymentMethod.getAttribute("SecureAuthenticationCode"))){
						popInputPaymentMethodElem.setAttribute("SecureAuthenticationCode", paymentMethod.getAttribute("SecureAuthenticationCode"));
					}
				}
			}
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.prepareFinalPOPInput");
		}
		return popInputElem;
	}
	
	private void updateBillingAddress(SCUIContext uiContext, Element inputEl) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.updateBillingAddress");
		}
		boolean billToUpdateRequired = false;
		Element customerDefaultBillingAdd = null;
		Element changeOrderInput = SCXmlUtil.createDocument("Order").getDocumentElement();
		changeOrderInput.setAttribute("OrderHeaderKey", inputEl.getAttribute("OrderHeaderKey"));
		if(!SCUtil.isVoid(inputEl) && !SCUtil.isVoid(SCXmlUtil.getAttribute(inputEl, "BillToID"))){
			customerDefaultBillingAdd = getCustomerDefBillTo(uiContext, inputEl);
		}
		if(SCUtil.isVoid(SCXmlUtil.getAttribute(inputEl, "BillToKey"))){
			//add new PersonInfoBillTo on Order based on the logic, 1) if Order has any ShipTo else 2) Customer has any Default BillTo or Bill To else 3) Store Bill To
			if(!SCUtil.isVoid(SCXmlUtil.getAttribute(inputEl, "ShipToKey"))){
				billToUpdateRequired = true;
				changeOrderInput.setAttribute("BillToKey", inputEl.getAttribute("ShipToKey"));
			}
			else if(!SCUtil.isVoid(SCXmlUtil.getAttribute(inputEl, "BillToID"))){
				if(!SCUtil.isVoid(customerDefaultBillingAdd)){
					billToUpdateRequired = true;
					Element changeOrderInputPersonInfoBillTo = SCXmlUtil.createChild(changeOrderInput, "PersonInfoBillTo");
					SCXmlUtil.mergeAttributes(customerDefaultBillingAdd, changeOrderInputPersonInfoBillTo, false);
				}
				else {
					//get Store Address
					YFCElement currentStore = (YFCElement)WSCSessionUtils.getObjFromSession(uiContext.getSession(),WSCConstants.SESSION_CURRENT_STORE);
					if(!SCUtil.isVoid(currentStore) && !SCUtil.isVoid(currentStore.getAttribute("BillingAddressKey"))){
						billToUpdateRequired = true;
						if(!SCUtil.isVoid(currentStore.getChildElement("ShipNodePersonInfo"))){
							YFCElement shipNodePersonInfo = currentStore.getChildElement("ShipNodePersonInfo");
							Element changeOrderInputPersonInfoBillTo = SCXmlUtil.createChild(changeOrderInput, "PersonInfoBillTo");
							changeOrderInputPersonInfoBillTo.setAttribute("Country", shipNodePersonInfo.getAttribute("Country"));
							changeOrderInputPersonInfoBillTo.setAttribute("State", shipNodePersonInfo.getAttribute("State"));
							changeOrderInputPersonInfoBillTo.setAttribute("City", shipNodePersonInfo.getAttribute("City"));
							changeOrderInputPersonInfoBillTo.setAttribute("ZipCode", shipNodePersonInfo.getAttribute("ZipCode"));
						}
						else {
							changeOrderInput.setAttribute("BillToKey", currentStore.getAttribute("BillingAddressKey"));
						}
					}
				}
			}
			else {
				//get Store Address
				YFCElement currentStore = (YFCElement)WSCSessionUtils.getObjFromSession(uiContext.getSession(),WSCConstants.SESSION_CURRENT_STORE);
				if(!SCUtil.isVoid(currentStore) && !SCUtil.isVoid(currentStore.getAttribute("BillingAddressKey"))){
					billToUpdateRequired = true;
					if(!SCUtil.isVoid(currentStore.getChildElement("ShipNodePersonInfo"))){
						YFCElement shipNodePersonInfo = currentStore.getChildElement("ShipNodePersonInfo");
						Element changeOrderInputPersonInfoBillTo = SCXmlUtil.createChild(changeOrderInput, "PersonInfoBillTo");
						changeOrderInputPersonInfoBillTo.setAttribute("Country", shipNodePersonInfo.getAttribute("Country"));
						changeOrderInputPersonInfoBillTo.setAttribute("State", shipNodePersonInfo.getAttribute("State"));
						changeOrderInputPersonInfoBillTo.setAttribute("City", shipNodePersonInfo.getAttribute("City"));
						changeOrderInputPersonInfoBillTo.setAttribute("ZipCode", shipNodePersonInfo.getAttribute("ZipCode"));
					}
					else {
						changeOrderInput.setAttribute("BillToKey", currentStore.getAttribute("BillingAddressKey"));
					}
				}
				else if(!SCUtil.isVoid(currentStore.getChildElement("ShipNodePersonInfo"))){
					billToUpdateRequired = true;
					YFCElement shipNodePersonInfo = currentStore.getChildElement("ShipNodePersonInfo");
					Element changeOrderInputPersonInfoBillTo = SCXmlUtil.createChild(changeOrderInput, "PersonInfoBillTo");
					changeOrderInputPersonInfoBillTo.setAttribute("Country", shipNodePersonInfo.getAttribute("Country"));
					changeOrderInputPersonInfoBillTo.setAttribute("State", shipNodePersonInfo.getAttribute("State"));
					changeOrderInputPersonInfoBillTo.setAttribute("City", shipNodePersonInfo.getAttribute("City"));
					changeOrderInputPersonInfoBillTo.setAttribute("ZipCode", shipNodePersonInfo.getAttribute("ZipCode"));
				}
				else {
					billToUpdateRequired = true;
					Element changeOrderInputPersonInfoBillTo = SCXmlUtil.createChild(changeOrderInput, "PersonInfoBillTo");
					changeOrderInputPersonInfoBillTo.setAttribute("Country", YFCLocale.getDefaultLocale().getCountry());
					changeOrderInputPersonInfoBillTo.setAttribute("ZipCode", "76666");
				}
			}
		}
		if(billToUpdateRequired){
			Element changeOrderOutput = (Element)SCUIMashupHelper.invokeMashup("wsc_paymentCapture_captureBillingAddress", changeOrderInput, uiContext);
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.updateBillingAddress");
		}
	}
	
	private Element getCustomerDefBillTo(SCUIContext uiContext, Element inputEl) {
		if (logger.isTimerEnabled()){
			logger.beginTimer("WSCOneClickProcessRefundMashup.getCustomerDefBillTo");
		}
		Element customerDetailsInputElem = SCXmlUtil.createDocument("Customer").getDocumentElement();
		SCXmlUtil.setAttribute(customerDetailsInputElem, "OrganizationCode", inputEl.getAttribute("EnterpriseCode"));
		SCXmlUtil.setAttribute(customerDetailsInputElem, "CustomerID", inputEl.getAttribute("BillToID"));
		List<Element> billToAddresses = new ArrayList<Element>();
		if(!SCUtil.isVoid(inputEl.getAttribute("CustomerContactID"))){
			Element customerContactElem = SCXmlUtil.createChild(customerDetailsInputElem, "CustomerContact");
			SCXmlUtil.setAttribute(customerContactElem, "CustomerContactID", inputEl.getAttribute("CustomerContactID"));
		}
		Element getCustomerDetailsOutput = (Element)SCUIMashupHelper.invokeMashup("wsc_getAllAddressesMashup_getCustomerDetails", customerDetailsInputElem, uiContext);
		//check for Customer Contact Default BillTo if CustomerContact present on Order
		if(!SCUtil.isVoid(inputEl.getAttribute("CustomerContactID"))){
			Element customerContactListElem = SCXmlUtil.getChildElement(getCustomerDetailsOutput, "CustomerContactList");
			Iterator<Element> customerContactElemlist = SCXmlUtil.getChildren(customerContactListElem, "CustomerContact").iterator();
			while(customerContactElemlist.hasNext()){
				Element customerContactElem = customerContactElemlist.next();
				if(customerContactElem.getAttribute("CustomerContactID").equals(inputEl.getAttribute("CustomerContactID"))){
					//Iterate through the addresses to find if any default billto
					Element customerContactAddressListElem = SCXmlUtil.getChildElement(customerContactElem, "CustomerAdditionalAddressList");
					Iterator<Element> customerContactAddressElemlist = SCXmlUtil.getChildren(customerContactAddressListElem, "CustomerAdditionalAddress").iterator();
					while(customerContactAddressElemlist.hasNext()){
						Element customerContactAddnAddress = customerContactAddressElemlist.next();
						if(!SCUtil.isVoid(customerContactAddnAddress) && !SCUtil.isVoid(customerContactAddnAddress.getAttribute("IsBillTo"))
								&& customerContactAddnAddress.getAttribute("IsBillTo").equals("Y")){
							billToAddresses.add(customerContactAddnAddress);
						}
						if(!SCUtil.isVoid(customerContactAddnAddress) && !SCUtil.isVoid(customerContactAddnAddress.getAttribute("IsDefaultBillTo"))
								&& customerContactAddnAddress.getAttribute("IsDefaultBillTo").equals("Y")){
							if (logger.isTimerEnabled()){
								logger.endTimer("WSCOneClickProcessRefundMashup.getCustomerDefBillTo");
							}
							return SCXmlUtil.getChildElement(customerContactAddnAddress, "PersonInfo");
						}
					}
				}
			}
		}
		Element customerAddressListElem = SCXmlUtil.getChildElement(getCustomerDetailsOutput, "CustomerAdditionalAddressList");
		Iterator<Element> customerAddressElemlist = SCXmlUtil.getChildren(customerAddressListElem, "CustomerAdditionalAddress").iterator();
		while(customerAddressElemlist.hasNext()){
			Element customerAddnAddress = customerAddressElemlist.next();
			if(!SCUtil.isVoid(customerAddnAddress) && !SCUtil.isVoid(customerAddnAddress.getAttribute("IsBillTo"))
					&& customerAddnAddress.getAttribute("IsBillTo").equals("Y")){
				billToAddresses.add(customerAddnAddress);
			}
			if(!SCUtil.isVoid(customerAddnAddress) && !SCUtil.isVoid(customerAddnAddress.getAttribute("IsDefaultBillTo"))
					&& customerAddnAddress.getAttribute("IsDefaultBillTo").equals("Y")){
				if (logger.isTimerEnabled()){
					logger.endTimer("WSCOneClickProcessRefundMashup.getCustomerDefBillTo");
				}
				return SCXmlUtil.getChildElement(customerAddnAddress, "PersonInfo");
			}
		}
		if(billToAddresses.size() > 0){
			if (logger.isTimerEnabled()){
				logger.endTimer("WSCOneClickProcessRefundMashup.getCustomerDefBillTo");
			}
			return billToAddresses.get(0);
		}
		if (logger.isTimerEnabled()){
			logger.endTimer("WSCOneClickProcessRefundMashup.getCustomerDefBillTo");
		}
		return null;
	}
	
	

}

