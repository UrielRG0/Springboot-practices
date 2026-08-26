webpackJsonp(["main"],{

/***/ "./src/$$_lazy_route_resource lazy recursive":
/***/ (function(module, exports) {

function webpackEmptyAsyncContext(req) {
	// Here Promise.resolve().then() is used instead of new Promise() to prevent
	// uncatched exception popping up in devtools
	return Promise.resolve().then(function() {
		throw new Error("Cannot find module '" + req + "'.");
	});
}
webpackEmptyAsyncContext.keys = function() { return []; };
webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
module.exports = webpackEmptyAsyncContext;
webpackEmptyAsyncContext.id = "./src/$$_lazy_route_resource lazy recursive";

/***/ }),

/***/ "./src/app/api/ApiService.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return ApiService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_http__ = __webpack_require__("./node_modules/@angular/http/esm5/http.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};


var ApiService = (function () {
    function ApiService(http) {
        this.http = http;
    }
    ApiService.prototype.get = function (path) {
        return this.http.get('http://localhost:8080' + path);
    };
    ApiService = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__angular_http__["a" /* Http */]])
    ], ApiService);
    return ApiService;
}());



/***/ }),

/***/ "./src/app/app.component.css":
/***/ (function(module, exports) {

module.exports = "div.content {\r\n  margin: auto;\r\n  margin-top: 30px;\r\n  margin-bottom: 30px;\r\n  width: 80%;\r\n  min-height: 500px;\r\n  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;\r\n  /* font-family: Arial, Helvetica, sans-serif; */\r\n  background-image: url('data:image/png;base64,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');\r\n  font-size: 16pt;\r\n  color: #003366;\r\n}\r\n"

/***/ }),

/***/ "./src/app/app.component.html":
/***/ (function(module, exports) {

module.exports = "<!--The content below is only a placeholder and can be replaced.-->\r\n<taco-header></taco-header>\r\n\r\n<div class=\"content\">\r\n    <router-outlet></router-outlet>\r\n</div>\r\n\r\n<taco-footer></taco-footer>\r\n"

/***/ }),

/***/ "./src/app/app.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AppComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var AppComponent = (function () {
    function AppComponent() {
        this.title = 'Taco Cloud';
    }
    AppComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'app-root',
            template: __webpack_require__("./src/app/app.component.html"),
            styles: [__webpack_require__("./src/app/app.component.css")]
        })
    ], AppComponent);
    return AppComponent;
}());



/***/ }),

