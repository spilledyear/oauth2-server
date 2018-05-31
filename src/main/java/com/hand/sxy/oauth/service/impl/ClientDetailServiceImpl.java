package com.hand.sxy.oauth.service.impl;

import com.hand.sxy.oauth.dto.ClientDetail;
import com.hand.sxy.oauth.service.IClientDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

/**
 * @author spilledyear
 */
@Repository
public class ClientDetailServiceImpl implements IClientDetailService {

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public ClientDetail queryByClientId(String clientId) {
        String sql = "SELECT * FROM sys_oauth_client_details WHERE CLIENT_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, clientId);

        if (rowSet.next()) {
            return generateEntity(rowSet);
        }

        return null;
    }

    private ClientDetail generateEntity(SqlRowSet rs) {
        ClientDetail dto = new ClientDetail();
        dto.setDetailId(rs.getLong("DETAIL_ID"));
        dto.setClientId(rs.getString("CLIENT_ID"));
        dto.setClientSecret(rs.getString("CLIENT_SECRET"));
        dto.setAuthorizedGrantTypes(rs.getString("AUTHORIZED_GRANT_TYPES"));
        dto.setResourceIds(rs.getString("RESOURCE_IDS"));
        dto.setAuthorities(rs.getString("AUTHORITIES"));
        dto.setAutoApprove(rs.getString("AUTO_APPROVE"));
        dto.setScope(rs.getString("SCOPE"));
        dto.setAccessTokenValidity(rs.getLong("ACCESS_TOKEN_VALIDITY"));
        dto.setRefreshTokenValidity(rs.getLong("REFRESH_TOKEN_VALIDITY"));
        dto.setRedirectUri(rs.getString("WEB_SERVER_REDIRECT_URI"));
        dto.setAdditionalInformation(rs.getString("ADDITIONAL_INFORMATION"));
        dto.setCreateTime(rs.getDate("CREATE_TIME"));
        dto.setUpdateTime(rs.getDate("UPDATE_TIME"));

        return dto;
    }
}
