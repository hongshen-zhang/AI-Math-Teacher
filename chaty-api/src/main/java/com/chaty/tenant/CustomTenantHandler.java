package com.chaty.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.stereotype.Component;

import static com.chaty.security.AuthUtil.getLoginSchool;


@Component
public class CustomTenantHandler implements TenantLineHandler {

    @Override
    public Expression getTenantId() {
        if (getLoginSchool() == null) {
            return null;
        }
        String tenantId = getLoginSchool().getSchoolId();
        return new StringValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        Boolean ignoreTenant = TenantContext.getIgnoreTenant();
        if (Boolean.TRUE.equals(ignoreTenant)) {
            return true;
        }
        String[] tables = {"doc_correct_file", "doc_correct_config", "doc_correct_record", "doc_correct_config_package", "doc_correct_task"};
        for (String ignoreTable : tables) {
            if (ignoreTable.equals(tableName)) {
                return false;
            }
        }
        return true;
    }

}