/***/ "./src/app/app.module.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return AppModule; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_platform_browser__ = __webpack_require__("./node_modules/@angular/platform-browser/esm5/platform-browser.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_http__ = __webpack_require__("./node_modules/@angular/http/esm5/http.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__angular_router__ = __webpack_require__("./node_modules/@angular/router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__angular_forms__ = __webpack_require__("./node_modules/@angular/forms/esm5/forms.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__app_component__ = __webpack_require__("./src/app/app.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__header_header_component__ = __webpack_require__("./src/app/header/header.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_7__footer_footer_component__ = __webpack_require__("./src/app/footer/footer.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_8__home_home_component__ = __webpack_require__("./src/app/home/home.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_9__login_login_component__ = __webpack_require__("./src/app/login/login.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_10__recents_recents_component__ = __webpack_require__("./src/app/recents/recents.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_11__api_ApiService__ = __webpack_require__("./src/app/api/ApiService.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_12__recents_RecentTacosService__ = __webpack_require__("./src/app/recents/RecentTacosService.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_13__specials_specials_component__ = __webpack_require__("./src/app/specials/specials.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_14__app_routes__ = __webpack_require__("./src/app/app.routes.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_15__cloud_title_cloudtitle_component__ = __webpack_require__("./src/app/cloud-title/cloudtitle.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_16__recents_NonWrapsPipe__ = __webpack_require__("./src/app/recents/NonWrapsPipe.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_17__recents_WrapsPipe__ = __webpack_require__("./src/app/recents/WrapsPipe.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_18__design_design_component__ = __webpack_require__("./src/app/design/design.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_19__group_box_groupbox_component__ = __webpack_require__("./src/app/group-box/groupbox.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_20__big_button_bigbutton_component__ = __webpack_require__("./src/app/big-button/bigbutton.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_21__little_button_littlebutton_component__ = __webpack_require__("./src/app/little-button/littlebutton.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_22__locations_locations_component__ = __webpack_require__("./src/app/locations/locations.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_23__angular_common_http__ = __webpack_require__("./node_modules/@angular/common/esm5/http.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_24_angular_oauth2_oidc__ = __webpack_require__("./node_modules/angular-oauth2-oidc/angular-oauth2-oidc.umd.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_24_angular_oauth2_oidc___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_24_angular_oauth2_oidc__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_25__cart_cart_component__ = __webpack_require__("./src/app/cart/cart.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_26__cart_cart_service__ = __webpack_require__("./src/app/cart/cart-service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};



























var AppModule = (function () {
    function AppModule() {
    }
    AppModule = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_2__angular_core__["NgModule"])({
            declarations: [
                __WEBPACK_IMPORTED_MODULE_5__app_component__["a" /* AppComponent */],
                __WEBPACK_IMPORTED_MODULE_6__header_header_component__["a" /* HeaderComponent */],
                __WEBPACK_IMPORTED_MODULE_8__home_home_component__["a" /* HomeComponent */],
                __WEBPACK_IMPORTED_MODULE_9__login_login_component__["a" /* LoginComponent */],
                __WEBPACK_IMPORTED_MODULE_7__footer_footer_component__["a" /* FooterComponent */],
                __WEBPACK_IMPORTED_MODULE_10__recents_recents_component__["a" /* RecentTacosComponent */],
                __WEBPACK_IMPORTED_MODULE_13__specials_specials_component__["a" /* SpecialsComponent */],
                __WEBPACK_IMPORTED_MODULE_22__locations_locations_component__["a" /* LocationsComponent */],
                __WEBPACK_IMPORTED_MODULE_15__cloud_title_cloudtitle_component__["a" /* CloudTitleComponent */],
                __WEBPACK_IMPORTED_MODULE_18__design_design_component__["a" /* DesignComponent */],
                __WEBPACK_IMPORTED_MODULE_25__cart_cart_component__["a" /* CartComponent */],
                __WEBPACK_IMPORTED_MODULE_16__recents_NonWrapsPipe__["a" /* NonWrapsPipe */],
                __WEBPACK_IMPORTED_MODULE_17__recents_WrapsPipe__["a" /* WrapsPipe */],
                __WEBPACK_IMPORTED_MODULE_19__group_box_groupbox_component__["a" /* GroupBoxComponent */],
                __WEBPACK_IMPORTED_MODULE_20__big_button_bigbutton_component__["a" /* BigButtonComponent */],
                __WEBPACK_IMPORTED_MODULE_21__little_button_littlebutton_component__["a" /* LittleButtonComponent */],
            ],
            imports: [
                __WEBPACK_IMPORTED_MODULE_0__angular_platform_browser__["a" /* BrowserModule */],
                __WEBPACK_IMPORTED_MODULE_1__angular_http__["b" /* HttpModule */],
                __WEBPACK_IMPORTED_MODULE_23__angular_common_http__["HttpClientModule"],
                __WEBPACK_IMPORTED_MODULE_4__angular_forms__["a" /* FormsModule */],
                __WEBPACK_IMPORTED_MODULE_3__angular_router__["b" /* RouterModule */].forRoot(__WEBPACK_IMPORTED_MODULE_14__app_routes__["a" /* routes */]),
                __WEBPACK_IMPORTED_MODULE_24_angular_oauth2_oidc__["OAuthModule"].forRoot()
            ],
            providers: [
                __WEBPACK_IMPORTED_MODULE_11__api_ApiService__["a" /* ApiService */],
                __WEBPACK_IMPORTED_MODULE_26__cart_cart_service__["a" /* CartService */],
                __WEBPACK_IMPORTED_MODULE_12__recents_RecentTacosService__["a" /* RecentTacosService */],
            ],
            bootstrap: [__WEBPACK_IMPORTED_MODULE_5__app_component__["a" /* AppComponent */]]
        })
    ], AppModule);
    return AppModule;
}());



/***/ }),

/***/ "./src/app/app.routes.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return routes; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__home_home_component__ = __webpack_require__("./src/app/home/home.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__recents_recents_component__ = __webpack_require__("./src/app/recents/recents.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__specials_specials_component__ = __webpack_require__("./src/app/specials/specials.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__design_design_component__ = __webpack_require__("./src/app/design/design.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__locations_locations_component__ = __webpack_require__("./src/app/locations/locations.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_5__cart_cart_component__ = __webpack_require__("./src/app/cart/cart.component.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_6__login_login_component__ = __webpack_require__("./src/app/login/login.component.ts");







var routes = [
    {
        path: 'home',
        component: __WEBPACK_IMPORTED_MODULE_0__home_home_component__["a" /* HomeComponent */]
    },
    {
        path: 'login',
        component: __WEBPACK_IMPORTED_MODULE_6__login_login_component__["a" /* LoginComponent */]
    },
    {
        path: 'recents',
        component: __WEBPACK_IMPORTED_MODULE_1__recents_recents_component__["a" /* RecentTacosComponent */]
    },
    {
        path: 'specials',
        component: __WEBPACK_IMPORTED_MODULE_2__specials_specials_component__["a" /* SpecialsComponent */]
    },
    {
        path: 'locations',
        component: __WEBPACK_IMPORTED_MODULE_4__locations_locations_component__["a" /* LocationsComponent */]
    },
    {
        path: 'design',
        component: __WEBPACK_IMPORTED_MODULE_3__design_design_component__["a" /* DesignComponent */]
    },
    {
        path: 'cart',
        component: __WEBPACK_IMPORTED_MODULE_5__cart_cart_component__["a" /* CartComponent */]
    },
    {
        path: '**',
        redirectTo: 'home',
        pathMatch: 'full'
    },
];


/***/ }),

/***/ "./src/app/big-button/bigbutton.component.css":
/***/ (function(module, exports) {

module.exports = "button {\r\n  background-color: rgba(118, 214, 255, 0.25);\r\n  border: 1px solid #76D6FF;\r\n  color: #003366;\r\n  font-size: 16pt;\r\n  padding: 8px;\r\n  border-radius: 10px;\r\n  font-weight: bold;\r\n  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif\r\n}\r\n"

/***/ }),

/***/ "./src/app/big-button/bigbutton.component.html":
/***/ (function(module, exports) {

module.exports = "<button>{{label}}</button>\r\n"

/***/ }),

/***/ "./src/app/big-button/bigbutton.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return BigButtonComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var BigButtonComponent = (function () {
    function BigButtonComponent() {
    }
    BigButtonComponent.prototype.ngOnInit = function () { };
    __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Input"])(),
        __metadata("design:type", String)
    ], BigButtonComponent.prototype, "label", void 0);
    BigButtonComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'big-button',
            template: __webpack_require__("./src/app/big-button/bigbutton.component.html"),
            styles: [__webpack_require__("./src/app/big-button/bigbutton.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], BigButtonComponent);
    return BigButtonComponent;
}());



/***/ }),

/***/ "./src/app/cart/cart-item.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return CartItem; });
var CartItem = (function () {
    function CartItem(taco) {
        this.quantity = 1;
        this.taco = taco;
    }
    Object.defineProperty(CartItem.prototype, "lineTotal", {
        get: function () {
            return this.quantity * 4.99;
        },
        enumerable: true,
        configurable: true
    });
    return CartItem;
}());



/***/ }),

/***/ "./src/app/cart/cart-service.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return CartService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__cart_item__ = __webpack_require__("./src/app/cart/cart-item.ts");

var CartService = (function () {
    function CartService() {
        this.items$ = [];
        this.items$ = [];
    }
    CartService.prototype.addToCart = function (taco) {
        this.items$.push(new __WEBPACK_IMPORTED_MODULE_0__cart_item__["a" /* CartItem */](taco));
    };
    CartService.prototype.getItemsInCart = function () {
        return this.items$;
    };
    CartService.prototype.getCartTotal = function () {
        var total = 0;
        this.items$.forEach(function (item) {
            total += item.lineTotal;
        });
        return total;
    };
    CartService.prototype.emptyCart = function () {
        this.items$ = [];
    };
    return CartService;
}());



/***/ }),

/***/ "./src/app/cart/cart.component.css":
/***/ (function(module, exports) {

module.exports = "ul.commalist {\r\n  display: inline;\r\n  list-style: none;\r\n  padding: 0;\r\n}\r\n\r\nul.commalist li {\r\n  display: inline;\r\n}\r\n\r\nul.commalist li:not(:first-child):before {\r\n  content: \", \";\r\n}\r\n\r\nul.commalist li:last-child:before {\r\n  content: \" and \";\r\n}\r\n\r\nul.commalist li:only-child:before {\r\n  content: \"\";\r\n}\r\n\r\nspan.caps {\r\n  text-transform: capitalize;\r\n}\r\n\r\na {\r\n  color: inherit;\r\n}\r\n\r\nlabel {\r\n  display: block;\r\n  font-weight: bold;\r\n  font-size: 14pt;\r\n}\r\n\r\ninput {\r\n  font-size: 14pt;\r\n  color: #003366;\r\n}\r\n\r\nselect {\r\n  font-size: 14pt;\r\n  color: #003366;\r\n  background-color: white;\r\n}\r\n\r\ndiv.groupBoxHalf {\r\n  width: 450px;\r\n}\r\n\r\ndiv.groupBoxFull {\r\n  width: 938px;\r\n}\r\n\r\ndiv.groupBox {\r\n  display: inline-block;\r\n  vertical-align: top;\r\n  background-image: url('data:image/png;base64,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');\r\n  background-repeat: no-repeat;\r\n  background-color: rgba(118, 214, 255, 0.25);\r\n  margin: 10px;\r\n  border-radius: 10px;\r\n  text-align: right;\r\n  padding: 0 10px 10px 0;\r\n  height: 100%;\r\n  color: #003366;\r\n  border: 1px solid #76D6FF;\r\n}\r\n\r\ndiv.groupBoxTitle {\r\n  text-align: center;\r\n  font-weight: bold;\r\n  border-bottom: 1px solid lightblue;\r\n  padding-bottom: 10px;\r\n  padding-top: 10px;\r\n}\r\n\r\ndiv.groupBoxContent {\r\n  padding: 20px;\r\n  text-align: left;\r\n}\r\n"

/***/ }),

/***/ "./src/app/cart/cart.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Order your taco creations</cloud-title>\r\n\r\n<p>You've crafted your mouthwatering taco masterpieces. Now, just imagine those\r\n  creations appearing before you at home...or work...or wherever you are!</p>\r\n\r\n<p *ngIf=\"(cartItems.length == 0)\"><b>There are no tacos currently in your cart.\r\n  <a [routerLink]=\"['../design']\">Create one now</a> or choose a\r\n  <a [routerLink]=\"['../recents']\">recently created taco</a>.</b></p>\r\n\r\n<div *ngIf=\"(cartItems.length > 0)\">\r\n<p>Here are your latest taco designs. Just say the\r\n    word and we'll set them on a cloud aloft to you!</p>\r\n\r\n    <div class=\"groupBox groupBoxFull\">\r\n      <div class=\"groupBoxTitle\">Here are my tacos...</div>\r\n      <div class=\"groupBoxContent\">\r\n        <table border=\"1\" width=\"90%\">\r\n          <thead>\r\n            <tr>\r\n              <td>Qty</td>\r\n              <td>Name</td>\r\n              <td>Description</td>\r\n              <td>Price</td>\r\n            </tr>\r\n          </thead>\r\n          <tbody>\r\n            <tr *ngFor=\"let item of cartItems\">\r\n              <td>\r\n                <select [(ngModel)]=\"item.quantity\">\r\n                  <option value=\"0\">Remove</option>\r\n                  <option value=\"1\">1</option>\r\n                  <option value=\"2\">2</option>\r\n                  <option value=\"3\">3</option>\r\n                  <option value=\"4\">4</option>\r\n                  <option value=\"5\">5</option>\r\n                  <option value=\"6\">6</option>\r\n                  <option value=\"7\">7</option>\r\n                  <option value=\"8\">8</option>\r\n                  <option value=\"9\">9</option>\r\n                  <option value=\"10\">10</option>\r\n                </select>\r\n              </td>\r\n              <td>The \"<span class=\"caps\">{{item.taco.name}}</span>\"</td>\r\n              <td>\r\n                <ul class=\"commalist\">\r\n                  <li *ngFor=\"let ingredient of item.taco.ingredients | nonwraps\">{{ingredient.name}}</li>\r\n                </ul>\r\n                <span *ngIf=\"(item.taco.ingredients | wraps)?.length > 0\">\r\n                    <span>wrapped in</span>\r\n                  <ul class=\"commalist\">\r\n                      <li *ngFor=\"let ingredient of item.taco.ingredients | wraps\">{{ingredient.name}}</li>\r\n                  </ul>\r\n                </span>\r\n                <span *ngIf=\"(item.taco.ingredients | wraps)?.length == 0\">in a bowl</span>\r\n                </td>\r\n              <td align=\"right\">{{4.99 * item.quantity | currency}}</td>\r\n            </tr>\r\n          </tbody>\r\n          <tfoot>\r\n            <tr>\r\n              <td colspan=\"3\" align=\"right\">Total:</td>\r\n              <td align=\"right\"><span>{{cartTotal | currency}}</span></td>\r\n            </tr>\r\n          </tfoot>\r\n        </table>\r\n      </div>\r\n    </div>\r\n\r\n    <form (ngSubmit)=\"onSubmit()\">\r\n      <div class=\"groupBox groupBoxHalf\">\r\n        <div class=\"groupBoxTitle\">Deliver them to...</div>\r\n        <div class=\"groupBoxContent\">\r\n          <label for=\"deliveryName\">Name: </label>\r\n          <input type=\"text\" [(ngModel)]=\"model.deliveryName\" name=\"deliveryName\" size=\"40\"/>\r\n          <br/>\r\n\r\n          <label for=\"deliveryStreet\">Street address: </label>\r\n          <input type=\"text\" [(ngModel)]=\"model.deliveryStreet\" name=\"deliveryStreet\" size=\"40\"/>\r\n          <br/>\r\n\r\n          <label for=\"deliveryCity\">City / State: </label>\r\n          <input type=\"text\" [(ngModel)]=\"model.deliveryCity\" name=\"deliveryCity\" size=\"30\"/>\r\n          <select [(ngModel)]=\"model.deliveryState\" name=\"deliveryState\">\r\n            <option value=\"\">--</option>\r\n            <option value=\"AL\">AL</option>\r\n            <option value=\"AK\">AK</option>\r\n            <option value=\"AR\">AR</option>\r\n            <option value=\"AZ\">AZ</option>\r\n            <option value=\"CA\">CA</option>\r\n            <option value=\"CO\">CO</option>\r\n            <option value=\"CT\">CT</option>\r\n            <option value=\"DE\">DE</option>\r\n            <option value=\"FL\">FL</option>\r\n            <option value=\"GA\">GA</option>\r\n            <option value=\"HI\">HI</option>\r\n            <option value=\"IA\">IA</option>\r\n            <option value=\"ID\">ID</option>\r\n            <option value=\"IL\">IL</option>\r\n            <option value=\"IN\">IN</option>\r\n            <option value=\"KS\">KS</option>\r\n            <option value=\"KY\">KY</option>\r\n            <option value=\"LA\">LA</option>\r\n            <option value=\"MA\">MA</option>\r\n            <option value=\"MD\">MD</option>\r\n            <option value=\"ME\">ME</option>\r\n            <option value=\"MI\">MI</option>\r\n            <option value=\"MN\">MN</option>\r\n            <option value=\"MO\">MO</option>\r\n            <option value=\"MS\">MS</option>\r\n            <option value=\"MT\">MT</option>\r\n            <option value=\"NC\">NC</option>\r\n            <option value=\"ND\">ND</option>\r\n            <option value=\"NE\">NE</option>\r\n            <option value=\"NH\">NH</option>\r\n            <option value=\"NJ\">NJ</option>\r\n            <option value=\"NM\">NM</option>\r\n            <option value=\"NV\">NV</option>\r\n            <option value=\"NY\">NY</option>\r\n            <option value=\"OH\">OH</option>\r\n            <option value=\"OK\">OK</option>\r\n            <option value=\"OR\">OR</option>\r\n            <option value=\"PA\">PA</option>\r\n            <option value=\"RI\">RI</option>\r\n            <option value=\"SC\">SC</option>\r\n            <option value=\"SD\">SD</option>\r\n            <option value=\"TN\">TN</option>\r\n            <option value=\"TX\">TX</option>\r\n            <option value=\"UT\">UT</option>\r\n            <option value=\"VA\">VA</option>\r\n            <option value=\"VT\">VT</option>\r\n            <option value=\"WA\">WA</option>\r\n            <option value=\"WI\">WI</option>\r\n            <option value=\"WV\">WV</option>\r\n            <option value=\"WY\">WY</option>\r\n          </select>\r\n          <br/>\r\n\r\n          <label for=\"deliveryZip\">Zip code: </label>\r\n          <input type=\"text\" [(ngModel)]=\"model.deliveryZip\" name=\"deliveryZip\"/>\r\n        </div>\r\n      </div>\r\n\r\n      <div class=\"groupBox groupBoxHalf\">\r\n        <div class=\"groupBoxTitle\">Here's how I'll pay...</div>\r\n        <div class=\"groupBoxContent\">\r\n          <label for=\"ccNumber\">Credit Card #: </label>\r\n          <input type=\"text\" [(ngModel)]=\"model.ccNumber\" name=\"ccNumber\"/>\r\n          <br/>\r\n\r\n          <label for=\"ccExpiration\">Expiration / CVV: </label>\r\n          <input type=\"text\" [(ngModel)]=\"model.ccExpiration\" name=\"ccExpiration\"/>\r\n          <input type=\"text\" [(ngModel)]=\"model.ccCVV\" name=\"ccCVV\" size=\"4\"/>\r\n        </div>\r\n      </div>\r\n\r\n      <br/>\r\n\r\n      <big-button label=\"Submit Order\"></big-button>\r\n      <!-- <input type=\"submit\" value=\"Submit order\"/> -->\r\n    </form>\r\n</div>\r\n"

/***/ }),

