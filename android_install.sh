#!/bin/bash - 

COMPONENTS="
tools
platform-tools
build-tools-21.1.1
android-21
extra-android-m2repository
extra-android-support
extra-google-m2repository"

sudo apt-get update
sudo apt-get install --force-yes libgd2-xpm ia32-libs ia32-libs-multiarch
wget http://dl.google.com/android/android-sdk_r23.0.2-linux.tgz
tar -zxf android-sdk_r23.0.2-linux.tgz

for COMP in ${COMPONENTS}
do
	echo "yes" | android-sdk-linux/tools/android update sdk -u -a -t ${COMP} > /dev/null
done