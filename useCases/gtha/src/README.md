In root dir

mvn install -pl useCases\\gtha -am -DskipTests

java -Xmx16g -jar useCases\\gtha\\target\\gtha-0.1.0-SNAPSHOT-jar-with-dependencies.jar useCases/gtha/test/GTAH_1/SiloGTHA.properties useCases/gtha/test/GTAH_1/matsim_input/SiloGTHA_matsimConfig.xml 