/***/ "./src/app/cart/cart.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return CartComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__cart_service__ = __webpack_require__("./src/app/cart/cart-service.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_common_http__ = __webpack_require__("./node_modules/@angular/common/esm5/http.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};



var CartComponent = (function () {
    function CartComponent(cart, httpClient) {
        this.cart = cart;
        this.httpClient = httpClient;
        this.model = {
            deliveryName: '',
            deliveryStreet: '',
            deliveryState: '',
            deliveryZip: '',
            ccNumber: '',
            ccExpiration: '',
            ccCVV: '',
            tacos: []
        };
        this.cart = cart;
    }
    CartComponent.prototype.ngOnInit = function () { };
    Object.defineProperty(CartComponent.prototype, "cartItems", {
        get: function () {
            return this.cart.getItemsInCart();
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(CartComponent.prototype, "cartTotal", {
        get: function () {
            return this.cart.getCartTotal();
        },
        enumerable: true,
        configurable: true
    });
    CartComponent.prototype.onSubmit = function () {
        var _this = this;
        // this.model.tacos = this.cart.getItemsInCart();
        this.cart.getItemsInCart().forEach(function (cartItem) {
            _this.model.tacos.push(cartItem.taco);
        });
        this.httpClient.post('http://localhost:8080/api/orders', this.model, {
            headers: new __WEBPACK_IMPORTED_MODULE_2__angular_common_http__["HttpHeaders"]().set('Content-type', 'application/json')
                .set('Accept', 'application/json'),
        }).subscribe(function (r) { return _this.cart.emptyCart(); });
        // TODO: Do something after this...navigate to a thank you page or something
    };
    CartComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'taco-cart',
            template: __webpack_require__("./src/app/cart/cart.component.html"),
            styles: [__webpack_require__("./src/app/cart/cart.component.css")]
        }),
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__cart_service__["a" /* CartService */], __WEBPACK_IMPORTED_MODULE_2__angular_common_http__["HttpClient"]])
    ], CartComponent);
    return CartComponent;
}());



