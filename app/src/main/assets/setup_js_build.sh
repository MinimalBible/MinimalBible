#!/bin/bash - 
#===============================================================================
#
#          FILE: setup_js_build.sh
# 
#         USAGE: ./setup_js_build.sh 
# 
#   DESCRIPTION: Set up a Linux host for building MinimalBible. It is possible
#					to do in Windows, but Linux is scriptable.
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: YOUR NAME (), 
#  ORGANIZATION: 
#       CREATED: 09/01/2014 15:19
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

# Check for apt. This could be done with RPM, but I don't have access to a
# computer for it.
if [ `which apt-get` == "" ]; then
	echo "Building on Redhat/RPM is not currently supported."
	exit 1
fi

# We need root before doing anything else
if [ $UID != 0 ]; then
	sudo bash "$0"
	exit $?
fi

# We don't really need Node, npm is enough.
apt-get install npm

# Gulp needs a global install
npm install -g gulp

# And run the rest of everything as the user who invoked sudo
sudo -u $SUDO_USER npm install
