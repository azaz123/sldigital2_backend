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
 * 对象表
 * </p>
 *
 * @author hrz
 * @since 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_object")

public class SldObject extends Model<SldObject> {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("object_code")
    private String objectCode;

    @TableField("object_struct")
    private Long objectStruct;

    @TableField("object_value_type")
    private String objectValueType;

    @TableField("object_value")
    private String objectValue;

    @TableField("class_code")
    private String classCode;

    @TableField("controll_param")
    private Long controllParam;

    @TableField("belong_object_id")
    private Long belongObjectId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
