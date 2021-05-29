# CEMCorrect
The CEMCorrect tool identifies and corrects anomalous values in continuous emissions monitoring (CEM) hourly emissions data.

## Install
These instructions install CEMCorrect into /opt/cemcorrect/.

```
> mkdir /opt/cemcorrect  
> cd /opt/cemcorrect  
> git clone https://github.com/CEMPD/cemcorrect.git .
```

## Build
To build CEMCorrect, you'll need JDK version 8 or higher.

```
> javac -version
javac 1.8.0_262
```

```
> cd cem_tools  
> ./gradlew build
```

## Run

### Create your config.properties file
```
> cd cem_tools/config  
> cp config.properties.example config.properties
```

### Update properties
Edit the config.properties file  
Update the `input.dir` and `output.dir` locations for your installation  
Update which states to run (`states.list`) and the list of files for each state (e.g. `alabama.files`, `arizona.files`, etc.)  
Optionally update the `report.prefix` and `report.suffix` properties

### Run CEMCorrect
```
> cd /opt/cemcorrect/cem_tools  
> ./cemcorrect.sh
```
