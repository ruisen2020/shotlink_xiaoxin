package org.example.shortlink.admin.controller;


import lombok.RequiredArgsConstructor;
import org.example.shortlink.admin.common.convention.result.Result;
import org.example.shortlink.admin.common.convention.result.Results;
import org.example.shortlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import org.example.shortlink.admin.service.GroupService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分组控制层
 */
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增分组
     * @param shortLinkGroupSaveReqDTO
     * @return
     */
    @PostMapping("/api/short-link/v1/group")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO shortLinkGroupSaveReqDTO) {
        groupService.SaveGroup(shortLinkGroupSaveReqDTO.getName());
        return Results.success();
    }
}
