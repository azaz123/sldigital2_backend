package com.sld.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 业务表(可以承载用户级接口，中间表)
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_business")
public class SldBusiness extends Model<SldBusiness> {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 协议开通记录id
     */
    @TableField("protocol_open_record_id")
    private String protocolOpenRecordId;

    /**
     * 租户Id
     */
    @TableField("tenant_id")
    private String tenantId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
