package ru.pt.api.dto.numbers;

/**
 * DTO для создания алгоритма нумератора
 */
public class NumberGeneratorDescription {

    private Integer id;
    private String productCode;
    private String mask;
    private ResetPolicy resetPolicy;
    private int maxValue = 999_999;
    private String xorMask;

    public NumberGeneratorDescription() {
    }

    public NumberGeneratorDescription(Integer id, String productCode, String mask, ResetPolicy resetPolicy, int maxValue, String xorMask) {
        this.id = id;
        this.productCode = productCode;
        this.mask = mask;
        this.resetPolicy = resetPolicy;
        this.maxValue = maxValue;
        this.xorMask = xorMask;
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

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public String getXorMask() {
        return xorMask;
    }

    public void setXorMask(String xorMask) {
        this.xorMask = xorMask;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NumberGeneratorDescription{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", mask='" + mask + '\'' +
                ", resetPolicy=" + resetPolicy +
                ", maxValue=" + maxValue +
                ", xorMask='" + xorMask + '\'' +
                '}';
    }
}
