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
 * 连接器信息
 * </p>
 *
 * @author hrz
 * @since 2023-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sld_connector")
public class SldConnector extends Model<SldConnector> {


    /**
     * 主键
     */
    @TableId("id")
    private Long id;

    /**
     * 连接器名称
     */
    @TableField("connector_name")
    private String connectorName;

    /**
     * 接口连接信息对象
     */
    @TableField("src_con_object_id")
    private Long srcConObjectId;

    @TableField("target_con_object_id")
    private Long targetConObjectId;

    @TableField("src_input_object_id")
    private Long srcInputObjectId;

    @TableField("src_output_object_id")
    private Long srcOutputObjectId;

    @TableField("target_input_object_id")
    private Long targetInputObjectId;

    @TableField("target_output_object_id")
    private Long targetOutputObjectId;

    @TableField("transform_function_id")
    private Long transformFunctionId;

    @TableField("connector_type")
    private Integer connectorType;

    @TableField("desc")
    private String desc;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
