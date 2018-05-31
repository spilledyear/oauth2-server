package com.hand.sxy.oauth.service;

import com.hand.sxy.oauth.dto.ClientDetail;

public interface IClientDetailService {


    ClientDetail queryByClientId(String clientId);


}
