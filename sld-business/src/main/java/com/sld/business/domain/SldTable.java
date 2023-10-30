package com.sld.business.domain;

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
 * 中间表
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_table")
public class SldTable extends Model<SldTable> {


    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 表英文名称
     */
    @TableField("table_eng_name")
    private String tableEngName;

    /**
     * 表中文名称
     */
    @TableField("table_cne_name")
    private String tableCneName;

    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
