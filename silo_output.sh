ls -lt | grep slurm | head -n1 | awk '{print $9}' | xargs cat 
