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
angular.module("store").config(["iscCordovaProvider",function(iscCordovaProvider){
    // Cordova initialization start
    iscCordovaProvider.registerPluginInitialization(["$q",function($q){
        //merchantName - pass logged in user id/name
        window.ezeTapSuccessCallBack = function(response){
            console.log(JSON.parse(response));
        };
        window.ezeTapFailureCallBack = function(response){
            console.log(JSON.parse(response));
        };


        window.ezetapExit = function(){
            cordova.exec(ezeTapSuccessCallBack,ezeTapFailureCallBack,"EzeAPIPlugin","close",[]);
        }

        //window.ezetapExit();

        window.initEzetap = function(){
            var Request = {
               "demoAppKey": "",
               "prodAppKey": "",
               "merchantName": "",
               "userName":"",
               "currencyCode": "INR",
               "appMode": "DEMO",
               "captureSignature": "false",
               "prepareDevice" : "false"
            };



            cordova.exec(ezeTapSuccessCallBack,ezeTapFailureCallBack,"EzeAPIPlugin","initialize",[Request]);
        }

        window.initEzetap();

        window.ezetapPay = function(paymentData){
			var deferred = $q.defer();

          var ezeTapCardSuccessCallBack = function(response){
              console.log("Transaction successful");
              console.log(JSON.parse(response));
            	deferred.resolve(JSON.parse(response));
          };
          var ezeTapCardFailureCallBack = function(response){
              console.log("Transaction failed");
            console.log(JSON.parse(response));
            deferred.reject(SON.parse(response));
          };

          var Request = {
                  "amount": paymentData.amount,
                  "mode": "SALE",
                  "options": {
                      "amountCashback": 0.0,
                      "amountTip": 0.0,
                      "references": paymentData.references,
                      "customer": paymentData.customer
                  }
          };
          
          cordova.exec(ezeTapCardSuccessCallBack,ezeTapCardFailureCallBack,"EzeAPIPlugin","cardTransaction",[Request]);
            
            
            
            return deferred.promise;
        }
        window.ezetapPayWithWallet = function(paymentData){
          
          var ezeTapCardSuccessCallBack = function(response){
              console.log("Transaction successful");
              console.log(JSON.parse(response));
            	deferred.resolve(JSON.parse(response));
          };
          var ezeTapCardFailureCallBack = function(response){
              console.log("Transaction failed");
            console.log(JSON.parse(response));
            deferred.reject(JSON.parse(response));
          };
            
          	var Request = {
                  "amount": paymentData.amount,
                  "options": {
                      "amountCashback": 0.0,
                      "amountTip": 0.0,
                      "references": paymentData.references,
                      "customer": paymentData.customer
                  }
          	};
          
            cordova.exec(ezeTapSuccessCallBack,ezeTapFailureCallBack,"EzeAPIPlugin","walletTransaction",[Request]);

        }


        window.ezetapPayWithCash = function(){
            var Request = {
                    "amount": 1,
                    "options": {
                        "references": {
                            "reference1":"StoreOrderCash0001"
                        },
                        "customer": {
                            "name":"",
                            "mobileNo":"",
                            "email":""
                        }
                    },
            };
            cordova.exec(ezeTapSuccessCallBack,ezeTapFailureCallBack,"EzeAPIPlugin","cashTransaction",[Request]);

        }
    }]);
}]);



