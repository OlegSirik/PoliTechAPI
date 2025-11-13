package ru.pt.numbers.entity;

import jakarta.persistence.*;
import ru.pt.api.dto.numbers.ResetPolicy;

import java.time.LocalDate;

@Entity
@Table(name = "pt_number_generators")
public class NumberGeneratorEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "product_code", nullable = false, length = 100)
    private String productCode;

    @Column(name = "mask", nullable = false, length = 255)
    private String mask;

    @Column(name = "reset_policy", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ResetPolicy resetPolicy;

    @Column(name = "max_value", nullable = false)
    private Integer maxValue = 999999;

    @Column(name = "last_reset", nullable = false)
    private LocalDate lastReset = LocalDate.now();

    @Column(name = "current_value", nullable = false)
    private Integer currentValue = 0;

    @Column(name = "xor_mask", nullable = false, length = 20)
    private String xorMask;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public ResetPolicy getResetPolicy() {
        return resetPolicy;
    }

    public void setResetPolicy(ResetPolicy resetPolicy) {
        this.resetPolicy = resetPolicy;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public LocalDate getLastReset() {
        return lastReset;
    }

    public void setLastReset(LocalDate lastReset) {
        this.lastReset = lastReset;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    public String getXorMask() {
        return xorMask;
    }

    public void setXorMask(String xorMask) {
        this.xorMask = xorMask;
    }
}