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
@TableName("sld_protocol_sub_open_record")
public class SldProtocolSubOpenRecord extends Model<SldProtocolSubOpenRecord> {


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("protocol_open_record_id")
    private String protocolOpenRecordId;

    /**
     * 子对象
     */
    @TableField("object_id")
    private String objectId;

    @TableField("is_need_config")
    private Long isNeedConfig;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
