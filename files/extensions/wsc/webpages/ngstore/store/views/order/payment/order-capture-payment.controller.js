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

/**
 *@iscdoc viewinfo
 *@viewname store.views.order.payment.order-capture-payment
 *@package store.views.order.payment
 *@class order-capture-payment
 *@description Displays the capture payment screen in order capture flow.
 *
 */
	angular.module('store').controller('store.views.order.payment.order-capture-payment',
	  ['$animate','$scope','$rootScope','iscScreen','iscWizard','$location','$filter','iscStateParams','iscMashup','iscModal','iscResourcePermission','iscI18n','iscObjectUtility','iscPayment','iscPaymentinput','iscOrder',
		function($animate,$scope,$rootScope,iscScreen,iscWizard,$location,$filter,iscStateParams,iscMashup,iscModal,iscResourcePermission,iscI18n,iscObjectUtility,iscPayment,iscPaymentinput,iscOrder) {			
			iscWizard.initializeWizardPage($scope,{
					/**
				       *ModelList
				       *Models that hold data
				       * 
				       */
					model:{
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getCountryList
				           *@description This model contains the getCommonCodeList api output to get list of countries.
				           */
						"getCountryList":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getCurrencyList
				           *@description This model contains the getCurrency api output.
				           */
						"getCurrencyList":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getRulesDetails_ReatTimeAuth
				           *@description This model contains the getRuleDetails api output for WSC_STORE_REAL_TIME_AUTHORIZATION.
				           */
						"getRulesDetails_ReatTimeAuth":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getRulesDetails_PaymentAttrEncrypt
				           *@description This model contains the getRuleDetails api output for ENCRYPT_ADDNL_ATTRIBUTES_CREDIT_CARD_PAYMENT_TYPE_GROUP.
				           */
						"getRulesDetails_PaymentAttrEncrypt":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getRulesDetails_CreditCardName
				           *@description This model contains the getRuleDetails api output for WSC_STORE_USE_CREDIT_CARD_NAME.
				           */
						"getRuleDetails_CCName":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getRuleDetails_ChargeShipLines
				           *@description This model contains the getRuleDetails api output for CHARGE_SHIP_LINES_AT_STORE.
				           */
						"getRuleDetails_ChargeShipLines":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getRuleDetails_PostponePickupPayment
				           *@description This model contains the getRuleDetails api output for POSTPONE_PMNT_PROSSESSING_TILL_PICKUP.
				           */
						"getRuleDetails_PostponePickupPayment":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getPaymentTypeList
				           *@description This model contains the getPaymentTypeList api output.
				           */
						"getPaymentTypeList":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getPaymentCardTypeList
				           *@description This model contains the getPaymentCardTypeList api output to get the list of credit card types.
				           */
						"getPaymentCardTypeList":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name getCompleteOrderDetails
				           *@description This model contains the getCompleteOrderDetails api output.
				           */
						"getCompleteOrderDetails":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name capturePayment
				           *@description This model contains the capturePayment api output.
				           */
						"capturePayment":{},
						/**
				           *@iscdoc model
				  		   *@viewname store.views.order.payment.order-capture-payment
				           *@name newPaymentMethods
				           *@description This model contains the payment attributes.
				           */
						"newPaymentMethods":{},
                        /**
				           *@iscdoc model
				  		   *@viewname store.views.order.cart-details.cart-details
				           *@name orderLineList
				           *@description This model contains the getCompleteOrderLineList api output.
				           */
				    	  "orderLineList" : {} ,
					},
					 /**
				       *MashupRefs
				       *array containing the list of mashups referred in this controller
				       */
					mashupRefs:[
								{
								/**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_common_getCountryList
								*@mashuprefid getCountryList
								*@modelname getCountryList
								*@description This mashup is used to get list of countries.
								*/
					            	mashupRefId: 'getCountryList',
					            	mashupId: 'wsc_common_getCountryList',
					            	modelName : 'getCountryList'
					            },
					            {
								/**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_common_getCurrencyList
								*@mashuprefid getCurrencyList
								*@modelname getCurrencyList
								*@description This mashup is used to get list of currencies.
								*/
					            	mashupRefId: 'getCurrencyList',
					            	mashupId: 'wsc_paymentCapture_getCurrencyList',
					            	modelName : 'getCurrencyList'
					            },
					            {
								/**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getRuleDetails_RealTimeAuthorization
								*@mashuprefid getRulesDetails_ReatTimeAuth
								*@modelname getRulesDetails_ReatTimeAuth
								*@description This mashup is used to get rule details of the Rule 'WSC_STORE_REAL_TIME_AUTHORIZATION' for the Store's Enterprise.
								*/
					            	mashupRefId: 'getRulesDetails_ReatTimeAuth',
					            	mashupId: 'wsc_paymentCapture_getRuleDetails_RealTimeAuthorization',
					            	modelName : 'getRulesDetails_ReatTimeAuth'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getRulesDetails_PaymentAttrEncrypt
								*@mashuprefid getRulesDetails_PaymentAttrEncrypt
								*@modelname getRulesDetails_PaymentAttrEncrypt
								*@description This mashup is used to get rule details of the Hub Rule 'ENCRYPT_ADDNL_ATTRIBUTES_CREDIT_CARD_PAYMENT_TYPE_GROUP'.
								*/
					            	mashupRefId: 'getRulesDetails_PaymentAttrEncrypt',
					            	mashupId: 'wsc_paymentCapture_getRulesDetails_PaymentAttrEncrypt',
					            	modelName : 'getRulesDetails_PaymentAttrEncrypt'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getRuleDetails_CCName
								*@mashuprefid getRuleDetails_CCName
								*@modelname getRuleDetails_CCName
								*@description This mashup is used to get rule details of the Rule 'WSC_STORE_USE_CREDIT_CARD_NAME' for the Store's Enterprise.
								*/
					            	mashupRefId: 'getRuleDetails_CCName',
					            	mashupId: 'wsc_paymentCapture_getRuleDetails_CCName',
					            	modelName : 'getRuleDetails_CCName'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getRuleDetails_ChargeShipLines
								*@mashuprefid getRuleDetails_ChargeShipLines
								*@modelname getRuleDetails_ChargeShipLines
								*@description This mashup is used to get rule details of the Rule 'CHARGE_SHIP_LINES_AT_STORE' for the Store's Enterprise.
								*/
					            	mashupRefId: 'getRuleDetails_ChargeShipLines',
					            	mashupId: 'wsc_paymentCapture_getRuleDetails_ChargeShipLines',
					            	modelName : 'getRuleDetails_ChargeShipLines'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getRuleDetails_PostponePickupPayment
								*@mashuprefid getRuleDetails_PostponePickupPayment
								*@modelname getRuleDetails_PostponePickupPayment
								*@description This mashup is used to get rule details of the Rule 'POSTPONE_PMNT_PROSSESSING_TILL_PICKUP' for the Store's Enterprise.
								*/
					            	mashupRefId: 'getRuleDetails_PostponePickupPayment',
					            	mashupId: 'wsc_paymentCapture_getRuleDetails_PostponePickupPayment',
					            	modelName : 'getRuleDetails_PostponePickupPayment'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getPaymentTypeList
								*@mashuprefid getPaymentTypeList
								*@modelname getPaymentTypeList
								*@description This mashup is used to get list of payment types for the current Store.
								*/
					            	mashupRefId: 'getPaymentTypeList',
					            	mashupId: 'wsc_paymentCapture_getPaymentTypeList',
					            	modelName : 'getPaymentTypeList'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentCapture_getRuleDetails_PaymentCardTypeConfLevel
								*@mashuprefid getPaymentCardTypeList
								*@modelname getPaymentCardTypeList
								*@description This mashup is used to get list of payment card types (credit card types) for the current Store.
								*/
					            	mashupRefId: 'getPaymentCardTypeList',
					            	mashupId: 'wsc_paymentCapture_getRuleDetails_PaymentCardTypeConfLevel',
					            	modelName : 'getPaymentCardTypeList'
					            },
					            {
								/**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentConfirmation_getCompleteOrderDetails
								*@mashuprefid getCompleteOrderDetails
								*@modelname getCompleteOrderDetails
								*@description This mashup is used to get complete order details for the order.
								*/
					            	mashupRefId: 'getCompleteOrderDetails',
					            	mashupId: 'wsc_paymentConfirmation_getCompleteOrderDetails',
					            	modelName : 'getCompleteOrderDetails'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentConfirmation_capturePayment
								*@mashuprefid capturePayment
								*@modelname capturePayment
								*@description This mashup is used to capture payment details on the order for capturePayment API call.
								*/
					            	mashupRefId: 'capturePayment',
					            	mashupId: 'wsc_paymentConfirmation_capturePayment',
					            	modelName : 'capturePayment'
					            },
					            {
								/**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentConfirmation_capturePayment
								*@mashuprefid deletePayment
								*@modelname capturePayment
								*@description This mashup is used to delete a payment method from the order.
								*/
					            	mashupRefId: 'deletePayment',
					            	mashupId: 'wsc_paymentConfirmation_capturePayment',
					            	modelName : 'capturePayment'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_paymentConfirmation_capturePayment
								*@mashuprefid resumePayment
								*@modelname capturePayment
								*@description This mashup is used to resume a suspended payment method on the order.
								*/
					            	mashupRefId: 'resumePayment',
					            	mashupId: 'wsc_paymentConfirmation_capturePayment',
					            	modelName : 'capturePayment'
					            },
					            {
					            /**
						        *@iscdoc mashup
								*@viewname store.views.order.payment.order-capture-payment
								*@mashupid wsc_orderCapture_oneClickCapturePayConfirm
								*@mashuprefid oneClickCapturePayConfirm
								*@modelname oneClickCapturePayConfirm
								*@description This mashup is called on click of Pay button to capture, process payment and confirm the order.
								*/
					            	mashupRefId: 'oneClickCapturePayConfirm',
					            	mashupId: 'wsc_orderCapture_oneClickCapturePayConfirm',
					            	modelName : 'oneClickCapturePayConfirm'
					            },
                            {
				  				
				  				/**
							        *@iscdoc mashup
									*@viewname store.views.order.cart-details.cart-details
									*@mashupid viewcart_getCompleteOrderLineList
									*@mashuprefid viewcart_getCompleteOrderLineList
									*@modelname orderLineList
									*@description This mashup is used to get the list of OrderLines.
									*/
				  				 mashupRefId: 'viewcart_getCompleteOrderLineList',
						         mashupId: 'viewcart_getCompleteOrderLineList',
						         modelName: "orderLineList"
						       
				  			}
					            
		            ],
		            ui:{
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {String}  orderBillToKey - BillToKey on the order.
						*/
		            	orderBillToKey : "",
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {String}  orderShipToKey - ShipToKey on the order.
						*/
		            	orderShipToKey : "",
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {String}  orderHeaderKey - OrderHeaderKey of the order.
						*/
		            	orderHeaderKey : "",
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Object} wizardOrderModel - Order model object on the wizard.
						*/
		            	wizardOrderModel: "",
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {String} orderCurrency - Order currency.
						*/
		            	orderCurrency:"",
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Object} personInfo - temporary PersonInfo model object for capturing address.
						*/
		            	personInfo:{},
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean} initcomplete - Boolean value to track completion of initialization or re-initialization (after save) for the screen.
						*/
		            	initcomplete:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean} reInitPadss - Boolean value to track completion of initialization or re-initialization for the PADSS directive.
						*/
		            	reInitPadss:'N',
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Object} custDefaultPersonInfo - Customer default billing address model object, if available, for capturing billing address on payment.
						*/
		            	custDefaultPersonInfo : {},
		            	actionClicked:'',
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Number}  remainingAmount - Calculate and store remaining amount to add payment for on the order.
						*/
		            	remainingAmount:0,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  orderHasAnyCarryLines - Boolean value to track if order has any Carry lines.
						*/
		            	orderHasAnyCarryLines:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  orderHasAnyShippingLines - Boolean value to track if order has any Shipping lines.
						*/
		            	orderHasAnyShippingLines:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  orderHasAnyPickupLines - Boolean value to track if order has any Pickup lines.
						*/
		            	orderHasAnyPickupLines:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  realTimeAuthEnabled - Boolean value to track if real time authorization is turned on for the current Store.
						*/
		            	realTimeAuthEnabled:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  ccNameRuleEnabled - Boolean value to track if 'Use single field for Credit Card Name' rule is turned on for the current Store.
						*/
		            	ccNameRuleEnabled:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  encryptEnabled - Boolean value to track if 'Encrypt additional attributes of the payment' rule is turned on for HUB.
						*/
		            	encryptEnabled:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  chargeShpLinesAtStoreRuleEnabled - Boolean value to track if 'Charge Ship To Home Order Lines At Store' rule is turned on for the current Store.
						*/
		            	chargeShpLinesAtStoreRuleEnabled:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  postponePaymentForPickupAtStoreRuleEnabled - Boolean value to track if 'Postpone Payment processing until pickup' rule is turned on for the current Store.
						*/
		            	postponePaymentForPickupAtStoreRuleEnabled:false,
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Boolean}  isInitDataReady - This is to track whether data initialization was complete.
						*/
		            	isInitDataReady:false,
		            	
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {Number}  refundAmount -  Calculate and store refund amount in confirmed order.
						*/
		            	
		            	refundAmount :0,
		            	
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {String}  refundAmount -  Calculate and store refund amount in confirmed order.
						*/
		            	
		            	screentitle : "payment.TITLE_CapturePayment",
		            	
		            	/**
						 *@iscdoc uiattr
						 *@viewname store.views.order.payment.order-capture-payment
						 *@property {String}  refundAmount -  Calculate and store refund amount in confirmed order.
						*/
		            	
		            	paymentAction : "Pay",
		            },
		            //Screen intialization
		            /**
					 * @iscdoc viewinit
					 * @viewname store.views.order.payment.order-capture-payment
					 * @method initialize
					 * @description This method calls getInitDetails method to get all initialization details for the current screen.
					 */
		            initialize: function(){		
		            	this.model.newPaymentMethods.newPaymentMethod = [];
		            	var pageInput = iscWizard.getWizardPageInput();
		            	this.ui.wizardOrderModel = iscWizard.getWizardModel("orderModel");
		            	this.ui.orderHeaderKey = this.ui.wizardOrderModel.Order.OrderHeaderKey;
		            	this.getInitDetails(this.ui.orderHeaderKey, this.ui.wizardOrderModel);
		            },
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname getInitDetails
					 *@description This method calls all init mashups to get the details of the payment types, card types, currency list, country list, relevant rules and 
					 * 				current order details and initializes the model objects for all.
					 */
		            getInitDetails : function(orderHeaderKey, wizardOrderModel){
		            	var getCountryListMashupRefObj = iscMashup.getMashupRefObj(this,"getCountryList",iscPaymentinput.getCountryListInput(wizardOrderModel));
		            	var getCurrencyListMashupRefObj = iscMashup.getMashupRefObj(this,"getCurrencyList",iscPaymentinput.getCurrencyListInput(wizardOrderModel));
		            	var ruleDetailsMashupRefObj = iscMashup.getMashupRefObj(this,"getRulesDetails_ReatTimeAuth",iscPaymentinput.getRuleDetailsInput(wizardOrderModel));
		            	var ruleDetailsEncryptMashupRefObj = iscMashup.getMashupRefObj(this,"getRulesDetails_PaymentAttrEncrypt",iscPaymentinput.getRuleDetailsInput(wizardOrderModel));
		            	var ruleDetailsCCNameMashupRefObj = iscMashup.getMashupRefObj(this,"getRuleDetails_CCName",iscPaymentinput.getRuleDetailsInput(wizardOrderModel));
		            	var ruleDetailsChargeShpLinesMashupRefObj = iscMashup.getMashupRefObj(this,"getRuleDetails_ChargeShipLines",iscPaymentinput.getRuleDetailsInput(wizardOrderModel));
		            	var ruleDetailsPostponePickupPaymentMashupRefObj = iscMashup.getMashupRefObj(this,"getRuleDetails_PostponePickupPayment",iscPaymentinput.getRuleDetailsInput(wizardOrderModel));
		            	var getPaymentTypeListMashupRefObj = iscMashup.getMashupRefObj(this,"getPaymentTypeList",iscPaymentinput.getPaymentTypeListInput(wizardOrderModel));
		            	var getPaymentCardTypeListMashupRefObj = iscMashup.getMashupRefObj(this,"getPaymentCardTypeList",iscPaymentinput.getPaymentCardTypeListInput(wizardOrderModel));
		            	var getCompleteOrderDetailsMashupRefObj = iscMashup.getMashupRefObj(this,"getCompleteOrderDetails",iscPaymentinput.getCompleteOrderDetailsInput(orderHeaderKey));
                        var getCompleteOrderLineListMashupRefObj = iscMashup.getMashupRefObj(this,"viewcart_getCompleteOrderLineList",{"OrderLine" : { "OrderHeaderKey" : orderHeaderKey}});
		            	var mashupRefList = [getCountryListMashupRefObj,getCurrencyListMashupRefObj,ruleDetailsMashupRefObj,ruleDetailsEncryptMashupRefObj,ruleDetailsCCNameMashupRefObj,
		            							ruleDetailsChargeShpLinesMashupRefObj,ruleDetailsPostponePickupPaymentMashupRefObj,getPaymentTypeListMashupRefObj,getPaymentCardTypeListMashupRefObj,
                                             getCompleteOrderDetailsMashupRefObj,getCompleteOrderLineListMashupRefObj];
		            	iscMashup.callMashups(this,mashupRefList,{}).then(this.onSuccessOfInitDetails.bind(this),angular.noop);		
		            },
		            
		            //Mashup Input Preparation Methods Start
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname getCapturePaymentInput
					 *@description This method calls prepares input model object for capturePayment API call for capturing the payment details on the order.
					 */
		            getCapturePaymentInput : function(){
		            	var apiInput = {};
		            	apiInput.Order = {};
		            	apiInput.Order.OrderHeaderKey = this.ui.wizardOrderModel.Order.OrderHeaderKey;
		            	
		            	//prepare payment method
		            	apiInput.Order.PaymentMethods = {};
		            	apiInput.Order.PaymentMethods.PaymentMethod = [];
		            	var totalOrderPaymentCounter = 0;
		            	for(var p=0; p < this.model.newPaymentMethods.newPaymentMethod.length; p++){
		            		apiInput.Order.PaymentMethods.PaymentMethod[p] = {};
		            		var newPaymentMethodObj = this.model.newPaymentMethods.newPaymentMethod[p];
		            		if(!newPaymentMethodObj.PaymentTypeGroup && (newPaymentMethodObj.PaymentType && newPaymentMethodObj.PaymentType.PaymentTypeGroup) ){
			            		newPaymentMethodObj.PaymentTypeGroup = newPaymentMethodObj.PaymentType.PaymentTypeGroup;
			            		newPaymentMethodObj.PaymentType = newPaymentMethodObj.PaymentType.PaymentType;
			            	}
			            	apiInput.Order.PaymentMethods.PaymentMethod[p].PaymentType = newPaymentMethodObj.PaymentType;
			            	apiInput.Order.PaymentMethods.PaymentMethod[p].PaymentTypeGroup = newPaymentMethodObj.PaymentTypeGroup;
			            	apiInput.Order.PaymentMethods.PaymentMethod[p].RequestedAmount = newPaymentMethodObj.RequestedAmount;
			            	if(newPaymentMethodObj.NoProcessingRequired){
			            		apiInput.Order.PaymentMethods.PaymentMethod[p].NoProcessingRequired = newPaymentMethodObj.NoProcessingRequired;
			            	}
			            	if(!this.ui.orderHasAnyCarryLines && !(this.ui.orderHasAnyShippingLines && this.ui.chargeShpLinesAtStoreRuleEnabled)){
		            			apiInput.Order.PaymentMethods.PaymentMethod[p].Operation = "Manage";
		            		}
		            		else{
				            	if(!newPaymentMethodObj.Operation){
				            		apiInput.Order.PaymentMethods.PaymentMethod[p].Operation = 'Manage';
				            	}
				            	else {
				            		apiInput.Order.PaymentMethods.PaymentMethod[p].Operation = angular.copy(newPaymentMethodObj.Operation);
				            	}
		            		}
			            	var paymentMethodObjectForInput = iscPaymentinput.prepareCapturePaymentInputForPaymentMethod(newPaymentMethodObj,this.ui.realTimeAuthEnabled,this.ui.ccNameRuleEnabled,false);
			            	apiInput.Order.PaymentMethods.PaymentMethod[p] = angular.extend(apiInput.Order.PaymentMethods.PaymentMethod[p],paymentMethodObjectForInput);
			            	totalOrderPaymentCounter++;
		            	}
		            	if(this.model.getCompleteOrderDetails.Order.PaymentMethods && this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod){
			            	for(var sp=0; sp < this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod.length; sp++){
			            		var newPaymentMethodObj = this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod[sp];
			            		var paymentMethodObjectForInput = iscPaymentinput.prepareCapturePaymentInputForSavedPaymentMethod(newPaymentMethodObj,this.ui.realTimeAuthEnabled,this.ui.ccNameRuleEnabled,false);
				            	if(!iscObjectUtility.isEmpty(paymentMethodObjectForInput)){
				            		if(newPaymentMethodObj.NoProcessingRequired){
					            		paymentMethodObjectForInput.NoProcessingRequired = newPaymentMethodObj.NoProcessingRequired;
					            	}
				            		if(!this.ui.orderHasAnyCarryLines && !(this.ui.orderHasAnyShippingLines && this.ui.chargeShpLinesAtStoreRuleEnabled)){
		            					paymentMethodObjectForInput.Operation = "Manage";
				            		}
				            		else{
					            		if(!newPaymentMethodObj.Operation){
					            			paymentMethodObjectForInput.Operation = "Manage";
					            		}
					            		else {
					            			paymentMethodObjectForInput.Operation = angular.copy(newPaymentMethodObj.Operation);
					            		}
				            		}
				            		var orderPaymentCounter = totalOrderPaymentCounter;
				            		totalOrderPaymentCounter++;
				            		apiInput.Order.PaymentMethods.PaymentMethod[orderPaymentCounter] = paymentMethodObjectForInput;
				            	}
			            	}
		            	}
		            	
		            	return apiInput;
		            },
		            //Mashup Input prepare methods END
		            
		            //Action methods START
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiOneClickCapturePayConfirm
			  		 *@description This method is called on click of Pay button in the Capture Payment screen. It prepares the input by capturing all the payment details and calls the 
			  		 *				'wsc_orderCapture_oneClickCapturePayConfirm' mashup (mashupRef : 'oneClickCapturePayConfirm'). Before preparing the input and calling the mashups, it
			  		 *				validates if enough payment methods have been added to capture funds for the Order. If validation fails, it throws an error message.
			  		 */
		            uiOneClickCapturePayConfirm : function(){
		            		
            			var remainingAmountToAuth = this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth;
            			var addedAmount = 0;
                        var hasEzeTapPayment = false;
                        var ezeTapPaymentData = {};
            			if(this.model.getCompleteOrderDetails.Order.PaymentMethods && this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod){
	            			for(var sp=0; sp < this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod.length; sp++){
	            				var newPaymentMethodObj = this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod[sp];
	            				var requestedAmount = newPaymentMethodObj.RequestedAmount;
	            				var maxChargeLimit = newPaymentMethodObj.MaxChargeLimit;
	            				addedAmount += parseFloat(requestedAmount) - parseFloat(maxChargeLimit);
	            			}
            			}
            			for(var p=0; p < this.model.newPaymentMethods.newPaymentMethod.length; p++){
            				var newPaymentMethodObj = this.model.newPaymentMethods.newPaymentMethod[p];
            				var requestedAmount = newPaymentMethodObj.RequestedAmount;
            				addedAmount += parseFloat(requestedAmount);
                            if(newPaymentMethodObj.PaymentType === "EZETAP"){
                                hasEzeTapPayment = true;
                                ezeTapPaymentData.amount = newPaymentMethodObj.RequestedAmount;
                                ezeTapPaymentData.references = {
                                    reference1 : this.model.getCompleteOrderDetails.Order.OrderNo,
                                    reference2 : this.model.getCompleteOrderDetails.Order.OrderHeaderKey
                                }
                                ezeTapPaymentData.customer = {
                                    name : this.model.getCompleteOrderDetails.Order.CustomerFirstName+' '+this.model.getCompleteOrderDetails.Order.CustomerLastName,
                                    mobileNo: this.model.getCompleteOrderDetails.Order.CustomerPhoneNo,
                                    email : this.model.getCompleteOrderDetails.Order.CustomerEMailID
                                
                                }
                            }
                            
            			}
	            			
	            		if(iscObjectUtility.isGreaterThanZero(parseFloat(remainingAmountToAuth) - parseFloat(addedAmount))){
	            			iscModal.showErrorMessage(iscI18n.translate('payment.INSUFFICIENT_PAYMENT'));
		            	}
		            	else {
			            	//** To do server side processing ** //
			            	var capturePaymentInput = this.getCapturePaymentInput();
			            	capturePaymentInput.Order.BillToKey = this.model.getCompleteOrderDetails.Order.BillToKey ? this.model.getCompleteOrderDetails.Order.BillToKey : "";
			            	capturePaymentInput.Order.ShipToKey = this.model.getCompleteOrderDetails.Order.ShipToKey ? this.model.getCompleteOrderDetails.Order.ShipToKey : "";
			            	capturePaymentInput.Order.BillToID = this.model.getCompleteOrderDetails.Order.BillToID ? this.model.getCompleteOrderDetails.Order.BillToID : "";
			            	capturePaymentInput.Order.EnterpriseCode = this.model.getCompleteOrderDetails.Order.EnterpriseCode;
			            	capturePaymentInput.Order.CustomerContactID = this.model.getCompleteOrderDetails.Order.CustomerContactID ? this.model.getCompleteOrderDetails.Order.CustomerContactID : "";
			            	capturePaymentInput.Order.PaymentTypeList = this.model.getPaymentTypeList.PaymentTypeList;
			            	if(iscOrder.orderHasOnlyCarryLines(this.model.getCompleteOrderDetails)){
			            		capturePaymentInput.Order.HasOnlyCarryLines = 'Y';
			            	}
                            
                            if(hasEzeTapPayment){
                                window.ezetapPay(ezeTapPaymentData)
                                    .then(function(successData){
                                        capturePaymentInput.Order.PaymentMethods.PaymentMethod[0].PaymentReference1 = successData.result.txn.paymentMode;
                                        capturePaymentInput.Order.PaymentMethods.PaymentMethod[0].PaymentReference2 = successData.result.txn.txnType;
                                        capturePaymentInput.Order.PaymentMethods.PaymentMethod[0].PaymentReference3 = successData.result.txn.txnId;
                                    
                                        iscMashup.callMashup(this,"oneClickCapturePayConfirm",capturePaymentInput,{}).then(this.onSuccessOfPayment.bind(this),this.onFailurePayment.bind(this));
                                    }.bind(this),function(failureData){
                                    
                                    }.bind(this));
                            }
                            else{
                                iscMashup.callMashup(this,"oneClickCapturePayConfirm",capturePaymentInput,{}).then(this.onSuccessOfPayment.bind(this),this.onFailurePayment.bind(this));
                            }
			            	
		            	}
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiExpandPayment
			  		 *@description This method is called on click of Edit link for a payment method panel in the Capture Payment screen. It opens the payment panel with all the fields which can be edited.
			  		 */
		            uiExpandPayment : function(paymentMethod){
		            	paymentMethod.expandPaymentMethodDetails();
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiCollapsePayment
			  		 *@description This method is called on click of Collapse link for a payment method panel in the Capture Payment screen. It collapses the payment panel hiding all the fields which can be edited.
			  		 */
		            uiCollapsePayment : function(paymentMethod){
		            	paymentMethod.collapsePaymentMethodDetails();
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiRemovePaymentPanel
			  		 *@description This method is called on click of Remove link for a new payment method in the Capture Payment screen. It deletes the corresponding new payment method panel.
			  		 */
		            uiRemovePaymentPanel : function(index){
		            	var deleteConfMsg = "payment.LABEL_Delete_Payment";
		            	var pendingRequestedAmount = this.model.newPaymentMethods.newPaymentMethod[index].RequestedAmount;
		            	var that = this;
		            	iscModal.showConfirmationMessage(deleteConfMsg).then(function(action){
		            				if(iscCore.isBooleanTrue(action)){
										that.model.newPaymentMethods.newPaymentMethod.splice(index,1);
						            	if(parseFloat(pendingRequestedAmount) > 0){
						            		var newRemainingAmount = parseFloat(pendingRequestedAmount) + parseFloat(that.ui.remainingAmount);
						            		that.ui.remainingAmount = newRemainingAmount.toFixed(2);
						            	}
		            				}
								});
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiAddNewPaymentPanel
			  		 *@description This method is called on click of 'Add Payment Method' link in the Capture Payment screen. It adds new payment method panel.
			  		 */
		            uiAddNewPaymentPanel : function(){
		            	var paymentPanelLength = this.model.newPaymentMethods.newPaymentMethod.length;
		            	var newPaymentMethodObj = this.getNewPaymentMethodObject();
		            	var finalRemainingAmount = 0;
		            	if(this.ui.remainingAmount > 0){
		            		finalRemainingAmount = finalRemainingAmount + parseFloat(this.ui.remainingAmount);
		            	}
		            	if(finalRemainingAmount > 0){
		            		newPaymentMethodObj.RequestedAmount = finalRemainingAmount.toFixed(2).toString();
		            		this.ui.remainingAmount = 0;
		            	}
		            	else{
		            		newPaymentMethodObj.RequestedAmount = "0";
		            	}
						
						this.model.newPaymentMethods.newPaymentMethod[paymentPanelLength] = newPaymentMethodObj;
						this.ui.actionClicked = '';
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiCancel
			  		 *@description This method is called on click of 'Cancel' button in the Capture Payment screen. It shows a confirmation message before closing the order capture flow.
			  		 */
		            uiCancel : function(){
		            	
		            	if($scope.paymentCaptureForm.$dirty){
		            
		            		iscModal.showConfirmationMessage(iscI18n.translate('payment.MSG_DirtyCheckMessage')).then(
									function(callBackAction){
				            			//
										if(callBackAction === 'YES'){
											iscWizard.closeWizard();
										}
				       				},
				  					function(callBackAction){
				            			//		Do Nothing
				  						
				       				});
		            	}
		            	else {
		            		iscModal.showConfirmationMessage(iscI18n.translate('order.WarningMessage_Cancel')).then(
									function(callBackAction){
				            			//
										if(callBackAction === 'YES'){
											iscWizard.closeWizard();
										}
				       				},
				  					function(callBackAction){
				            			//		Do Nothing
				  						
				       				});
		            	}
		            	
		            	
		            	//iscWizard.closeWizard();
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiRemovePayment
			  		 *@description This method is called on click of Remove link for an already payment method on the order in the Capture Payment screen. It deletes the corresponding saved payment method from the order
			  		 *				by making a server API call.
			  		 */
		            uiRemovePayment : function(paymentMethod){
		            	var deleteConfMsg = "payment.LABEL_Delete_Payment";
		            	var that = this;
		            	iscModal.showConfirmationMessage(deleteConfMsg).then(function(action){
            				if(iscCore.isBooleanTrue(action)){
				            	var apiInput = {};
				            	apiInput.CapturePayment = {};
				            	apiInput.CapturePayment.OrderHeaderKey = that.ui.wizardOrderModel.Order.OrderHeaderKey;
				            	apiInput.CapturePayment.PaymentMethods = {};
				            	apiInput.CapturePayment.PaymentMethods.PaymentMethod = {};
				            	apiInput.CapturePayment.PaymentMethods.PaymentMethod.PaymentKey = paymentMethod.PaymentKey;
				            	apiInput.CapturePayment.PaymentMethods.PaymentMethod.Operation = "Delete";
				            	apiInput.CapturePayment.PaymentMethods.PaymentMethod.IsCorrection = "N";
				            	var mashupRefList = [];
				            	mashupRefList = [iscMashup.getMashupRefObj(that,"deletePayment",apiInput)];
				            	var pendingRequestedAmount = paymentMethod.RequestedAmount;
				            	var chargedAmount = paymentMethod.TotalCharged;
				            	var authorizedAmount = paymentMethod.TotalAuthorized;
				            	if(parseFloat(pendingRequestedAmount) > 0){
				            		var newRemainingAmount = parseFloat(pendingRequestedAmount) + parseFloat(that.ui.remainingAmount);
				            		that.ui.remainingAmount = newRemainingAmount.toFixed(2);
				            	}
				            	if(parseFloat(chargedAmount) > 0 || parseFloat(authorizedAmount) > 0){
				            		if(parseFloat(chargedAmount) > 0){
				            			var finalRemainingAmount = parseFloat(that.ui.remainingAmount) - parseFloat(chargedAmount);
				            			that.ui.remainingAmount = finalRemainingAmount.toFixed(2);
				            		}
				            		else if(parseFloat(authorizedAmount) > 0){
				            			var finalRemainingAmount = parseFloat(that.ui.remainingAmount) - parseFloat(authorizedAmount);
				            			that.ui.remainingAmount = finalRemainingAmount.toFixed(2);
				            		}
				            	}
				            	
					            iscMashup.callMashups(that,mashupRefList,{}).then(that.onSuccessOfSavePayment.bind(that),angular.noop);
            				}
		            	});
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiResumePayment
			  		 *@description This method is called on click of Resume link for an already payment method on the order, which is in Suspended state, in the Capture Payment screen. 
			  		 *				It resumes the corresponding saved payment method on the order by making a server API call to reset the Suspension status.
			  		 */
		            uiResumePayment : function(paymentMethod){
		            	var apiInput = {};
		            	apiInput.CapturePayment = {};
		            	apiInput.CapturePayment.OrderHeaderKey = this.ui.wizardOrderModel.Order.OrderHeaderKey;
		            	apiInput.CapturePayment.PaymentMethods = {};
		            	apiInput.CapturePayment.PaymentMethods.PaymentMethod = {};
		            	apiInput.CapturePayment.PaymentMethods.PaymentMethod.PaymentKey = paymentMethod.PaymentKey;
		            	apiInput.CapturePayment.PaymentMethods.PaymentMethod.ResetSuspensionStatus = "Y";
		            	apiInput.CapturePayment.PaymentMethods.PaymentMethod.RequestedAmount = "0";
		            	var mashupRefList = [];
		            	mashupRefList = [iscMashup.getMashupRefObj(this,"resumePayment",apiInput)];
		            	this.ui.resumePaymentKey = paymentMethod.PaymentKey;
			            iscMashup.callMashups(this,mashupRefList,{}).then(this.onSuccessOfSavePayment.bind(this),angular.noop);
		            },
		            uiSavePayment : function(){
		            	var mashupRefList = [];
			            var capturePaymentMashupRefObj = iscMashup.getMashupRefObj(this,"capturePayment",this.getCapturePaymentInput());
			            mashupRefList = [capturePaymentMashupRefObj];
			            	
			            iscMashup.callMashups(this,mashupRefList,{}).then(this.onSuccessOfSavePayment.bind(this),angular.noop);
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiFinishWizard
			  		 *@description This method is called on click on View Order Summayr button on the success popup when payment capture and processing is successful to go to Order Summary screen.
			  		 */
		            uiFinishWizard : function(){
		               iscWizard.finishWizard();
		            },
		            /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uihandleScreenInvalid
			  		 *@description This method is called on click of Pay button in the Capture Payment screen, if there are any validation errors on the fields.
			  		 */
		            uihandleScreenInvalid : function(){
		            	this.ui.actionClicked = 'pay';
		            	$scope.$broadcast('callVerifyAddress',{'callingScreeen':'order-capture-payment'});
		            	iscModal.showErrorMessage(iscI18n.translate('payment.MSG_SCREEN_ERRORS'));
		            },
		             /**
			  		 *@iscdoc uimethod
					 *@viewname store.views.order.payment.order-capture-payment		 
			  		 *@methodname uiDoNothing
			  		 *@description This method is handler method for enter button click to prevent form submit on click on enter on any of the screen fields.
			  		 */
					uiDoNothing:function(){
						return;
					},
		            //Action methods END
		            
		            //Mashup Success/Failure Handler Methods START
					/**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname onSuccessOfInitDetails
					 *@description callback handler for init mashups call which initializes all the model objects.
					 *
					 */
		            onSuccessOfInitDetails : function(data){
		            	var mashupRef = data.MashupRefs.MashupRef;
		            	var newPaymentMethodObj = {};
		            	if(mashupRef !== null && mashupRef !== undefined){
		            		var len = mashupRef.length;
		            		for(var i = 0; i < len; i++){
		            			var mashupRefObj = mashupRef[i];
		            			if(mashupRefObj.mashupRefId === 'getRulesDetails_ReatTimeAuth'){
		            				this.model.getRulesDetails_ReatTimeAuth = mashupRefObj.Output;
		            				if(this.model.getRulesDetails_ReatTimeAuth && this.model.getRulesDetails_ReatTimeAuth.Rules && this.model.getRulesDetails_ReatTimeAuth.Rules.RuleSetValue == '02'){
		            					this.ui.realTimeAuthEnabled = true;
		            				}
		            				else {
		            					this.ui.realTimeAuthEnabled = false;
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getRuleDetails_ChargeShipLines'){
		            				this.model.getRuleDetails_ChargeShipLines = mashupRefObj.Output;
		            				if(this.model.getRuleDetails_ChargeShipLines && this.model.getRuleDetails_ChargeShipLines.Rules && this.model.getRuleDetails_ChargeShipLines.Rules.RuleSetValue == 'Y'){
		            					this.ui.chargeShpLinesAtStoreRuleEnabled = true;
		            				}
		            				else {
		            					this.ui.chargeShpLinesAtStoreRuleEnabled = false;
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getRuleDetails_PostponePickupPayment'){
		            				this.model.getRuleDetails_PostponePickupPayment = mashupRefObj.Output;
		            				if(this.model.getRuleDetails_PostponePickupPayment && this.model.getRuleDetails_PostponePickupPayment.Rules && this.model.getRuleDetails_PostponePickupPayment.Rules.RuleSetValue == 'Y'){
		            					this.ui.postponePaymentForPickupAtStoreRuleEnabled = true;
		            				}
		            				else {
		            					this.ui.postponePaymentForPickupAtStoreRuleEnabled = false;
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getRuleDetails_CCName'){
		            				this.model.getRuleDetails_CCName = mashupRefObj.Output;
		            				if(this.model.getRuleDetails_CCName && this.model.getRuleDetails_CCName.Rules && this.model.getRuleDetails_CCName.Rules.RuleSetValue == 'Y'){
		            					this.ui.ccNameRuleEnabled = true;
		            				}
		            				else {
		            					this.ui.ccNameRuleEnabled = false;
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getRulesDetails_PaymentAttrEncrypt'){
		            				this.model.getRulesDetails_PaymentAttrEncrypt = mashupRefObj.Output;
		            				if(this.model.getRulesDetails_PaymentAttrEncrypt && this.model.getRulesDetails_PaymentAttrEncrypt.Rules && this.model.getRulesDetails_PaymentAttrEncrypt.Rules.RuleSetValue == 'Y'){
		            					this.ui.encryptEnabled = true;
		            				}
		            				else {
		            					this.ui.encryptEnabled = false;
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getCompleteOrderDetails'){
		            				this.model.getCompleteOrderDetails = mashupRefObj.Output;
		            				this.ui.orderShipToKey = mashupRefObj.Output.Order.ShipToKey;
		            				if(mashupRefObj.Output.Order.BillToKey){
		            					this.ui.orderBillToKey = mashupRefObj.Output.Order.BillToKey;
		            				}
		            				if(iscOrder.orderHasAnyCarryLines(this.model.getCompleteOrderDetails)){
		            					this.ui.orderHasAnyCarryLines = true;
		            				}
		            				if(iscOrder.orderHasAnyShippingLines(this.model.getCompleteOrderDetails)){
		            					this.ui.orderHasAnyShippingLines = true;
		            				}
		            				if(iscOrder.orderHasAnyPickupLines(this.model.getCompleteOrderDetails)){
		            					this.ui.orderHasAnyPickupLines = true;
		            				}
		            				this.ui.orderHeaderKey = mashupRefObj.Output.Order.OrderHeaderKey;
		            				this.ui.orderCurrency = this.model.getCompleteOrderDetails.Order.PriceInfo.Currency;
		            				if(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth > 0){
		            					var paymentPanelLength = this.model.newPaymentMethods.newPaymentMethod.length;
		            					newPaymentMethodObj.RequestedAmount = iscPayment.calculateRemainingAmountToAdd(this.model.getCompleteOrderDetails, null);
		            					if(this.model.getCurrencyList.CurrencyList){
			            					for(var j = 0; j < this.model.getCurrencyList.CurrencyList.Currency.length; j++){
				            					var currency = this.model.getCurrencyList.CurrencyList.Currency[j];
				            					var currencyType = currency.Currency;
				            					if(this.ui.orderCurrency === currencyType){
				            						newPaymentMethodObj.CustomerAccountCurrency = this.model.getCurrencyList.CurrencyList.Currency[j];
				            					}
				            				}
			            				}
			            				if(this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo && this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo.PersonInfo){
		            						this.ui.custDefaultPersonInfo = this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo.PersonInfo;
		            						newPaymentMethodObj.PersonInfoBillTo = this.ui.custDefaultPersonInfo;
		            					}
		            					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerFirstName) && iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerLastName)){
		            						newPaymentMethodObj.FirstName = this.model.getCompleteOrderDetails.Order.CustomerFirstName;
											newPaymentMethodObj.LastName = this.model.getCompleteOrderDetails.Order.CustomerLastName;
											newPaymentMethodObj.CreditCardName = iscPayment.getFormattedName(newPaymentMethodObj.FirstName,newPaymentMethodObj.LastName);
		            					}
		            				}
		            				if(this.ui.orderShipToKey != "" && this.ui.orderBillToKey && this.ui.orderShipToKey === this.ui.orderBillToKey){
		            					newPaymentMethodObj.useShpAddressForBill = 'Y';
		            				}
		            				else {
			        					newPaymentMethodObj.useShpAddressForBill = 'N';
			        				}
		            				if(this.model.getCompleteOrderDetails.Order.PersonInfoBillTo){
		            					//do nothing
		            				}
		            				else if(this.model.getCompleteOrderDetails.Order.BillToID
		            						&& this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo && this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo.PersonInfo){
		            							//do nothing
		            				}
		            				else {
		            					this.model.getCompleteOrderDetails.Order.PersonInfoBillTo = {};
		            					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerFirstName) && iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerLastName)){
		            						this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.FirstName = this.model.getCompleteOrderDetails.Order.CustomerFirstName;
											this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.LastName = this.model.getCompleteOrderDetails.Order.CustomerLastName;
		            					}
		            					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerPhoneNo)){
		            						this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.DayPhone = this.model.getCompleteOrderDetails.Order.CustomerPhoneNo;
		            					}
		            					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerEMailID)){
		            						this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.EMailID = this.model.getCompleteOrderDetails.Order.CustomerEMailID;
		            					}
		            				}
		            				
		            				this.ui.initcomplete = true;
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getPaymentTypeList'){
		            				this.model.getPaymentTypeList = mashupRefObj.Output;
		            				var paymentTypeFound = false;
		            				//set CREDIT_CARD as default to load
		            				for(var j = 0; j < this.model.getPaymentTypeList.PaymentTypeList.PaymentType.length; j++){
		            					var paymentTypeDef = this.model.getPaymentTypeList.PaymentTypeList.PaymentType[j];
		            					var paymentType = paymentTypeDef.PaymentType;
		            					var paymentTypeGroup = paymentTypeDef.PaymentTypeGroup;
		            					if(paymentType === 'EZETAP'){
		            						newPaymentMethodObj.PaymentType = this.model.getPaymentTypeList.PaymentTypeList.PaymentType[j];
		            						paymentTypeFound = true;
		            						break;
		            					}
		            				}
		            				if(!paymentTypeFound){
		            					newPaymentMethodObj.PaymentType = this.model.getPaymentTypeList.PaymentTypeList.PaymentType[0];
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getPaymentCardTypeList'){
		            				this.model.getPaymentCardTypeList = mashupRefObj.Output;
		            				if(this.model.getPaymentCardTypeList.PaymentCardTypeList.PaymentCardType && this.model.getPaymentCardTypeList.PaymentCardTypeList.PaymentCardType.length > 0){
		            					newPaymentMethodObj.CardType = this.model.getPaymentCardTypeList.PaymentCardTypeList.PaymentCardType[0];
		            				}
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getCountryList'){
		            				this.model.getCountryList = mashupRefObj.Output;
		            			}
		            			else if(mashupRefObj.mashupRefId === 'getCurrencyList'){
		            				this.model.getCurrencyList = mashupRefObj.Output;
		            				if(this.orderCurrency && this.orderCurrency != ""){
		            					for(var k = 0; k < this.model.getCurrencyList.CurrencyList.Currency.length; k++){
			            					var currency = this.model.getCurrencyList.CurrencyList.Currency[k];
			            					var currencyType = currency.Currency;
			            					if(this.ui.orderCurrency === currencyType){
					            				newPaymentMethodObj.CustomerAccountCurrency = this.model.getCurrencyList.CurrencyList.Currency[k];
			            					}
			            				}
		            				}
		            				else{
			            				newPaymentMethodObj.CustomerAccountCurrency = this.model.getCurrencyList.CurrencyList.Currency[0];
		            				}
		            			}
		            		}
		            	}	
		            	if(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth > 0){
		            		if(this.model.getCompleteOrderDetails.Order.PaymentMethods && this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod 
		            		&& this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod && this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod.length  > 0){
		            			this.ui.remainingAmount = iscPayment.calculateRemainingAmountToAdd(this.model.getCompleteOrderDetails, null);
                                this.model.newPaymentMethods.newPaymentMethod[0] = newPaymentMethodObj;
		            		}
		            		else {
		            			this.model.newPaymentMethods.newPaymentMethod[0] = newPaymentMethodObj;
		            		}
		            	}else 
		            		{
		            		if(this.model.getCompleteOrderDetails.Order.DraftOrderFlag =='N'){
		            			
		            			this.ui.refundAmount = 0- parseFloat(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth,10);
		            			if(this.ui.refundAmount > 0){
		            				this.ui.screentitle = "payment.TITLE_Refund";
		            			    this.ui.paymentAction ="Refund";
		            			}
		            		}
		            		
		            	}
		            	this.ui.isInitDataReady = true;
		            	
		            	if(this.model.getCompleteOrderDetails.Order.DraftOrderFlag =='N' && this.ui.refundAmount == 0 && this.ui.remainingAmount == 0){
		            		 this.ui.paymentAction ="Nochange";
		            	}
		            },
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname onFailurePayment
					 *@description failure handler for any kind of mashup or ajax call errors on mashup call when Pay button is clicked on the Capture Payment screen.
					 *				On any kind of error, it shows an error message popup with details of the errors found.
					 *
					 */
		            onFailurePayment : function(data){
		            	this.ui.actionClicked = '';
		            	if(data.Errors && data.Errors.Error){
		            		for(var i=0; i < data.Errors.Error.length; i ++){
		            			if(data.Errors.Error[i] && data.Errors.Error[i].ErrorCode && data.Errors.Error[i].ErrorCode === "YFS10210"){
		            				break;
		            			}
		            		}
		            	}

		            	if(data.Errors.OrderDetails && data.Errors.OrderDetails.Order && data.Errors.OrderDetails.Order.length > 0){
		            		this.model.getCompleteOrderDetails.Order = data.Errors.OrderDetails.Order[0];
		            	}
		            	this.model.newPaymentMethods = {};
						this.model.newPaymentMethods.newPaymentMethod = [];
						if(parseFloat(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth) > 0){
							var newPaymentMethodObj = this.getNewPaymentMethodObject();
							this.model.newPaymentMethods.newPaymentMethod[0] = newPaymentMethodObj;
						}
		            	var messageOption = { 
	    					options: { 
	    						headerText: "payment.LABEL_PaymentFailure"
	    					},
	    					settings : {
	    						contentTemplate : "./store/views/order/payment/payment-error-content.tpl.html"
	    					},
	    					data : {
	    						contentData : {
	    							errors : data.Errors ? data.Errors : {}
	    						}
	    					}
	    				};
	    				var message = iscI18n.translate('payment.MESSAGE_PaymentFailure');
	    				iscModal.showErrorMessage(message, messageOption);
		            },
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname onSuccessOfPayment
					 *@description callback handler for mashup call when Pay button is clicked on the Capture Payment screen.
					 *				On any kind of error, it shows an error message popup with details of the errors found.
					 *				If no errors found, it shows success message popup with payment details and a button to go to Order Summary screen.
					 *
					 */
		            onSuccessOfPayment : function(data){
		            	this.ui.actionClicked = '';
		            	var mashupRef = data.MashupRefs.MashupRef;
		            	if(mashupRef !== null && mashupRef !== undefined){
		            		var len = mashupRef.length;
		            		for(var i = 0; i < len; i++){
		            			var mashupRefObj = mashupRef[i];
		            			if(mashupRefObj.mashupRefId === "oneClickCapturePayConfirm"){
		            				var errorFound = mashupRefObj.Output.Order.ErrorFound;
		            				if(errorFound === 'Y'){
		            					if(mashupRefObj.Output.Order.ErrorInAddress && mashupRefObj.Output.Order.ErrorInAddress === 'Y'){
		            						$scope.$broadcast('callVerifyAddress',{'callingScreeen':'order-capture-payment'});
		            					}
		            					else{
		            						var customerDefaultBillTo = angular.copy(this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo);
		            						var shippingAddressList = angular.copy(this.model.getCompleteOrderDetails.Order.ShippingAddressList);
		            						this.model.getCompleteOrderDetails = mashupRefObj.Output;
		            						if(!this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo && customerDefaultBillTo){
		            							this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo = customerDefaultBillTo;
		            						}
		            						if(!this.model.getCompleteOrderDetails.Order.ShippingAddressList && shippingAddressList){
		            							this.model.getCompleteOrderDetails.Order.ShippingAddressList = shippingAddressList;
		            						}
			            					this.model.newPaymentMethods = {};
			            					this.model.newPaymentMethods.newPaymentMethod = [];
			            					var remainingAmountToAuth = mashupRefObj.Output.Order.ChargeTransactionDetails.RemainingAmountToAuth;
			            					if(parseFloat(remainingAmountToAuth) > 0){
			            						//update remaining amount with AwaitingChargeInterfaceAmount and AwaitingAuthInterfaceAmount for Suspended payment methods
			            						this.ui.remainingAmount = angular.copy(remainingAmountToAuth);
			            						var suspendAwaitAmt = iscPayment.getAwtAuthNChrgAmtOnSuspndedPmts(mashupRefObj.Output.Order.PaymentMethods,this.model.getCompleteOrderDetails.Order.DocumentType);
			            						if(suspendAwaitAmt > 0){
			            							this.ui.remainingAmount = parseFloat(this.ui.remainingAmount) + parseFloat(suspendAwaitAmt);
			            							this.ui.remainingAmount = (this.ui.remainingAmount).toFixed(2);
			            						}
			            					}
									        var messageOption = { 
						    					options: { 
						    						headerText: "payment.LABEL_PaymentFailure"
						    					},
						    					settings : {
						    						contentTemplate : "./store/views/order/payment/payment-error-content.tpl.html"
						    					}
						    				};
						    				var message = iscI18n.translate('payment.MESSAGE_PaymentFailure');
						    				iscModal.showErrorMessage(message, messageOption);
		            					}
		            					
			            			}
			            			else {
			            				var customerDefaultBillTo = angular.copy(this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo);
	            						var shippingAddressList = angular.copy(this.model.getCompleteOrderDetails.Order.ShippingAddressList);
	            						this.model.getCompleteOrderDetails = mashupRefObj.Output;
	            						if(!this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo && customerDefaultBillTo){
	            							this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo = customerDefaultBillTo;
	            						}
	            						if(!this.model.getCompleteOrderDetails.Order.ShippingAddressList && shippingAddressList){
	            							this.model.getCompleteOrderDetails.Order.ShippingAddressList = shippingAddressList;
	            						}
			            				this.model.newPaymentMethods = {};
			            				this.model.newPaymentMethods.newPaymentMethod = [];
			            				var draftOrderFlag = mashupRefObj.Output.Order.DraftOrderFlag;
			            				if(draftOrderFlag === "N"){
			            					callConfirmDraftOrder = false;
			            					
			            					if(this.ui.paymentAction == 'Refund'|| this.ui.paymentAction == 'Nochange'){
			            						iscWizard.finishWizard();	    
			            					}else{
			            						
							       			var modalContentData = iscPayment.prepareSuccessMessagePopupData(this.model.getCompleteOrderDetails,this.model.getPaymentTypeList.PaymentTypeList,this.model.getPaymentCardTypeList.PaymentCardTypeList,this.ui.realTimeAuthEnabled);
							       			var messageOption = { 
						    					options: { 
						    						headerText: "payment.LABEL_PaymentSuccess",
						    						action:[
						    						  {
						    						    actionName:"OK",
						    						    actionLabel:"payment.ACTION_ViewOrderSummary"
						    						  }
						    						]
						    					},
						    					settings : {
						    						contentTemplate : "./store/views/order/payment/payment-success-content.tpl.html"
						    					},
						    					data : {
						    						contentData : modalContentData
						    					}
						    				};
						    				var message = iscI18n.translate('payment.MESSAGE_PaymentSUCCESS');
						    				iscModal.showSuccessMessage(message, messageOption).then(
						    							function(callBackData){
									            			iscWizard.finishWizard();	            			
									       				},
									       				function(callBackData){
									            			iscWizard.finishWizard();	            			
									       				}); 
			            					//iscWizard.finishWizard();
			            					}
						    				
			            				}
			            				else {
			            					var remainingAmountToAuth = mashupRefObj.Output.Order.ChargeTransactionDetails.RemainingAmountToAuth;
			            					if(parseFloat(remainingAmountToAuth) > 0){
			            						callConfirmDraftOrder = false;
			            						//update remaining amount with AwaitingChargeInterfaceAmount and AwaitingAuthInterfaceAmount for Suspended payment methods
			            						this.ui.remainingAmount = angular.copy(remainingAmountToAuth);
			            						var suspendAwaitAmt = iscPayment.getAwtAuthNChrgAmtOnSuspndedPmts(mashupRefObj.Output.Order.PaymentMethods,this.model.getCompleteOrderDetails.Order.DocumentType);
			            						if(suspendAwaitAmt > 0){
			            							this.ui.remainingAmount = parseFloat(this.ui.remainingAmount) + parseFloat(suspendAwaitAmt);
			            							this.ui.remainingAmount = (this.ui.remainingAmount).toFixed(2);
			            						}
			            						iscModal.showErrorMessage(iscI18n.translate('payment.INSUFFICIENT_PAYMENT'));
			            					}
			            					else {
								  				var messageOption = { 
							    					options: { 
							    						headerText: "payment.LABEL_PaymentFailure"
							    					},
							    					settings : {
							    						contentTemplate : "./store/views/order/payment/payment-error-content.tpl.html"
							    					},
							    					data : {
							    						contentData : {
							    							errors : data.Errors ? data.Errors : {}
							    						}
							    					}
							    				};
							    				var message = iscI18n.translate('payment.MESSAGE_PaymentFailure');
							    				iscModal.showErrorMessage(message, messageOption);
			            					}
			            				}
			            			}
		            			}
		            		}
		            	}		           
		            },
		            onSuccessOfSavePayment : function(data){
		            	var mashupRef = data.MashupRefs.MashupRef;
		            	if(mashupRef !== null && mashupRef !== undefined){
		            		var len = mashupRef.length;
		            		for(var i = 0; i < len; i++){
		            			var mashupRefObj = mashupRef[i];
		            			if(mashupRefObj.mashupRefId === 'capturePayment'){
		            				this.ui.initcomplete=false;
		            				this.ui.reInitPadss='Y';
		            				var shippingAddressList = angular.copy(this.model.getCompleteOrderDetails.Order.ShippingAddressList);
		            				this.model.getCompleteOrderDetails = mashupRefObj.Output;
		            				this.model.getCompleteOrderDetails.Order.ShippingAddressList = shippingAddressList;
		            				this.ui.orderShipToKey = mashupRefObj.Output.Order.ShipToKey;
		            				if(mashupRefObj.Output.Order.BillToKey != undefined){
		            					this.ui.orderBillToKey = mashupRefObj.Output.Order.BillToKey;
		            				}
		            				this.model.newPaymentMethods = {};
		            				this.model.newPaymentMethods.newPaymentMethod = [];
		            				var newPaymentMethodObj = {};
		            				if(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth > 0){
		            					newPaymentMethodObj = this.getNewPaymentMethodObject();
										this.model.newPaymentMethods.newPaymentMethod[0] = newPaymentMethodObj;
									}
		            				else{
		            					this.model.newPaymentMethods = {};
		            					this.model.newPaymentMethods.newPaymentMethod = [];
		            				}
		            				
		            				this.ui.initcomplete=true;
		            			}
		            			else if (mashupRefObj.mashupRefId === 'deletePayment' || mashupRefObj.mashupRefId === 'resumePayment'){		            				
		            				var getCompleteOrderDetailsMashupRefObj = iscMashup.getMashupRefObj(this,"getCompleteOrderDetails",iscPaymentinput.getCompleteOrderDetailsInput(mashupRefObj.Output.Order.OrderHeaderKey));
					            	var mashupRefList = [getCompleteOrderDetailsMashupRefObj];
					            	iscMashup.callMashups(this,mashupRefList,{}).then(this.onSuccessOfOrderDetails.bind(this),angular.noop);
		            			}
		            		}
		            	}
		            },
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname onSuccessOfOrderDetails
					 *@description callback handler for mashup call to re-initializes getCompleteOrderDetails model object when any saved payment method is deleted or resumed.
					 *
					 */
		            onSuccessOfOrderDetails : function(data){
		            	var mashupRef = data.MashupRefs.MashupRef;
		            	if(mashupRef !== null && mashupRef !== undefined){
		            		var len = mashupRef.length;
		            		for(var i = 0; i < len; i++){
		            			var mashupRefObj = mashupRef[i];
		            			if(mashupRefObj.mashupRefId === 'getCompleteOrderDetails'){
		            				this.initScreenDetails(mashupRefObj.Output);
		            				if(this.ui.resumePaymentKey){
		            					var paymentMethod = iscPayment.getPaymentMethodForKey(this.model.getCompleteOrderDetails, this.ui.resumePaymentKey);
		            					if(paymentMethod && !iscPayment.isPaymentMethodSuspended(paymentMethod,this.model.getCompleteOrderDetails.Order.DocumentType)){
		            						var resumedAmount = paymentMethod.MaxChargeLimit;
		            						var finalResumedAmount = 0;
		            						var chargedAmount = paymentMethod.TotalCharged;
		            						var authorizedAmount = paymentMethod.TotalAuthorized;
		            						if(parseFloat(chargedAmount) > 0 || parseFloat(authorizedAmount) > 0){
							            		if(parseFloat(chargedAmount) > 0){
							            			finalResumedAmount = parseFloat(resumedAmount) - parseFloat(chargedAmount);
							            		}
							            		else if(parseFloat(authorizedAmount) > 0){
							            			 finalResumedAmount = parseFloat(resumedAmount) - parseFloat(authorizedAmount);
							            		}
							            	}
							            	else {
							            		finalResumedAmount = resumedAmount;
							            	}
							            	if(parseFloat(finalResumedAmount) > 0){
							            		var newRemainingAmount = parseFloat(this.ui.remainingAmount) - parseFloat(finalResumedAmount);
							            		this.ui.remainingAmount = newRemainingAmount.toFixed(2);
							            	}
		            					}
		            					this.ui.resumePaymentKey = null;
		            				}
		            				this.ui.remainingAmount = iscPayment.calculateRemainingAmountToAdd(this.model.getCompleteOrderDetails, this.model.newPaymentMethods.newPaymentMethod);
		            			}
		            		}
		            	}
		            },
		            //Mashup Success/Failure Handler Methods END
		            
		            //Other methods	
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname getNewPaymentMethodObject
					 *@description gets new PaymentMethod model object to initalize a new Payment Method panel in the Capture Payment screen.
					 *				By default, it sets the default selected Payment Type as the first payment type belonging to the Payment type gourp "CREDIT_CARD".
					 *				If no credit card type payment types are available, then it defaults the selected payment type to the first in the list.
					 *
					 */
		            getNewPaymentMethodObject : function(){
		            	var newPaymentMethodObj = {};
    					newPaymentMethodObj.RequestedAmount = this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth;
						if(this.model.getCurrencyList.CurrencyList){
							if(this.ui.orderCurrency && this.ui.orderCurrency != ""){
								for(var j = 0; j < this.model.getCurrencyList.CurrencyList.Currency.length; j++){
									var currency = this.model.getCurrencyList.CurrencyList.Currency[j];
									var currencyType = currency.Currency;
									if(this.ui.orderCurrency === currencyType){
										newPaymentMethodObj.CustomerAccountCurrency = this.model.getCurrencyList.CurrencyList.Currency[j];
									}
								}
							}
							else {
								newPaymentMethodObj.CustomerAccountCurrency = this.model.getCurrencyList.CurrencyList.Currency[0];
							}
						}
						if(this.model.getCompleteOrderDetails.Order.PersonInfoBillTo && !iscObjectUtility.isEmpty(this.model.getCompleteOrderDetails.Order.PersonInfoBillTo)){
							newPaymentMethodObj.PersonInfoBillTo = angular.copy(this.model.getCompleteOrderDetails.Order.PersonInfoBillTo);
						}
						else if(this.ui.custDefaultPersonInfo){
							newPaymentMethodObj.PersonInfoBillTo = angular.copy(this.ui.custDefaultPersonInfo);
						}
						if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerFirstName) && iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerLastName)){
    						newPaymentMethodObj.FirstName = this.model.getCompleteOrderDetails.Order.CustomerFirstName;
							newPaymentMethodObj.LastName = this.model.getCompleteOrderDetails.Order.CustomerLastName;
							newPaymentMethodObj.CreditCardName = iscPayment.getFormattedName(newPaymentMethodObj.FirstName,newPaymentMethodObj.LastName);
    					}
						if(this.model.getPaymentTypeList.PaymentTypeList){
							var paymentTypeFound = false;
							for(var j = 0; j < this.model.getPaymentTypeList.PaymentTypeList.PaymentType.length; j++){
								var paymentTypeDef = this.model.getPaymentTypeList.PaymentTypeList.PaymentType[j];
								var paymentType = paymentTypeDef.PaymentType;
								var paymentTypeGroup = paymentTypeDef.PaymentTypeGroup;
								if(paymentType === 'EZETAP'){
									newPaymentMethodObj.PaymentType = this.model.getPaymentTypeList.PaymentTypeList.PaymentType[j];
									paymentTypeFound = true;
									break;
								}
							}
							if(!paymentTypeFound){
								newPaymentMethodObj.PaymentType = this.model.getPaymentTypeList.PaymentTypeList.PaymentType[0];
							}
						}
        				if(this.ui.orderShipToKey != "" && this.ui.orderBillToKey && this.ui.orderShipToKey === this.ui.orderBillToKey){
        					newPaymentMethodObj.useShpAddressForBill = 'Y';
        				}
        				else {
        					newPaymentMethodObj.useShpAddressForBill = 'N';
        				}
						if(this.model.getPaymentCardTypeList.PaymentCardTypeList.PaymentCardType && this.model.getPaymentCardTypeList.PaymentCardTypeList.PaymentCardType.length > 0){
        					newPaymentMethodObj.CardType = this.model.getPaymentCardTypeList.PaymentCardTypeList.PaymentCardType[0];
        				}
						return newPaymentMethodObj;
		            },
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname getTotalAmountAlreadyAdded
					 *@description gets amount value for which payment details are already added, including the saved and new payment methods on order.
					 *
					 */
		            getTotalAmountAlreadyAdded : function(){
		            	var totalAmount = 0;
		            	for(var p=0; p < this.model.newPaymentMethods.newPaymentMethod.length; p++){
		            		var paymentMethod = this.model.newPaymentMethods.newPaymentMethod[p];
		            		var requestedAmount = paymentMethod.RequestedAmount;
		            		totalAmount = parseFloat(totalAmount) + parseFloat(requestedAmount);
		            	}
		            	if(this.model.getCompleteOrderDetails.Order.PaymentMethods && this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod
		            	&& this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod.length >0){
		            		for(var p=0; p < this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod.length; p++){
			            		var paymentMethod = this.model.getCompleteOrderDetails.Order.PaymentMethods.PaymentMethod[p];
			            		var requestedAmount = paymentMethod.RequestedAmount;
			            		totalAmount = parseFloat(totalAmount) + parseFloat(requestedAmount);
			            	}
		            	}
		            	return totalAmount;
		            },
		            /**
					 *@iscdoc method
					 *@viewname store.views.order.payment.order-capture-payment
					 *@methodname initScreenDetails
					 *@description called by callback handler for mashup call to re-initializes getCompleteOrderDetails model object when any saved payment method is deleted or resumed.
					 *
					 */
		            initScreenDetails : function(orderModel){
		            	var newPaymentMethodObj = {};
        				this.model.getCompleteOrderDetails = angular.copy(orderModel);
        				this.ui.orderShipToKey = orderModel.Order.ShipToKey;
        				if(orderModel.Order.BillToKey){
        					this.ui.orderBillToKey = orderModel.Order.BillToKey;
        				}
        				/**if(parseFloat(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth) > 0){
        					if(this.model.newPaymentMethods && this.model.newPaymentMethods.newPaymentMethod 
        					&& this.model.newPaymentMethods.newPaymentMethod.length && this.model.newPaymentMethods.newPaymentMethod.length > 0){
        						var requestedAmount = this.model.newPaymentMethods.newPaymentMethod[this.model.newPaymentMethods.newPaymentMethod.length-1].RequestedAmount;
        						var pendingRequestedAmount = parseFloat(this.model.getCompleteOrderDetails.Order.ChargeTransactionDetails.RemainingAmountToAuth) - parseFloat(this.getTotalAmountAlreadyAdded());
        						if(pendingRequestedAmount > 0 ){
        							var newRequestedAmount = (requestedAmount + pendingRequestedAmount).toFixed(2);
        							this.model.newPaymentMethods.newPaymentMethod[this.model.newPaymentMethods.newPaymentMethod.length-1].RequestedAmount = newRequestedAmount.toString();
        						}
        					}
        					else {
        						newPaymentMethodObj = this.getNewPaymentMethodObject();
        						this.model.newPaymentMethods.newPaymentMethod[0] = newPaymentMethodObj;
        						this.ui.remainingAmount = this.ui.remainingAmount - newPaymentMethodObj.RequestedAmount;
        					}
        				} **/
        				if(!this.model.getCompleteOrderDetails.Order.PersonInfoBillTo
        				&& !(this.model.getCompleteOrderDetails.Order.BillToID
        						&& this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo && this.model.getCompleteOrderDetails.Order.CustomerDefaultBillTo.PersonInfo)){
        					this.model.getCompleteOrderDetails.Order.PersonInfoBillTo = {};
        					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerFirstName) && iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerLastName)){
        						this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.FirstName = this.model.getCompleteOrderDetails.Order.CustomerFirstName;
								this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.LastName = this.model.getCompleteOrderDetails.Order.CustomerLastName;
        					}
        					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerPhoneNo)){
        						this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.DayPhone = this.model.getCompleteOrderDetails.Order.CustomerPhoneNo;
        					}
        					if(iscObjectUtility.trimString(this.model.getCompleteOrderDetails.Order.CustomerEMailID)){
        						this.model.getCompleteOrderDetails.Order.PersonInfoBillTo.EMailID = this.model.getCompleteOrderDetails.Order.CustomerEMailID;
        					}
        				}
        				
        				this.ui.initcomplete = true;

		            },
		            uiIsPaymentRequired : function(){
		            	return (this.model.getCompleteOrderDetails && this.model.getCompleteOrderDetails.Order) ? iscObjectUtility.isGreaterThanZero(this.model.getCompleteOrderDetails.Order.OverallTotals.GrandTotal) : false;
		            },
					handleWizardBack:function(){
						console.log("Screen is dirty");
						var that = this;
						if($scope.paymentCaptureForm.$dirty){
		            		iscModal.showConfirmationMessage("globals.MSG_BackClickDirtyMessage").then(function(actionName){if(actionName==="YES"){
		            			
		            			if(that.model.getCompleteOrderDetails.Order.DraftOrderFlag =='N'){
		            			var OrderModificationModel = {};
		            			OrderModificationModel.Order = {};
		            			OrderModificationModel.Order.OrderHasPendingChanges = 'N';
		            			iscWizard.setWizardModel("OrderModificationModel",OrderModificationModel);
		            			}
		            			
		            			iscWizard.gotoPreviousPage(true)}},function(){});
		            	}
		            	else {
		            		if(this.model.getCompleteOrderDetails.Order.DraftOrderFlag =='N'){
		            			var OrderModificationModel = {};
		            			OrderModificationModel.Order = {};
		            			OrderModificationModel.Order.OrderHasPendingChanges = 'N';
		            			iscWizard.setWizardModel("OrderModificationModel",OrderModificationModel);
		            		}
		            		iscWizard.gotoPreviousPage(true);
		            	}
						return true;	
					},
					handleWizardExit:function(){
						var confirmationMsg = iscI18n.translate('order.WarningMessage_Cancel');
						
						iscModal.showConfirmationMessage(confirmationMsg).then(
							function(callBackAction){
									//
									if(callBackAction === 'YES'){
											iscWizard.exitWizard();
									}
							},
							function(callBackAction){
									//      Do Nothing

							});
						return true;						
					},
              		
		            
				});
		}
	]);