/***/ }),

/***/ "./src/app/cloud-title/cloudtitle.component.css":
/***/ (function(module, exports) {

module.exports = "div.cloudtitle {\r\n  height:95px;\r\n  width: 100%;\r\n  text-align: right;\r\n  background-image: url('Cloud_sm.38af094ac456ef6df12e.png');\r\n  background-repeat: no-repeat;\r\n  background-position: right;\r\n  margin-bottom: 20px;\r\n}\r\n\r\ndiv.cloudtitletext {\r\n  padding-top: 20px;\r\n  width: 100%;\r\n  text-align: right;\r\n  color: #003366;\r\n}\r\n"

/***/ }),

/***/ "./src/app/cloud-title/cloudtitle.component.html":
/***/ (function(module, exports) {

module.exports = "<div class=\"cloudtitle\">\r\n  <div class=\"cloudtitletext\">\r\n    <h2><ng-content></ng-content></h2>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "./src/app/cloud-title/cloudtitle.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return CloudTitleComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var CloudTitleComponent = (function () {
    function CloudTitleComponent() {
    }
    CloudTitleComponent.prototype.ngOnInit = function () { };
    CloudTitleComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'cloud-title',
            template: __webpack_require__("./src/app/cloud-title/cloudtitle.component.html"),
            styles: [__webpack_require__("./src/app/cloud-title/cloudtitle.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], CloudTitleComponent);
    return CloudTitleComponent;
}());



/***/ }),

/***/ "./src/app/design/design.component.css":
/***/ (function(module, exports) {

module.exports = "div.ingredientsblock {\r\n  width: 1000px;\r\n}\r\n\r\ninput.nameField {\r\n  font-size: 16pt;\r\n  background: none;\r\n  border: none;\r\n  border-bottom: 1px solid #003366;\r\n}\r\n"

/***/ }),

/***/ "./src/app/design/design.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Design your taco masterpiece</cloud-title>\r\n\r\n<p>We provide a palette of fresh ingredients...you provide the inspiration! Select the\r\n  components that will come together as your crowning achievement of taco workmanship.</p>\r\n\r\n<form (ngSubmit)=\"onSubmit()\">\r\n  <div class=\"ingredientsblock\" style=\"text-align: center; width: 100%;\">\r\n    <group-box title=\"Record your wrap\">\r\n      <span *ngFor=\"let wrap of wraps\">\r\n        <input type=\"checkbox\" (change)=\"updateIngredients(wrap, $event)\" name=\"ingredients\" value=\"{{wrap.id}}\"/>{{wrap.name}}<br/>\r\n      </span>\r\n    </group-box>\r\n    <group-box title=\"Pick your proteins\">\r\n      <span *ngFor=\"let protein of proteins\">\r\n        <input type=\"checkbox\" (change)=\"updateIngredients(protein, $event)\" name=\"ingredients\" value=\"{{protein.id}}\"/>{{protein.name}}<br/>\r\n      </span>\r\n    </group-box>\r\n    <group-box title=\"Choose your cheeses\">\r\n      <span *ngFor=\"let cheese of cheeses\">\r\n        <input type=\"checkbox\" (change)=\"updateIngredients(cheese, $event)\" name=\"ingredients\" value=\"{{cheese.id}}\"/>{{cheese.name}}<br/>\r\n      </span>\r\n    </group-box>\r\n    <group-box title=\"Vouch for your veggies\">\r\n      <span *ngFor=\"let veggie of veggies\">\r\n        <input type=\"checkbox\" (change)=\"updateIngredients(veggie, $event)\" name=\"ingredients\" value=\"{{veggie.id}}\"/>{{veggie.name}}<br/>\r\n      </span>\r\n    </group-box>\r\n    <group-box title=\"Select your sauces\">\r\n      <span *ngFor=\"let sauce of sauces\">\r\n        <input type=\"checkbox\" (change)=\"updateIngredients(sauce, $event)\" name=\"ingredients\" value=\"{{sauce.id}}\"/>{{sauce.name}}<br/>\r\n      </span>\r\n    </group-box>\r\n    <group-box title=\"Name your taco creation\">\r\n      My taco magnum opus shall be called<br/>\r\n      the \"<input type=\"text\" [(ngModel)]=\"model.name\" name=\"name\" class=\"nameField\"/>\"!\r\n    </group-box>\r\n  </div>\r\n\r\n  <div style=\"text-align:center; width:100%;\">\r\n    <big-button label=\"Construct this taco\"></big-button>\r\n  </div>\r\n</form>\r\n"

/***/ }),

/***/ "./src/app/design/design.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return DesignComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_common_http__ = __webpack_require__("./node_modules/@angular/common/esm5/http.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__angular_router___ = __webpack_require__("./node_modules/@angular/router/esm5/router.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__cart_cart_service__ = __webpack_require__("./src/app/cart/cart-service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};




var DesignComponent = (function () {
    function DesignComponent(httpClient, router, cart) {
        this.httpClient = httpClient;
        this.router = router;
        this.cart = cart;
        this.model = {
            name: '',
            ingredients: []
        };
        this.wraps = [];
        this.proteins = [];
        this.veggies = [];
        this.cheeses = [];
        this.sauces = [];
    }
    // tag::ngOnInit[]
    DesignComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.httpClient.get('http://localhost:8080/api/ingredients')
            .subscribe(function (data) {
            _this.allIngredients = data;
            _this.wraps = _this.allIngredients.filter(function (w) { return w.type === 'WRAP'; });
            _this.proteins = _this.allIngredients.filter(function (p) { return p.type === 'PROTEIN'; });
            _this.veggies = _this.allIngredients.filter(function (v) { return v.type === 'VEGGIES'; });
            _this.cheeses = _this.allIngredients.filter(function (c) { return c.type === 'CHEESE'; });
            _this.sauces = _this.allIngredients.filter(function (s) { return s.type === 'SAUCE'; });
        });
    };
    // end::ngOnInit[]
    DesignComponent.prototype.updateIngredients = function (ingredient, event) {
        if (event.target.checked) {
            this.model.ingredients.push(ingredient);
        }
        else {
            this.model.ingredients.splice(this.model.ingredients.findIndex(function (i) { return i === ingredient; }), 1);
        }
    };
    // tag::onSubmit[]
    DesignComponent.prototype.onSubmit = function () {
        var _this = this;
        this.httpClient.post('http://localhost:8080/api/tacos', this.model, {
            headers: new __WEBPACK_IMPORTED_MODULE_1__angular_common_http__["HttpHeaders"]().set('Content-type', 'application/json'),
        }).subscribe(function (taco) { return _this.cart.addToCart(taco); });
        this.router.navigate(['/cart']);
    };
    DesignComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'taco-design',
            template: __webpack_require__("./src/app/design/design.component.html"),
            styles: [__webpack_require__("./src/app/design/design.component.css")]
        }),
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__angular_common_http__["HttpClient"], __WEBPACK_IMPORTED_MODULE_2__angular_router___["a" /* Router */], __WEBPACK_IMPORTED_MODULE_3__cart_cart_service__["a" /* CartService */]])
    ], DesignComponent);
    return DesignComponent;
}());



/***/ }),

