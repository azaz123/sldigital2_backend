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



    @TableId("id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField("is_list")
    private Integer isList;

    @TableField("list_type")
    private Long listType;

    @TableField("is_atom")
    private Integer isAtom;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
