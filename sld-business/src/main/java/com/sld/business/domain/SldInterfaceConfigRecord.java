package com.sld.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 接口配置记录
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_interface_config_record")
public class SldInterfaceConfigRecord extends Model<SldInterfaceConfigRecord> {


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("interface_open_record_id")
    private Long interfaceOpenRecordId;

    @TableField("object_Id")
    private Long objectId;

    /**
     * 1 是公共  2 不是公共
     */
    @TableField("is_public")
    private Long isPublic;

    /**
     * 1 数据归属id—租户id
     */
    @TableField("belong_id")
    private Long belongId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
