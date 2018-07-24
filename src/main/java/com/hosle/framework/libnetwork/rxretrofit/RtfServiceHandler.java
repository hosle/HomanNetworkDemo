package com.hosle.framework.libnetwork.rxretrofit;

import com.hosle.framework.libnetwork.ResponseBodyStringListener;

/**
 * Created by tanjiahao on 17/5/2.
 * Original Project HomanNetwork
 *
 */

public interface RtfServiceHandler<S> {
    public S createService(String apiString);

    public S createService(String apiString, ResponseBodyStringListener listener);
}
