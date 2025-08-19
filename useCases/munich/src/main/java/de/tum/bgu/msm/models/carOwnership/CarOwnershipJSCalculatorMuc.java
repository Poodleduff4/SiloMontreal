package de.tum.bgu.msm.models.carOwnership;

//import de.tum.bgu.msm.util.js.JavaScriptCalculator;
import de.tum.bgu.msm.utils.JavaScriptCalculator;

import java.io.Reader;

/**
 * Created by matthewokrah on 29/09/2017.
 */
public class CarOwnershipJSCalculatorMuc extends JavaScriptCalculator<double[]> {

    public CarOwnershipJSCalculatorMuc(Reader reader){
        super(reader);
    }

    public double[] calculateCarOwnerShipProbabilities(int previousCars, int hhSizePlus, int hhSizeMinus,
                                                       int hhIncomePlus, int hhIncomeMinus, int licensePlus,
                                                       int changeResidence) {
//        return super.calculate("calculateCarOwnerShipProbabilities", previousCars, hhSizePlus, hhSizeMinus,
//                hhIncomePlus, hhIncomeMinus, licensePlus, changeResidence);
        double[] intercept = {-3.0888, -5.6650};
        double[] betaPreviousCars = {-0.5201, 1.3526};
        double[] betaHHSizePlus = {2.0179, 0.0};
        double[] betaHHSizeMinus = {0.0, 1.1027};
        double[] betaIncomePlus = {0.4842, 0.0};
        double[] betaIncomeMinus = {0.0, 0.3275};
        double[] betaLicensePlus = {1.8213, 0.0};
        double[] betaChangeResidence = {1.1440, 0.9055};

        double[] results = new double[3];
        double sum = 0;

        for(int i = 0; i < 2; i++) {
            double utility = intercept[i] + (betaPreviousCars[i] * previousCars) + (betaHHSizePlus[i] * hhSizePlus)
                    + (betaHHSizeMinus[i] * hhSizeMinus) + (betaIncomePlus[i] * hhIncomePlus)
                    + (betaIncomeMinus[i] * hhIncomeMinus) + (betaLicensePlus[i] * licensePlus)
                    + (betaChangeResidence[i] * changeResidence);

            double result = Math.exp(utility);
            sum += result;
            results[i + 1] = result;
        }

        double probNoChange = 1.0 / (sum + 1.0);

        sum = 0;
        for(int i = 0; i < 2; i++) {
            results[i + 1] = results[i + 1] * probNoChange;
            sum += results[i + 1];
        }

        results[0] = 1 - sum;

        return results;
    }

}
