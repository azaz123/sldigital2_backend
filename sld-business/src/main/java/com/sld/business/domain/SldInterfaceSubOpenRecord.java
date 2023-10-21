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
 * 接口开通子记录
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_interface_sub_open_record")
public class SldInterfaceSubOpenRecord extends Model<SldInterfaceSubOpenRecord> {


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("interface_open_record_id")
    private Long interfaceId;

    /**
     * 子对象
     */
    @TableField("object_id")
    private Long objectId;

    @TableField("is_need_config")
    private Long isNeedConfig;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
