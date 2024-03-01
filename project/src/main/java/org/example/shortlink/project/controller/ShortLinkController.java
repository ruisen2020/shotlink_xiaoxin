package org.example.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shortlink.common.convention.result.Result;
import org.example.shortlink.common.convention.result.Results;
import org.example.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.example.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.example.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.example.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/project/v1/create")
    public Result<ShortLinkCreateRespDTO> createShotLink(@RequestBody ShortLinkCreateReqDTO shortLinkSaveReqDTO) {
        return Results.success(shortLinkService.createShotLink(shortLinkSaveReqDTO));
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/project/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO shortLinkSaveReqDTO) {
        return Results.success(shortLinkService.pageShortLink(shortLinkSaveReqDTO));
    }

    /**
     * 第一种方式：根据每个gid去查询
     * 根据分组id查询短链接分组内数量
     */
    @GetMapping("/api/short-link/project/v1/countByGid")
    public Result<Long> countShortLinkByGid(@RequestParam("gid") String gid) {
        return Results.success(shortLinkService.countShortLinkByGid(gid));
    }
    /**
     * 第二种方式：根据gid集合去查询
     * 根据分组id查询短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("gids")List<String> gids) {
        return Results.success(shortLinkService.listGroupShortLinkCount(gids));
    }


}
