package com.dsh.douyin.module.material;

import com.dsh.douyin.core.AccessTokenManager;
import com.dsh.douyin.core.DouyinApiClient;
import com.dsh.douyin.module.material.dto.MaterialInfo;
import com.dsh.douyin.module.material.dto.MaterialListResult;
import com.dsh.douyin.module.material.dto.MaterialQuery;
import org.springframework.stereotype.Service;

/**
 * 素材 API（material.*）。
 */
@Service
public class MaterialApi {

    private final DouyinApiClient client;
    private final AccessTokenManager tokenManager;

    public MaterialApi(DouyinApiClient client, AccessTokenManager tokenManager) {
        this.client = client;
        this.tokenManager = tokenManager;
    }

    /** 查询素材列表（method=material.searchMaterial） */
    public MaterialListResult search(Long shopId, MaterialQuery query) {
        String token = tokenManager.getAccessToken(shopId);
        return client.execute("material.searchMaterial", query, token, MaterialListResult.class);
    }
}