/***/ "./src/app/footer/footer.component.css":
/***/ (function(module, exports) {

module.exports = "div.footer {\r\n  background-color: #76D6FF;\r\n  background: -webkit-gradient(linear, left top, left bottom, from(white), to(#76D6FF));\r\n  background: linear-gradient(white, #76D6FF);\r\n  width: 100%;\r\n  height: 70px;\r\n  border-radius: 10px;\r\n}\r\n\r\ndiv.footerText {\r\n  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;\r\n  text-align: center;\r\n  padding-top: 35px;\r\n  font-size: 10pt;\r\n}\r\n\r\na {\r\n  color: inherit;\r\n}\r\n"

/***/ }),

/***/ "./src/app/footer/footer.component.html":
/***/ (function(module, exports) {

module.exports = "<div class=\"footer\">\r\n  <div class=\"footerText\">\r\n    To learn more about Taco Cloud, read <a href=\"https://www.manning.com/books/spring-in-action-fifth-edition\"><i>Spring in Action, 5th Edition</i></a>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "./src/app/footer/footer.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return FooterComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var FooterComponent = (function () {
    function FooterComponent() {
    }
    FooterComponent.prototype.ngOnInit = function () { };
    FooterComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'taco-footer',
            template: __webpack_require__("./src/app/footer/footer.component.html"),
            styles: [__webpack_require__("./src/app/footer/footer.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], FooterComponent);
    return FooterComponent;
}());



/***/ }),

/***/ "./src/app/group-box/groupbox.component.css":
/***/ (function(module, exports) {

module.exports = "div.groupBox {\r\n  display: inline-block;\r\n  vertical-align: top;\r\n  width: 400px;\r\n  background-image: url('data:image/png;base64,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');\r\n  background-repeat: no-repeat;\r\n  background-color: rgba(118, 214, 255, 0.25);\r\n  margin: 10px;\r\n  border-radius: 10px;\r\n  text-align: right;\r\n  padding: 0 10px 10px 0;\r\n  height: 100%;\r\n  color: #003366;\r\n  border: 1px solid #76D6FF;\r\n}\r\n\r\ndiv.groupBoxTitle {\r\n  text-align: center;\r\n  font-weight: bold;\r\n  border-bottom: 1px solid lightblue;\r\n  padding-bottom: 10px;\r\n  padding-top: 10px;\r\n}\r\n\r\ndiv.groupBoxContent {\r\n  padding: 20px;\r\n  text-align: left;\r\n}\r\n"

/***/ }),

/***/ "./src/app/group-box/groupbox.component.html":
/***/ (function(module, exports) {

module.exports = "<div class=\"groupBox\">\r\n  <div class=\"groupBoxTitle\">{{title}}</div>\r\n  <div class=\"groupBoxContent\">\r\n    <ng-content></ng-content>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "./src/app/group-box/groupbox.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return GroupBoxComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var GroupBoxComponent = (function () {
    function GroupBoxComponent() {
    }
    GroupBoxComponent.prototype.ngOnInit = function () { };
    __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Input"])(),
        __metadata("design:type", String)
    ], GroupBoxComponent.prototype, "title", void 0);
    GroupBoxComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'group-box',
            template: __webpack_require__("./src/app/group-box/groupbox.component.html"),
            styles: [__webpack_require__("./src/app/group-box/groupbox.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], GroupBoxComponent);
    return GroupBoxComponent;
}());



/***/ }),

/***/ "./src/app/header/header.component.css":
/***/ (function(module, exports) {

module.exports = "div.header {\r\n  background-color: #76D6FF;\r\n  background: -webkit-gradient(linear, left top, left bottom, from(#76D6FF), to(white));\r\n  background: linear-gradient(#76D6FF, white);\r\n  width: 100%;\r\n  height: 140px;\r\n  border-radius: 10px;\r\n  position: relative;\r\n  color: #003366;\r\n}\r\n\r\ndiv.logo {\r\n  position: absolute;\r\n  top: 0px;\r\n  width: 100px;\r\n  height: 100px;\r\n}\r\n\r\ndiv.logo img {\r\n  width: 270px;\r\n  height: 192px;\r\n}\r\n\r\ndiv.topbar {\r\n  width: 100%;\r\n  height: 70px;\r\n}\r\n\r\ndiv.topbaritems {\r\n  width: 700px;\r\n  height: 70px;\r\n  float: right;\r\n}\r\n\r\ndiv.topbaritem:first-child {\r\n  border-left: 0px;\r\n}\r\n\r\ndiv.topbaritem {\r\n  display: inline-block;\r\n  padding-top: 20px;\r\n  padding-left: 20px;\r\n  width: 200px;\r\n  height: 50px;\r\n  vertical-align: middle;\r\n  border-left: 1px solid aliceblue;\r\n  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;\r\n  font-weight: bold;\r\n}\r\n\r\ndiv.bottombar {\r\n  border-top: 1px solid aliceblue;\r\n  width: 100%;\r\n  height: 70px;\r\n}\r\n\r\ndiv.bottombaritems {\r\n  position: absolute;\r\n  left: 300px;\r\n  width: 800px;\r\n  height:70px;\r\n}\r\n\r\ndiv.bottombaritem {\r\n  display: inline-block;\r\n  width: 220px;\r\n  height: 100%;\r\n  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;\r\n  font-weight: bold;\r\n  font-size: 18pt;\r\n  padding-top: 20px;\r\n  padding-right: 15px;\r\n  text-align: center;\r\n}\r\n\r\n.caps {\r\n  text-transform: uppercase;\r\n}\r\n\r\n.small {\r\n  font-size: 12pt;\r\n}\r\n\r\n.smaller {\r\n  font-size: 10pt;\r\n}\r\n\r\n.plainlink {\r\n  text-decoration: none;\r\n  color: inherit;\r\n}\r\n"

/***/ }),

/***/ "./src/app/header/header.component.html":
/***/ (function(module, exports) {

module.exports = "<div class=\"header\">\r\n  <div class=\"logo\"><a [routerLink]=\"['/home']\"><img src=\"assets/TacoCloud.png\"/></a></div>\r\n  <div class=\"topbar\">\r\n    <div class=\"topbaritems\">\r\n      <div class=\"topbaritem\" style=\"font-size: 17pt;\">\r\n        <a [routerLink]=\"['design']\" class=\"plainlink caps\">Design a Taco</a>\r\n      </div>\r\n      <div class=\"topbaritem\">\r\n        <a href=\"login\" class=\"plainlink\">\r\n          <img src=\"assets/down-triangle.png\" style=\"float: right; margin-right:15px; margin-top: 12px;\"/>\r\n          <span class=\"caps small\">Sign in</span><br/>\r\n          <span class=\"smaller\">or Create an Account</span>\r\n        </a>\r\n      </div>\r\n      <div class=\"topbaritem\" style=\"font-size: 17pt;\">\r\n        <a [routerLink]=\"['cart']\" class=\"plainlink\">\r\n          <img src=\"assets/cart.png\" align=\"center\" />\r\n          {{cart.getItemsInCart().length * 4.99 | currency}}\r\n        </a>\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <div class=\"bottombar\">\r\n    <div class=\"bottombaritems\">\r\n      <div class=\"bottombaritem\">\r\n        <!-- <a href=\"recents\" class=\"plainlink\">Latest designs</a> -->\r\n        <a [routerLink]=\"['recents']\" class=\"plainlink\">Latest designs</a>\r\n      </div>\r\n      <div class=\"bottombaritem\">\r\n        <!-- <a href=\"specials\" class=\"plainlink\">Specials</a> -->\r\n        <a [routerLink]=\"['specials']\" class=\"plainlink\">Specials</a>\r\n      </div>\r\n      <div class=\"bottombaritem\">\r\n        <!-- <a href=\"locations\" class=\"plainlink\">Locations</a> -->\r\n        <a [routerLink]=\"['locations']\" class=\"plainlink\">Locations</a>\r\n      </div>\r\n    </div>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "./src/app/header/header.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return HeaderComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__cart_cart_service__ = __webpack_require__("./src/app/cart/cart-service.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};


var HeaderComponent = (function () {
    function HeaderComponent(cart) {
        this.cart = cart;
    }
    HeaderComponent.prototype.ngOnInit = function () { };
    HeaderComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'taco-header',
            template: __webpack_require__("./src/app/header/header.component.html"),
            styles: [__webpack_require__("./src/app/header/header.component.css")]
        }),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__cart_cart_service__["a" /* CartService */]])
    ], HeaderComponent);
    return HeaderComponent;
}());



