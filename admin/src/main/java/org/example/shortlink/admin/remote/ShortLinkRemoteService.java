package org.example.shortlink.admin.remote;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.shortlink.admin.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import org.example.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import org.example.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import org.example.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.example.shortlink.common.convention.result.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短链接中台远程调用服务
 */
public interface ShortLinkRemoteService {
    default Result<ShortLinkCreateRespDTO> createShotLink(ShortLinkCreateReqDTO shortLinkSaveReqDTO) {
        String post = HttpUtil.post("http://localhost:8001/api/short-link/project/v1/create", JSON.toJSONString(shortLinkSaveReqDTO));
        return JSON.parseObject(post, new TypeReference<Result<ShortLinkCreateRespDTO>>() {
        });

    }

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO shortLinkSaveReqDTO) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("current", shortLinkSaveReqDTO.getCurrent());
        requestMap.put("size", shortLinkSaveReqDTO.getSize());
        requestMap.put("gid", shortLinkSaveReqDTO.getGid());
        String result = HttpUtil.get("http://localhost:8001/api/short-link/project/v1/page", requestMap);
        return JSON.parseObject(result, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });

    }

    default Result<Long> countShortLinkByGid(String gid) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", gid);
        String result = HttpUtil.get("http://localhost:8001/api/short-link/project/v1/countByGid?gid=" + gid);
        System.out.println(result);
        return JSON.parseObject(result, new TypeReference<Result<Long>>() {
        });
    }

    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> gids) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gids", gids);
//        String url = "http://localhost:8001/api/short-link/v1/count?";
//        for (String gid : gids) {
//            url += "gids=" + gid + "&";
//        }
        String result = HttpUtil.get("http://localhost:8001/api/short-link/v1/count",requestMap);
        return JSON.parseObject(result, new TypeReference<Result<List<ShortLinkGroupCountQueryRespDTO>>>() {
        });
    }
}
