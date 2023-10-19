package com.sld.business.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 接口开通记录
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_interface_open_record")
public class SldInterfaceOpenRecord extends Model<SldInterfaceOpenRecord> {


    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 接口中文名
     */
    @TableField("interface_cn_ame")
    private String interfaceCnAme;

    /**
     * 接口英文名
     */
    @TableField("interface_eng_name")
    private String interfaceEngName;

    @TableField("protocol_object_id")
    private Long protocolObjectId;

    /**
     * 协议code
     */
    @TableField("protocol_code")
    private String protocolCode;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private LocalDateTime createDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
