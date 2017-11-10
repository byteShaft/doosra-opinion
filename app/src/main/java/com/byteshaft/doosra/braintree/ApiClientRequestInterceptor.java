package com.byteshaft.doosra.braintree;

import retrofit.RequestInterceptor;

public class ApiClientRequestInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "braintree/doosra_app/" + 1000);
        request.addHeader("Accept", "application/json");
    }
}
