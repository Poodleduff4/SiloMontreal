package de.tum.bgu.msm;

import de.tum.bgu.msm.container.ModelContainer;
import de.tum.bgu.msm.data.dwelling.DefaultDwellingTypes;
import de.tum.bgu.msm.data.dwelling.DwellingFactoryImpl;
import de.tum.bgu.msm.data.household.HouseholdDataImpl;
import de.tum.bgu.msm.io.MultiFileResultsMonitorMuc;
import de.tum.bgu.msm.io.output.HouseholdSatisfactionMonitor;
import de.tum.bgu.msm.io.output.ModalSharesResultMonitor;
import de.tum.bgu.msm.io.output.MultiFileResultsMonitor;
import de.tum.bgu.msm.properties.Properties;
import de.tum.bgu.msm.schools.DataContainerWithSchools;
import de.tum.bgu.msm.utils.SiloUtil;
import org.apache.log4j.Logger;
import org.matsim.contrib.dvrp.run.Modal;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

/**
 * Implements SILO for the Munich Metropolitan Area
 *
 * @author Rolf Moeckel and Ana Moreno
 * Created on May 12, 2016 in Munich, Germany
 */
public class SiloMuc {

    private final static Logger logger = Logger.getLogger(SiloMuc.class);

    public static void main(String[] args) {

        System.out.println("Total MB: " + (double) (Runtime.getRuntime().maxMemory()) / 1024 / 1024);
        System.out.println("Used MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);

        Properties properties = SiloUtil.siloInitialization(args[0]);

        Config config = null;
        if (args.length > 1 && args[1] != null) {
            config = ConfigUtils.loadConfig(args[1]);
        }
        logger.info("Started SILO land use model for the Munich Metropolitan Area");
        DataContainerWithSchools dataContainer = DataBuilder.getModelDataForMuc(properties, config);
        DataBuilder.read(properties, dataContainer);
        ModelContainer modelContainer = ModelBuilderMuc.getModelContainerForMuc(dataContainer, properties, config);
        System.out.println("Total MB: " + (double) (Runtime.getRuntime().totalMemory()) / 1024 / 1024);
        System.out.println("Used MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);

        SiloModel model = new SiloModel(properties, dataContainer, modelContainer);
        model.addResultMonitor(new MultiFileResultsMonitorMuc(dataContainer, properties));
        model.addResultMonitor(new ModalSharesResultMonitor(dataContainer, properties));
        model.addResultMonitor(new HouseholdSatisfactionMonitor(dataContainer, properties, modelContainer));
        System.out.println("Total MB: " + (double) (Runtime.getRuntime().totalMemory()) / 1024 / 1024);
        System.out.println("Used MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);
        model.runModel();
        logger.info("Finished SILO.");
    }
}
