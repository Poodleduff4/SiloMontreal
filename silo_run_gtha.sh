#!/bin/bash
#SBATCH --time=01:00:00
#SBATCH --account=def-bfarooq
#SBATCH --mem=64g


cd "/project/def-bfarooq/lukeg03/SiloMontreal"
export MAVEN_OPTS="-Xmx16g";
export JAVA_HOME="/cvmfs/soft.computecanada.ca/easybuild/software/2023/x86-64-v3/Core/java/17.0.6"
maven="/cvmfs/soft.computecanada.ca/easybuild/software/2023/x86-64-v3/Core/maven/3.9.10/bin/mvn"
java="/cvmfs/soft.computecanada.ca/easybuild/software/2023/x86-64-v3/Core/java/17.0.6/bin/java"
echo "Java version:"
$java -version
$java -Xmx60g -jar useCases/gtha/target/gtha-0.1.0-SNAPSHOT-jar-with-dependencies.jar useCases/gtha/test/GTAH_1/SiloGTHA.properties useCases/gtha/test/GTAH_1/matsim_input/SiloGTHA_matsimConfig.xml
echo "Starting Silo GTHA jar"