/***/ }),

/***/ "./src/app/home/home.component.css":
/***/ (function(module, exports) {

module.exports = ""

/***/ }),

/***/ "./src/app/home/home.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Where taco hunger meets taco creativity</cloud-title>\r\n\r\n<img src=\"assets/TacoPhoto.jpg\" style=\"float:right;margin: 20px; border-radius: 20px;\" />\r\n\r\n<p>We love tacos. All kinds of tacos. And we love being creative with the way we make our tacos.</p>\r\n\r\n<p>Some taco joints offer a set, finite menu of tacos to choose from. At Taco Cloud, we want to share\r\n  the joy of taco creativity with you, allowing you to design your own tacos from a palette of fresh\r\n  and delicious ingredients. While we may offer some of our creations for you to try, the real fun\r\n  is when you create your own tortilla-wrapped works of art.</p>\r\n\r\n<p>And, even though we love tacos and creativity, we prefer to avoid doing complex math. That's why\r\n  all tacos are priced modestly at $4.99 each, no matter how many or what ingredients you choose.</p>\r\n\r\n<p>Click <a href=\"design\">here</a> to get started on your taco masterpiece!</p>\r\n"

/***/ }),

/***/ "./src/app/home/home.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return HomeComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var HomeComponent = (function () {
    function HomeComponent() {
    }
    HomeComponent.prototype.ngOnInit = function () { };
    HomeComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'taco-home',
            template: __webpack_require__("./src/app/home/home.component.html"),
            styles: [__webpack_require__("./src/app/home/home.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], HomeComponent);
    return HomeComponent;
}());



/***/ }),

/***/ "./src/app/little-button/littlebutton.component.css":
/***/ (function(module, exports) {

module.exports = "button {\r\n  background-color: rgba(118, 214, 255, 0.25);\r\n  border: 1px solid #76D6FF;\r\n  color: #003366;\r\n  font-size: 10pt;\r\n  padding: 4px;\r\n  border-radius: 10px;\r\n  font-weight: bold;\r\n  font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif\r\n}\r\n"

/***/ }),

/***/ "./src/app/little-button/littlebutton.component.html":
/***/ (function(module, exports) {

module.exports = "<button>{{label}}</button>\r\n"

/***/ }),

/***/ "./src/app/little-button/littlebutton.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LittleButtonComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var LittleButtonComponent = (function () {
    function LittleButtonComponent() {
    }
    LittleButtonComponent.prototype.ngOnInit = function () { };
    __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Input"])(),
        __metadata("design:type", String)
    ], LittleButtonComponent.prototype, "label", void 0);
    LittleButtonComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'little-button',
            template: __webpack_require__("./src/app/little-button/littlebutton.component.html"),
            styles: [__webpack_require__("./src/app/little-button/littlebutton.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], LittleButtonComponent);
    return LittleButtonComponent;
}());



/***/ }),

/***/ "./src/app/locations/locations.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Come visit one of our taco showrooms</cloud-title>\r\n\r\n<p>Although our tacos are conveniently available online, we love to have\r\n  our taco artists make in-person appearances at one of our taco showrooms.\r\n  Choose the location nearest you. We'll resist the urge to ask you for an\r\n  autograph.</p>\r\n"

/***/ }),

/***/ "./src/app/locations/locations.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LocationsComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var LocationsComponent = (function () {
    function LocationsComponent() {
    }
    LocationsComponent.prototype.ngOnInit = function () { };
    LocationsComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'locations-tacocloud',
            template: __webpack_require__("./src/app/locations/locations.component.html")
        }),
        __metadata("design:paramtypes", [])
    ], LocationsComponent);
    return LocationsComponent;
}());



/***/ }),

/***/ "./src/app/login/login.component.css":
/***/ (function(module, exports) {

module.exports = "input.formField {\r\n  font-size: 16pt;\r\n  background: none;\r\n  border: none;\r\n  border-bottom: 1px solid #003366;\r\n}\r\n\r\ngroup-box {\r\n  width:1000px;\r\n}\r\n"

/***/ }),

/***/ "./src/app/login/login.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Sign in to Taco Cloud</cloud-title>\r\n\r\n<p style=\"font-weight: bold; color: red;\">\r\n  The sign in and security features of this version of Taco Cloud are not\r\n  yet implemented. The forms presented below do absolutely nothing...yet.\r\n  Stay tuned.\r\n</p>\r\n\r\n<group-box title=\"Sign in\">\r\n  <form>\r\n    <label for=\"username\"><b>Username:</b></label><br/>\r\n    <input type=\"text\" name=\"username\" class=\"formField\"/><br/>\r\n    <label for=\"password\"><b>Password:</b></label><br/>\r\n    <input type=\"password\" name=\"password\" class=\"formField\"/><br/><br/>\r\n    <big-button label=\"Sign in\"></big-button>\r\n  </form>\r\n</group-box>\r\n\r\n<group-box title=\"Register\">\r\n  <form>\r\n    <label for=\"username\"><b>Username:</b></label><br/>\r\n    <input type=\"text\" name=\"username\" class=\"formField\"/><br/>\r\n    <label for=\"password\"><b>Password:</b></label><br/>\r\n    <input type=\"password\" name=\"password\" class=\"formField\"/><br/><br/>\r\n    <label for=\"password\"><b>(verify password)</b></label><br/>\r\n    <input type=\"password\" name=\"verifyPassword\" class=\"formField\"/><br/><br/>\r\n\r\n    <label for=\"fullName\"><b>Full name:</b></label><br/>\r\n    <input type=\"text\" name=\"fullName\" class=\"formField\"/><br/><br/>\r\n\r\n    <label for=\"street\"><b>Street:</b></label><br/>\r\n    <input type=\"text\" name=\"street\" class=\"formField\"/><br/><br/>\r\n\r\n    <label for=\"city\"><b>City / State:</b></label><br/>\r\n    <input type=\"text\" name=\"city\" class=\"formField\"/><input type=\"text\" name=\"state\" class=\"formField\"/><br/><br/>\r\n\r\n    <label for=\"zipCode\"><b>Zip code:</b></label><br/>\r\n    <input type=\"text\" size=\"2\" name=\"zipCode\" class=\"formField\"/><br/><br/>\r\n\r\n    <label for=\"phoneNumber\"><b>Phone number:</b></label><br/>\r\n    <input type=\"text\" name=\"phoneNumber\" class=\"formField\"/><br/><br/>\r\n\r\n\r\n    <big-button label=\"Register\"></big-button>\r\n  </form>\r\n</group-box>\r\n"

/***/ }),

/***/ "./src/app/login/login.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return LoginComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var LoginComponent = (function () {
    function LoginComponent() {
    }
    LoginComponent.prototype.ngOnInit = function () { };
    LoginComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'login-tacocloud',
            template: __webpack_require__("./src/app/login/login.component.html"),
            styles: [__webpack_require__("./src/app/login/login.component.css")]
        }),
        __metadata("design:paramtypes", [])
    ], LoginComponent);
    return LoginComponent;
}());



/***/ }),

/***/ "./src/app/recents/NonWrapsPipe.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return NonWrapsPipe; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var NonWrapsPipe = (function () {
    function NonWrapsPipe() {
    }
    NonWrapsPipe.prototype.transform = function (ingredients) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var nonwraps = [];
        for (var _a = 0, ingredients_1 = ingredients; _a < ingredients_1.length; _a++) {
            var ingredient = ingredients_1[_a];
            if (ingredient.type !== 'WRAP') {
                nonwraps.push(ingredient);
            }
        }
        return nonwraps;
    };
    NonWrapsPipe = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Pipe"])({
            name: 'nonwraps'
        })
    ], NonWrapsPipe);
    return NonWrapsPipe;
}());



