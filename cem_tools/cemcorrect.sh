#! /bin/sh
THRESHOLDHI=0.3
THRESHOLDNOX=0.3
THRESHOLDSO2=0.3
export THRESHOLDHI
export THRESHOLDNOX
export THRESHOLDSO2
java -classpath ./cemcorrect.jar edu.unc.cem.correct.CemCorrect $*
