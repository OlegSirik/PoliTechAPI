package ru.pt.api.dto.payment;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentData {

    private String policyNumber;
    private String paymentLink;
    private String qrLink;
    private String fileId;
    private ZonedDateTime paymentDate;
    private Long installmentNumber;
    private BigDecimal amount;

    public PaymentData() {
    }

    public PaymentData(String policyNumber, String paymentLink, String qrLink, String fileId, ZonedDateTime paymentDate, Long installmentNumber, BigDecimal amount) {
        this.policyNumber = policyNumber;
        this.paymentLink = paymentLink;
        this.qrLink = qrLink;
        this.fileId = fileId;
        this.paymentDate = paymentDate;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public String getQrLink() {
        return qrLink;
    }

    public void setQrLink(String qrLink) {
        this.qrLink = qrLink;
    }

    public ZonedDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(ZonedDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Long getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Long installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
