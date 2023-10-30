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
 * 业务配置（租户级）
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_business_config")
public class SldBusinessConfig extends Model<SldBusinessConfig> {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 业务对象id
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 配置对象id
     */
    @TableField("object_id")
    private String objectId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
