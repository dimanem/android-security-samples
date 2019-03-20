#!/bin/sh

source ~/.bash_profile

apktool b -f -d SecurityDemo
mv SecurityDemo/dist/SecurityDemo.apk SecurityDemo2.apk
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore hacker.keystore SecurityDemo2.apk alias_name
jarsigner -verify -verbose -certs SecurityDemo2.apk


