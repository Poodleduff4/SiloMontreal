#!/bin/bash
#SBATCH --time=00:10:00
#SBATCH --account=def-bfarooq
#SBATCH --mem=12g


cd "/project/def-bfarooq/lukeg03/SiloMontreal"
export MAVEN_OPTS="-Xmx16g";
export JAVA_HOME="/cvmfs/soft.computecanada.ca/easybuild/software/2023/x86-64-v3/Core/java/17.0.6"
maven="/cvmfs/soft.computecanada.ca/easybuild/software/2023/x86-64-v3/Core/maven/3.9.10/bin/mvn"
echo "Maven dir: $maven"
echo "Starting Silo GTHA install..."
$maven -version
$maven install -pl useCases/gtha -am -DskipTests;
echo "Install Complete";
cd ..
