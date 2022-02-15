# Java exercises for distributed computing

Install OpenMPI: https://www.open-mpi.org/software/ompi/v2.0

```bash
tar xf openmpi-2.0.2.tar
cd openmpi-2.0.2
./configure --prefix=$HOME/opt/usr/local
make all
make install
$HOME/opt/usr/local/bin/mpirun --version
```

On MAC OS:

```
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" 2> /dev/null

brew install open-mpi
```

To run MPI test on Terminal:

```bash
mvn clean:clean compile test -P MPITests
```
