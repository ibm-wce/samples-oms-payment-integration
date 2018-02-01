[comment]: # (This file has been furnished by IBM as a simple example to provide an illustration. These examples have not)
[comment]: # (been thoroughly tested under all conditions. IBM, therefore, cannot guarantee reliability, serviceability )
[comment]: # (or function of these programs. All programs contained herein are provided to you "AS IS". )

[comment]: # (This file may include the names of individuals, companies, brands and products in order to illustrate them )
[comment]: # (as completely as possible. All of these names are ficticious and any similarity to the names and addresses )
[comment]: # (used by actual persons or business enterprises is entirely coincidental. )


# IBM Order Management on Cloud : Ezetap Payment Integration Asset
<br/>

## Table of contents

- [Overview](#overview)
- [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Downloading the asset](#downloading-the-asset)
    - [Working with existing customizations](#working-with-existing-customizations)
- [Setting up Ezetap](#setting-up-ezetap)
- [Setting up IBM Order Management](#setting-up-ibm-order-management)
    - [Configuring custom properties](#configuring-custom-properties)
    - [Configuring participant model](#configuring-participant-model)
    - [Configuring payment rule](#configuring-payment-rule)
    - [Configuring payment type](#configuring-payment-type)
    - [Configuring seller attributes](#configuring-seller-attributes)
    - [Configuring services](#configuring-services)
    - [Extending custom implementation](#extending-custom-implementation)
- [Disclaimer](#disclaimer)

## Overview

IBM Order Management provides order orchestration through a centralized inventory, order promising and fulfillment hub to support omni-channel fulfillment.
<br/> <br/>
IBM Store Engagement is a cloud offering that provides a rich set of features to assist and enhance the efficiency of various retail store operations.
<br/> <br/>
IBM Call Center for Commerce is a web-based call center solution to allow customer service representatives (CSRs) to efficiently address customer issues while handling calls with the customer (including ”Order Capture” and ”Order Modifications”).
<br/> <br/>
Each customer has vastly different business processes and operations in the Store and Call Center. Both offerings provide flexibility to customers to model them to suit their business requirements. IBM services or business partners carry out this customization and integration with various vendors and services. Payment capture is one such customizable module.
<br/> <br/>
India cloud market is little peculiar where customers ask for quick on-boarding with little or no customization to lower down TCO/TCOI. During our regular interlock with India sales team, we identified “Payment Capture” as one such module where Sales need to demonstrate a readymade payment capture solution.
<br/> <br/>
While exploring options, we evaluated mulitple suitable payment solution providers considering there are multiple ways today, end customers prefer to pay in India like Wallets, UPI, Aadhaar Pay on top of conventional ways like cards, online payments, cash etc. Considering all these, for our current implementation, we have integrated with Ezetap, one of the fastest growing mPoS and leading disruptors in digital payments in India.
<br/><br/>
Our asset has been modelled as a jumpstarter kit for services and business partners for the following payment capture scenarios:
1. Assisted Sales/Line Bursting
2. Remote Payment
3. Refund

This asset is developed as open source project and code is be hosted on IBM Git Hub repository. Business partners and developers will be free to download the asset and use it in their custom implementation. Going forward we might consider opening this repository for contribution from business partners and developers.
<br/> <br/>
As part of asset, integration source code, ReadMe document, Installation Guide will be provided
<br/>[Top](#table-of-contents)<br/><br/><br/>

## Getting started

#### Prerequisites

- Developer toolkit environment for Order Management on Cloud v17.3


#### Downloading the asset

Use git commands from your local developer toolkit system to download this asset. Alternately, download the repository zip, extract the contents, and get started.


#### Working with existing customizations

The files in this asset are structured as per customization directory structure suggested in developer toolkit. 

- Copy the files to your existing customization project directory. If you do not have one, create. 
- Modify the files as per your needs. (details in sections below)
- Follow the devtoolkit [importfromproject](https://www.ibm.com/support/knowledgecenter/SSGTJF/com.ibm.help.omcloud.custom.doc/customization/t_omc_customize_progenv_impext.html) step to deploy the customizations to your devtoolkit environment.
- Rebuild and deploy EAR and test your customization changes for integration with Ezetap.
- Follow the devtoolkit [export](https://www.ibm.com/support/knowledgecenter/SSGTJF/com.ibm.help.omcloud.custom.doc/customization/t_omc_customize_progenv_expext.html) step to export customizations package to deploy on you IBM Order Management on Cloud environment.

<br/>[Top](#table-of-contents)<br/><br/><br/>

## Setting up Ezetap

Please work with your Ezetap account contact to setup Merchant and all details required by Ezetap.

Work with your Ezetap account contact to setup OMS rest api end point and Basic authentication required to call rest api on OMS. Please note that Basic Authentication is required only when OMS rest layer is configured to use BASIC authentication mechanism. If OMS rest layer is configured with some other authenticaiton mechanism, then this setup will change  in Ezetap also.

Get the cordova plugin for Ezetap sdk from your Ezetap account contact.

<br/>[Top](#table-of-contents)<br/><br/><br/>

## Setting up IBM Order Management

#### Configuring custom properties

Add the below properties to your customer_overrides.properties file.

##### Mandatory properties
- `yfs.wsc.cordova.supportCordovaApp` - set the value as Y.
- `yfs.wsc.cordova.useragent.cordova_ezetap` - Path to cordova.js, it's value should be `./shared/cordova_ezetap/cordova.js`.
- `yfs.ezeTapAppKey` - AppKey provided by Ezetap
- `yfs.ezeTapUserName`- UserName provided by Ezetap
- `yfs.ezeTapUrl`- Ezetap instance url like `http://demo.ezetap.com`
- `yfs.ezeTapRemotePayEndPoint` - Ezetap API endpoint for remote payment, value should be `/api/2.0/pay/createPaymentLink`
- `yfs.ezeTapRefundEndPoint`- Ezetap API endpoint for refund `/api/2.0/payment/unified/refund`
- `yfs.ezetapTrasactionEndPoint` - Ezetap API endpoint for `/api/2.0/txn/details`

##### Optional properties
- `xapirest.servlet.authstyle` - Rest api auth mechanism on OMS. Set it to BASIC for simplicity.

<br/>[Top](#table-of-contents)<br/><br/><br/>

#### Setting up Ezetup Cordova plugin

- Extract Ezetap cordova plugin inside `files/extensions/wsc/webpages/ngstore/shared/cordova_ezetap/` folder so that path of `cordova.js` becomes `files/extensions/wsc/webpages/ngstore/shared/cordova_ezetap/cordova.js`.
- Update `initEzetap` method inside `extensions/wsc/webpages/ngstore/store/config/store-cordova-ezetap.config.js` to have right values for `keys`, `merchantName` and `userName`.

<br/>[Top](#table-of-contents)<br/><br/><br/>

#### Configuring participant model

As part of configing OMoC, you would have already configured your enterprise and node organizations. The below configurations are done based on a dummy enterprise **madisons** and its child node - **m-south**.
<br/>

<br/>[Top](#table-of-contents)<br/><br/><br/>

#### Configuring payment rule

Create a Payment Rule in SBC (System Setup -> Payment Rules -> Create Payment Rule) and select these three attributes
- Settlement Required
- Authorization Required
- Authorize Before Scheduling and Reauthorize on Expiration

![SBC_Payment_Rule](images/SBC_Payment_Rule.png?raw=true "SBC_Payment_Rule")
<br/><br/><br/>[Top](#table-of-contents)<br/><br/>

#### Configuring payment type

Create a Payment Type in SBC (System Setup -> Payment Types -> Create Payment Type) and select these attributes
- Payment Type ID - EZETAP
- Payment Type Group - Other
- Valid for Return - Check this checkbox
- Processing Not Required - Check this checkbox
- Charge Instead Of Authorize - Check this checkbox

![SBC_Payment_Type](images/SBC_Payment_Type.png?raw=true "SBC_Payment_Type")
<br/><br/><br/>[Top](#table-of-contents)<br/><br/>

#### Configuring seller attributes

For the enterprise select these attributes
- Payment Processing Required - Select this checkbox
- Default Payment Rule - Select the rule configured [here](#configuring-payment-rule)

![Config_Seller_Attributes](images/Config_Seller_Attributes.png?raw=true "Config_Seller_Attributes")
<br/><br/><br/>[Top](#table-of-contents)<br/><br/>

#### Configuring services

Create a synchronous service `InvokeRemotePayment` as shown in the images below. This service is used to invoke APIs on Ezetap<br/><br/>
![InvokeRemotePayment_Service](images/InvokeRemotePayment_Service.png?raw=true "InvokeRemotePayment_Service")<br/><br/>
Set the API Name as `invokeExternalPayment`, Method Name as `invokePayment` and Class name as `com.ibm.integration.payment.be.InvokeExternalPayment` <br/><br/>
<br/><br/><br/>

Create a synchronous service `ExecuteExternalPayment` as shown in the images below. This service is used to consume transaction postings from Ezetap <br/><br/>
![ExecuteExternalPayment_Service](images/ExecuteExternalPayment_Service.png?raw=true "ExecuteExternalPayment_Service")<br/><br/>
Set the API Name as `executeExternalPayment`, Method Name as `executePayment` and Class name as `com.ibm.integration.payment.be.ExecuteExternalPayment` <br/><br/>
<br/><br/><br/>

#### Create Cordova app for Store application

Follow standard practices to build hybrid cordova app for Store application.

#### Extending custom implementation 

Though OOB implementations are provided to easily get started on Ezetap integration, these implementations can be extended/changed to suit specific needs of the implementation not handled by the OOB implementations.


<br/>[Top](#table-of-contents)<br/><br/><br/>


## Disclaimer

The asset(s) provided on or through this asset repository is provided “as is” and “as available” basis. Use of the asset(s) and services offered in this asset repository is at the sole discretion of its users. 

IBM makes no statement, representation, or warranty about the accuracy or completeness of any content contained in this asset repository. IBM disclaims all responsibility and all liability (including without limitation, liability in negligence) for all expenses, losses, damages and costs you might incur as a result of the content being inaccurate or incomplete in any way for any reason.

IBM disclaims all liability for any damages arising from your access to, use of, or downloading of any material or part thereof from this asset repository.

<br/>[Top](#table-of-contents)<br/><br/><br/>
