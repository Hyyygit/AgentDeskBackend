package com.agentdesk.user.converter;

import com.agentdesk.api.user.dto.UserDTO;
import com.agentdesk.user.domain.UserPO;
import com.agentdesk.user.domain.UserVO;

/**
 * @author hyyy
 * @date 2026/5/6
 * @description 用户对象转换器
 */
public class UserConverter {

    public static UserVO toVO(UserPO po) {
        if (po == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(po.getId());
        vo.setUserNo(po.getUserNo());
        vo.setUserName(po.getUserName());
        vo.setRealName(po.getRealName());
        vo.setEmail(po.getEmail());
        vo.setPhone(po.getPhone());
        vo.setDepartment(po.getDepartment());
        vo.setRoleCode(po.getRoleCode());
        vo.setStatus(po.getStatus());
        vo.setTenantId(po.getTenantId());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }

    public static UserDTO toDTO(UserPO po) {
        if (po == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(po.getId());
        dto.setUserNo(po.getUserNo());
        dto.setUserName(po.getUserName());
        dto.setRealName(po.getRealName());
        dto.setEmail(po.getEmail());
        dto.setPhone(po.getPhone());
        dto.setDepartment(po.getDepartment());
        dto.setRoleCode(po.getRoleCode());
        dto.setStatus(po.getStatus());
        dto.setCreateTime(po.getCreateTime());
        return dto;
    }
}
