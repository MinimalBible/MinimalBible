#!/bin/bash - 

COMPONENTS="
tools
platform-tools
build-tools-21.1.2
android-22
extra-android-m2repository
extra-android-support
extra-google-m2repository"

SDK_VERSION="24.1.2"

echo "Updating packages..."
sudo apt-get update -qq

echo "Installing Android dependencies..."
sudo apt-get install -qq --force-yes libgd2-xpm ia32-libs ia32-libs-multiarch

wget http://dl.google.com/android/android-sdk_r${SDK_VERSION}-linux.tgz
tar -zxf android-sdk_r${SDK_VERSION}-linux.tgz

for COMP in ${COMPONENTS}
do
	echo "Installing Android component: $COMP"
	echo "yes" | android-sdk-linux/tools/android update sdk -u -a -t ${COMP} > /dev/null
done
