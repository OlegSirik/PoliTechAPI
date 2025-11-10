package ru.pt.process.service;

import org.springframework.stereotype.Component;
import ru.pt.api.dto.process.Cover;
import ru.pt.api.dto.process.InsuredObject;
import ru.pt.api.dto.product.LobVar;
import ru.pt.api.service.process.PostProcessService;

import java.util.List;

@Component
public class PostProcessServiceImpl implements PostProcessService {
    @Override
    public InsuredObject setCovers(InsuredObject insuredObject, List<LobVar> calculatedValues) {
        if (insuredObject != null && insuredObject.getCovers() != null) {
            for (Cover cover : insuredObject.getCovers()) {
                if (cover == null || cover.getCover() == null) {
                    continue;
                }
                String sumInsuredVarCode = cover.getCover().getCode() + "_SumIns";
                String premiumVarCode = cover.getCover().getCode() + "_Prem";
                String deductibleNrVarCode = cover.getCover().getCode() + "_DedNr";

                // TODO медленно, надо мапу передавать в метод
                // Find values in lobVars
                String sumInsured = calculatedValues.stream()
                        .filter(v -> sumInsuredVarCode.equals(v.getVarCode()))
                        .map(LobVar::getVarValue)
                        .findFirst()
                        .orElse(null);

                String premium = calculatedValues.stream()
                        .filter(v -> premiumVarCode.equals(v.getVarCode()))
                        .map(LobVar::getVarValue)
                        .findFirst()
                        .orElse("0.0");

                String deductibleNr = calculatedValues.stream()
                        .filter(v -> deductibleNrVarCode.equals(v.getVarCode()))
                        .map(LobVar::getVarValue)
                        .findFirst()
                        .orElse(null);

                // Set values to cover if setters exist
                if (sumInsured != null) {
                    try {
                        cover.setSumInsured(Double.parseDouble(sumInsured));
                    } catch (Exception ignored) {
                    }
                }
                try {
                    cover.setPremium(Double.parseDouble(premium));
                } catch (Exception ignored) {
                }
                if (deductibleNr != null) {
                    try {
                        cover.setDeductible(Double.parseDouble(deductibleNr));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return insuredObject;
    }
}
