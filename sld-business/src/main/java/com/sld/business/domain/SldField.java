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
 * 对象字段表
 * </p>
 *
 * @author hrz
 * @since 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_field")
public class SldField extends Model<SldField> {


    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    @TableField("belong_object_id")
    private Long belongObjectId;

    @TableField("action_id")
    private Long actionId;

    /**
     * 字段中文名称
     */
    @TableField("field_ch_name")
    private String fieldChName;

    /**
     * 字段英文名称
     */
    @TableField("field_eng_name")
    private String fieldEngName;

    /**
     * 字段类型
     */
    @TableField("field_type")
    private Long fieldType;




    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
