package ru.pt.db.utils;

import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;
import ru.pt.api.dto.auth.UserData;
import ru.pt.api.dto.db.PolicyStatus;
import ru.pt.api.dto.versioning.Version;
import ru.pt.db.entity.PolicyIndexEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class PolicyProjectionService {

    public PolicyIndexEntity readPolicyIndex(UUID uuid, Version version, UserData userData, String policy) {
        var index = new PolicyIndexEntity();
        index.setPolicyId(uuid);

        var ctx = JsonPath.parse(policy);

        try {
            index.setPolicyNumber(ctx.read("$.policyNumber", String.class));
        } catch (Exception e) {
        }
        try {
            // TODO уточнить можно ли под DEV версией заводить полисы
            index.setVersionNo(version.getProdVersion());
        } catch (Exception e) {
        }
        try {
            index.setProductCode(ctx.read("$.product.code", String.class));
        } catch (Exception e) {
        }
        try {
            String createDateStr = ctx.read("$.createDate", String.class);
            if (createDateStr != null) {
                index.setCreateDate(OffsetDateTime.parse(createDateStr));
            }
        } catch (Exception e) {
        }
        try {
            String issueDateStr = ctx.read("$.issueDate", String.class);
            if (issueDateStr != null) {
                index.setIssueDate(OffsetDateTime.parse(issueDateStr));
            }
        } catch (Exception e) {
        }
        try {
            index.setIssueTimezone(ctx.read("$.issueTimezone", String.class));
        } catch (Exception e) {
        }
        try {
            String paymentDateStr = ctx.read("$.paymentDate", String.class);
            if (paymentDateStr != null) {
                index.setPaymentDate(OffsetDateTime.parse(paymentDateStr));
            }
        } catch (Exception e) {
        }
        try {
            String startDateStr = ctx.read("$.startDate", String.class);
            if (startDateStr != null) {
                index.setStartDate(OffsetDateTime.parse(startDateStr));
            }
        } catch (Exception e) {
        }
        try {
            String endDateStr = ctx.read("$.endDate", String.class);
            if (endDateStr != null) {
                index.setEndDate(OffsetDateTime.parse(endDateStr));
            }
        } catch (Exception e) {
        }


        index.setUserAccountId(userData.getAccountId());
        index.setClientAccountId(userData.getClientId());

        if (version.getDevVersion() != null) {
            index.setVersionStatus("dev");
        } else {
            index.setVersionStatus("prod");
        }


        // Set default create date if not provided
        if (index.getCreateDate() == null) {
            index.setCreateDate(OffsetDateTime.now());
        }

        index.setPolicyStatus(PolicyStatus.NEW);

        return index;
    }


}
