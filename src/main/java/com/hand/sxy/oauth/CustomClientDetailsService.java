package com.hand.sxy.oauth;

import com.hand.sxy.oauth.dto.ClientDetail;
import com.hand.sxy.oauth.service.IClientDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;


public class CustomClientDetailsService implements ClientDetailsService {

    private static final ThreadLocal<ClientDetails> CLIENT_DETAILS = new ThreadLocal<>();


    @Autowired
    IClientDetailService clientDetailService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if (CLIENT_DETAILS.get() != null) {
            return CLIENT_DETAILS.get();
        }

        ClientDetail clientDetail = clientDetailService.queryByClientId(clientId);
        if (clientDetail == null) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        BaseClientDetails detail = new BaseClientDetails(
                clientDetail.getClientId(),
                clientDetail.getResourceIds(),
                clientDetail.getScope(),
                clientDetail.getAuthorizedGrantTypes(),
                clientDetail.getAuthorities(),
                clientDetail.getRedirectUri()
        );

        detail.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(clientDetail.getAutoApprove()));
        detail.setClientSecret(clientDetail.getClientSecret());

        CLIENT_DETAILS.set(detail);
        return detail;
    }
}