/***/ }),

/***/ "./src/app/recents/RecentTacosService.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return RecentTacosService; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__api_ApiService__ = __webpack_require__("./src/app/api/ApiService.ts");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};


var RecentTacosService = (function () {
    function RecentTacosService(apiService) {
        this.apiService = apiService;
    }
    RecentTacosService.prototype.getRecentTacos = function () {
        return this.apiService.get('/tacos?recent');
    };
    RecentTacosService = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__api_ApiService__["a" /* ApiService */]])
    ], RecentTacosService);
    return RecentTacosService;
}());



/***/ }),

/***/ "./src/app/recents/WrapsPipe.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return WrapsPipe; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var WrapsPipe = (function () {
    function WrapsPipe() {
    }
    WrapsPipe.prototype.transform = function (ingredients) {
        var args = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            args[_i - 1] = arguments[_i];
        }
        var wraps = [];
        for (var _a = 0, ingredients_1 = ingredients; _a < ingredients_1.length; _a++) {
            var ingredient = ingredients_1[_a];
            if (ingredient.type === 'WRAP') {
                var wrap = {};
                wrap.id = ingredient.id;
                wrap.name = 'a ' + ingredient.name;
                wrap.type = ingredient.type;
                wraps.push(wrap);
            }
        }
        return wraps;
    };
    WrapsPipe = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Pipe"])({
            name: 'wraps'
        })
    ], WrapsPipe);
    return WrapsPipe;
}());



/***/ }),

/***/ "./src/app/recents/recents.component.css":
/***/ (function(module, exports) {

module.exports = "div.recentsblock {\r\n  min-width: 1000px;\r\n}\r\n\r\ndiv.tacoblock {\r\n  display: inline-block;\r\n  vertical-align: top;\r\n  width: 300px;\r\n  background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAfCAYAAABtYXSPAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAACXBIWXMAAA7EAAAOxAGVKw4bAAABxWlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iCiAgICAgICAgICAgIHhtbG5zOnRpZmY9Imh0dHA6Ly9ucy5hZG9iZS5jb20vdGlmZi8xLjAvIj4KICAgICAgICAgPHhtcDpDcmVhdG9yVG9vbD5QaG90b1NjYXBlPC94bXA6Q3JlYXRvclRvb2w+CiAgICAgICAgIDx0aWZmOk9yaWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgr/vrTsAAAJgklEQVRYCb1XC3BcVRn+zrn37iu72bw22zyatMnQpk3aaNtAW8GmgBYFVOqk1HGYgSk+cIARgQriDIvDDEpxLFVxoCrgDKgJMjBQrcxoMigFhjqGtiHQNiRts0maZF/Z530e/7NhQSJqqgxncufuPffc+3/n+7//+2+A/30welQeixsR8MUtPPdV74Lo7+9WxTtHfwSq6O1RhACLUPDuSLeKHijy97mHWMQThx9er5WWXRtZ5in9/m/n1q/7a0Egad27G1n4zL+9sXCh3P2OmQHRtwO2vLfuWw3PHisYLcPnt93R0lBVPTP5rJY+hUQihej6T9127ObBZyr/HMvfevJ49CcXbl29K5M27jwbTd4y9rPZvSAG6bAWxlAXTrzvmiju7elBT0+fYGyg+LB47aqOPw6duX13PPM543QKg2bu+WT8KGqav42WKz6PmeFf4YW/HIyGedDhTerS9azpOhhO0K0xhFS2fFSywyJOX09E2dE3v7FSzMUzc/D8i16emLkrYVdsU90cN8Tz1pJEgR8quICpN8Wh6+7Hpi3rMDvyNP/KEwfYRB6Y8LrNcVNoBMZBKkswxAS8ThDhmc24A0ORbqiRgfcY+s9grvNsebSjU79mvb5r0qy5/tmxCtw48BSuVpssXqeoRwIavLolzhQEu9EZxbatS3Ak0SquH5kSSJi4ppzxNWG3ExQmzxsmYrM5tL3uR+PBygPhsLi1beLIW4I0RCDoBMrdgiErYCBCKbm98bMQ4sAb6Qm8HWtA/Se/gwtq+k28CHVnqlJNnkriuQ0ZZDjYU2M16OrrwkMPDOMHNwyzJ7rq2aYLfKgv98HtCXKoxB5TIQxDsItFAZcqlx+55ViKQn8ZvT0advQZEsa/gBnAgJzH1/z+C//mcrBnbNIYTPmUxxpf4Pjry9oVeQVXNU3ggrBAlaXhEkNHR6+GHBLYdVc9br3Yh1CFG3A1AkolBKOCU8rpjQqYlzNU6y60JLByc6hdBEP1rO3+CWkHbEef/b40lSjb/s3Gm65oCkZ6M2bV0bzhfGJW5ffu1+h1BvZvT+H7a/zYMKcjUFeO/mgWTyYtfPrKWlS3UNUrdXC0ZoBTcILAeJCSQL+0KgjHJIIq5D0bfEhBpjANW+1i7Q+eFiLC32OGKodJde8MffzpoLrv8GQKKTvtpKwsj6YsmJevxXhOx8Gwg21hP2IErT9l49cfq8D2VW64fOUUcxngaQVX/YAxTkgoPZIVa44AkH6L6iBlo0xBvkJHZbIWSf17NHEtJYRoe2cIQVSxPnvqmS0P9k/lbv5SdNJ4INnmWtPiR+tlO1FTloHHeR0HXhvGFwePY7Xmw2OdbnQ1eylgIxy1GtxNqeF+TA8dQ8XScrjKyeeEm9ggEianoLhc8FYRaNsmxkgD1hkOO58Ec3ewlT+KFpnplTkjILPPX7pKZembNjEvnjNWa554DdZuBGrrCrCSJ6DOvIEO0kabUPH8Ji+WhwNw+FIigID41gBmkrZmI7D0PKhek36TuuFA/nkCPioZWTQKXRM7ZpJTukx4lArknNV0Yx5MKDRTZEhTk98oszvZ9LTfVKdGtbiZwG9enMQ25UnUehpxNKrjlYQbf9pahfpwiNK9EgppQVYKLCoOO0axKuANLwPyp+cLlpOOHB1qeR1dEyorPd8PzKiBQJULBTEHzRkkMGRD86kUM7/YHci7X3jTU7i0/sxblh2o8SuFVBp3Ht+Hsy0taHKyeKag4e0rK9HcsBy2tpZoJzFKccrXFyYICFUOk1VKqQMxw3zzIF0hEjAdUjPGWQd21oEvoyKj0zp+NWvb1ytlwgfu6SbegLTy+01Vvo766GjCGTl1lM+cncLJ0yPoijdjxTBDfjCAU5dVEZBG2K51xEg1HJM6hNytzINGwOTBqKytSZrK0DylhcteSvfNqIPCGJXTLEdZioAUXoItOuaBRLiUidq9hdbSCFYrF/pUKl89YJeVlWmnxkdhWSZWhVrADk1i530+NDa5YfuvgqK4IIiJzHQcgSVh2jAFc9VSbGLDJj1IkxPUT2XaGJWRcFngpoayBMccITWVu1nbT/fLuKI/ojIWKfY9ju4BuTV4PJXrpmMjGJ0cYx3t6xDw+WE6BZx4M4ett7mxYo1K/rGR6p+WU6ky0kJgSS2diXoZmHYv5QnuIBdLw8haAs6cicIQdch6DcyfRTJxL7SmVWzlj98DsnUeiHyUUMGJIKLazsEVdqEV1RU+NjJ2Elkjg+lxG3XdWVy0mXbKV1MGGopA5INyFIHI7ybPMkCfIOUQHJETisotwqihOqghRqxMvvoI9NP3sc4/kPnQksNf1bDhEYv9ExA5XyztXY8fDts2rzOSZcjpWZbKT+Ps2SSUkI6dXyAxSgH621CIz0GfS6GiZTlEagjMQ+DUAOkhTshUAX3cgohr7qYaDcmc6STMx7nbfz9r3XOiCEJEKN7dNpmfKa8XjiIY5s2EqJv5ZxIzYEqA5fQcREbDZ24Q8FUplJ522r8Kd8ADzUeCdMi0ylrpXaR9KRhrjvKU1uAe15CmQEnjUVjOHqX9oZMyoNQFuuHMayMipz5wFMG4FFc5Z2lwrVx0rl2LV/YfRuNmLzqkFTESsKuG4laRzQtwKwbHkhpRBeeOBSdOwtQ5Umkdds3j0OwfspV7j8toRRAzQ2JhOuS9DxpFMFyzVE59RM/mcCI5RFXioGsjCdXthXAvJ7aIDTNG1k6mpKrIx2ctRUkqnia/JmZTOWYqP4ei7mPn7RuRQajpqehbPIgSsCIYx3IMQf3DsDN4eWAYtZsUrGihJbyZPE32EocYmSWBUkrUJU5ZIyHSqYpi2f1M8d4j+0oRxPvSUQqx+HMRjEdzzRaMgi0cr+Lm1U77xhjTyqiCPM1IT8Xl14AINDaTF5AFq9PkFfGXILTdbNW+Qx8GiBLcIpiC3xhXEuqU6s02FKyUWLOSpjX5n4VHBOqCVKY2fV+SQGPRCfLz77K2hx/9MEGUwHARifDarQP09cj/zt0OzHrdCYcUEkylyUSavo3yGuxpA7HRPchMrZZAyE1Y0TnJJ6hCiqZZeuH/c+bYMkCuBfIO67flYWDzJUwowUqOKvIKpyAwM/ZLmBPtBGI329CXKhoWrV9shZwLuHe7thDdqvVq4bBa4+mEvjxOPf93sAp7WfvDb8gXzntF0bCKrn8uQc5pLVVskR0x2NkgBi/fnj4Zoa43P2Rrl9+npeuP5FwCVArWT2Uqv9pL1x/F+R9i+AgBhguEGAAAAABJRU5ErkJggg==');\r\n  background-repeat: no-repeat;\r\n  background-color: rgba(118, 214, 255, 0.25);\r\n  margin: 10px;\r\n  border-radius: 10px;\r\n  text-align: right;\r\n  padding: 0 10px 10px 0;\r\n  height: 100%;\r\n  color: #003366;\r\n  border: 1px solid #76D6FF;\r\n}\r\n\r\ndiv.tacoblock a {\r\n  text-decoration: none;\r\n  font-size: 12pt;\r\n  font-weight: bold;\r\n  color: #003366;\r\n  background-color: #76D6FF;\r\n  padding: 5px;\r\n  border-radius: 10px;\r\n}\r\n\r\ndiv.tacoName {\r\n  text-align: center;\r\n  font-weight: bold;\r\n  border-bottom: 1px solid lightblue;\r\n  padding-bottom: 10px;\r\n  text-transform: capitalize;\r\n}\r\n\r\ndiv.tacoDesc {\r\n  padding: 20px;\r\n  text-align: center;\r\n  font-size: 12pt;\r\n}\r\n\r\nul.commalist {\r\n  display: inline;\r\n  list-style: none;\r\n  padding: 0;\r\n}\r\n\r\nul.commalist li {\r\n  display: inline;\r\n}\r\n\r\nul.commalist li:not(:first-child):before {\r\n  content: \", \";\r\n}\r\n\r\nul.commalist li:last-child:before {\r\n  content: \" and \";\r\n}\r\n\r\nul.commalist li:only-child:before {\r\n  content: \"\";\r\n}\r\n\r\n.small {\r\n  font-size: 12pt;\r\n}\r\n"

/***/ }),

