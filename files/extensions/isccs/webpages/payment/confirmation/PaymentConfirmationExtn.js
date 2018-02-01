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
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/payment/confirmation/PaymentConfirmationExtnUI", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!idx/dialogs", "scbase/loader!sc/plat/dojo/utils/BundleUtils", "scbase/loader!dojo/_base/lang", "scbase/loader!sc/plat/dojo/utils/EventUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnPaymentConfirmationExtnUI,
				_scWidgetUtils,
				_isccsUIUtils,
				scControllerUtils,
				iDialogs,
				scBundleUtils,
				dLang,
				scEventUtils
){ 
	return _dojodeclare("extn.payment.confirmation.PaymentConfirmationExtn", [_extnPaymentConfirmationExtnUI],{
		isSendPaymentLinkClickedAtLeastOnce: false,
		fn_extnSaveHandler: function(event, bEvent, ctrl, args) {
			if (this.isSendPaymentLinkClickedAtLeastOnce) {
				scEventUtils.fireEventToParent(this, "onSaveSuccess", null);
				scEventUtils.stopEvent(bEvent);
			}
		},
		fn_manageComponentVisibilityPostInit: function(event, bEvent, ctrlId, args) {
			_scWidgetUtils.hideWidget(this, "btnAddPaymentMethod", false);
		},
		fn_sendPaymentLink: function(event, bEvent, ctrlId, args) {
			this.isSendPaymentLinkClickedAtLeastOnce = true;
			var modelData = this.getModel("paymentConfirmation_getCompleteOrderDetails_Output");
			//We can also use "capturePayment_Output" or "salesOrderTotal_Output" source namespace. At least in this use case, we got same data. Additionally, in case of returns or exchanges, may also need to consider: "returnOrderTotal_Output" and/or "exchangeOrderTotal_Output".
			console.log("required model for source namespace 'paymentConfirmation_getCompleteOrderDetails_Output' - data is as follows (need to pick required attributes): ", modelData);
            var mashuInput = {"Order":{"OrderHeaderKey":modelData.Order.OrderHeaderKey,"OrderNo":modelData.Order.OrderNo}};
            mashuInput.Order.PersonInfoBillTo={"FirstName":modelData.Order.PersonInfoBillTo.FirstName, "LastName":modelData.Order.PersonInfoBillTo.FirstName, "EMailID":modelData.Order.PersonInfoBillTo.EMailID, "DayPhone":modelData.Order.PersonInfoBillTo.DayPhone, "MobilePhone":modelData.Order.PersonInfoBillTo.MobilePhone, "EveningPhone":modelData.Order.PersonInfoBillTo.EveningPhone};
            mashuInput.Order.OverallTotals={"GrandTotal":modelData.Order.OverallTotals.GrandTotal};
			_isccsUIUtils.callApi(this, mashuInput, "extn_sendPaymentLink", scControllerUtils.getMashupContext(this));
		},
		handleMashupOutput: function(mashupRefId, modelOutput, modelInput, mashupContext) {
			if (mashupRefId === "extn_sendPaymentLink") {
				var that = this;
				
				iDialogs.confirm(scBundleUtils.getString("extn_paymentLinkSentMsg"),
					dLang.hitch(that, function() {
						that.fn_confirmDraftOrder();
					}),
					dLang.hitch(that, function() {
						that.fn_blankFnToClose();
					}),
					scBundleUtils.getString("extn_Yes"),
					scBundleUtils.getString("extn_No")
				);
			}
		},
		fn_confirmDraftOrder: function() {
			if (this.ownerScreen.declaredClass === "isccs.order.wizards.createOrder.CreateOrderWizard") {
				this.ownerScreen.handleConfirm();
			}
		},
		fn_blankFnToClose: function() {
			
		}
	
});
});

