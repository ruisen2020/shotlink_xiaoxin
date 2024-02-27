package org.example.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shortlink.common.convention.result.Result;
import org.example.shortlink.common.convention.result.Results;
import org.example.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import org.example.shortlink.project.dto.req.ShortLinkPageReqDTO;
import org.example.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import org.example.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import org.example.shortlink.project.service.ShortLinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


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
     * 只要用户注册成功，就会默认生成一个默认分组
     * 用户要删除分组前，需要判断当前是不是只剩下一个分组了，
     * 用户至少要有一个分组
     */

}
