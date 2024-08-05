package org.jeecg.modules.testnet.server.service.asset;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.testnet.server.dto.AssetIpDTO;
import org.jeecg.modules.testnet.server.dto.AssetSubDomainIpsDTO;
import org.jeecg.modules.testnet.server.entity.asset.AssetIpSubDomainRelation;
import org.jeecg.modules.testnet.server.vo.asset.AssetIpVO;
import org.jeecg.modules.testnet.server.vo.asset.AssetSubDomainVO;

import java.util.List;

/**
 * @Description: 接口资产
 * @Author: jeecg-boot
 * @Date: 2024-06-01
 * @Version: V1.0
 */
public interface IAssetIpSubdomainRelationService extends IService<AssetIpSubDomainRelation> {


    void addDomainRelation(AssetSubDomainIpsDTO domainToSubdomainAndIpDTO);

    void delByAssetIpId(String ipId);

    void delByAssetSubDomainId(String subDomainId);

    List<AssetIpSubDomainRelation> getByAssetIpId(String ipId);

    List<AssetIpSubDomainRelation> getByAssetSubDomainId(String subDomainId);

    String getSubDomainIdsByIpId(String ipId);

    String getIpIdsBySubDomainId(String subDomainId);

    String getSubDomainsByIpId(String ipId);

    String getIpsBySubDomainId(String subDomainId);

    AssetIpDTO getExtra(AssetIpDTO assetIpDTO);


    List<AssetIpVO> getAssetIpBySubDomainId(String subDomainId);

    List<AssetSubDomainVO> getAssetSubDomainByIpId(String ipId);

    void addDomainRelation(AssetIpDTO asset);
}
