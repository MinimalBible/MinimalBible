#!/bin/bash - 

wget http://dl.google.com/android/android-sdk_r23.0.2-linux.tgz
tar xf android-sdk_r23.0.2-linux.tgz
cd android-sdk-linux/
export ANDROID_HOME=`pwd`

COMPONENT_ARRAY=$(echo $COMPONENTS | tr "," "\n")
for COMP in $COMPONENT_ARRAY
do
	echo "yes" | tools/android update sdk -u -a -t $COMP
done

cd -
