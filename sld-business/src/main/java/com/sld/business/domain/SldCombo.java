package com.sld.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 套餐
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_combo")
public class SldCombo extends Model<SldCombo> {


    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 源端接口
     */
    @TableField("src_interface_open_record_id")
    private Long srcInterfaceOpenRecordId;

    /**
     * 源接口到中间库的转换器开通记录id
     */
    @TableField("src_trans_mid_open_record_id")
    private Long srcTransMidOpenRecordId;

    /**
     * 中间库
     */
    @TableField("mid_db_open_record_id")
    private Long midDbOpenRecordId;

    /**
     * 中间库到目标接口的转换器开通记录id
     */
    @TableField("mid_trans_target_open_record_id")
    private Long midTransTargetOpenRecordId;

    /**
     * 目标端接口
     */
    @TableField("target_interface_open_record_id")
    private Long targetInterfaceOpenRecordId;

    /**
     * 描述
     */
    @TableField("desc")
    private String desc;

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