/***/ "./src/app/recents/recents.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Admire some recently created taco masterpieces</cloud-title>\r\n\r\n<p>Even the greatest artists sometimes need to be inspired by the works of others.\r\n  Here are some of the most recently created tacos, designed by taco artists just like you...</p>\r\n\r\n<div class=\"recentsblock\">\r\n  <div class=\"tacoblock\" *ngFor=\"let taco of recentTacos\">\r\n      <div class=\"tacoName\"><span class=\"small\">The</span><br/>\"{{taco.name}}\"</div>\r\n      <div class=\"tacoDesc\">\r\n        <ul class=\"commalist\">\r\n          <li *ngFor=\"let ingredient of taco.ingredients | nonwraps\">{{ingredient.name}}</li>\r\n        </ul>\r\n        <div *ngIf=\"(taco.ingredients | wraps)?.length > 0\">\r\n        <span>wrapped in</span><ul class=\"commalist\">\r\n            <li *ngFor=\"let ingredient of taco.ingredients | wraps\">{{ingredient.name}}</li>\r\n        </ul>\r\n        </div>\r\n        <div *ngIf=\"(taco.ingredients | wraps)?.length == 0\">in a bowl</div>\r\n      </div>\r\n      <little-button label=\"Order this taco\"></little-button>\r\n  </div>\r\n</div>\r\n"

/***/ }),

/***/ "./src/app/recents/recents.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return RecentTacosComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_common_http__ = __webpack_require__("./node_modules/@angular/common/esm5/http.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};


var RecentTacosComponent = (function () {
    function RecentTacosComponent(httpClient) {
        this.httpClient = httpClient;
    }
    RecentTacosComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.httpClient.get('http://localhost:8080/api/tacos?recent') // <1>
            .subscribe(function (data) { return _this.recentTacos = data; });
    };
    RecentTacosComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'recent-tacos',
            template: __webpack_require__("./src/app/recents/recents.component.html"),
            styles: [__webpack_require__("./src/app/recents/recents.component.css")]
        }),
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Injectable"])(),
        __metadata("design:paramtypes", [__WEBPACK_IMPORTED_MODULE_1__angular_common_http__["HttpClient"]])
    ], RecentTacosComponent);
    return RecentTacosComponent;
}());



/***/ }),

/***/ "./src/app/specials/specials.component.html":
/***/ (function(module, exports) {

module.exports = "<cloud-title>Check out some of our specials!</cloud-title>\r\n"

/***/ }),

/***/ "./src/app/specials/specials.component.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return SpecialsComponent; });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};

var SpecialsComponent = (function () {
    function SpecialsComponent() {
    }
    SpecialsComponent.prototype.ngOnInit = function () { };
    SpecialsComponent = __decorate([
        Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["Component"])({
            selector: 'taco-specials',
            template: __webpack_require__("./src/app/specials/specials.component.html")
        }),
        __metadata("design:paramtypes", [])
    ], SpecialsComponent);
    return SpecialsComponent;
}());



/***/ }),

/***/ "./src/environments/environment.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return environment; });
// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
var environment = {
    production: false
};


/***/ }),

/***/ "./src/main.ts":
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__angular_core__ = __webpack_require__("./node_modules/@angular/core/esm5/core.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__angular_platform_browser_dynamic__ = __webpack_require__("./node_modules/@angular/platform-browser-dynamic/esm5/platform-browser-dynamic.js");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__app_app_module__ = __webpack_require__("./src/app/app.module.ts");
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__environments_environment__ = __webpack_require__("./src/environments/environment.ts");




if (__WEBPACK_IMPORTED_MODULE_3__environments_environment__["a" /* environment */].production) {
    Object(__WEBPACK_IMPORTED_MODULE_0__angular_core__["enableProdMode"])();
}
Object(__WEBPACK_IMPORTED_MODULE_1__angular_platform_browser_dynamic__["a" /* platformBrowserDynamic */])().bootstrapModule(__WEBPACK_IMPORTED_MODULE_2__app_app_module__["a" /* AppModule */])
    .catch(function (err) { return console.log(err); });


/***/ }),

/***/ 0:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__("./src/main.ts");


/***/ })

},[0]);
//# sourceMappingURL=main.bundle.js.map