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
 * 套餐开通记录
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_combo_open_record")
public class SldComboOpenRecord extends Model<SldComboOpenRecord> {


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 套餐id
     */
    @TableField("combo_id")
    private Long comboId;

    /**
     * 数据归属id
     */
    @TableField("belong_id")
    private Long belongId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
