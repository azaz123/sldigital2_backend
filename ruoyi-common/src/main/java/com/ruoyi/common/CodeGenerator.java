package com.ruoyi.common;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodeGenerator {
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 自动生成代码
     */
    public static void main(String[] args) {
//        String markdownText = "这是一段Markdown文本，包含一个代码块：\n\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}\n```\n\n接下来是另一个代码块：\n\n```python\nprint('Hello, World!')\n```";
//        Pattern pattern = Pattern.compile("```(\\w+)[\\s\\S]*?```");
//        Matcher matcher = pattern.matcher(markdownText);
//        while (matcher.find()) {
//            String codeBlock = matcher.group();
//            String language = matcher.group(1);
//            String code = codeBlock.substring(language.length() + 3, codeBlock.length() - 3).trim();
//            System.out.println("语言：" + language);
//            System.out.println("代码：" + code);
//            System.out.println();
//        }

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // TODO 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = "/Users/huruizhe/Documents/extend/数据交换/sldigital2_backend/sld-business";
        // 生成文件的输出目录【默认 D 盘根目录】
        gc.setOutputDir(projectPath + "/src/main/java");
        // 作者
        gc.setAuthor("hrz");
        // 是否打开输出目录
        gc.setOpen(false);
        // controller 命名方式，注意 %s 会自动填充表实体属性
        gc.setControllerName("%sController");
        // service 命名方式
        gc.setServiceName("%sService");
        // serviceImpl 命名方式
        gc.setServiceImplName("%sServiceImpl");
        // mapper 命名方式
        gc.setMapperName("%sMapper");
        // xml 命名方式
        gc.setXmlName("%sMapper");
        // 开启 swagger2 模式
//        gc.setSwagger2(true);
        // 是否覆盖已有文件
        gc.setFileOverride(true);
        // 是否开启 ActiveRecord 模式
        gc.setActiveRecord(true);
        // 是否在xml中添加二级缓存配置
        gc.setEnableCache(false);
        // 是否开启 BaseResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(false);
        // 全局 相关配置
        mpg.setGlobalConfig(gc);

        // TODO 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://123.56.0.173:3306/sld?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("opsa1234");
        mpg.setDataSource(dsc);

        // TODO 包配置
        PackageConfig pc = new PackageConfig();
        // 父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
        pc.setParent("com.sld.business");
        // Entity包名
        pc.setEntity("domain");
        // Service包名
        pc.setService("service");
        // Service Impl包名
        pc.setServiceImpl("service.impl");
        mpg.setPackageInfo(pc);

        // TODO 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        // 输出文件配置
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        // 自定义输出文件
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setTemplate(new TemplateConfig().setXml(null));

        // TODO 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 数据库表映射到实体的命名策略，驼峰原则
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 字数据库表字段映射到实体的命名策略，驼峰原则
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 实体是否生成 serialVersionUID
        strategy.setEntitySerialVersionUID(false);
        // 是否生成实体时，生成字段注解
        strategy.setEntityTableFieldAnnotationEnable(true);
        // 使用lombok
        strategy.setEntityLombokModel(true);
        // 设置逻辑删除键
//        strategy.setLogicDeleteFieldName("del_flag");
        // TODO 指定生成的bean的数据库表名
        //strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        //strategy.setInclude("gen_table,gen_table_column,qrtz_blob_triggers,qrtz_calendars,qrtz_cron_triggers,qrtz_fired_triggers,qrtz_job_details,qrtz_locks,qrtz_paused_trigger_grps,qrtz_scheduler_state,qrtz_simple_triggers,qrtz_simprop_triggers,qrtz_triggers,sys_config,sys_dept,sys_dict_data,sys_dict_type,sys_job,sys_job_log,sys_logininfor,sys_menu,sys_notice,sys_oper_log,sys_post,sys_role,sys_role_dept,sys_role_menu,sys_user,sys_user_online,sys_user_post,sys_user_role,t_business_info,t_business_system,t_business_usage,t_category,t_domain_info,t_domain_object_info,t_error_code,t_error_code_category,t_field_enum,t_field_info,t_field_status,t_file_info,t_file_page_tag_ref,t_front_interface_field_source,t_interface_generator,t_interface_info,t_interface_model,t_interface_object,t_interface_params_field_relation,t_interface_test_log,t_page,t_page_element,t_page_image_element_condition,t_page_image_element_condition_detail,t_page_image_element_ref,t_page_tag,t_params,t_params_field_relation,t_participate_role,t_process_branch,t_process_branch_condition,t_process_branch_condition_detail,t_process_branch_end,t_product_info,t_product_version_obejct,t_question_info,t_status_process,t_table_index,t_table_index_field,t_table_info,t_table_object,t_table_relation,t_usage,t_usage_obejct,t_usage_participate_role,t_user_data_auth,t_virtual_usage,t_virtual_usage_field_merge,t_virtual_usage_group,t_virtual_usage_group_detail,t_virtual_usage_param_source".split(","));
        strategy.setInclude("sld_object");
        // 驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
