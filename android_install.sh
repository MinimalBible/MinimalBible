#!/bin/bash - 

COMPONENTS="
tools
platform-tools
build-tools-21.1.1
android-21
extra-android-m2repository
extra-android-support
extra-google-m2repository"

wget http://dl.google.com/android/android-sdk_r23.0.2-linux.tgz
tar -zxvf android-sdk_r23.0.2-linux.tgz
cd android-sdk-linux/
export ANDROID_HOME=`pwd`
ls
ls *

for COMP in ${COMPONENTS}
do
	echo "yes" | tools/android update sdk -u -a -t ${COMP}
done

cd -
