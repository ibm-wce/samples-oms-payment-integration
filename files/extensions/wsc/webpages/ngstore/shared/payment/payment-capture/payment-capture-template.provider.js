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


(function(iscCore) {
	'use strict';

	iscCore.getTemplateForPaymentType = function(paymentType) {
		var capturePaymentTemaplatesForPaymentType = {
			'CHECK' : './shared/payment/payment-capture/templates/payment-capture-check.tpl.html',
			'REFUND_CHECK' : './shared/payment/payment-capture/templates/payment-capture-check.tpl.html',
			'PRE_PAID' : './shared/payment/payment-capture/templates/payment-capture-prepaid.tpl.html',
          	'EZETAP' : './shared/payment/payment-capture/templates/payment-capture-ezetap.tpl.html'
		};
		var capturePaymentTemaplatesForPaymentTypeGroup = {
			'CREDIT_CARD' : './shared/payment/payment-capture/templates/payment-capture-cc.tpl.html',
			'CUSTOMER_ACCOUNT' : './shared/payment/payment-capture/templates/payment-capture-cacc.tpl.html',
			'STORED_VALUE_CARD' : './shared/payment/payment-capture/templates/payment-capture-svc.tpl.html',
			'OTHER' : './shared/payment/payment-capture/templates/payment-capture-other.tpl.html'
		};
		var paymentTypeGroup = paymentType.PaymentTypeGroup;
		var paymentType = paymentType.PaymentType;
		if(capturePaymentTemaplatesForPaymentType[paymentType]){
			return capturePaymentTemaplatesForPaymentType[paymentType];
		} else if (capturePaymentTemaplatesForPaymentTypeGroup[paymentTypeGroup]){
			return capturePaymentTemaplatesForPaymentTypeGroup[paymentTypeGroup];
		}
		else {
			return null;
		}
	};

})(window.iscCore);

(function(iscCore){
  'use strict';

  
  /**
   * @ngdoc provider
   * @name  iscPaymentInputTemplateProvider
   * @description
   * Use the `iscPaymentInputTemplateProvider` to register the list of input templates specific to a payment type
   */

  angular.module('isc.shared').provider('iscPaymentInputTemplate', function() {
  	
  	this.$get = function(){
  	  /**
       * 
       * @ngdoc service
       * @name iscPaymentInputTemplate
       * 
       * @description
       * The iscPaymentInputTemplate is the core service which facilitates getting input template for capture payment
       */
  	  return {
  	    /**
      	 * @ngdoc method
      	 * @name iscPaymentInputTemplate#getTemplateForPaymentType
      	 * @description The getTemplateForPaymentType checks for the payment type (or payment type group) and returns the appropriate html input template
      	 * 
      	 * @param {object} paymentType object
      	 * @returns {string} Returns html input template name along with relative path
      	 */
  	    getTemplateForPaymentType : iscCore.getTemplateForPaymentType
  	  };
  	};
  	
  	
  	}	
  );

})(window.iscCore);
